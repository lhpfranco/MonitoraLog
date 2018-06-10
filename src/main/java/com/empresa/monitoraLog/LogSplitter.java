package com.empresa.monitoraLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import com.empresa.monitoraLog.domain.StackTrace;
import com.empresa.monitoraLog.util.LogUtil;


public class LogSplitter {
	
	@Value("${server.address}")
	private String serverAddress;
	
	@Value("${application.name}")
	private String applicationName;
	
	@Value("${package.naming}")
	private String packageNaming;

	private String strStackTrace;
	private String line;
	
	private static final String _NAME = "LogSplitter"; 
	private List<Message<StackTrace>> outboundMessages;
	

	public Collection<Message<StackTrace>> split(List<Message<?>> inboundMessages) throws Exception {

		LogUtil.info(_NAME, "[Metodo split] Nro msgs recebidas: " + inboundMessages.size());

		outboundMessages = new ArrayList<Message<StackTrace>>();

		line = "";
		strStackTrace = "";
		boolean padraoReconhecido = false;

		for(Message<?> message : inboundMessages){

			line = (String) message.getPayload();

			if (line != null && !line.equals("") &&
					line.matches(".*\\sR\\s{1}\\w.*") && !line.matches(".*Caused by.*")) {

				Pattern pattern = Pattern.compile("(?<=R\\s{1})(.*?)(?=\\.)(.*?)(?=:)");
				Matcher matcher = pattern.matcher(line);

				if (matcher.find()) {
					padraoReconhecido = true;

					if(!strStackTrace.equals("")) {
						createMessage();
						clearStackTrace();
						appendLineToStackTrace(line + "\n");

					}else { 
						appendLineToStackTrace(line + "\n");
					}

				}else {
					padraoReconhecido = false;
				}

			} else if(!strStackTrace.equals("") && padraoReconhecido) {
				appendLineToStackTrace(line + "\n");
			}
		}

		if(!strStackTrace.equals("")){
			createMessage();
			clearStackTrace();
		}

		LogUtil.info(_NAME, "[Metodo split()] enviando " + outboundMessages.size() + " traces.");

		return outboundMessages;
	}

	
	private void appendLineToStackTrace(String newLine) {
		if (newLine != null && !newLine.equals(""))
			strStackTrace = strStackTrace + newLine;
	}
		
	
	private void clearStackTrace() {
		strStackTrace = "";
	}

	
	public void createMessage() throws Exception{
		StackTrace traceObj = new StackTrace(serverAddress, applicationName, packageNaming, strStackTrace);
		System.out.println("Splitted ==> " + traceObj.toString());
		outboundMessages.add(new GenericMessage<StackTrace>(traceObj));
	}

}