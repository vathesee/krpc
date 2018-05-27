package krpc.rpc.bootstrap;

public class MonitorConfig  {

    int logThreads = 1;
    int logQueueSize = 10000;
    String logFormatter = "simple";
	String maskFields;
	int maxRepeatedSizeToLog = 1;
	boolean printDefault = false;
    String serverAddr;
    
	public int getLogThreads() {
		return logThreads;
	}
	public MonitorConfig setLogThreads(int logThreads) {
		this.logThreads = logThreads;
		return this;
	}
	public int getLogQueueSize() {
		return logQueueSize;
	}
	public MonitorConfig setLogQueueSize(int logQueueSize) {
		this.logQueueSize = logQueueSize;
		return this;
	}
	public String getLogFormatter() {
		return logFormatter;
	}
	public MonitorConfig setLogFormatter(String logFormatter) {
		this.logFormatter = logFormatter;
		return this;
	}

	public String getServerAddr() {
		return serverAddr;
	}
	public MonitorConfig setServerAddr(String serverAddr) {
		this.serverAddr = serverAddr;
		return this;
	}
	public String getMaskFields() {
		return maskFields;
	}
	public MonitorConfig setMaskFields(String maskFields) {
		this.maskFields = maskFields;
		return this;
	}
	public boolean isPrintDefault() {
		return printDefault;
	}
	public MonitorConfig setPrintDefault(boolean printDefault) {
		this.printDefault = printDefault;
		return this;
	}
	public int getMaxRepeatedSizeToLog() {
		return maxRepeatedSizeToLog;
	}
	public MonitorConfig setMaxRepeatedSizeToLog(int maxRepeatedSizeToLog) {
		this.maxRepeatedSizeToLog = maxRepeatedSizeToLog;
		return this;
	}

}
