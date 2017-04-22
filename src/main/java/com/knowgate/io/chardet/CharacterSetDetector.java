package com.knowgate.io.chardet;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

/**
 * Detect the character encoding of a text file
 * @author Sergio Montoro Ten
 */
public class CharacterSetDetector implements nsICharsetDetectionObserver {

  private boolean bDetectedCharset;

  private String sDetectedCharset;
  
  private nsDetector oDetector;

  public CharacterSetDetector() {
	bDetectedCharset = false;
	sDetectedCharset = null;
	oDetector = new nsDetector(nsPSMDetector.ALL);
  }

  /**
   * Set the encoding to a given value
   * @param charSet Character encoding
   */
  public void Notify(String charSet) {
  	sDetectedCharset = charSet;
    bDetectedCharset = true ;
  }

  /**
   * Detect character encoding of the text data coming from an InputStream
   * @param oInStrm InputStream 
   * @param sDefaultCharset String
   * @throws IOException
   */
  public String detect(InputStream oInStrm, String sDefaultCharset)
  	throws IOException {

	byte[] aBytes = new byte[1024] ;
	int iLen;
	boolean bDone = false ;
	boolean bIsAscii = true ;

	oDetector.Init(this);
	   
	while( (iLen=oInStrm.read(aBytes,0,aBytes.length)) != -1) {

	  // Check if the stream is only ascii.
	  if (bIsAscii) bIsAscii = oDetector.isAscii(aBytes,iLen);

	  // DoIt if non-ascii and not done yet.
		if (!bIsAscii && !bDone) bDone = oDetector.DoIt(aBytes, iLen, false);
	} // wend
	
	oDetector.DataEnd();

	if (bIsAscii) {	   
	   bDetectedCharset = true;
	   sDetectedCharset = "ASCII";
	}

	if (!bDetectedCharset) {
	  if (sDefaultCharset==null)	  	
	    sDetectedCharset = oDetector.getProbableCharsets()[0];  	
	  else
	  	sDetectedCharset = sDefaultCharset;
    } // fi

	return sDetectedCharset;
  } // detect

  /**
   * Detect character encoding of the text data in a File
   * @param oFile File
   * @param sDefaultCharset String
   * @throws IOException
   */
  public String detect(File oFile, String sDefaultCharset)
  	throws IOException {

    FileInputStream oInStrm = new FileInputStream(oFile);
  
    String sRetVal = detect(oInStrm, sDefaultCharset);
    
  	oInStrm.close();
  
    return sRetVal;
  }
  	
  /**
   * Detect character encoding of the text data in a File
   * @param sFile String Full path to file
   * @param sDefaultCharset String
   * @throws IOException
   */
  public String detect(String sFile, String sDefaultCharset)
  	throws IOException {

    return detect(new File(sFile), sDefaultCharset);
  } // detect

}
