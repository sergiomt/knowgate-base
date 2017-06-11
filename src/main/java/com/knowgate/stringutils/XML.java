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

/**
 * XML Helper methods
 * @author Sergio Montoro Ten
 */
public class XML {

	/**
	 * <p>Convert an input text into one or more CDATA sections</p>
	 * If input contains "]]&gt;" substring then the returned value will contain more than one CDATA
	 * else the returned value will be &lt;![CDATA[<i>input</i>]]&gt;
	 * @param input String
	 * @return String
	 */
	public static String toCData(final String input) {
		if (null==input)
			return null;
		
		StringBuilder output = new StringBuilder(input.length()+48);
		int doubleSquareBrackets = input.indexOf("]]>");
		
		if (doubleSquareBrackets<0)
			return output.append("<![CDATA[").append(input).append("]]>").toString();

		int beginIndex = 0;
		while (doubleSquareBrackets>=0) {
			if (output.length()==0)
				output.append("<![CDATA[");
			else
				output.append("<![CDATA[>");
			output.append(input.substring(beginIndex, doubleSquareBrackets));
			doubleSquareBrackets += 3;
			beginIndex = doubleSquareBrackets;
			doubleSquareBrackets = input.indexOf("]]>", beginIndex);
			output.append("]]]]>");
			if (doubleSquareBrackets<0) {
				output.append("<![CDATA[>").append(input.substring(beginIndex)).append("]]>");
			}
		}
		
		return output.toString();
		
	}
	
}
