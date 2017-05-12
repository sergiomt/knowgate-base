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
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;
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
	 */
	public static Constructor<? extends Object> getConstructor(Class<? extends Object> objectClass, Class<?>[] parameterClasses) {
		
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
			
			if (null==objectContructor) {
				if (DebugFile.trace) {
					StringBuilder variants = new StringBuilder();
					if (parameterClasses!=null) {
						if (parameterClasses.length>0) {
							variants.append("[");
							for (int c=0; c<parameterClasses.length; c++) {
								variants.append(parameterClasses[c].getName());
								if (c<parameterClasses.length-1)
									variants.append(",");
							}
							variants.append("], ");
						}
						if (parameterClasses.length==2) {
							variants.append("[").append(parameterClasses[0].getName()).append("], ");
							variants.append("[").append(parameterClasses[1].getName()).append("]");
						}
					}
					DebugFile.writeln("No suitable constructor found for "+objectClass.getName()+" after trying "+variants.toString()+(variants.length()>0 ? " and " : "")+" parameterless default constructor");
				}
			}
			if (null!=objectContructor)
				constructorCache.put(constructorSignature, objectContructor);
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

	private static String signature(Class<? extends Object> objectClass, Class<?>[] parameterClasses) {
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
	
	private static void getSuperClasses(Class<?> clss, List<Class<?>> chain) {
		Class<?> superClss = clss.getSuperclass();
		if (superClss!=null) {
			chain.add(superClss);
			getSuperClasses(superClss, chain);
		}
	}

	private static void getInterfaces(Class<?> clss, List<Class<?>> chain) {
		Class<?>[] intfaces = clss.getInterfaces();
		if (null!=intfaces && intfaces.length>0) {
			for (Class<?> iface : intfaces) {
				chain.add(iface);
				getInterfaces(iface, chain);
			}
		}
	}

	private static List<Class<?>[]> combinations(List<List<Class<?>>> parametersClasses) {
		List<Class<?>[]> combined = new LinkedList<>();
		final int paramCount = parametersClasses.size();
		if (paramCount>8)
			throw new IllegalArgumentException("Cannot create combinations for more than 8 parameter classes");
		for (Class<?> first : parametersClasses.get(0)) {
			Class<?>[] combination = new Class<?>[paramCount];
			combination[0] = first;
			if (paramCount>1) {
				for (Class<?> second : parametersClasses.get(1)) {
					combination[1] = second;
					if (paramCount>2) {
						for (Class<?> third : parametersClasses.get(2)) {
							combination[2] = third;
							if (paramCount>3) {
								for (Class<?> fourth : parametersClasses.get(3)) {
									combination[3] = fourth;
									if (paramCount>4) {
										for (Class<?> fifth : parametersClasses.get(4)) {
											combination[4] = fifth;
											if (paramCount>5) {
												for (Class<?> sixth : parametersClasses.get(5)) {
													combination[5] = sixth;
													if (paramCount>6) {
														for (Class<?> seventh : parametersClasses.get(6)) {
															combination[6] = seventh;
															if (paramCount>7) {
																for (Class<?> eighth : parametersClasses.get(6)) {
																	combination[7] = eighth;
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			combined.add(combination);
		}
		return combined;
	}

	private static Constructor<? extends Object> tryConstructorExtended(Class<? extends Object> objectClass, Class<?>... parameterClasses) {
		Constructor<? extends Object> objectConstructor = tryConstructor(objectClass, parameterClasses);
		if (null==objectConstructor) {
			
			// Try all superclasses and implemented interfaces of given parameter class
			if (parameterClasses.length==1) {
				List<Class<?>> chain = new LinkedList<>();
				getSuperClasses(parameterClasses[0], chain);
				getInterfaces(parameterClasses[0], chain);
				for (Class<?> clss : chain) {
					objectConstructor = tryConstructor(objectClass, clss);
					if (null!=objectConstructor) break;
				}
			
			} else if (parameterClasses.length>1) {
				
				// Try all the combinations of superclasses and implemented interfaces of given parameter classes
				List<List<Class<?>>> variants = new LinkedList<>();
				for (Class<?> pclss : parameterClasses) {
					List<Class<?>> supersi = new LinkedList<>();
					getSuperClasses(pclss, supersi);
					getInterfaces(pclss, supersi);
					variants.add(supersi);
				}
				for (Class<?>[] paramClasses : combinations(variants)) {
					objectConstructor = tryConstructor(objectClass, paramClasses);
					if (null!=objectConstructor) break;
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