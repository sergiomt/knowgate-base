package com.knowgate.debug;

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

import java.util.Properties;
import java.util.logging.LogManager;

import javax.net.ssl.KeyManagerFactory;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map.Entry;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.ImageCapabilities;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class JavaInfo {

    private PrintStream out = System.out;

    public JavaInfo() {
    	out = System.out;
    }

    public JavaInfo(PrintStream outStrm) {
    	out = outStrm;
    }
    
    public void printCurrentTime() {
        out.println("current date " + new Date());
        out.println("nano time "+System.nanoTime());        
    }

    public void printJVMInfo() {
    	out.println(System.getProperty("java.vendor") + " Runtime Environment " + System.getProperty("java.version"));
    	out.println(System.getProperty("java.vm.vendor") + " " + System.getProperty("java.vm.name") +  " " + System.getProperty("java.vm.version"));
    	out.println(System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch"));
    	out.println("JVM encoding " + (new java.io.OutputStreamWriter(new java.io.ByteArrayOutputStream()).getEncoding()));    	

    	/*
		 * WINDOWS MILLENIUM ----------------- java.vendor=Sun Microsystems Inc.
		 * java.version=1.3.1 java.vm.vendor=Sun Microsystems Inc.
		 * java.vm.name=Java HotSpot(TM) Client VM java.vm.version=1.3.1-b24
		 * os.name=Windows Me os.version=4.90 os.arch=x86
		 * 
		 * ALPHA ----- java.vendor=Compaq Computer Corp. java.version=1.2.2-8
		 * java.vm.vendor=Compaq Computer Corp. java.vm.name=Classic VM
		 * java.vm.version=1.2.2-8 os.name=OSF1 os.version=V4.0 os.arch=alpha
		 * 
		 * JSERVER ------- java.vendor=Oracle Corporation java.version=1.2.1
		 * java.vm.vendor=Oracle Corporation java.vm.name=JServer VM
		 * java.vm.version=1.2.1 os.name=Solaris os.version=V4.0 os.arch=alpha
		 */
    }

    public void printSystemEnvironment() {
        for (Entry<String,String> e : System.getenv().entrySet())
            out.println(e.getKey()+" "+e.getValue());
    }

    public void printSystemProperties() {
        Properties props = System.getProperties();
        Enumeration<Object> enums = props.keys();
        while (enums.hasMoreElements()) {
            String key = enums.nextElement().toString();
            out.println(key + " : " + props.getProperty(key));
        }
    }

    public void printRuntimeMemory() {
        Runtime r = Runtime.getRuntime();
        out.println("free memory " + r.freeMemory());
        out.println("max memory " + r.maxMemory());
        out.println("total memory " + r.totalMemory());
    }

    public void printCommandLineArguments() {
        out.print("JVM arguments");
        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments())
            out.print(" "+arg);
        out.println();
    }

    public void printClassLoaderInfo() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        out.println("thread context class loader "+cl.getClass().getName());
        out.println("system class loader "+ClassLoader.getSystemClassLoader().getClass().getName());
        ClassLoadingMXBean cx = ManagementFactory.getClassLoadingMXBean();
        out.println("loaded classes count "+cx.getLoadedClassCount());
    }

    public void printOSInfo() {
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        out.println("OS name "+os.getName()+" version "+os.getVersion());
        out.println("architecture "+os.getArch());
        out.println("available processors "+os.getAvailableProcessors());
    }

    public void printCPUUsage() {
        out.println("Current thread CPU time "+ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime());
        out.println("number of threads "+ManagementFactory.getThreadMXBean().getThreadCount());     
        out.println("system load average "+ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());        
    }

    public void printDisplayInfo() {
        int g = 0;
        for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            out.println("graphics device #"+(++g)+": "+gd.getIDstring()+" type "+gd.getType());
            out.println("\tavailable accelerated memory " + gd.getAvailableAcceleratedMemory());
            int c = 0;
            for (GraphicsConfiguration gc : gd.getConfigurations()) {
                out.println("\tgraphics configuration #"+(++c)+":");
                out.println("\t\twidth "+gc.getBounds().getWidth()+" height "+gc.getBounds().getHeight());
                out.println("\t\tfull screen "+gc.getBufferCapabilities().isFullScreenRequired());
                ImageCapabilities ic = gc.getImageCapabilities();
                out.println("\t\tis accelerated "+ic.isAccelerated());

            }
            DisplayMode dm = gd.getDisplayMode();   
            out.println("\tdisplay mode bit width "+dm.getWidth()+" height "+dm.getHeight()+" bit depth "+dm.getBitDepth()+" refresh rate "+dm.getRefreshRate());
            int m = 0;
            for (DisplayMode d : gd.getDisplayModes())
                out.println("\talt display mode #"+(++m)+" bit width "+d.getWidth()+" height "+d.getHeight()+" bit depth "+d.getBitDepth()+" refresh rate "+d.getRefreshRate());    
        }
    }

    public void printFontsInfo() {
        out.println("available fonts: "+String.join(",", GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
    }

    public void printLocaleInfo() {
        out.println("default locale: "+Locale.getDefault().getDisplayName()+" country "+Locale.getDefault().getCountry()+" language "+Locale.getDefault().getLanguage());
        out.println("available locales:");
        for (Locale l : Locale.getAvailableLocales())
            out.println("\t"+l.getDisplayName()+" country "+l.getCountry()+" language "+l.getLanguage());
    }

    public void printDiskInfo() {
        System.out.println("Current directory: "+Paths.get(".").toAbsolutePath().normalize().toString());
        File[] roots = File.listRoots();
        for (File r : roots) {
              out.println("File system root: " + r.getAbsolutePath());
              out.println("\tTotal space (bytes): " + r.getTotalSpace());
              out.println("\tFree space (bytes): " + r.getFreeSpace());
              out.println("\tUsable space (bytes): " + r.getUsableSpace());
              out.println("\tcan write "+r.canWrite());
            }
    }

    public void printNetworkInfo() throws UnknownHostException {
        out.println("host name "+InetAddress.getLocalHost().getHostName());
        out.println("host IP address "+InetAddress.getLocalHost().getHostAddress());

    }

    public void printSecurityInfo() throws UnknownHostException {
        SecurityManager security = System.getSecurityManager();
         if (security != null) {
             out.println("security manager "+security.getClass().getName()+" in check "+security.getInCheck());

         } else {
             out.println("no security manager");
         }
    }

    public void printKeyManagerInfo() {
        out.println("key manager default algorithm "+KeyManagerFactory.getDefaultAlgorithm());      
        out.println("key store default type "+KeyStore.getDefaultType());
    }

    public void printLoggingInfo() {
        for (String logger : LogManager.getLoggingMXBean().getLoggerNames()) {
            out.println("logger: \""+logger+"\" level \""+LogManager.getLoggingMXBean().getLoggerLevel(logger)+"\"");
        }

    }

    public static void main(String args []) throws Exception {

        PrintStream out = System.out;
    	
    	JavaInfo i = new JavaInfo(out);
    	
        out.println("DATE INFO");
        i.printCurrentTime();
        out.println("");
        out.println("JVM COMMAND LINE ARGUMENTS");
        i.printCommandLineArguments();
        out.println("");
        out.println("ENVIRONMENT");
        i.printSystemEnvironment();
        out.println("");
        out.println("SYSTEM PROPERTIES");
        i.printSystemProperties();
        out.println("");
        out.println("CLASS LOADER");
        i.printClassLoaderInfo();
        out.println("");
        out.println("OPERATING SYSTEM");
        i.printOSInfo();
        out.println("");
        out.println("MEMORY");
        i.printRuntimeMemory();
        out.println("");
        out.println("CPU");
        i.printCPUUsage();
        out.println("");
        out.println("DISK");
        i.printDiskInfo();
        out.println("");
        out.println("NETWORK");
        i.printNetworkInfo();
        out.println("");
        out.println("SECURITY");
        i.printSecurityInfo();
        out.println("");
        out.println("");
        out.println("LOG");
        i.printLoggingInfo();
        out.println("");
        out.println("KEY MANAGER");
        i.printKeyManagerInfo();
        out.println("");
        out.println("DISPLAY DEVICES");
        i.printDisplayInfo();
        out.println("");
        out.println("FONTS");
        i.printFontsInfo();
        out.println("");
        out.println("LOCALES");
        i.printLocaleInfo();
    }

}