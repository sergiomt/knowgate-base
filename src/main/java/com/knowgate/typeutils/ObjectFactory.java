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
import java.util.WeakHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.knowgate.debug.DebugFile;

/**
 * Base class with helper methods for object instantiation using reflection.
 * @author Sergio Montoro Ten
 * @version 1.0
 */
public class ObjectFactory {

	private static final Map<String, Constructor<? extends Object>> constructorCache = Collections.synchronizedMap(new WeakHashMap<>());

	/**
	 * <p>Get the constructor for a class that matches the given parameter classes.</p>
	 * @param objectClass Class&lt;? extends Object&gt;
	 * @param parameterClasses Class&lt;?&gt;[]
	 * @return Constructor&lt;? extends Object&gt; or <b>null</b> if there is no constructor with given parameters at the class.
	 * @throws IllegalArgumentException If more than one constructor is suitable for the given arguments but its signature does not exactly match the classes of the parameters
	 */
	public static Constructor<? extends Object> getConstructor(Class<? extends Object> objectClass, Class<?>[] parameterClasses) throws IllegalArgumentException {
		
		Constructor<? extends Object> objectContructor = null;
		
		final String constructorSignature = signature(objectClass, parameterClasses);
		objectContructor = constructorCache.get(constructorSignature);

		if (null==objectContructor) {
			objectContructor = tryConstructorExtended (objectClass, parameterClasses);
		
			// Try to permute first, second and third arguments
			if (null==objectContructor) {
				if (parameterClasses.length==2) {
					// Try parameters in reverse order
					objectContructor = tryConstructorExtended (objectClass, parameterClasses[1], parameterClasses[0]);
					if (null==objectContructor)
						// Try using only first parameter
						objectContructor = tryConstructorExtended (objectClass, parameterClasses[0]);
					if (null==objectContructor)
						// Try using only second parameter
						objectContructor = tryConstructorExtended (objectClass, parameterClasses[1]);					
					if (null==objectContructor)
						// Try default constructor
						objectContructor = tryConstructor (objectClass);
				}
				else if (parameterClasses.length==3) {
					// Try parameter permutations
					for (int[] p : new int[][]{new int[]{0,2,1}, new int[]{1,0,2}, new int[]{1,2,0}, new int[]{2,0,1}, new int[]{2,1,0}}) {
						objectContructor = tryConstructorExtended(objectClass, parameterClasses[p[0]], parameterClasses[p[1]], parameterClasses[p[2]]);
						if (null==objectContructor) break;
					} 
				}
			}
			
			if (DebugFile.trace) {
				StringBuilder variants = new StringBuilder();
				if (null==objectContructor) {
					if (parameterClasses!=null) {
						if (parameterClasses.length>0) {
							variants.append("{");
							for (int c=0; c<parameterClasses.length; c++) {
								variants.append(parameterClasses[c].getName());
								if (c<parameterClasses.length-1)
									variants.append(",");
							}
							variants.append("}, ");
						}
						if (parameterClasses.length==2) {
							variants.append("{").append(parameterClasses[0].getName()).append("}, ");
							variants.append("{").append(parameterClasses[1].getName()).append("}");
						}
					}
					DebugFile.writeln("No suitable constructor found for "+objectClass.getName()+" after trying "+variants.toString()+(variants.length()>0 ? " and " : "")+" parameterless default constructor");
				} else {
					variants.append("{");
					for (Class<?> t : objectContructor.getParameterTypes())
						variants.append(t.getName()).append(",");
					variants.setLength(variants.length()-1);
					variants.append("}");
					DebugFile.writeln("Matched constructor "+objectClass.getName()+variants.toString());
				}
			}
			if (null!=objectContructor)
				constructorCache.put(constructorSignature, objectContructor);
		} else {
			if (DebugFile.trace)
				DebugFile.writeln("ObjectFactory hit cached constructor "+constructorSignature);
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
					if (!parameters[p].getType().isAssignableFrom(parameterValues[p].getClass()))
						throw new InstantiationException("Parameter type mismatch expected for constructor, expected "+parameters[p].getType().getName()+" but got "+parameterValues[p].getClass().getName());
				return parameterValues;
			} else if (valuesCount>parameterCount) {
				Object[] actuallyUsedParameters = new Object[parameterCount];
				int q = 0;
				for (int p=0; p<valuesCount && q<parameterCount; p++) {
					if (parameters[q].getType().isAssignableFrom(parameterValues[p].getClass())) {
						actuallyUsedParameters[q++] = parameterValues[p];
					}
				}
				if (q!=parameterCount) {
					StringBuilder errmsg = new StringBuilder();
					errmsg.append("Parameter type mismatch for constructor, expected (");
					for (int p=0; p<parameterCount; p++)
						errmsg.append(parameters[p].getType().getName()).append(p<parameterCount-1 ? "," : "");
					errmsg.append(") but got (");
					for (int p=0; p<valuesCount; p++)
						errmsg.append(parameterValues[p].getClass().getName()).append(p<valuesCount-1 ? "," : "");
					errmsg.append(")");
					throw new InstantiationException(errmsg.toString());
				}
				return actuallyUsedParameters;
			} else {
				throw new InstantiationException("Not enough parameters supplied for constructor");								
			}
		}
	}

	/**
	 * Get an array with the classes of the given parameters
	 * @param constructorParameters Object&hellip;
	 * @return static Class&lt;?&gt;[]
	 */
	protected static Class<?>[] getParameterClasses(Object... constructorParameters) {
		final int paramCount = constructorParameters.length;
		Class<?>[] parameterClasses = new Class<?>[paramCount];
		for (int p=0; p<paramCount; p++)
			parameterClasses[p] = constructorParameters[p].getClass();
		return parameterClasses;
	}

	/**
	 * Generate a String which represent the signature of the given classes.
	 * The signature is generated by concatenating the class names.
	 * @param objectClass Class&lt;? extends Object&gt;
	 * @param parameterClasses Class&lt;?&gt;[]
	 * @return String
	 */
	public static String signature(Class<? extends Object> objectClass, Class<?>[] parameterClasses) {
		StringBuilder sign = new StringBuilder(256);
		sign.append(objectClass.getName());
		sign.append("(");
		if (parameterClasses!=null) {
			final int paramCount = parameterClasses.length;
			for (int p=0; p<paramCount; p++)
				sign.append(parameterClasses[p].getName()).append(p<paramCount-1 ? "," : "");
		}
		sign.append(")");
		return sign.toString();
	}
	
	/**
	 * Get super classes and implemented interfaces of the given class
	 * @param clss Class&lt;?&gt;
	 * @return Class&lt;?&gt;[] Superclasses followed
	 */
	public static Class<?>[] getClassTree(Class<?> clss) {
		List<Class<?>> classTree = new ArrayList<>(16);
		classTree.add(clss);
		getSuperClasses(clss, classTree);
		classTree.addAll(Arrays.asList(clss.getInterfaces()));		
		return classTree.toArray(new Class<?>[classTree.size()]);
	}

	/**
	 * Get the super classes of the given class. The immediate parent is returned first and then up the hierarchy until Object class.
	 * @param clss Class&lt;?&gt;
	 * @param classTree List&lt;Class&lt;?&gt;&gt; Output parameter must not be null, the classes will be appended to the given List
	 */
	private static void getSuperClasses(Class<?> clss, List<Class<?>> classTree) {
		Class<?> superClss = clss.getSuperclass();
		if (superClss!=null) {
			classTree.add(superClss);
			getSuperClasses(superClss, classTree);
		}
	}

	private static boolean allAssignableFrom(Class<?>[] params1, Class<?>[] params2) {
		boolean allAssignable = true;
		if (params1.length!=params2.length)
			allAssignable = false;
		else
			for (int p=params1.length-1; p>=0; p--)
				allAssignable = allAssignable && (params1[p].isAssignableFrom(params2[p]));
		return allAssignable;
	}
	
	/**
	 * Try to find a suitable constructor of a class for the given parameter list.
	 * @param objectClass Class&lt;? extends Object&gt; Class of the object to be constructed
	 * @param parameterClasses Class&lt;?&gt;&hellip; Parameter classes
	 * @return Constructor&lt;? extends Object&gt; Object constructor or <b>null</b> 
	 * @throws IllegalArgumentException If more than one constructor is suitable for the given arguments but its signature does not exactly match the classes of the parameters
	 */
	private static Constructor<? extends Object> tryConstructorExtended(Class<? extends Object> objectClass, Class<?>... parameterClasses) throws IllegalArgumentException {
		
		// Try the constructor which parameters match exactly the ones given
		Constructor<? extends Object> objectConstructor = tryConstructor (objectClass, parameterClasses);
		
		if (null==objectConstructor) {
			final int paramClassCount = parameterClasses.length;
			
			// Try all superclasses and implemented interfaces of given parameter class
			if (paramClassCount==1) {

				for (Class<?> clss : getClassTree(parameterClasses[0])) {
					objectConstructor = tryConstructor (objectClass, clss);
					if (null!=objectConstructor) break;
				}
			
			} else if (paramClassCount>1) {
				
				// List all the superclasses and implemented interfaces of the given parameter classes
				
				ArrayList<Class<?>[]> variants = new ArrayList<>(paramClassCount);
				for (Class<?> pclss : parameterClasses) {
					variants.add (getClassTree(pclss));

					if (DebugFile.trace) {
						StringBuilder variations = new StringBuilder();
						for (Class<?> clazz : variants.get(variants.size()-1))
							variations.append(clazz.getName()).append(",");
						variations.setLength(variations.length()-1);
						DebugFile.writeln("generated parameters variation {"+variations.toString()+"}");
					}
				}

				// Try all the possible combinations of superclasses and implemented interfaces of each constructor parameter
				boolean matchFound = false;
				Class<?>[] matchedParamClasses = null;
				for (Class<?>[] paramClasses : ClassSpaceHelper.product(variants)) {
					Constructor<? extends Object> matchedConstructor = tryConstructor (objectClass, paramClasses);
					if (null!=matchedConstructor) {
						if (matchFound) {
							if (allAssignableFrom(matchedParamClasses, paramClasses)) {
								objectConstructor = matchedConstructor;
								matchedParamClasses = paramClasses;			
							} else if (!allAssignableFrom(paramClasses, matchedParamClasses)) {
								throw new IllegalArgumentException("Ambiguous parameter list matches constructor "+signature(objectClass, paramClasses)+" but also "+signature(objectClass, matchedParamClasses));
							}
						} else {
							matchFound = true;
							objectConstructor = matchedConstructor;
							matchedParamClasses = paramClasses;
						}
					}
				}
				
			}
		}
		return objectConstructor;
	}
	
	protected static Constructor<? extends Object> tryConstructor(Class<? extends Object> objectClass, Class<?>... parameterClasses) {
		Constructor<? extends Object> objectConstructor = null;
		try {
			if (parameterClasses==null || parameterClasses.length==0) {
				objectConstructor = objectClass.getConstructor();
			} else {
				objectConstructor = objectClass.getConstructor(parameterClasses);
			}
		}
		catch (NoSuchMethodException notfound) {
			if (DebugFile.trace) {
				StringBuilder paramClassNames = new StringBuilder();
				paramClassNames.append("(");
				if (parameterClasses!=null) {
					for (int p=0; p<parameterClasses.length; p++)
						paramClassNames.append(parameterClasses[p].getName()).append(p<parameterClasses.length-1 ? "," : "");
				}
				paramClassNames.append(")");
				if (DebugFile.trace)
					DebugFile.writeln("no constructor with signature " + paramClassNames.toString() + " found for " + objectClass.getName());
			}	
		}
		catch (SecurityException secxcpt) { }
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