package com.knowgate.gis;

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

import java.util.ArrayList;

/**
 * 
 * @author Sergio Montoro Ten
 */
public class AddressComponent {

  private String sShortName;
  private String sLongName;
  private ArrayList<String> aTypes;
  
  public AddressComponent() {
    sShortName = null;
	sLongName = null;
	aTypes = new ArrayList<String>();
  }

  public AddressComponent(String sShort, String sLong, String... vTypes) {
	  sShortName = sShort;
	  sLongName = sLong;
	  aTypes = new ArrayList<String>();
	  for (int t=0; t<vTypes.length; t++)
	    aTypes.add(vTypes[t]);
  }

  public String getShortName() {
    return sShortName;
  }

  public void setShortName(String s) {
    sShortName = s;
  }
  
  public String getLongName() {
	return sLongName;
  }

  public void setLongName(String s) {
	sLongName = s;
  }

  public ArrayList<String> getTypes() {
	return aTypes;
  }

  public String getType(int t) {
	return aTypes.get(t);
  }

  public void addType(String sType) {
	aTypes.add(sType);
  }
  
  public boolean isOfType(String s) {
	return aTypes.contains(s);
  }
}
