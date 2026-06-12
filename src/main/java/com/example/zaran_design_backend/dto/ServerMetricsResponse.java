package com.example.zaran_design_backend.dto;

/**
 * 服务器性能指标响应 DTO（对应接口文档 9.4.4）。
 */
public class ServerMetricsResponse {

    private String timestamp;
    private CpuInfo cpu;
    private MemoryInfo memory;
    private GpuInfo gpu;
    private DiskInfo disk;
    private NetworkInfo network;

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public CpuInfo getCpu() { return cpu; }
    public void setCpu(CpuInfo cpu) { this.cpu = cpu; }
    public MemoryInfo getMemory() { return memory; }
    public void setMemory(MemoryInfo memory) { this.memory = memory; }
    public GpuInfo getGpu() { return gpu; }
    public void setGpu(GpuInfo gpu) { this.gpu = gpu; }
    public DiskInfo getDisk() { return disk; }
    public void setDisk(DiskInfo disk) { this.disk = disk; }
    public NetworkInfo getNetwork() { return network; }
    public void setNetwork(NetworkInfo network) { this.network = network; }

    public static class CpuInfo {
        private double usage;
        private int cores;

        public double getUsage() { return usage; }
        public void setUsage(double usage) { this.usage = usage; }
        public int getCores() { return cores; }
        public void setCores(int cores) { this.cores = cores; }
    }

    public static class MemoryInfo {
        private long total;
        private long used;
        private double usage;

        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
        public long getUsed() { return used; }
        public void setUsed(long used) { this.used = used; }
        public double getUsage() { return usage; }
        public void setUsage(double usage) { this.usage = usage; }
    }

    public static class GpuInfo {
        private double usage;
        private long memoryUsed;
        private long memoryTotal;
        private int temperature;

        public double getUsage() { return usage; }
        public void setUsage(double usage) { this.usage = usage; }
        public long getMemoryUsed() { return memoryUsed; }
        public void setMemoryUsed(long memoryUsed) { this.memoryUsed = memoryUsed; }
        public long getMemoryTotal() { return memoryTotal; }
        public void setMemoryTotal(long memoryTotal) { this.memoryTotal = memoryTotal; }
        public int getTemperature() { return temperature; }
        public void setTemperature(int temperature) { this.temperature = temperature; }
    }

    public static class DiskInfo {
        private long total;
        private long used;
        private double usage;

        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
        public long getUsed() { return used; }
        public void setUsed(long used) { this.used = used; }
        public double getUsage() { return usage; }
        public void setUsage(double usage) { this.usage = usage; }
    }

    public static class NetworkInfo {
        private double inMbps;
        private double outMbps;

        public double getInMbps() { return inMbps; }
        public void setInMbps(double inMbps) { this.inMbps = inMbps; }
        public double getOutMbps() { return outMbps; }
        public void setOutMbps(double outMbps) { this.outMbps = outMbps; }
    }
}
