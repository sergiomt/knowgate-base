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

/**
 * Hold a Latitude and Longitude coordinates pair
 * @author Sergio Montoro Ten
 *
 */
public class LatLong {

  private float fLat, fLng;

  public LatLong() {
    fLng = fLat = 0f;
  }
  
  public LatLong(float fLattitude, float fLongitude) {
    fLat = fLattitude;
    fLng = fLongitude;
  }

  public LatLong(double fLattitude, double fLongitude) {
    fLat = (float) fLattitude;
    fLng = (float) fLongitude;
  }

  public float getLattitude() {
    return fLat;
  }

  public void setLattitude(float fLattitude) {
	fLat = fLattitude;
  }
  
  public float getLongitude() {
	return fLng;
  }

  public void setLongitude(float fLongitude) {
	fLng = fLongitude;
  }
  
}