package com.knowgate.debug;

/**
 * This file is licensed under the Apache License version 2.0.
 * You may not use this file except in compliance with the license.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.
 */

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.UnknownHostException;

/**
 * <p>Write execution traces to Log4j2, a set of text files (one per thread) or STDOUT</p>
 * <p>
 * DebugFile will decide where to write debug traces with following algorithm: 
 * <ol>
 * <li>If a System property named <code>knowgate.debugfile</code> exists this will be assumed to be the absolute path to the properties file that tells whether Log4j2, files, or stdout must be used for output.</li>
 * <li>If System <code>knowgate.debugfile</code> is not set then DebugFile will try to read <code>/etc/debugfile.conf</code> on *nix or <code>C:\\Windows\System32\drivers\etc\debugfile.conf on Windows</code></li>
 * <li>If <code>debugfile.conf</code> is not found the DebugFile will use the package resource at <code>com/knowgate/debug/debugfile.conf</code></li>
 * <li>Using the properties read from the external .conf file or the internal resource file, DebugFile will decide which output to use.</li>
 * </ol>
 * </p>
 * <h2>Configuration taken from Log4j2</h2>
 * <p>The default behavior is use a Log4j2 configuration provided by the client application for <code>com.knowgate.debug.DebugFile.class</code>
 * This configuration file must be placed in the CLASSPATH as described in <a href="https://logging.apache.org/log4j/2.x/manual/configuration.html">Log4j 2.c configuration</a>.</p>
 * <p>A sample log4j2.xml file looks like:</p>
 * <code>
 * &lt;?xml&nbsp;version="1.0"&nbsp;encoding="UTF-8"?&gt;<br>
 * &lt;Configuration&nbsp;status="debug"&nbsp;strict="true"&nbsp;name="XMLConfigTest"&nbsp;packages="org.apache.logging.log4j.test"&gt;<br>
 * &nbsp;&nbsp;&lt;Properties&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;Property&nbsp;name="filename"&gt;/var/log/knowgate/debug.log&lt;/Property&gt;<br>
 * &nbsp;&nbsp;&lt;/Properties&gt;<br>
 * &nbsp;&nbsp;&lt;Appenders&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;Appender&nbsp;type="File"&nbsp;name="File"&nbsp;fileName="${filename}"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;Layout&nbsp;type="PatternLayout"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;Pattern&gt;%d&nbsp;%p&nbsp;%C{1.}&nbsp;[%t]&nbsp;%m%n&lt;/Pattern&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/Layout&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/Appender&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;Appender&nbsp;type="List"&nbsp;name="List"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/Appender&gt;<br>
 * &nbsp;&nbsp;&lt;/Appenders&gt;<br>
 * &nbsp;&nbsp;&lt;Loggers&gt;&nbsp;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;Logger&nbsp;name="com.knowgate.debug"&nbsp;level="debug"&nbsp;additivity="false"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;AppenderRef&nbsp;ref="File"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/Logger&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;Root&nbsp;level="trace"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;AppenderRef&nbsp;ref="List"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/Root&gt;<br>
 * &nbsp;&nbsp;&lt;/Loggers&gt;<br>
 * &lt;/Configuration&gt;<br>
 * </code>
 * <h2>Configuration taken from debugfile.conf properties</h2>
 * <p>To avoid using Log4j2 create a properties file at /etc/debugfile.conf on *nix or C:\Windows\System32\drivers\etc\debugfile.conf on Windows.</p>
 * <p>The file must contain the following properties:</p>
 * <code>
 * # Accepted values are file stdout and log4j<br>
 * sink=file<br><br>
 * # Only applies if sink is file or stdout otherwise debug is controlled by Log4J<br>
 * debug=true<br><br>
 * # Only applies if sink is file<br>
 * debugdir_win32=C:\\TEMP\\Debug\\<br>
 * debugdir_linux=/var/log/knowgate/<br>
 * </code>
 * @author Sergio Montoro Ten
 * @version 9.0
 */
public final class DebugFile {

	public static final short DUMP_TO_FILE = (short) 1;
	public static final short DUMP_TO_STDOUT = (short) 2;
	public static final short DUMP_TO_LOG4J = (short) 4;
	
	private static final String DEFAULT_TMP_LINUX = "/tmp/";
	private static final String DEFAULT_TMP_WIN32 = "C:\\TEMP\\Debug\\";

	private static String confFile;

	private static String debugPath;
	
	private static String filePath;

