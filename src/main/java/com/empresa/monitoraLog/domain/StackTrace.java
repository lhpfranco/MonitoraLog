package com.empresa.monitoraLog.domain;

public class StackTrace {
	
	String date;
	String serverAddress;
	String applicationName;
	String stackTrace;
	
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	
	public void setDate() {
		
		if(this.stackTrace != null && !this.stackTrace.equals("")) {
			
			if(this.stackTrace.length() > 23)
				this.date = this.stackTrace.substring(1, 22);
			
		}
	}
	
	
	public String getServerAddress() {
		return serverAddress;
	}
	
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	
	public String getApplicationName() {
		return applicationName;
	}
	
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	public String getStackTrace() {
		return stackTrace;
	}
	
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
	
	@Override
	public String toString() {
		return "\n[STACKTRACE][date:" + this.getDate() + "][serverAddress:" + this.getServerAddress() + "][appName:" + this.getApplicationName();
		
	}
	

}
