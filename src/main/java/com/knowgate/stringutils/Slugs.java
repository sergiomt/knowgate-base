package com.knowgate.stringutils;

import java.text.Normalizer;

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

public class Slugs {

	  /**
	   * <p>Normalize string and remove non spacing marks</p>
	   * @param sStrIn String
	   * @return String
	   */
	  public static String transliterate (String sStrIn) {
		  String x = Normalizer.normalize(sStrIn, Normalizer.Form.NFD);

		  StringBuilder sb = new StringBuilder(sStrIn.length());
		  for (char c : x.toCharArray())
			  if (Character.getType(c) != Character.NON_SPACING_MARK)
		            sb.append(c);

		  return sb.toString();
	  }
	  
	  /**
	   * Restrict to an ASCII-7 String
	   * @param sStrIn String
	   * @return String
	   */
	  public static String slugify (String sStrIn) {
	    if (sStrIn==null) return null;
	    int iLen = sStrIn.length();
	    if (iLen==0) return sStrIn;
	    StringBuilder sStrBuff = new StringBuilder(iLen);
	    String sStr = sStrIn.toUpperCase();

	    for (int c=0; c<iLen; c++) {
	      switch (sStr.charAt(c)) {
	        case 'Á':
	        case 'À':
	        case 'Ä':
	        case 'Â':
	        case 'Å':
	        case 'Ã':
	          sStrBuff.append('A');
	          break;
	        case 'É':
	        case 'È':
	        case 'Ë':
	        case 'Ê':
	          sStrBuff.append('E');
	          break;
	        case 'Í':
	        case 'Ì':
	        case 'Ï':
	        case 'Î':
	          sStrBuff.append('I');
	          break;
	        case 'Ó':
	        case 'Ò':
	        case 'Ö':
	        case 'Ô':
	        case 'Ø':
	          sStrBuff.append('O');
	          break;
	        case 'Ú':
	        case 'Ù':
	        case 'Ü':
	        case 'Û':
	          sStrBuff.append('U');
	          break;
	        case 'Æ':
	          sStrBuff.append('E');
	          break;
	        case 'Ñ':
	          sStrBuff.append('N');
	          break;
	        case 'Ç':
	          sStrBuff.append('C');
	          break;
	        case '°':
	          sStrBuff.append('o');
	          break;
	        case 'ª':
	          sStrBuff.append('a');
	          break;
	        case '\\':
	        case '.':
	        case '/':
	          sStrBuff.append('_');
	          break;
	        case '&':
	          sStrBuff.append('A');
	          break;
	        case ':':
	          sStrBuff.append(';');
	          break;
	        case '<':
	          sStrBuff.append('L');
	          break;
	        case '>':
	          sStrBuff.append('G');
	          break;
	        case '"':
	          sStrBuff.append((char)39);
	          break;
	        case '|':
	          sStrBuff.append('P');
	          break;
	        case '¡':
	          sStrBuff.append('E');
	          break;
	        case '¿':
	        case '?':
	          sStrBuff.append('Q');
	          break;
	        case '*':
	          sStrBuff.append('W');
	          break;
	        case '%':
	          sStrBuff.append('P');
	          break;
	        case 'ß':
	          sStrBuff.append('B');
	          break;
	        case '¥':
	          sStrBuff.append('Y');
	          break;
	        case (char)255:
	          sStrBuff.append('_');
	          break;
	        default:
	          sStrBuff.append(sStr.charAt(c));
	      } // end switch
	    } // next ()
	    return sStrBuff.toString();
	  } // slugify

	  // ----------------------------------------------------------

	  /**
	   * Return text enconded as an URL.
	   * For example, "Tom's Bookmarks" is encodes as "Tom%27s%20Bookmarks"
	   * @param sStr Text to encode
	   * @return URL-encoded text
	   */
	  public static String urlEncode (String sStr) {
	    if (sStr==null) return null;
	    int iLen = sStr.length();
	    StringBuffer sEscaped = new StringBuffer(iLen+100);
	    char c;
	    for (int p=0; p<iLen; p++) {
	      c = sStr.charAt(p);
	      switch (c) {
	        case ' ':
	          sEscaped.append("%20");
	          break;
	        case '/':
	          sEscaped.append("%2F");
	          break;
	        case '"':
	          sEscaped.append("%22");
	          break;
	        case '#':
	          sEscaped.append("%23");
	          break;
	        case '%':
	          sEscaped.append("%25");
	          break;
	        case '&':
	          sEscaped.append("%26");
	          break;
	        case (char)39:
	          sEscaped.append("%27");
	          break;
	        case '+':
	          sEscaped.append("%2B");
	          break;
	        case ',':
	          sEscaped.append("%2C");
	          break;
	        case '=':
	          sEscaped.append("%3D");
	          break;
	        case '?':
	          sEscaped.append("%3F");
	          break;
	        case 'á':
	          sEscaped.append("%E1");
	          break;
	        case 'é':
	          sEscaped.append("%E9");
	          break;
	        case 'í':
	          sEscaped.append("%ED");
	          break;
	        case 'ó':
	          sEscaped.append("%F3");
	          break;
	        case 'ú':
	          sEscaped.append("%FA");
	          break;
	        case 'Á':
	          sEscaped.append("%C1");
	          break;
	        case 'É':
	          sEscaped.append("%C9");
	          break;
	        case 'Í':
	          sEscaped.append("%CD");
	          break;
	        case 'Ó':
	          sEscaped.append("%D3");
	          break;
	        case 'Ú':
	          sEscaped.append("%DA");
	          break;
	        case 'à':
	          sEscaped.append("%E0");
	          break;
	        case 'è':
	          sEscaped.append("%E8");
	          break;
	        case 'ì':
	          sEscaped.append("%EC");
	          break;
	        case 'ò':
	          sEscaped.append("%F2");
	          break;
	        case 'ù':
	          sEscaped.append("%F9");
	          break;
	        case 'À':
	          sEscaped.append("%C0");
	          break;
	        case 'È':
	          sEscaped.append("%C8");
	          break;
	        case 'Ì':
	          sEscaped.append("%CC");
	          break;
	        case 'Ò':
	          sEscaped.append("%D2");
	          break;
	        case 'Ù':
	          sEscaped.append("%D9");
	          break;
	        case 'ñ':
	          sEscaped.append("%F1");
	          break;
	        case 'Ñ':
	          sEscaped.append("%D1");
	          break;
	        case 'ç':
	          sEscaped.append("%E7");
	          break;
	        case 'Ç':
	          sEscaped.append("%C7");
	          break;
	        case 'ô':
	          sEscaped.append("%F4");
	          break;
	        case 'Ô':
	          sEscaped.append("%D4");
	          break;
	        case 'ö':
	          sEscaped.append("%F6");
	          break;
	        case 'Ö':
	          sEscaped.append("%D6");
	          break;
	        case '`':
	          sEscaped.append("%60");
	          break;
	        case '¨':
	          sEscaped.append("%A8");
	          break;
	        default:
	          sEscaped.append(c);
	          break;
	      }
	    } // next

	    return sEscaped.toString();
	  } // urlEncode
	  
}
