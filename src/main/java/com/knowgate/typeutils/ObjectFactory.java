package com.knowgate.typeutils;

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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.knowgate.debug.DebugFile;

/**
 * Base class with helper methods for object instantiation using reflection.
 * @author Sergio Montoro Ten
 * @version 1.0
 */
public class ObjectFactory {

	/**
	 * <p>Get the constructor for a class that matches the given parameter classes.</p>
	 * @param objectClass Class&lt;? extends Object&gt;
	 * @param parameterClasses Class&lt;?&gt;[]
	 * @return Constructor&lt;? extends Object&gt; or <b>null</b> if there is no constructor with given parameters at the class.
	 */
	public static Constructor<? extends Object> getConstructor(Class<? extends Object> objectClass, Class<?>[] parameterClasses) {
		Constructor<? extends Object> objectContructor = null;
		objectContructor = tryConstructor(objectClass, parameterClasses);
		if (null==objectContructor && parameterClasses.length==2) {
			objectContructor = tryConstructor(objectClass, parameterClasses[0]);
			if (null==objectContructor)
				objectContructor = tryConstructor(objectClass, parameterClasses[1]);
			if (null==objectContructor)
				objectContructor = tryConstructor(objectClass);
		}
		if (null==objectContructor) {
			if (DebugFile.trace)
				DebugFile.writeln("No suitable constructor found for "+objectClass.getName());
		}
		return objectContructor;
	}

	/**
	 * <p>Filter parameter values that has the necessary types.</p>
	 * <p><b>Case 1.</b> If parameters.length is equal to parameterValues.length then
	 * the types of parameterValues will be checked to match the ones specified at parameters.
	 * If the types do not match then InstantiationException will be thrown.
	 * If the types match then parameterValues will be returned.</p>
	 * <p><b>Case 2.</b> If parameters.length is less than parameterValues.length then
	 * the first n parameter values with the same types as parameters will be returned.</p>
	 * <p><b>Case 3.</b> If parameters.length is greater than parameterValues.length then
	 * InstantiationException will be thrown.</p>
	 * @param parameters Parameter[]
	 * @param parameterValues Object[]
	 * @return Object[]
	 * @throws InstantiationException
	 */
	public static Object[] filterParameters(Parameter[] parameters, Object[] parameterValues) throws InstantiationException {
		if (parameters==null || parameters.length==0) {
			return NoParams;
		} else {
			final int parameterCount = parameters.length;
			final int valuesCount = parameterValues.length;
			if (parameterCount==valuesCount) {
				for (int p=0; p<valuesCount; p++)
					if (!parameterValues[p].getClass().equals(parameters[p].getType()))
						throw new InstantiationException("Parameter type mismatch expected for constructor, expected "+parameters[p].getType().getName()+" but got "+parameterValues[p].getClass().getName());
				return parameterValues;
			} else if (valuesCount>parameterCount) {
				Object[] actuallyUsedParameters = new Object[parameterCount];
				int q = 0;
				for (int p=0; p<valuesCount && q<parameterCount; p++)
					if (parameterValues[p].getClass().equals(parameters[q].getType()))
						actuallyUsedParameters[q++] = parameterValues[p];
				if (q!=parameterCount)
					throw new InstantiationException("Parameter type mismatch for constructor");
				return actuallyUsedParameters;
			} else {
				throw new InstantiationException("Not enough parameters supplied for constructor");								
			}
		}
	}

	protected static Class<?>[] getParameterClasses(Object... constructorParameters) {
		final int paramCount = constructorParameters.length;
		Class<?>[] parameterClasses = new Class<?>[paramCount];
		for (int p=0; p<paramCount; p++)
			parameterClasses[p] = constructorParameters[p].getClass();
		return parameterClasses;
	}

	protected static Constructor<? extends Object> tryConstructor(Class<? extends Object> objectClass, Class<?>... parameterClasses) {
		Constructor<? extends Object> objectConstructor = null;
		try {
			if (parameterClasses==null || parameterClasses.length==0)
				objectConstructor = objectClass.getConstructor();
			else
				objectConstructor = objectClass.getConstructor(parameterClasses);
		}
		catch (SecurityException secxcpt) { }
		catch (NoSuchMethodException notfound) { }
		return objectConstructor;
	}

	protected static Set<Class<?>> getSubclassesOf(Package pkg, Class<?> superClass) throws RuntimeException {
	    String pkgname = pkg.getName();
	    HashSet<Class<?>> classes = new HashSet<Class<?>>();
	    // Get a File object for the package
	    File directory = null;
	    String fullPath;
	    String relPath = pkgname.replace('.', '/');
	    URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
	    if (resource == null) {
	        throw new RuntimeException("No resource for " + relPath);
	    }
	    fullPath = resource.getFile();

	    try {
	        directory = new File(resource.toURI());
	    } catch (URISyntaxException e) {
	        throw new RuntimeException(pkgname + " (" + resource + ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...", e);
	    } catch (IllegalArgumentException e) {
	        directory = null;
	    }

	    if (directory != null && directory.exists()) {
	        // Get the list of the files contained in the package
	        String[] files = directory.list();
	        for (int i = 0; i < files.length; i++) {
	            // we are only interested in .class files
	            if (files[i].endsWith(".class")) {
	                // removes the .class extension
	                String className = pkgname + '.' + files[i].substring(0, files[i].length() - 6);
	                try {
	                    Class<?> clss = Class.forName(className);
	                    if (superClass.isAssignableFrom(clss))
	                        classes.add(clss);
	                } 
	                catch (ClassNotFoundException e) {
	                    throw new RuntimeException("ClassNotFoundException loading " + className);
	                }
	            }
	        }
	    }
	    else {
	        try {
	            String jarPath = fullPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
	            JarFile jarFile = new JarFile(jarPath);         
	            Enumeration<JarEntry> entries = jarFile.entries();
	            while(entries.hasMoreElements()) {
	                JarEntry entry = entries.nextElement();
	                String entryName = entry.getName();
	                if(entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
	                    String className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
	                    try {
		                	Class<?> clss = Class.forName(className);
		                    if (superClass.isAssignableFrom(clss))
		                    	classes.add(clss);
	                    } 
	                    catch (ClassNotFoundException e) {
	        		        jarFile.close();
	                        throw new RuntimeException("ClassNotFoundException loading " + className);
	                    }
	                }
	            }
		        jarFile.close();
	        } catch (IOException e) {
	            throw new RuntimeException(pkgname + " (" + directory + ") does not appear to be a valid package", e);
	        }
	    }
	    return classes;
	}

	private static final Object[] NoParams = new Object[0];
	
}