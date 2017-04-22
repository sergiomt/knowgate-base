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

public class StreetAddress {

  private ArrayList<AddressComponent> aComponents;
  private LatLong oLatLong;
  private String sDisplayAddress;
  
  public StreetAddress() {
    aComponents = new ArrayList<AddressComponent>();
	oLatLong = new LatLong();
	sDisplayAddress = null;
  }

  public void addComponent(String sShortName, String sLongName, String... aTypes) {
	aComponents.add(new AddressComponent(sShortName, sLongName, aTypes));
  }

  public void addComponent(AddressComponent oAddrComp) {
	aComponents.add(oAddrComp);
  }
  
  public ArrayList<AddressComponent> getComponents() {
    return aComponents;
  }

  public String getStreetNumber() {
    String sStreetNumber = "";
    for (AddressComponent c : aComponents) {
      if (c.isOfType("street_number")) {
    	sStreetNumber = c.getLongName();
    	break;
      }
    }
    return sStreetNumber;
  }

  public void setStreetNumber(String s) {
    for (AddressComponent c : aComponents) {
	  if (c.isOfType("street_number")) {
	    c.setLongName(s);
	    c.setShortName(s);
	    return;
	  }
	}
    addComponent(s, s, "street_number");
  }
  
  public String getStreetName() {
	String sStreetName = "";
	for (AddressComponent c : aComponents) {
	  if (c.isOfType("route")) {
	    sStreetName = c.getLongName();
	    break;
	  }
	}
	return sStreetName;
  }

  public void setStreetName(String s) {
	  for (AddressComponent c : aComponents) {
	    if (c.isOfType("route")) {
	      c.setLongName(s);
		    c.setShortName(s);
		  return;
	    }
	  }
	  addComponent(s, s, "route");
  }
  
  public String getCity() {
	  String sCity = "";
	  for (AddressComponent c : aComponents) {
	    if (c.isOfType("locality")) {
		    sCity = c.getLongName();
	      break;
	    }
	  }
	  return sCity;
  }

  public void setCity(String s) {
	  for (AddressComponent c : aComponents) {
	    if (c.isOfType("locality")) {
	      c.setLongName(s);
		    c.setShortName(s);
		  return;
	    }
	  }
	  addComponent(s, s, "locality");
  }

  public String getState() {
	String sState = "";
	for (AddressComponent c : aComponents) {
	  if (c.isOfType("administrative_area_level_2")) {
		  sState = c.getLongName();
	    break;
	  }
	}
	return sState;
  }

  public void setState(String s) {
	  for (AddressComponent c : aComponents) {
	    if (c.isOfType("administrative_area_level_2")) {
	      c.setLongName(s);
		    c.setShortName(s);
		  return;
	    }
	  }
	  addComponent(s, s, "administrative_area_level_2");
  }

  public String getRegion() {
	String sRegion = "";
	for (AddressComponent c : aComponents) {
	  if (c.isOfType("administrative_area_level_1")) {
	    sRegion = c.getLongName();
	    break;
	  }
	}
	return sRegion;
  }

  public void setRegion(String s) {
	  for (AddressComponent c : aComponents) {
	    if (c.isOfType("administrative_area_level_1")) {
	      c.setLongName(s);
		    c.setShortName(s);
		  return;
	    }
	  }
	  addComponent(s, s, "administrative_area_level_1");
  }

  public String getCountryName() {
	String sCountryName = "";
	for (AddressComponent c : aComponents) {
	  if (c.isOfType("country")) {
	    sCountryName = c.getLongName();
	    break;
	  }
	}
	return sCountryName;
  }

  public void setCountryName(String s) {
	  for (AddressComponent c : aComponents) {
	    if (c.isOfType("country")) {
	      c.setLongName(s);
		  return;
	    }
	  }
	  addComponent("", s, "country");
  }

  public String getCountryCode() {
	String sCountryCode = "";
	for (AddressComponent c : aComponents) {
	  if (c.isOfType("country")) {
	    sCountryCode = c.getShortName();
	    break;
	  }
	}
	return sCountryCode;
  }

  public void setCountryCode(String s) {
	  for (AddressComponent c : aComponents) {
	    if (c.isOfType("country")) {
		    c.setShortName(s);
		  return;
	    }
	  }
	  addComponent(s, "", "country");
  }

  public String getPostalCode() {
	String sPostalCode = "";
	for (AddressComponent c : aComponents) {
	  if (c.isOfType("postal_code")) {
	    sPostalCode = c.getShortName();
	    break;
	  }
	}
	return sPostalCode;
  }

  public void setPostalCode(String s) {
	  for (AddressComponent c : aComponents) {
	    if (c.isOfType("postal_code")) {
		    c.setLongName(s);
		    c.setShortName(s);
		  return;
	    }
	  }
	  addComponent(s, s, "postal_code");
  }
  
  public float getLattitude() {
    return oLatLong.getLattitude();
  }
  
  public float getLongitude() {
    return oLatLong.getLongitude();
  }

  public void setCoordinates(LatLong oLatLng) {
    oLatLong = oLatLng;
  }
  
  public LatLong getCoordinates() {
    return oLatLong;
  }

  public String getDisplayAddress() {
    return sDisplayAddress;
  }

  public void setDisplayAddress(String s) {
	sDisplayAddress = s;
  }
  
}
