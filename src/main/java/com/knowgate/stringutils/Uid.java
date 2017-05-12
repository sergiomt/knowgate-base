package com.knowgate.stringutils;

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

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Random;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Functions for creating universal unique identifiers
 * @author Sergio Montoro Ten
 * @version 1.0
 */
public class Uid {

	  /**
	   * Generate a random identifier of a given length
	   * @param iLength int Length of identifier to be generated /between 1 and 4096 characters)
	   * @param sCharset String Character set to be used for generating the identifier
	   * @param byCategory byte Character category, must be one of Character.UNASSIGNED, Character.UPPERCASE_LETTER or Character.LOWERCASE_LETTER
	   * If sCharset is <b>null</b> then it is "abcdefghjkmnpqrstuvwxyz23456789" by default
	   * @return Identifier of given length composed using the designated character set
	   */
	  public static String generateRandomId(int iLength, String sCharset, byte byCategory )
	  	throws StringIndexOutOfBoundsException {
	    
	    if (iLength<=0) 
	      throw new StringIndexOutOfBoundsException("Uid.generateRandomId() identifier length must be greater than zero");

	    if (iLength>4096) 
	      throw new StringIndexOutOfBoundsException("Uid.generateRandomId() identifier length must be less than or equal to 4096");

	    if (sCharset!=null) {
	      if (sCharset.length()==0) throw new StringIndexOutOfBoundsException("Uid.generateRandomId() character set length must be greater than zero");
	    } else {
	      sCharset = "abcdefghjkmnpqrstuvwxyz23456789";
	    }
	    
		if (byCategory!=Character.UNASSIGNED && byCategory!=Character.UPPERCASE_LETTER && byCategory!=Character.LOWERCASE_LETTER)
		  throw new IllegalArgumentException("Uid.generateRandomId() Character category must be one of {UNASSIGNED, UPPERCASE_LETTER, LOWERCASE_LETTER}");

		int iCsLen = sCharset.length();
	    StringBuilder oId = new StringBuilder(iLength);
	    Random oRnd = new Random();
	    for (int i=0; i<iLength; i++){
		  char c = sCharset.charAt(oRnd.nextInt(iCsLen));
		  if (byCategory==Character.UPPERCASE_LETTER)
		  	c = Character.toUpperCase(c);
		  else if (byCategory==Character.LOWERCASE_LETTER)
		  	c = Character.toLowerCase(c);
		  oId.append(c);
		} // next
		return oId.toString();
	  } // generateRandomId

	/**
	 * Create a universal unique key for a new record
	 * @return String of 32 characters length
	 */
	public static String createUniqueKey() {

		int iRnd;
		long lSeed = System.currentTimeMillis();
		Random oRnd = new Random(lSeed);
		String sHex;
		StringBuffer sUUID = new StringBuffer(32);
		byte[] localIPAddr = new byte[4];

		try {

			// 8 characters Code IP address of this machine
			localIPAddr = InetAddress.getLocalHost().getAddress();

			sUUID.append(byteToStr[((int) localIPAddr[0]) & 255]);
			sUUID.append(byteToStr[((int) localIPAddr[1]) & 255]);
			sUUID.append(byteToStr[((int) localIPAddr[2]) & 255]);
			sUUID.append(byteToStr[((int) localIPAddr[3]) & 255]);
		}
		catch (UnknownHostException e) {
			// Use localhost by default
			sUUID.append("7F000000");
		}

		// Append a seed value based on current system date
		sUUID.append(Long.toHexString(lSeed));

		// 6 characters - an incremental sequence
		sUUID.append(Integer.toHexString(iSequence.incrementAndGet()));

		iSequence.compareAndSet(16777000, 1048576);

		do {
			iRnd = oRnd.nextInt();
			if (iRnd>0) iRnd = -iRnd;
			sHex = Integer.toHexString(iRnd);
		} while (0==iRnd);

		// Finally append a random number
		sUUID.append(sHex);

		return sUUID.substring(0, 32);
	} // createUniqueKey()