	/**
	 * One of DUMP_TO_LOG4J, DUMP_TO_FILE or DUMP_TO_STDOUT constants
	 */
	public static short dumpTo;

	/**
	 * Activate/Deactivate debug output
	 */
	public static boolean trace;
	
	/**
	 * Number of ident spaces currently on each file
	 */
	private static ConcurrentHashMap<Long, String> mIdent = new ConcurrentHashMap<Long, String>();
	
	static {
		refresh();
	}
	
	/**
	 * <p>Get absolute path of the debugfile.conf file</p>
	 * <p>This is not the Log4j2 file at the appender but the properties files
	 * used to set whether DebugFile will use Log4j2, stdout or its own set of output files.</p>
	 * <p>By default is /etc/debugfile.conf on *nix and C:\Windows\System32\drivers\etc\debugfile.conf on Windows.</p>
	 * @return String File path
	 */
	public static String getConfFile() {
		final String debugfile = System.getProperty("knowgate.debugfile");
		if (null==confFile)
			if (null==debugfile)
				if (System.getProperty("os.name").startsWith("Windows"))
					return "C:\\Windows\\System32\\drivers\\etc\\debugfile.conf";
				else
					return  "/etc/debugfile.conf";
			else
				return debugfile;
				
		else
			return confFile;		
	}

	/**
	 * <p>Set absolute path to debugfile properties file</p>
	 * @param confFilePath String
	 */
	public static void setConfFile(final String confFilePath) {
		confFile = confFilePath;
	}

	/**
	 * <p>Refresh DebugFile and here on use changes made in configuration.</p>
	 */
	public static void refresh() {
		File etc = new File(getConfFile());
		if (etc.exists() && etc.isFile() && etc.canRead()) {
			setConfFile(etc.getAbsolutePath());
			FileInputStream fInStrm;
			try {
				fInStrm = new FileInputStream(etc);
				try {
					readDebugConfFromPropertiesInputStream(fInStrm);
					fInStrm.close();
				} catch (IOException ioe) {
					System.err.println("IOException at com.knowgate.debug.DebugFile "+ioe.getMessage());
				}
			} catch (FileNotFoundException neverthrown) { }
		} else {
			try {
				Class oThisClass = Class.forName("com.knowgate.debug.DebugFile");
				InputStream rInStrm = oThisClass.getResourceAsStream("debugfile.conf");
				readDebugConfFromPropertiesInputStream(rInStrm);
				rInStrm.close();				
			} catch (ClassNotFoundException neverthrown) {					
			} catch (IOException ioe) {
				System.err.println("IOException at com.knowgate.debug.DebugFile "+ioe.getMessage());
			}
		}					 
	} // getDebugPath

	private static void readDebugConfFromPropertiesInputStream(InputStream oInStrm) {
		String sWin32DebugDir = DEFAULT_TMP_WIN32;
		String sLinuxDebugDir = DEFAULT_TMP_LINUX;
		try {
			if (oInStrm != null) {
				
				Properties oProps = new Properties();
				oProps.load(oInStrm);
				
				final String debug = oProps.getProperty("debug", "false");
				trace = debug.equalsIgnoreCase("true") || debug.equalsIgnoreCase("yes") || debug.equalsIgnoreCase("on") || debug.equalsIgnoreCase("1");
				
				final String sink = oProps.getProperty("sink", "file");
				if (sink.equalsIgnoreCase("file"))
					dumpTo = DUMP_TO_FILE;
				else if (sink.equalsIgnoreCase("log4j"))
					dumpTo = DUMP_TO_LOG4J;
				else
					dumpTo = DUMP_TO_STDOUT;

				if (dumpTo==DUMP_TO_FILE) {
					sWin32DebugDir = oProps.getProperty("debugdir_win32", DEFAULT_TMP_WIN32);
					sLinuxDebugDir = oProps.getProperty("debugdir_linux", DEFAULT_TMP_LINUX);
				} else {
					sWin32DebugDir = sLinuxDebugDir = null;
				}

				oInStrm.close();
			}
		} catch (IOException ioe) {
			System.err.println("IOException at DebugFile.readDebugConfFromPropertiesInputStream() "+ioe.getMessage());			
		}
		if (System.getProperty("os.name").startsWith("Windows"))
			debugPath = sWin32DebugDir;
		else
			debugPath = sLinuxDebugDir;
	}

