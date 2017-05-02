package com.knowgate.system;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Properties;

import com.knowgate.debug.DebugFile;

public class Shell {

	Properties envVars;
	
	//-----------------------------------------------------------

	public Shell() {
		readEnvVars();
	}

	//-----------------------------------------------------------

	public String getEnvironmentVariable(String variableName) {
		return envVars.getProperty(variableName);
	}

	public String getEnvironmentVariableOrDefault(String variableName, String defaultValue) {
		return envVars.getProperty(variableName, defaultValue);
	}

	//-----------------------------------------------------------

	private void readEnvVars() throws IllegalArgumentException {
		envVars = new Properties();
		Runtime oRT;
		Process oPT;
		InputStream oST;

		final int ENV_BUFFER_SIZE = 131072;

		try {
			if (System.getProperty("os.name").startsWith("Windows")) {

				if (DebugFile.trace) DebugFile.writeln ("Runtime.getRuntime()");

				oRT = Runtime.getRuntime();

				if (DebugFile.trace) DebugFile.writeln ("Runtime.exec(\"cmd.exe /cset\")");

				oPT = oRT.exec("cmd.exe /cset");

				oST = oPT.getInputStream();

				byte[] byBuffer = new byte[ENV_BUFFER_SIZE];

				int iReaded = oST.read (byBuffer, 0, ENV_BUFFER_SIZE);

				oST.close();

				oPT.destroy();

				oRT = null;

				// Double back slashes
				byte[] byEnvVars = new byte[ENV_BUFFER_SIZE+4096];
				int iEnvLength = 0;

				for (int i=0; i<iReaded; i++) {
					byEnvVars[iEnvLength++] = byBuffer[i];
					if (92==byBuffer[i])
						byEnvVars[iEnvLength++] = byBuffer[i];
				} // next

				byBuffer = null;

				if (DebugFile.trace) DebugFile.writeln (new String(byEnvVars, 0, iEnvLength));

				envVars.load (new StringBufferInputStream(new String(byEnvVars, 0, iEnvLength)));

			}
			else {

				if (DebugFile.trace) DebugFile.writeln ("Runtime.getRuntime()");

				oRT = Runtime.getRuntime();

				if (DebugFile.trace) DebugFile.writeln ("Runtime.exec(\"/usr/bin/env\")");

				oPT = oRT.exec("/usr/bin/env");

				oST = oPT.getInputStream();

				if (DebugFile.trace) DebugFile.writeln ("Properties.load(Process.getInputStream())");

				envVars.load(oST);

				oST.close();

				oPT.destroy();

				oRT = null;
			}
		}
		catch (IOException ioe) {
			if (DebugFile.trace) DebugFile.writeln ("Runtime.getRuntime().exec(...) IOException " + ioe.getMessage());
		}
		catch (NullPointerException npe) {
			if (DebugFile.trace) DebugFile.writeln ("Runtime.getRuntime().exec(...) NullPointerException " + npe.getMessage());
		}
	} // readEnvVars

	//-----------------------------------------------------------

}
