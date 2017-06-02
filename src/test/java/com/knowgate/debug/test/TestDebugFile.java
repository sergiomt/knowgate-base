package com.knowgate.debug.test;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import com.knowgate.io.FileUtils;

import static com.knowgate.debug.DebugFile.*;

public class TestDebugFile {
	
	@Test
	public void testConfig() throws IOException {
		
		// Default config
		assertFalse(trace);
		assertNull(getFile(-1l));
		assertEquals(dumpTo, DUMP_TO_LOG4J);

		// Config read from a file
		File conf = File.createTempFile("temp-debugfile", ".conf");
		FileUtils.writeLines(conf, Arrays.asList("sink=file","debug=true","debugdir_win32=/var/log/knowgate/","debugdir_linux=/var/log/knowgate/"));
		try {
			setConfFile(conf.getAbsolutePath());
			assertTrue(trace);
			assertEquals(dumpTo, DUMP_TO_FILE);
			assertEquals(chomp(System.getProperty("knowgate.debugdir", "/var/log/knowgate/")+"javatrc.txt"), getFile(-1l)+"javatrc.txt");
			
		} finally {
			if (conf.exists())
				conf.delete();
		}
	}

	private String chomp(String filePath) {
		return filePath.endsWith(File.separator) ? filePath : filePath + File.separator;
	} // chomp
	
}