package com.empresa.monitoraLog;

import java.io.File;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.file.tail.ApacheCommonsFileTailingMessageProducer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class TailFiles {
	
	ApacheCommonsFileTailingMessageProducer adapter;
	
//	private volatile Tailer tailer;

	private volatile long pollingDelay = 1000;

	private volatile boolean end = true;

	private volatile boolean reopen = false;
		
	private final String dir = "C:\\home\\ique\\Desenvolvimento e Pesquisas\\textfiles\\logs websphere\\20171201\\SiafNet1";
	
	private final String fileName = "SystemErr.log";
	
	private File file = new File(dir, fileName);
	
	ApplicationContext context = null;
	QueueChannel outChannel;

	
	
	public TailFiles(){
		context = new ClassPathXmlApplicationContext("websphereLog.xml");
		outChannel = context.getBean("outChannel", QueueChannel.class);

		adapter = new ApacheCommonsFileTailingMessageProducer();
		adapter.setPollingDelay(pollingDelay);
		adapter.setEnd(end);
		
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.afterPropertiesSet();
		adapter.setTaskScheduler(taskScheduler);
		
		adapter.setFile(new File(dir,""));
		adapter.setOutputChannel(outChannel);
		
		adapter.afterPropertiesSet();
	}

	
	public File getFile(){
		return file;
	}
}
