package com.wuzl.im.common.util;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.Map;

/**
 * 类SystemInfoUtils.java的实现描述：系统信息
 * 
 * @author ziliang.wu 2017年2月24日 下午5:06:24
 */
public class SystemInfoUtils {

    public static class SystemInfoBean {

        // 加载类的数量
        private int                 loadClazzCount;
        // 已经加载类的数量
        private long                hasloadClazzCount;
        // 尚未加载类的数量
        private long                hasUnloadClazzCount;
        // 堆内存信息
        private MemoryUsage         heapMemoryUsage;
        // 非堆内存信息
        private MemoryUsage         nonHeapMemoryUsage;
        // 操作系统的名称
        private String              operateName;
        // 操作系统的进程数
        private int                 processListCount;
        // 操作系统的架构
        private String              archName;
        // 操作系统的版本号码
        private String              versionName;
        // 虚拟机的名称
        private String              vmName;
        // 虚拟机的版本
        private String              vmVersion;
        // 系统的供应商的名称
        private String              vmVendor;
        // JVM启动时间
        private long                startTime;
        // 输入参数
        private List<String>        arguments;
        // 系统参数
        private Map<String, String> systemProperties;

        public int getLoadClazzCount() {
            return loadClazzCount;
        }

        public void setLoadClazzCount(int loadClazzCount) {
            this.loadClazzCount = loadClazzCount;
        }

        public long getHasloadClazzCount() {
            return hasloadClazzCount;
        }

        public void setHasloadClazzCount(long hasloadClazzCount) {
            this.hasloadClazzCount = hasloadClazzCount;
        }

        public long getHasUnloadClazzCount() {
            return hasUnloadClazzCount;
        }

        public void setHasUnloadClazzCount(long hasUnloadClazzCount) {
            this.hasUnloadClazzCount = hasUnloadClazzCount;
        }

        public MemoryUsage getHeapMemoryUsage() {
            return heapMemoryUsage;
        }

        public void setHeapMemoryUsage(MemoryUsage heapMemoryUsage) {
            this.heapMemoryUsage = heapMemoryUsage;
        }

        public MemoryUsage getNonHeapMemoryUsage() {
            return nonHeapMemoryUsage;
        }

        public void setNonHeapMemoryUsage(MemoryUsage nonHeapMemoryUsage) {
            this.nonHeapMemoryUsage = nonHeapMemoryUsage;
        }

        public String getOperateName() {
            return operateName;
        }

        public void setOperateName(String operateName) {
            this.operateName = operateName;
        }

        public int getProcessListCount() {
            return processListCount;
        }

        public void setProcessListCount(int processListCount) {
            this.processListCount = processListCount;
        }

        public String getArchName() {
            return archName;
        }

        public void setArchName(String archName) {
            this.archName = archName;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getVmName() {
            return vmName;
        }

        public void setVmName(String vmName) {
            this.vmName = vmName;
        }

        public String getVmVersion() {
            return vmVersion;
        }

        public void setVmVersion(String vmVersion) {
            this.vmVersion = vmVersion;
        }

        public String getVmVendor() {
            return vmVendor;
        }

        public void setVmVendor(String vmVendor) {
            this.vmVendor = vmVendor;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public List<String> getArguments() {
            return arguments;
        }

        public void setArguments(List<String> arguments) {
            this.arguments = arguments;
        }

        public Map<String, String> getSystemProperties() {
            return systemProperties;
        }

        public void setSystemProperties(Map<String, String> systemProperties) {
            this.systemProperties = systemProperties;
        }

    }

    public static SystemInfoBean getSystemInfo() {
        SystemInfoBean infoBean = new SystemInfoBean();
        OperatingSystemMXBean operateSystemMBean = ManagementFactory.getOperatingSystemMXBean();
        String operateName = operateSystemMBean.getName();
        infoBean.setOperateName(operateName);
        int processListCount = operateSystemMBean.getAvailableProcessors();
        infoBean.setProcessListCount(processListCount);
        String archName = operateSystemMBean.getArch();// System.getProperty("os.arch");
        infoBean.setArchName(archName);
        String versionName = operateSystemMBean.getVersion();// System.getProperty("os.version");
        infoBean.setVersionName(versionName);

        // 运行时信息
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String vmName = runtimeMXBean.getVmName();
        infoBean.setVmName(vmName);
        String vmVersion = runtimeMXBean.getVmVersion();
        // infoBean.setVmVersion(vmVersion);
        infoBean.setVmVersion(System.getProperty("java.version") + " (" + vmVersion + ")");
        String vmVendor = runtimeMXBean.getVmVendor();
        infoBean.setVmVendor(vmVendor);
        long startTime = runtimeMXBean.getStartTime();
        infoBean.setStartTime(startTime);

        infoBean.setArguments(runtimeMXBean.getInputArguments());
        infoBean.setSystemProperties(runtimeMXBean.getSystemProperties());
        // 类信息
        ClassLoadingMXBean classLoadMXBean = ManagementFactory.getClassLoadingMXBean();
        int loadClazzCount = classLoadMXBean.getLoadedClassCount();
        infoBean.setLoadClazzCount(loadClazzCount);
        long hasloadClazzCount = classLoadMXBean.getTotalLoadedClassCount();
        infoBean.setHasloadClazzCount(hasloadClazzCount);
        long hasUnloadClazzCount = classLoadMXBean.getUnloadedClassCount();
        infoBean.setHasUnloadClazzCount(hasUnloadClazzCount);

        // 内存
        MemoryMXBean memoryUsage = ManagementFactory.getMemoryMXBean();
        infoBean.setHeapMemoryUsage(memoryUsage.getHeapMemoryUsage());
        infoBean.setNonHeapMemoryUsage(memoryUsage.getNonHeapMemoryUsage());
        return infoBean;
    }
}
