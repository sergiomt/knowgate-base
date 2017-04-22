package com.knowgate.encryption;

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

import java.util.HashMap;

public enum AuthenticationOutcome {

	OK(0),
	USER_NOT_FOUND(-1),
	INVALID_PASSWORD(-2),
	ACCOUNT_DEACTIVATED(-3),
	SESSION_EXPIRED(-4),
	DOMAIN_NOT_FOUND(-5),
	WORKAREA_NOT_FOUND(-6),
	WORKAREA_NOT_SET(-7),
	ACCOUNT_CANCELLED(-8),
	PASSWORD_EXPIRED(-9),
	CAPTCHA_MISMATCH(-10),
	CAPTCHA_TIMEOUT(-11),
	WORKAREA_ACCESS_DENIED(-12),
	ACCOUNT_UNCONFIRMED(-13),
	INVALID_KEY(-14),
	INTERNAL_ERROR(-255);

	private final int iCode;

	private static final HashMap<Integer,AuthenticationOutcome> intToRdbmsMap = new HashMap<Integer,AuthenticationOutcome>(23);
	
	static {
	  for (AuthenticationOutcome ao : AuthenticationOutcome.values())
	    intToRdbmsMap.put(ao.intValue(), ao);	    
	}
	
	AuthenticationOutcome (int iCode) {
	  this.iCode = iCode;
	}

	public final int intValue() {
		return iCode;
	}

	public static AuthenticationOutcome valueOf(final int i) throws IllegalArgumentException {
		AuthenticationOutcome ao = intToRdbmsMap.get(i);
		if (ao==null)
			throw new IllegalArgumentException("Unrecognized AuthenticationOutcome code "+String.valueOf(i));
		return ao;
	}
	
}
