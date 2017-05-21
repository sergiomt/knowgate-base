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

public class Html {

	/**
	 * <p>Return text encoded as XHTML.</p>
	 * ASCII-7 characters [0..127] are returned as they are,
	 * any other character is returned as &amp;#<i>code</i>;
	 * Double quotes and angle brackets are also escaped.
	 * @param text String
	 * @return String
	 */
	public static String encode(String text) {

		if (text==null) return null;

		char c;
		int len = text.length();
		StringBuffer results = new StringBuffer(len*2);

		for (int i = 0; i < len; ++i) {
			c = text.charAt(i);
			if (c<=127) {
				if (c=='"')
					results.append("&#34;");
				else if (c=='<')
					results.append("&lt;");
				else if (c=='>')
					results.append("&gt;");
				else
					results.append(c);
			} else {
				results.append("&#"+String.valueOf((int)c)+";");
			}
		}
		return results.toString();
	}	
}
