package com.knowgate.ldap;

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
 * LDAP Exception Wrapper
 * @author Sergio Montoro Ten
 * @version 1.0
 */
public class LDAPException extends Exception {

  private static final long serialVersionUID = 1l;

  public LDAPException(String sMessage) {
    super(sMessage);
  }

  public LDAPException(String sMessage, Throwable oCause) {
    super(sMessage, oCause);
  }
}