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

import java.util.Arrays;
import java.util.List;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import com.knowgate.typeutils.ClassSpaceHelper;

public class ClassSpaceHelperTest {

	@Test
	public void testCartesianProduct() {

		Class<?>[] clss1 = new Class<?>[]{Short.class, Integer.class, Long.class};
		Class<?>[] clss2 = new Class<?>[]{String.class, Boolean.class};
		
		List<Class<?>[]> product = ClassSpaceHelper.cartesianProduct(clss1, clss2);
		assertEquals(clss1.length*clss2.length, product.size());
		assertArrayEquals(new Class<?>[]{Short.class, String.class}, product.get(0));
		assertArrayEquals(new Class<?>[]{Short.class, Boolean.class}, product.get(1));
		assertArrayEquals(new Class<?>[]{Integer.class, String.class}, product.get(2));
		assertArrayEquals(new Class<?>[]{Integer.class, Boolean.class}, product.get(3));
		assertArrayEquals(new Class<?>[]{Long.class, String.class}, product.get(4));
		assertArrayEquals(new Class<?>[]{Long.class, Boolean.class}, product.get(5));
	}

	@Test
	public void testAddDimension() {

		Class<?>[] clss1 = new Class<?>[]{Short.class, Integer.class, Long.class};
		Class<?>[] clss2 = new Class<?>[]{BigInteger.class, BigDecimal.class};
		Class<?>[] clss3 = new Class<?>[]{String.class, Boolean.class};

		List<Class<?>[]> product = ClassSpaceHelper.addDimension(Arrays.asList(clss1, clss2), clss3);
		assertArrayEquals(new Class<?>[]{Short.class,Integer.class,Long.class,String.class}, product.get(0));
		assertArrayEquals(new Class<?>[]{Short.class,Integer.class,Long.class,Boolean.class}, product.get(1));
		assertArrayEquals(new Class<?>[]{BigInteger.class,BigDecimal.class,String.class}, product.get(2));
		assertArrayEquals(new Class<?>[]{BigInteger.class,BigDecimal.class,Boolean.class}, product.get(3));
		
	}

	@Test
	public void testProduct2() {
		Class<?>[] clss1 = new Class<?>[]{Short.class, Integer.class, Long.class, BigInteger.class, BigDecimal.class};
		Class<?>[] clss2 = new Class<?>[]{String.class, Boolean.class};
		List<Class<?>[]> product = ClassSpaceHelper.product(Arrays.asList(clss1, clss2));		
		assertArrayEquals(new Class<?>[]{Short.class,String.class}, product.get(0));
		assertArrayEquals(new Class<?>[]{Short.class,Boolean.class}, product.get(1));
		assertArrayEquals(new Class<?>[]{Integer.class,String.class}, product.get(2));
		assertArrayEquals(new Class<?>[]{Integer.class,Boolean.class}, product.get(3));
		assertArrayEquals(new Class<?>[]{Long.class,String.class}, product.get(4));
		assertArrayEquals(new Class<?>[]{Long.class,Boolean.class}, product.get(5));
		assertArrayEquals(new Class<?>[]{BigInteger.class,String.class}, product.get(6));
		assertArrayEquals(new Class<?>[]{BigInteger.class,Boolean.class}, product.get(7));
		assertArrayEquals(new Class<?>[]{BigDecimal.class,String.class}, product.get(8));
		assertArrayEquals(new Class<?>[]{BigDecimal.class,Boolean.class}, product.get(9));
	}

	@Test
	public void testProduct3() {
		Class<?>[] clss1 = new Class<?>[]{Short.class, Integer.class, Long.class};
		Class<?>[] clss2 = new Class<?>[]{String.class, CharSequence.class};
		Class<?>[] clss3 = new Class<?>[]{Boolean.class, Object.class};
		List<Class<?>[]> product = ClassSpaceHelper.product(Arrays.asList(clss1, clss2, clss3));
		assertArrayEquals(new Class<?>[]{Short.class,String.class,Boolean.class}, product.get(0));
		assertArrayEquals(new Class<?>[]{Short.class,String.class,Object.class}, product.get(1));
		assertArrayEquals(new Class<?>[]{Short.class,CharSequence.class,Boolean.class}, product.get(2));
		assertArrayEquals(new Class<?>[]{Short.class,CharSequence.class,Object.class}, product.get(3));
		assertArrayEquals(new Class<?>[]{Integer.class,String.class,Boolean.class}, product.get(4));
		assertArrayEquals(new Class<?>[]{Integer.class,String.class,Object.class}, product.get(5));
		assertArrayEquals(new Class<?>[]{Integer.class,CharSequence.class,Boolean.class}, product.get(6));
		assertArrayEquals(new Class<?>[]{Integer.class,CharSequence.class,Object.class}, product.get(7));
		assertArrayEquals(new Class<?>[]{Long.class,String.class,Boolean.class}, product.get(8));
		assertArrayEquals(new Class<?>[]{Long.class,String.class,Object.class}, product.get(9));
		assertArrayEquals(new Class<?>[]{Long.class,CharSequence.class,Boolean.class}, product.get(10));
		assertArrayEquals(new Class<?>[]{Long.class,CharSequence.class,Object.class}, product.get(11));
	}	
}