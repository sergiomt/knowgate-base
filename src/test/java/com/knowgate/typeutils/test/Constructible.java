package com.knowgate.typeutils.test;

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

import java.io.InputStream;
import java.io.FilterInputStream;

public class Constructible {

	public Constructible(byte[] b) {
	}

	public Constructible(Number n, CharSequence s) {		
	}
	
	public Constructible(Object n, String s) {		
	}
		
	public Constructible(Integer i, InputStream n, CharSequence s) {		
	}
	
	public Constructible(Integer i, FilterInputStream n, String s) {		
	}

}
