package com.knowgate.typeutils.test;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.lang.reflect.Constructor;

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

import java.math.BigDecimal;

import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import com.knowgate.typeutils.ObjectFactory;

public class ObjectFactoryTest {

	@Test
	public void testClassTree() {
		Class<?>[] clsss = ObjectFactory.getClassTree(BigDecimal.class);
		assertEquals(BigDecimal.class, clsss[0]);
		assertEquals(Number.class, clsss[1]);
		assertEquals(Object.class, clsss[2]);
		assertEquals(Comparable.class, clsss[3]);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetConstructorAmbiguous() {
		ObjectFactory.getConstructor(Constructible.class, new Class[]{BigDecimal.class,String.class});
	}
	
	@Test
	public void testGetConstructorBestPossible() throws NoSuchMethodException, SecurityException {
		Constructor<? extends Object> c = ObjectFactory.getConstructor(Constructible.class, new Class[]{Integer.class, BufferedInputStream.class,String.class});
		assertEquals(Constructible.class.getConstructor(Integer.class, FilterInputStream.class, String.class), c);
	}

	@Test
	public void testGetConstructorNoMatch() {
		assertNull(ObjectFactory.getConstructor(Constructible.class, new Class[]{Long.class}));
	}

	@Test
	public void testGetConstructorReversedParams() throws NoSuchMethodException, SecurityException {
		Constructor<? extends Object> c = ObjectFactory.getConstructor(Constructible.class, new Class[]{CharSequence.class, Number.class});
		assertEquals(Constructible.class.getConstructor(Number.class, CharSequence.class), c);		
	}
	@Test
	public void testGetConstructorReversedOneOfTwo() throws NoSuchMethodException, SecurityException {
		Constructor<? extends Object> c = ObjectFactory.getConstructor(Constructible.class, new Class[]{byte[].class, Long.class});
		assertEquals(Constructible.class.getConstructor(byte[].class), c);		
	}

}
