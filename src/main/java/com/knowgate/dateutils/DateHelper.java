package com.knowgate.dateutils;

import java.sql.Timestamp;

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
import java.util.GregorianCalendar;

public class DateHelper {

	/**
	 * Try to convert the given object to a java.util.Date
	 * @param dt Object of class java.util.Date, java.sql.Date, java.util.Calendar, java.sql.Timestamp or String The recognized string formats are: "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "dd-MM-yyyy" and "yyyyMMdd"
	 * @return Date
	 * @throws ClassCastException if the conversion cannot be performed
	 */
	@SuppressWarnings("deprecation")
	public static Date toDate(Object dt) throws ClassCastException {
		if (null == dt)
			return null;
		else if (dt instanceof Date)
			return (Date) dt;
		else if (dt instanceof java.sql.Timestamp)
			return new Date(((java.sql.Timestamp) dt).getTime());
		else if (dt instanceof java.sql.Date)
			return new Date(((java.sql.Date) dt).getYear(), ((java.sql.Date) dt).getMonth(),
					((java.sql.Date) dt).getDate());
		else if (dt instanceof Calendar)
			return new Date(((Calendar) dt).getTimeInMillis());
		else if (dt instanceof String) {
			String st = (String) dt;
			if (st.length() == 19) {
				try {
					return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(st);
				} catch (java.text.ParseException pe) {
					throw new ClassCastException("Cannot parse date " + st);
				}
			} else if (st.length() == 10) {
				String st2 = st.replace('/', '-');
				String[] d = st2.split("-");
				try {
					if (d[0].length() == 4)
						return new SimpleDateFormat("yyyy-MM-dd").parse(st2);
					else
						return new SimpleDateFormat("dd-MM-yyyy").parse(st2);
				} catch (java.text.ParseException pe) {
					throw new ClassCastException("Cannot parse date " + st);
				}
			} else if (st.length() == 8) {
				try {
					return new SimpleDateFormat("yyyyMMdd").parse(st);
				} catch (java.text.ParseException pe) {
					throw new ClassCastException("Cannot parse date " + st);
				}
			} else
				throw new ClassCastException("Cannot parse date " + st);
		} else
			throw new ClassCastException("Cannot cast from class " + dt.getClass().getName() + " to java.util.Date");
	}

	/**
	 * Try to convert the given object to a java.util.Calendar
	 * @param dt Object of class java.util.Date, java.sql.Date, java.util.Calendar, java.sql.Timestamp or String
	 * The recognized string formats are: "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "dd-MM-yyyy" and "yyyyMMdd"
	 * @return Calendar
	 * @throws ClassCastException if the conversion cannot be performed
	 */
	public static Calendar toCalendar(Object dt) throws ClassCastException {
		Calendar retval;
		if (null == dt) {
			retval = null;
		} else if (dt instanceof Calendar) {
			retval = (Calendar) dt;
		} else {
			retval = new GregorianCalendar();
			if (dt instanceof java.sql.Timestamp)
				retval.setTimeInMillis(((java.sql.Timestamp) dt).getTime());
			else if (dt instanceof java.sql.Date)
				retval.setTimeInMillis(((java.sql.Date) dt).getTime());
			else if (dt instanceof java.util.Date)
				retval.setTimeInMillis(((java.util.Date) dt).getTime());
			else if (dt instanceof String) {
				String st = (String) dt;
				if (st.length() == 19) {
					try {
						retval.setTimeInMillis(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(st).getTime());
					} catch (java.text.ParseException pe) {
						throw new ClassCastException("Cannot parse date " + st);
					}
				} else if (st.length() == 10) {
					String st2 = st.replace('/', '-');
					String[] d = st2.split("-");
					try {
						if (d[0].length() == 4)
							retval.setTimeInMillis(new SimpleDateFormat("yyyy-MM-dd").parse(st2).getTime());
						else
							retval.setTimeInMillis(new SimpleDateFormat("dd-MM-yyyy").parse(st2).getTime());
					} catch (java.text.ParseException pe) {
						throw new ClassCastException("Cannot parse date " + st);
					}
				} else if (st.length() == 8) {
					try {
						retval.setTimeInMillis(new SimpleDateFormat("yyyyMMdd").parse(st).getTime());
					} catch (java.text.ParseException pe) {
						throw new ClassCastException("Cannot parse date " + st);
					}
				} else
					throw new ClassCastException("Cannot parse date " + st);
			} else {
				throw new ClassCastException("Cannot cast from class " + dt.getClass().getName() + " to java.util.Calendar");
			}
		}
		return retval;
	}

