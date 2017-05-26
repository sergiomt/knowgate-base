package com.knowgate.tuples;

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

import java.io.Serializable;

public class Triplet<U,V,W> implements Serializable {

  private static final long serialVersionUID = 8L;

  private U val1;
  private V val2;
  private W val3;
  
  public Triplet(U val1, V val2, W val3) {
  	this.val1 = val1;
  	this.val2 = val2;
  	this.val3 = val3;
  }
  
  public U $1() {
  	return val1;
  }

  public V $2() {
  	return val2;
  }

  public W $3() {
  	return val3;
  }

  public static Triplet<String,String,String> T$(String val1, String val2, String val3) {
	  return new Triplet<String,String,String>(val1, val2, val3);
  }

}