	private static String chomp(String sSource, String cEndsWith) {
		return sSource.endsWith(cEndsWith) ? sSource : sSource + cEndsWith;
	} // chomp
	
	/**
	 * Set debug file path
	 * @param sDebugFilePath Full path to file where debug traces will be written including directory and file name
	 */
	public static void setFile(String sDebugFilePath) {
		filePath = sDebugFilePath;
	}

	/**
	 * Get debug file path Read Java environment variable knowgate.debugdir to
	 * get the directory where javatrc.txt files are generated. If
	 * knowgate.debugdir is not set then read debugdir.cnf resource file in this
	 * package. If knowgate.debugdir is not set and debugdir.cnf is not found or
	 * empty the return /tmp on Unix systems and C:\Temp\Debug on Windows
	 * @param threadId long Files are generated per thread, so inform which one is dumping the traces
	 * @return String Full path to the file where the current thread will write its traces
	 */
	public static String getFile(final long threadId) {
		if (dumpTo == DUMP_TO_FILE) {
			if (null == filePath)
				filePath = chomp(System.getProperty("knowgate.debugdir", debugPath), File.separator) + "javatrc.";
			if (-1l==threadId)
				return filePath + "txt";
			else
				return filePath + String.valueOf(threadId) + ".txt";
		} else {
			return  null;
		}
	} // getFile()

	/**
	 * Increment indentation level Maximum indentation level is 80, after
	 * reaching that limit Indentation is automatically set to zero
	 */
	public static void incIdent() {
		Long oThId = new Long(Thread.currentThread().getId());
		String sIdent = "";
		if (mIdent.containsKey(oThId)) {
			sIdent = mIdent.get(oThId);
			mIdent.remove(oThId);
			if (sIdent.length() > 80)
				sIdent = "";

		}
		mIdent.put(oThId, sIdent + "  ");
	} // incIdent()

	/**
	 * Decrement indentation level
	 */
	public static void decIdent() {
		Long oThId = new Long(Thread.currentThread().getId());
		String sIdent = "";
		if (mIdent.containsKey(oThId)) {
			sIdent = mIdent.get(oThId);
			if (sIdent.length() > 2)
				sIdent = sIdent.substring(0, sIdent.length() - 2);
			else
				sIdent = "";
			mIdent.remove(oThId);
		} // fi
		mIdent.put(oThId, sIdent);
	}

	private static String getIdent() {
		Long oThId = new Long(Thread.currentThread().getId());
		String sIdent = "";
		if (mIdent.containsKey(oThId)) {
			sIdent = mIdent.get(oThId);
		}
		return sIdent;
	} // getIdent()

	/**
	 * <p>Write trace</p>
	 * If setFile() has not been called, traces are written to /vagrant/tmp/ on
	 * UNIX systems or C:\Temp\Debug on Windows
	 * @param str Characters to be written
	 */
	public static void write(char[] str) {
		FileWriter oDebugWriter;

		try {

			// Volcar las trazas de salida a una ubicación por defecto
			// según el sistema operativo sea Windows o UNIX

			switch (dumpTo) {
			case DUMP_TO_FILE:
				oDebugWriter = new FileWriter(getFile(Thread.currentThread().getId()), true);
				oDebugWriter.write(str);
				oDebugWriter.close();
				break;
			case DUMP_TO_STDOUT:
				System.out.print(str);
				break;
			case DUMP_TO_LOG4J:
				Log.out.debug(str);
				break;
			}
		} catch (IOException e) {
			System.out.print(str);
		}
	}

	/**
	 * <p>Write trace</p>
	 * Traces are written to /tmp/javatrc.txt on UNIX systems or C:\TEMP\Debug\javatrc.txt on Windows
	 * @param str String to be written
	 */
	public static void write(String str) {
		FileWriter oDebugWriter;

		try {

			// Volcar las trazas de salida a una ubicación por defecto
			// según el sistema operativo sea Windows o UNIX

			switch (dumpTo) {
			case DUMP_TO_FILE:
				oDebugWriter = new FileWriter(getFile(Thread.currentThread().getId()), true);
				oDebugWriter.write(str);
				oDebugWriter.close();
				break;
			case DUMP_TO_STDOUT:
				System.out.print(str);
				break;
			case DUMP_TO_LOG4J:
				Log.out.debug(str);
				break;
			}
		} catch (IOException e) {
			System.out.print(str);
		}
	}

