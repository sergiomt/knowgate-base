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

public class Triplet<T,S,R> implements Serializable {

  private static final long serialVersionUID = 8L;

  private T val1;
  private S val2;
  private R val3;
  
  public Triplet(T val1, S val2, R val3) {
  	this.val1 = val1;
  	this.val2 = val2;
  	this.val3 = val3;
  }
  
  public T $1() {
  	return val1;
  }

  public S $2() {
  	return val2;
  }

  public R $3() {
  	return val3;
  }
  
}