	/**
	 * Try to convert the given object to a java.sql.Timestamp
	 * @param dt Object of class java.util.Date, java.sql.Date, java.util.Calendar, java.sql.Timestamp or String
	 * The recognized string formats are: "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "dd-MM-yyyy" and "yyyyMMdd"
	 * @return Calendar
	 * @throws ClassCastException if the conversion cannot be performed
	 */
	public static Timestamp toTimestamp(Object dt) throws ClassCastException {
		Timestamp retval;
		if (null == dt) {
			retval = null;
		} else if (dt instanceof Timestamp) {
			retval = (Timestamp) dt;
		} else {
			if (dt instanceof Calendar)
				retval = new Timestamp(((Calendar) dt).getTimeInMillis());
			else if (dt instanceof java.sql.Date)
				retval = new Timestamp(((java.sql.Date) dt).getTime());
			else if (dt instanceof java.util.Date)
				retval = new Timestamp(((java.util.Date) dt).getTime());
			else if (dt instanceof String) {
				String st = (String) dt;
				if (st.length() == 19) {
					try {
						retval = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(st).getTime());
					} catch (java.text.ParseException pe) {
						throw new ClassCastException("Cannot parse date " + st);
					}
				} else if (st.length() == 10) {
					String st2 = st.replace('/', '-');
					String[] d = st2.split("-");
					try {
						if (d[0].length() == 4)
							retval = new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse(st2).getTime());
						else
							retval = new Timestamp(new SimpleDateFormat("dd-MM-yyyy").parse(st2).getTime());
					} catch (java.text.ParseException pe) {
						throw new ClassCastException("Cannot parse date " + st);
					}
				} else if (st.length() == 8) {
					try {
						retval = new Timestamp(new SimpleDateFormat("yyyyMMdd").parse(st).getTime());
					} catch (java.text.ParseException pe) {
						throw new ClassCastException("Cannot parse date " + st);
					}
				} else
					throw new ClassCastException("Cannot parse date " + st);
			} else {
				throw new ClassCastException("Cannot cast from class " + dt.getClass().getName() + " to java.util.Calendar");
			}
		}
		return retval;
	}

	/**
	 * Try to convert the given object to a java.sql.Date
	 * @param dt Object of class java.util.Date, java.sql.Date, java.util.Calendar, java.sql.Timestamp or String
	 * The recognized string formats are: "yyyy-MM-dd", "dd-MM-yyyy" and "yyyyMMdd"
	 * @return Date
	 * @throws ClassCastException if the conversion cannot be performed
	 */
	public static java.sql.Date toSQLDate(Object dt) throws ClassCastException {
		java.sql.Date retval;
		if (null == dt) {
			retval = null;
		} else if (dt instanceof java.sql.Date) {
			retval = (java.sql.Date) dt;
		} else {
			if (dt instanceof Calendar)
				retval = new java.sql.Date(((Calendar) dt).getTimeInMillis());
			else if (dt instanceof java.sql.Timestamp)
				retval = new java.sql.Date(((java.sql.Timestamp) dt).getTime());
			else if (dt instanceof java.util.Date)
				retval = new java.sql.Date(((java.util.Date) dt).getTime());
			else if (dt instanceof String) {
				String st = (String) dt;
				if (st.length() == 10) {
					String st2 = st.replace('/', '-');
					String[] d = st2.split("-");
					try {
						if (d[0].length() == 4)
							retval = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(st2).getTime());
						else
							retval = new java.sql.Date(new SimpleDateFormat("dd-MM-yyyy").parse(st2).getTime());
					} catch (java.text.ParseException pe) {
						throw new ClassCastException("Cannot parse date " + st);
					}
				} else if (st.length() == 8) {
					try {
						retval = new java.sql.Date(new SimpleDateFormat("yyyyMMdd").parse(st).getTime());
					} catch (java.text.ParseException pe) {
						throw new ClassCastException("Cannot parse date " + st);
					}
				} else
					throw new ClassCastException("Cannot parse date " + st);
			} else {
				throw new ClassCastException("Cannot cast from class " + dt.getClass().getName() + " to java.util.Calendar");
			}
		}
		return retval;
	}
}
