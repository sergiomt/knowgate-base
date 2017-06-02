package com.knowgate.debug.test;

import org.junit.Test;
import org.junit.Ignore;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import com.knowgate.io.FileUtils;

import static com.knowgate.debug.Log.out;
import static com.knowgate.debug.DebugFile.*;

public class TestDebugFile {
	
	@Ignore
	public void testConfig() throws IOException {
		
		// Default config
		assertFalse(trace);
		assertNull(getFile(-1l));
		assertEquals(dumpTo, DUMP_TO_LOG4J);
		
		File logf = setUpDebugLog4j2();
		
		writeln("Test debug log trace");
		
		String logContent = FileUtils.readFileToString(logf);
		assertTrue(logContent.trim().endsWith("Test debug log trace"));
		
		logf.delete();
		
		File conf = File.createTempFile("temp-debugfile", ".conf");
		final String logDir = "/" + String.join("/", new String[]{"var","log","knowgate"}) + "/";
		FileUtils.writeLines(conf, Arrays.asList("sink=file","debug=true","debugdir_win32="+logDir,"debugdir_linux="+logDir));
		try {
			setConfFile(conf.getAbsolutePath());
			refresh();
			assertTrue(trace);
			assertEquals(dumpTo, DUMP_TO_FILE);
		} finally {
			if (conf.exists())
				conf.delete();
		}
	}

	@SuppressWarnings("deprecation")
	private File setUpDebugLog4j2() throws IOException {
		Logger lgr = (Logger) out;

		final LoggerContext ctx = lgr.getContext();
		final Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(lgr.getName()); 
		loggerConfig.setLevel(Level.DEBUG);
		final Layout<?> layout = PatternLayout.createDefaultLayout();
		File logf = File.createTempFile("temp-logfile", ".log");
		Appender appender = FileAppender.createAppender(logf.getAbsolutePath(), "true", "false", "testAppender", "true", "false", "true", "8192", layout, null, "false", null, config);
		appender.start();
		
		lgr.addAppender(appender);
		config.addLoggerAppender(lgr, appender);
		ctx.updateLoggers();
		
		return logf;
	}

	private String chomp(String filePath) {
		return filePath.endsWith(File.separator) ? filePath : filePath + File.separator;
	} // chomp
	
}