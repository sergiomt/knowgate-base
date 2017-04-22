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
 * <p>Wrapper interface for geocoding services</p>
 * @author Sergio Montoro Ten
 */
public interface Geocoder {

  /**
   * Get an StreetAddress object from an address string
   * @param sFullAddress String Address to be geocoded
   * @return StreetAddress String instance or <b>null</b> if given address could not be geocoded
   * @throws GeocodingException
   * @since 8.0
   */
	StreetAddress geoCode(String sFullAddress) throws GeocodingException;

  /**
   * Get an StreetAddress object from an latitude and longitude
   * @param fLat float Latitude
   * @param fLng float Longitude
   * @return StreetAddress instance or null if given address could not be geocoded
   * @throws GeocodingException
   * @since 8.0
   */
	StreetAddress reverseGeoCode(float fLat, float fLng) throws GeocodingException;

}
