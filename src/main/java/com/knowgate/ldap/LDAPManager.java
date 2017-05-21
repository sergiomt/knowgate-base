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

import java.util.List;
import java.util.Map;

/**
 * LDAP Manager Interface
 * @author Sergio Montoro Ten
 * @version 2.0
 */

public interface LDAPManager extends AutoCloseable {

  /**
   * <p>Connect to LDAP Service</p>
   * At this point, there is no authentication, and any operations are conducted as an anonymous client.
   * @param sConnStr ldap://<i>host</i>:port/<i>distinguished_name</i><br><b>Example</b> "ldap://fobos.kg.int:389/dc=knowgate,dc=org"
   * @throws LDAPException
   */

  void connect (String sConnStr) throws LDAPException;

  /**
   * <P>Connect to LDAP Server using a Properties object</P>
   * @param oProps Map&lt;String,String&gt; for connecting to LDAP server.<BR>
   * For example :<BR>
   * ldapconnect=ldap://fobos.kg.int:389/dc=knowgate,dc=org<BR>
   * ldapuser=cn=Manager,dc=knowgate,dc=org<BR>
   * ldappassword=manager<BR>
   * @throws LDAPException
   */

  void connectAndBind (Map<String,String> oProps) throws LDAPException;

  /**
   * <p>Synchronously authenticates to the LDAP server using LDAP_V3.</p>
   * If the object has been disconnected from an LDAP server, this method attempts to reconnect to the server. If the object has already authenticated, the old authentication is discarded.
   * @param sUser If non-null and non-empty, specifies that the connection and all operations through it should be authenticated with dn as the distinguished name.
   * @param sPass If non-null and non-empty, specifies that the connection and all operations through it should be authenticated with dn as the distinguished name and passwd as password.
   * @throws LDAPException
   * @throws IllegalStateException If not connected to LDAP
   */

  void bind (String sUser, String sPass) throws LDAPException;

  /**
   * <p>Synchronously disconnects from the LDAP server</p>
   * The disconnect method abandons any outstanding requests, issues an unbind request to the server, and then closes the socket.
   * @throws LDAPException
   */

  void close() throws LDAPException;

  // ---------------------------------------------------------------------------

  void add(String sDN, Map<String,Object> mAttributes) throws LDAPException;

  // ---------------------------------------------------------------------------
  
  LDAPEntry read (String sDN, String[] aAttrs) throws LDAPException;
  
  // ---------------------------------------------------------------------------
  
  List<String> search (String sSearchString, int iLimit) throws LDAPException;
  
  // ---------------------------------------------------------------------------
  
  /**
   * <p>Check whether or not an LDAP entry exists</p>
   * The directory is searched from the connection string key.<br>
   * For example if ldapconnect connection property is ldap://192.168.1.1:389/dc=auth,dc=com
   * then only entries under "dc=auth,dc=com" will be searched
   * @param sSearchString String LDAP search string, for example "(&amp;(ou=Users)(cn=johnp))"
   * @return String DN of firt entry found match the search criteria or null if no entry was found matching the criteria
   * @throws LDAPException
   */
  String exists (String sSearchString) throws LDAPException;

  // ---------------------------------------------------------------------------
  
  /**
   * Delete an LDAP entry
   * @param sDN String
   * @throws LDAPException
   * @throws IllegalStateException If not connected to LDAP
   */

  void delete(String sDN) throws LDAPException;
  
  // ---------------------------------------------------------------------------

  String getPartitionName();

  // ---------------------------------------------------------------------------

  void setPartitionName(String sName);

}