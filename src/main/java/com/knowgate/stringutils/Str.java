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

public class Str {

	  /**
	   * Ensure that a String ends with a given substring
	   * @param sSource Input String
	   * @param sEndsWith Substring that the String must end with.
	   * @return If sSource ends with sEndsWith then sSource is returned,
	   * else sSource+sEndsWith is returned.
	   */
	  public static String chomp(String sSource, String sEndsWith) {

	    if (null==sSource)
	      return null;
	    else if (sSource.length()==0)
	      return "";
	    else if (sSource.endsWith(sEndsWith))
	      return sSource;
	    else
	      return sSource + sEndsWith;
	  } // chomp
	
	  /**
	   * Ensure that a String does not end with a given character
	   * @param sSource Input String
	   * @param cEndsWith Character that the String must not end with.
	   * @return If sSource does not end with sEndsWith then sSource is returned,
	   * else sSource-cEndsWith is returned.
	   */
	  public static String dechomp(String sSource, char cEndsWith) {

	    if (null==sSource)
	      return null;
	    else if (sSource.length()<1)
	      return sSource;
	    else if (sSource.charAt(sSource.length()-1)==cEndsWith)
	      return sSource.substring(0, sSource.length()-1);
	    else
	      return sSource;
	  } // dechomp


	  /**
	   * Search for a String in an array
	   * @param sStr String sought
	   * @param aSet String[] Strings searched
	   * @return boolean true if any String in set is equalsIgnoreCase to the given String.
	   */
	  public static boolean in (String sStr, String[] aSet) {
		    boolean bRetVal = false;

		    if (aSet!=null) {
		      final int iLen = aSet.length;

		      for (int i=0; i<iLen && !bRetVal; i++)
		        bRetVal = sStr.equalsIgnoreCase(aSet[i]);
		    } // fi

		    return bRetVal;
      }


	  /**
	   * Remove a character set from a String
	   * @param sInput Input String
	   * @param sRemove A String containing all the characters to be removed from input String
	   * @return The input String without all the characters of sRemove
	   */
	  public static String removeChars(String sInput, String sRemove) {
	    if (null==sInput) return null;
	    if (null==sRemove) return sInput;
	    if (sInput.length()==0) return sInput;
	    if (sRemove.length()==0) return sInput;

	    final int iLen = sInput.length();
	    StringBuffer oOutput = new StringBuffer(iLen);

	    for (int i=0; i<iLen; i++) {
	      char c = sInput.charAt(i);
	      if (sRemove.indexOf(c)<0)
	        oOutput.append(c);
	    } // next

	    return oOutput.toString();
	  } // removeChars

	  /**
	   * Get index of a substring inside another string
	   * @param sSource String String to be scanned
	   * @param sSought Substring to be sought
	   * @param iStartAt int Index to start searching from
	   * @return int Start index of substring or -1 if not found
	   */

	  public static int indexOfIgnoreCase(String sSource, String sSought, int iStartAt) {
	    if ((sSource==null) || (sSought==null)) return -1;

	    final int iSrcLen = sSource.length();
	    final int iSghLen = sSought.length();

	    if (iSrcLen<iSghLen) return -1;

	    if (iSrcLen==iSghLen) return (sSource.equalsIgnoreCase(sSought) ? 0 : -1);

	    final int iReducedLen = iSrcLen-iSghLen;

	    if (iStartAt+iSghLen>iSrcLen) return -1;

	    for (int p=iStartAt; p<iReducedLen; p++) {
	      if (sSource.substring(p, p+iSghLen).equalsIgnoreCase(sSought))
	        return p;
	    }
	    return -1;
	  }


