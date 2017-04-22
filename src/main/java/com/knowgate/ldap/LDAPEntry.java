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

import java.util.HashMap;

public class LDAPEntry extends HashMap<String,Object> {
  
  private static final long serialVersionUID = 1L;

  public String getDN() {
    return (String) get("dn");
  }

}
