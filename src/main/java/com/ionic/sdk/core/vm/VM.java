package com.ionic.sdk.core.vm;

import java.io.File;

/**
 * Constants associated with the system properties of a running JRE process.
 */
public final class VM {

    /**
     * Constructor.
     * http://checkstyle.sourceforge.net/config_design.html#FinalClass
     */
    private VM() {
    }

    /**
     * Constants associated with keys of environment variables available to the VM.
     */
    public static class Env {

        /**
         * The environment variable associated with the name of the platform machine (Windows).
         */
        public static final String COMPUTERNAME = "COMPUTERNAME";

        /**
         * The environment variable associated with the name of the platform machine (Linux).
         */
        public static final String HOSTNAME = "HOSTNAME";

        /**
         * The environment variable associated with the label for the platform machine processor (Windows).
         */
        public static final String PROCESSOR_ID = "PROCESSOR_IDENTIFIER";

        /**
         * The environment variable associated with the revision of the platform machine processor (Windows).
         */
        public static final String PROCESSOR_REV = "PROCESSOR_REVISION";
    }

    /**
     * Constants associated with keys of system properties available to the VM.
     */
    public static class Sys {

        /**
         * The system property key associated with the home filesystem directory of the in-use JRE.
         */
        public static final String JAVA_HOME = "java.home";

        /**
         * The system property key associated with the version of the in-use JRE.
         */
        public static final String JAVA_VERSION = "java.version";

        /**
         * The system property key associated with the operating system architecture.
         * <p>
         * (e.g. x86, i386, amd64, ppc)
         */
        public static final String OS_ARCH = "os.arch";

        /**
         * The system property key associated with the operating system name.
         * <p>
         * Windows XP, Windows 2003, Linux, Windows 2000, Mac OS X
         */
        public static final String OS_NAME = "os.name";

        /**
         * The system property key associated with the operating system version.
         */
        public static final String OS_VERSION = "os.version";

        /**
         * The system property key associated with the home folder of the current user.
         */
        public static final String USER_HOME = "user.home";

        /**
         * The system property key associated with the current user.
         */
        public static final String USER_NAME = "user.name";

        /**
         * Constituent data incorporated into system property for VMs running on Linux OS.
         */
        private static final String VALUE_LINUX = "Linux";

        /**
         * Constituent data incorporated into system property for VMs running on Mac OS.
         */
        private static final String VALUE_MAC = "Mac";

        /**
         * Constituent data incorporated into system property for VMs running on Windows OS.
         */
        private static final String VALUE_WINDOWS = "Windows";
    }

    /**
     * Read the timestamp associated with the home folder of the in-use JVM.
     *
     * @return A long value representing the time the file was last modified
     * @see File#lastModified()
     */
    public static long getTimestamp() {
        final File folderJavaHome = new File(System.getProperty(Sys.JAVA_HOME));
        return folderJavaHome.lastModified();
    }

    /**
     * Read the Java system property used to denote the home folder for the active user.
     *
     * @return the native operating system home folder for the active user
     */
    public static String getUserHome() {
        return System.getProperty(Sys.USER_HOME);
    }

    /**
     * Read the Java system property used to denote the active operating system.
     *
     * @return the native operating system hosting this running VM
     */
    public static String getOsName() {
        return System.getProperty(Sys.OS_NAME);
    }

    /**
     * @return true iff system property indicates a Linux VM.
     */
    public static boolean isLinux() {
        return (System.getProperty(Sys.OS_NAME).contains(Sys.VALUE_LINUX));
    }

    /**
     * @return true iff system property indicates a Mac VM.
     */
    public static boolean isMac() {
        return (System.getProperty(Sys.OS_NAME).contains(Sys.VALUE_MAC));
    }

    /**
     * @return true iff system property indicates a Windows VM.
     */
    public static boolean isWindows() {
        return (System.getProperty(Sys.OS_NAME).contains(Sys.VALUE_WINDOWS));
    }
}
