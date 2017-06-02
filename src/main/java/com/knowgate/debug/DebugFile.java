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
 * <p>Write execution traces to a flat text file</p>
 * Traces are written to javatrc.txt file on the specified directory.<br>
 * By default /vagrant/tmp/ on Unix and C:\TEMP\\Debug on Windows.
 * @author Sergio Montoro Ten
 * @version 7.0
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

	public static short dumpTo;

	/**
	 * Activate/Deactivate trace output
	 */
	public static boolean trace;
	
	/**
	 * Number of ident spaces currently on each file
	 */
	private static ConcurrentHashMap<Long, String> mIdent = new ConcurrentHashMap<Long, String>();
	
	static {
		refresh();
	}
	
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

	public static void setConfFile(final String confFilePath) {
		confFile = confFilePath;
	}

	public static void refresh() {
		System.out.println("Refreshing debug config");
		File etc = new File(getConfFile());
		if (etc.exists() && etc.isFile() && etc.canRead()) {
			System.out.println("using config file "+etc.getAbsolutePath());
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
			System.out.println("using internal config at com/knowgate/debug/debugfile.conf");
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
		System.out.println("Debug config refreshed");
					 
	} // getDebugPath

	private static void readDebugConfFromPropertiesInputStream(InputStream oInStrm) {
		System.out.println("Begin readDebugConfFromPropertiesInputStream()");
		String sWin32DebugDir = DEFAULT_TMP_WIN32;
		String sLinuxDebugDir = DEFAULT_TMP_LINUX;
		try {
			if (oInStrm != null) {
				
				Properties oProps = new Properties();
				oProps.load(oInStrm);
				
				final String debug = oProps.getProperty("debug", "false");
				trace = debug.equalsIgnoreCase("true") || debug.equalsIgnoreCase("yes") || debug.equalsIgnoreCase("on") || debug.equalsIgnoreCase("1");
				System.out.println("trace is "+trace);
				
				final String sink = oProps.getProperty("sink", "file");
				if (sink.equalsIgnoreCase("file"))
					dumpTo = DUMP_TO_FILE;
				else if (sink.equalsIgnoreCase("log4j"))
					dumpTo = DUMP_TO_LOG4J;
				else
					dumpTo = DUMP_TO_STDOUT;

				System.out.println("dumpTo is "+dumpTo);

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
		System.out.println("End readDebugConfFromPropertiesInputStream()");
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
	 * empty the return /tmp on Unix systems and C:\Temp\Debug on
	 * Windows
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
        out.println("DISPLAY DEVICES");
        i.printDisplayInfo();
        out.println("FONTS");
        i.printFontsInfo();
        out.println("LOCALES");
        i.printLocaleInfo();
		
        DebugFile.writeln(new String(byteOut.toByteArray()));
        
        try { byteOut.close(); } catch (IOException ignore) { }
	}

	public static void main(String[] argv) throws Exception {
		System.out.println("DEBUG MODE " + (trace ? "ENABLED" : "DISABLED"));
		System.out.println("DEBUG FILE " + (getFile(-1l)==null ? "N/A" : getFile(-1l)));
		JavaInfo.main(argv);
	}

}