	  /**
	   * Get index of a substring inside another string
	   * @param sSource String String to be scanned
	   * @param sSought Substring to be sought
	   * @return int Start index of substring or -1 if not found
	   */
	  public static int indexOfIgnoreCase(String sSource, String sSought) {
	    if ((sSource==null) || (sSought==null)) return -1;

	    final int iSrcLen = sSource.length();
	    final int iSghLen = sSought.length();

	    if (iSrcLen<iSghLen) return -1;

	    if (iSrcLen==iSghLen) return (sSource.equalsIgnoreCase(sSought) ? 0 : -1);

	    final int iReducedLen = iSrcLen-iSghLen;

	    for (int p=0; p<iReducedLen; p++) {
	      if (sSource.substring(p, p+iSghLen).equalsIgnoreCase(sSought))
	        return p;
	    }
	    return -1;
	  } // indexOfIgnoreCase

	  /**
	   * Get substring after a given character sequence
	   * @param sSource Source String
	   * @param iFromIndex Index top start searching character sequence from
	   * @param sSought Character sequence sought
	   * @return Substring after sSought character sequence.
	   * If source string is empty then return value is always an empty string.
	   * If sought substring is empty then return value is the whole source string.
	   * If sought substring is not found then return value is <b>null</b>
	   * @throws StringIndexOutOfBoundsException
	   * @throws NullPointerException is source or sought string is null
	   * @since 4.0
	   */
	  public static String substrAfter(String sSource, int iFromIndex, String sSought)
	  	throws StringIndexOutOfBoundsException,NullPointerException {
	    String sRetVal;
	    
	    if (sSource.length()==0) {
	      sRetVal = "";
	    } else {
	      if (sSought.length()==0) {
	      	sRetVal =  sSource;
	      } else {
	        iFromIndex = sSource.indexOf(sSought, iFromIndex);
	        if (iFromIndex<0) {
	          sRetVal = null;
	        } else {
	  	      if (iFromIndex==sSource.length()-1) {
	  	  	   sRetVal = "";
	  	      } else {
	  	  	    sRetVal = sSource.substring(iFromIndex+sSought.length());
	  	      }
	        }
	      }
	    }
	    return sRetVal;    
	  } // substrAfter

	  	
	  /**
	   * Get substring from an index up to next given character
	   * @param sSource Source String
	   * @param iFromIndex Index top start searching character from
	   * @param cSought Character sought
	   * @return Substring between iFromIndex and cSought character
	   * @throws StringIndexOutOfBoundsException if cSought character is not found at sSource
	   * @since 4.0
	   */
	  public static String substrUpTo(String sSource, int iFromIndex, char cSought)
	  	throws StringIndexOutOfBoundsException {
	  	String sRetVal;
	  	if (null==sSource) {
	  		sRetVal=null;  	
	  	} else {
	      int iToIndex = sSource.indexOf(cSought, iFromIndex);
		  if (iToIndex<0) throw new StringIndexOutOfBoundsException ("Gadgets.substrUpTo() character "+cSought+" not found");
		  if (iFromIndex==iToIndex)
		    sRetVal = "";
		  else
		    sRetVal = sSource.substring(iFromIndex, iToIndex);
	  	}
	  	return sRetVal;
	  } // substrUpTo


	  /**
	   * Get substring from an index up to next given character sequence
	   * @param sSource Source String
	   * @param iFromIndex Index top start searching character from
	   * @param sSought Character sequence sought
	   * @return Substring between iFromIndex and sSought
	   * @throws StringIndexOutOfBoundsException if sSought sequence is not found at sSource
	   * @since 4.0
	   */
	  public static String substrUpTo(String sSource, int iFromIndex, String sSought)
	  	throws StringIndexOutOfBoundsException {
	  	String sRetVal;
	  	if (null==sSource) {
	  		sRetVal=null;  	
	  	} else {
	      int iToIndex = sSource.indexOf(sSought, iFromIndex);
		  if (iToIndex<0) throw new StringIndexOutOfBoundsException ("Gadgets.substrUpTo() character "+sSought+" not found");
		  if (iFromIndex==iToIndex)
		    sRetVal = "";
		  else
		    sRetVal = sSource.substring(iFromIndex, iToIndex);
	  	}
	  	return sRetVal;
	  } // substrUpTo

	  
	  /**
	   * Get substring between two given character sequence
	   * @param sSource Source String
	   * @param sLowerBound Lower bound character sequence
	   * @param sUpperBound Upper bound character sequence
	   * @return Substring between sLowerBound and sUpperBound or <b>null</b> if
	   * either sLowerBound or sUpperBound are not found at sSource
	   * @since 4.0
	   */
	  public static String substrBetween(String sSource, String sLowerBound, String sUpperBound)
	    throws StringIndexOutOfBoundsException,NullPointerException {
	    String sRetVal = substrAfter(sSource, 0, sLowerBound);
	    if (null!=sRetVal) {
	      if (sRetVal.indexOf(sUpperBound)>=0) {
	        sRetVal = substrUpTo(sRetVal,0,sUpperBound);
	      } else {
	      	sRetVal = null;
	      } // fi
	    } // fi
	    return sRetVal;
	  } // substrBetween

