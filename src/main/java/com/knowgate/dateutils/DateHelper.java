package com.knowgate.dateutils;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

	@SuppressWarnings("deprecation")
	/**
	 * Try to convert the given object to a java.util.Date
	 * @param dt Object of class java.util.Date, java.sql.Date, java.util.Calendar, java.sql.Timestamp or String
	 * The recognized string formats are: "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "dd-MM-yyyy" and "yyyyMMdd"
	 * @return Date
	 * @throws ClassCastException if the conversion cannot be performed
	 */
	public static Date toDate(Object dt) throws ClassCastException {
		if (null==dt)
			return null;
		else if (dt instanceof Date)
			return (Date) dt;
		else if (dt instanceof java.sql.Timestamp)
			return new Date(((java.sql.Timestamp) dt).getTime());
		else if (dt instanceof java.sql.Date)
			return new Date(((java.sql.Date) dt).getYear(), ((java.sql.Date) dt).getMonth(), ((java.sql.Date) dt).getDate());
		else if (dt instanceof Calendar)
			return new Date(((Calendar) dt).getTimeInMillis());
		else if (dt instanceof String) {
			String st = (String) dt;
			if (st.length()==19) {
				try {
					return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(st);    	  
				} catch (java.text.ParseException pe) {
					throw new ClassCastException("Cannot parse date " + st);
				}				
			} else if (st.length()==10) {
				String st2 = st.replace('/', '-');
				String[] d = st2.split("-");
				try {
					if (d[0].length()==4)
						return new SimpleDateFormat("yyyy-MM-dd").parse(st2);
					else
						return new SimpleDateFormat("dd-MM-yyyy").parse(st2);
				} catch (java.text.ParseException pe) {
					throw new ClassCastException("Cannot parse date " + st);
				}								
			} else if (st.length()==8) {
				try {
					return new SimpleDateFormat("yyyyMMdd").parse(st);    	  
				} catch (java.text.ParseException pe) {
					throw new ClassCastException("Cannot parse date " + st);
				}								
			} else
				throw new ClassCastException("Cannot parse date " + st);				
		}
		else
			throw new ClassCastException("Cannot cast from class "+dt.getClass().getName()+" to java.util.Date");
	}
}