	/**
	 * <p>Write trace and append line feed</p>
	 * @param str String to be written
	 */
	public static void writeln(String str) {
		FileWriter oDebugWriter;
		Date dt = new Date(System.currentTimeMillis());

		try {
			StringBuilder line = new StringBuilder(256);
			final long threadId = Thread.currentThread().getId();
			line .append(String.valueOf(threadId)).append(" ").append(dt.toString()).append(" ").append(getIdent()).append(str).append("\n");
			switch (dumpTo) {
			case DUMP_TO_FILE:
				oDebugWriter = new FileWriter(getFile(threadId), true);
				oDebugWriter.write(line.toString());
				oDebugWriter.close();
				break;
			case DUMP_TO_STDOUT:
				System.out.print(line.toString());
				break;
			case DUMP_TO_LOG4J:
				Log.out.debug(line.toString());
				break;
			}
		} catch (IOException e) {
			System.out.print(dt.toString() + getIdent() + str + "\n");
		}
	}

	/**
	 * <p>Write trace and append line feed</p>
	 * @param str String to be written
	 */
	public static void writeln(char[] str) {
		FileWriter oDebugWriter;
		Date dt = new Date();

		try {
			final long threadId = Thread.currentThread().getId();
			StringBuilder line = new StringBuilder(256);
			line .append(String.valueOf(threadId)).append(" ").append(dt.toString()).append(" ").append(getIdent()).append(str).append("\n");
			switch (dumpTo) {
			case DUMP_TO_FILE:
				oDebugWriter = new FileWriter(getFile(threadId), true);
				oDebugWriter.write(line.toString());
				oDebugWriter.close();
				break;
			case DUMP_TO_STDOUT:
				System.out.print(line.toString());
				break;
			case DUMP_TO_LOG4J:
				Log.out.debug(line.toString());
				break;
			}
		} catch (IOException e) {
			System.out.print(dt.toString() + getIdent() + new String(str) + "\n");
		}
	} // write

	/**
	 * <p>Write stack trace for an exception to debug output</p
	 * @param t Throwable
	 */
	public static void writeStackTrace(Throwable t) {
		try {
			DebugFile.write(StackTraceUtil.getStackTrace(t));
		} catch (Exception ignore) {
		}
	}

	/**
	 * <p>This method is just an alias for DebugFile.writeln</p>
	 * @param str String
	 */
	public void debug(String str) {
		DebugFile.writeln(str);
	}

	/**
	 * <p>Write message and stack trace for an exception to debug output</p
	 * @param str String Message
	 * @param xcpt Exception
	 */
	public void debug(String str, Exception xcpt) {
		DebugFile.writeln(str);
		new ErrorHandler(xcpt);
	}

	/**
	 * <p>Write environment information to debug output<p>
	 * @throws java.security.AccessControlException
	 */
	public static void envinfo() throws java.security.AccessControlException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2048);
		PrintStream out = new PrintStream(byteOut);
		JavaInfo i = new JavaInfo(out);
		
		out.println("DEBUG MODE " + (trace ? "ENABLED" : "DISABLED"));
		out.println("DEBUG FILE " + (getFile(Thread.currentThread().getId())==null ? "N/A" : getFile(Thread.currentThread().getId())));
		
        out.println("DATE INFO");
        i.printCurrentTime();
        out.println("JVM INFO");
        i.printJVMInfo();
        out.println("ENVIRONMENT");
        i.printSystemEnvironment();
        out.println("SYSTEM PROPERTIES");
        i.printSystemProperties();
        out.println("CLASS LOADER");
        i.printClassLoaderInfo();
        out.println("OPERATING SYSTEM");
        i.printOSInfo();
        out.println("MEMORY");
        i.printRuntimeMemory();
        out.println("DISK");
        i.printDiskInfo();
        out.println("SECURITY");
        try {
			i.printSecurityInfo();
	        out.println("");
		} catch (UnknownHostException e) {
			out.println("UnknownHostException "+e.getMessage());
		}
        out.println("LOG");
        i.printLoggingInfo();
        out.println("KEY MANAGER");
        i.printKeyManagerInfo();
		
        DebugFile.writeln(new String(byteOut.toByteArray()));
        
        try { byteOut.close(); } catch (IOException ignore) { }
	}

	public static void main(String[] argv) throws Exception {
		System.out.println("DEBUG MODE " + (trace ? "ENABLED" : "DISABLED"));
		System.out.println("DEBUG FILE " + (getFile(-1l)==null ? "N/A" : getFile(-1l)));
		JavaInfo.main(argv);
	}

}
