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

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {
	
	public static Logger out = LogManager.getLogger(DebugFile.class);

	/**
	 * Get the stack trace of a throwable  
	 * @param Throwable 
	 * @return String
	 */
	public static String stackTrace( Throwable aThrowable ) {
		  String sRetVal = null;
		  try {
			  sRetVal = StackTraceUtil.getStackTrace(aThrowable);
		  } catch (IOException e) { }
		  return sRetVal;
	  }	  
}
