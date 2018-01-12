package com.knowgate.typeutils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * This class is used to produce combinations of superclasses
 * and interfaces for trying alternative constructors at ObjectFactory
 * @author Sergio Montoro Ten
 */
public class ClassSpaceHelper {

	/**
	 * <p>Cartesian product of two Class arrays.</p>
	 * For example, for {A,B,C} {X,Y,Z} it'd be:
	 * List(9) = ({A,X},{A,Y},{A,Z},{B,X},{B,Y},{B,Z},{C,X},{C,Y},{C,Z})
	 * @param classes1 Class&lt;?&gt;[]
	 * @param classes2 Class&lt;?&gt;[]
	 * @return List&lt;Class&lt;?&gt;[2]&gt; Each element in the list will be an array of two classes
	 */
	public static List<Class<?>[]> cartesianProduct(Class<?>[] classes1, Class<?>[] classes2) {
		List<Class<?>[]> product = new ArrayList<Class<?>[]>(classes1.length * classes2.length);
		for (Class<?> c1 : classes1)
			for (Class<?> c2 : classes2)
				product.add(new Class<?>[] {c1,c2});
		return product;
	}

	/**
	 * <p>Add an additional dimension to a list of vectors.</p>
	 * For example, for List({A,B},{C,D}) , {X,Y} it will be:
	 * List(4) = ({A,B,X},{A,B,Y},{C,D,X},{C,D,Y})
	 * @param baseVectors List&lt;Class&lt;?&gt;[]&gt;
	 * @param newDimension Class&lt;?&gt;[]
	 * @return List&lt;Class&lt;?&gt;[2]&gt;
	 */
	public static List<Class<?>[]> addDimension(List<Class<?>[]> baseVectors, Class<?>[] newDimension) {
		final int vectorCount = baseVectors.size() * newDimension.length;
		List<Class<?>[]> extended = new ArrayList<Class<?>[]>(vectorCount);
		for (Class<?>[] baseVector : baseVectors) {
			for (Class<?> dim : newDimension) {
				Class<?>[] newVector = Arrays.copyOf(baseVector, baseVector.length+1);
				newVector[baseVector.length] = dim;
				extended.add(newVector);
			}
		}
		return extended;
	}

	/**
	 * <p>Product of a list of vectors</p>.
	 * @param classVector List&lt;Class&lt;?&gt;[2]&gt;
	 * @return List&lt;Class&lt;?&gt;[]&gt;
	 */
	public static List<Class<?>[]> product(List<Class<?>[]> classVector) {
		List<Class<?>[]> retval = classVector;
		if (classVector.size()>=2) {
			retval = cartesianProduct(classVector.get(0), classVector.get(1));
			if (classVector.size()>2)
				for (Class<?>[] dimension : classVector.subList(2, classVector.size()))
					retval = addDimension(retval, dimension);
		}
		return retval;
	}

	 /**
	 * <p>Scans all classes accessible from the context class loader which belong to the given package and subpackages.</p>
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Class<?>[] getClassesAtPackage(final String packageName)
	        throws ClassNotFoundException, IOException {
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    final String path = packageName.replace('.', '/');
	    Enumeration<URL> resources = classLoader.getResources(path);
	    final List<File> dirs = new ArrayList<File>();
	    while (resources.hasMoreElements())
	        dirs.add(new File(resources.nextElement().getFile()));
	    List<Class<?>> classes = new ArrayList<>();
	    for (File directory : dirs)
	        classes.addAll(findClasses(directory, packageName));
	    return classes.toArray(new Class[classes.size()]);
	}	

	/**
	 * <p>Recursive method used to find all classes in a given directory and subdirs.</p>
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(final File directory, final String packageName) throws ClassNotFoundException {
	    List<Class<?>> classes = new ArrayList<>();
	    if (!directory.exists()) {
	        return classes;
	    }
	    File[] files = directory.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            assert !file.getName().contains(".");
	            classes.addAll(findClasses(file, packageName + "." + file.getName()));
	        } else if (file.getName().endsWith(".class")) {
	            classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	        }
	    }
	    return classes;
	}
}
