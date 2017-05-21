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

import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * Simple utility to return the stack trace of an exception as a String.
 * @author John O'Hanley
 * @version 1.0
*/
public final class StackTraceUtil {

  /**
   * Get the stack trace of a throwable  
   * @param aThrowable Throwable 
   * @return String
   * @throws IOException
  */	
  public static String getStackTrace( Throwable aThrowable )
    throws IOException {
    final Writer result = new StringWriter();
    final PrintWriter printWriter = new PrintWriter( result );
    aThrowable.printStackTrace( printWriter );
    String sRetVal = result.toString();
    printWriter.close();
    result.close();
    return sRetVal;
  }
}
