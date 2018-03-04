package com.empresa.monitoraLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.integration.annotation.Splitter;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import com.empresa.monitoraLog.domain.StackTrace;

public class WebsphereLogSplitter {
	
	private String stackTrace;
	private String line;
	private List<Message<StackTrace>> messages = new ArrayList<Message<StackTrace>>();
	
	
	
	@Splitter(inputChannel="inChannel", outputChannel="outChannel")
	public Collection<Message<StackTrace>> split(File file) {
		
		
		System.out.println("Atingiu o splitter...");
		
		
		try {

			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			
			
			line = "";
			stackTrace = "";
			
			

			while ((line = bufferedReader.readLine()) != null) {
				

				if (line.matches(".*\\sR\\s{1}\\w.*") && !line.matches(".*Caused by.*")) {
					
					if(!stackTrace.equals("")) {
						createMessage();
						clearStackTrace();
						appendLineToStackTrace(line + "\n");
					
					}else { 
						appendLineToStackTrace(line + "\n");
					}
					
						
				} else if(!stackTrace.equals("")) {
					appendLineToStackTrace(line + "\n");
				}

			}
			

		} catch (Exception e) {
			System.out.println("Erro: " + e.getMessage());
			e.printStackTrace();
		}

	
	return messages;

	}
	
	

	private void appendLineToStackTrace(String newTextFragment) {
		
		if (newTextFragment != null && !newTextFragment.equals(""))
			stackTrace = stackTrace + newTextFragment;
		
	}
	
		
	
	private void clearStackTrace() {
		stackTrace = "";
	}
	
	
	
	public void createMessage() throws Exception{
		
		StackTrace trace = new StackTrace(stackTrace);
		messages.add(new GenericMessage<StackTrace>(trace));

	}


}