	/**
	 * Create a universal unique key which increases over time
	 * @return String of 32 characters length
	 */
	public static String createTimeDependentKey() {
		char pad[];
		int padLen;
		StringBuilder retval = new StringBuilder(32);
		final long seed = System.currentTimeMillis();
		Random rnd = new Random(seed);
		// 10 characters time dependent part
		String timePart = String.valueOf(Long.MAX_VALUE-seed);
		padLen = timePart.length()-10;
		pad = new char[padLen];
		java.util.Arrays.fill(pad, '0');
		retval.append(pad).append(timePart);
		// 6 characters sequential
		retval.append(Integer.toHexString(iSequence.incrementAndGet()));
		iSequence.compareAndSet(16777000, 1048576);
		// 8 characters IP dependent part		
		try {
			byte[] localIPAddr = InetAddress.getLocalHost().getAddress();
			retval.append(byteToStr[((int) localIPAddr[0]) & 255]);
			retval.append(byteToStr[((int) localIPAddr[1]) & 255]);
			retval.append(byteToStr[((int) localIPAddr[2]) & 255]);
			retval.append(byteToStr[((int) localIPAddr[3]) & 255]);
		}
		catch (UnknownHostException e) {
			retval.append("7f000000");
		}
		// 8 characters random part
		String randomPart = String.valueOf(rnd.nextInt(Integer.MAX_VALUE));
		padLen = timePart.length()-8;
		retval.append(randomPart).append(generateRandomId(padLen, "0123456789abcdef", Character.LOWERCASE_LETTER));
		return retval.toString();
	}

	  /**
	   * Generate a reverse timestamp plus random int identifier of 32 characters length
	   * The identifier will be composed of Long.MAX_VALUE-Current Time in Milliseconds followed by a random 32 bits integer
	   * @return Numeric identifier of 32 characters length padded with zeros at the left
	   */
	  public static String generateReverseTimestampId() {
		String sTs = Str.leftPad(String.valueOf(Long.MAX_VALUE-System.currentTimeMillis()),'0',20);
		String sRd = Str.leftPad(String.valueOf(new Random().nextInt(Integer.MAX_VALUE)),'0',10);
		return Str.leftPad(sTs+sRd,'0',32);
	  }
	
	private static AtomicInteger iSequence = new AtomicInteger(1048576);

	private static String[] byteToStr = {
		"00","01","02","03","04","05","06","07","08","09","0a","0b","0c","0d","0e","0f",
		"10","11","12","13","14","15","16","17","18","19","1a","1b","1c","1d","1e","1f",
		"20","21","22","23","24","25","26","27","28","29","2a","2b","2c","2d","2e","2f",
		"30","31","32","33","34","35","36","37","38","39","3a","3b","3c","3d","3e","3f",
		"40","41","42","43","44","45","46","47","48","49","4a","4b","4c","4d","4e","4f",
		"50","51","52","53","54","55","56","57","58","59","5a","5b","5c","5d","5e","5f",
		"60","61","62","63","64","65","66","67","68","69","6a","6b","6c","6d","6e","6f",
		"70","71","72","73","74","75","76","77","78","79","7a","7b","7c","7d","7e","7f",
		"80","81","82","83","84","85","86","87","88","89","8a","8b","8c","8d","8e","8f",
		"90","91","92","93","94","95","96","97","98","99","9a","9b","9c","9d","9e","9f",
		"a0","a1","a2","a3","a4","a5","a6","a7","a8","a9","aa","ab","ac","ad","ae","af",
		"b0","b1","b2","b3","b4","b5","b6","b7","b8","b9","ba","bb","bc","bd","be","bf",
		"c0","c1","c2","c3","c4","c5","c6","c7","c8","c9","ca","cb","cc","cd","ce","cf",
		"d0","d1","d2","d3","d4","d5","d6","d7","d8","d9","da","db","dc","dd","de","df",
		"e0","e1","e2","e3","e4","e5","e6","e7","e8","e9","ea","eb","ec","ed","ee","ef",
		"f0","f1","f2","f3","f4","f5","f6","f7","f8","f9","fa","fb","fc","fd","fe","ff" };
	
}
