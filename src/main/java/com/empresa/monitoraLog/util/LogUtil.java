package com.empresa.monitoraLog.util;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public abstract class LogUtil {
	
	private static Logger LOGGER;
	
	public static void info(String classe, String info) {
		LOGGER = LogManager.getLogger(classe+".class");  
		LOGGER.info(info);
	}
	
	public static void debug(String classe, String debug) {
		LOGGER = LogManager.getLogger(classe+".class");  
		LOGGER.debug(debug);
	}

}