	  /**
	   * Get the leftmost characters of a String
	   * @param sSource Source String
	   * @param cChars Number of characters
	   * @throws ArrayIndexOutOfBoundsException If nChars<0
	   */
	  public static String left(String sSource, int nChars) throws ArrayIndexOutOfBoundsException {
	  	if (sSource==null) return null;
	  	if (nChars<0) throw new ArrayIndexOutOfBoundsException("Str.left() invalid maximum character length "+String.valueOf(nChars)) ;
	  	if (sSource.length()<=nChars)
	  		return sSource;
	  	else
	  		return sSource.substring(0, nChars);
	  }

	  /**
	   * Add padding characters to the left.
	   * @param sSource Input String
	   * @param cPad Padding character
	   * @param nChars Final length of the padded string
	   * @return Padded String
	   */
	  public static String leftPad(String sSource, char cPad, int nChars) {
	      if (null==sSource) return null;

	      int iPadLen = nChars - sSource.length();

	      if (iPadLen<=0) return sSource;

	      char aPad[] = new char[iPadLen];

	      java.util.Arrays.fill(aPad, cPad);

	      return new String(aPad) + sSource;
	  } // leftPad

	  // ----------------------------------------------------------

	  /**
	   * <p>Calculate Levenshtein distance between two strings</p>
	   * The Levenshtein distance is defined as the minimal number of characters
	   * you have to replace, insert or delete to transform str1 into str2.
	   * The complexity of the algorithm is O(m*n),
	   * where n and m are the length of str1 and str2.
	   * @return Levenshtein distance between str1 and str2
	   * @throws IllegalArgumentException if either str1 or str2 is <b>null</b>
	   * @author Michael Gilleland & Chas Emerick
	   * @see <a href="http://www.merriampark.com/ldjava.htm">http://www.merriampark.com/ldjava.htm</a>
	   * @since 4.0
	   */

	  public static int levenshteinDistance (String s, String t) {
	    if (s == null || t == null) {
	      throw new IllegalArgumentException("Strings must not be null");
	    }
	  		
	    int n = s.length(); // length of s
	    int m = t.length(); // length of t
	  		
	    if (n == 0) {
	      return m;
	    } else if (m == 0) {
	      return n;
	    }
	  
	    int p[] = new int[n+1]; //'previous' cost array, horizontally
	    int d[] = new int[n+1]; // cost array, horizontally
	    int _d[]; //placeholder to assist in swapping p and d
	  
	    // indexes into strings s and t
	    int i; // iterates through s
	    int j; // iterates through t
	  
	    char t_j; // jth character of t
	  
	    int cost; // cost
	  
	    for (i = 0; i<=n; i++) {
	       p[i] = i;
	    }

	    for (j = 1; j<=m; j++) {
	       t_j = t.charAt(j-1);
	       d[0] = j;

	       for (i=1; i<=n; i++) {
	          cost = s.charAt(i-1)==t_j ? 0 : 1;
	          // minimum of cell to the left+1, to the top+1, diagonally left and up +cost				
	          d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);  
	       }
	  
	       // copy current distance counts to 'previous row' distance counts
	       _d = p;
	       p = d;
	       d = _d;
	    } 

	    // our last action in the above loop was to switch d and p, so p now 
	    // actually has the most recent cost counts
	    return p[n];
	  } // getLevenshteinDistance
	  
}
