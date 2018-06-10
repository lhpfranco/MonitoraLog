package com.empresa.monitoraLog;

import java.util.List;

import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.CorrelationStrategy;
import org.springframework.integration.annotation.ReleaseStrategy;
import org.springframework.messaging.Message;


public class LogAggregator{

	@Aggregator
	public List<Message<?>> aggregate(List<Message<?>> messages) throws Exception {
		return messages;

	}
	
	
	@CorrelationStrategy
	public Object getCorrelationKey(Message<?> message) {
//		System.out.println("[getCorrelationKey]: " + (String) message.getHeaders().get("file_name"));
//		System.out.println("[getCorrelationKey.payload]: " + (String) message.getPayload());
		return (String) message.getHeaders().get("file_name");
	}


	@ReleaseStrategy
	public boolean canRelease(List<String> lista) throws Exception {
		return false;
	}

}