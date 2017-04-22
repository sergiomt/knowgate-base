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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;

/**
 * Write to file using BufferedOutputStream
 * @author Sergio Montoro Ten
 * @version 1.0
 */
public class BufferedFileWriter {
  private FileOutputStream fos;
  private BufferedOutputStream bos;
  
  public BufferedFileWriter() {
  	fos = null;
  	bos = null;
  }

  /**
   * Open file for write.
   * @param sFilePath String File path
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void open (String sFilePath) throws FileNotFoundException, IOException {
  	fos = new FileOutputStream(sFilePath);
  	bos = new BufferedOutputStream(fos);
  }

  /**
   * Close file output stream  
   * @throws IOException
   */
  public void close () throws IOException {
  	bos.close();
  	fos.close();
  }

  /**
   * Append line to file  
   * @param lin String
   * @throws IOException
   */
  public void writeln(String lin) throws IOException  {
  	final String nl = "\n";
  	bos.write(lin.getBytes());
  	bos.write(nl.getBytes());
  }

}
