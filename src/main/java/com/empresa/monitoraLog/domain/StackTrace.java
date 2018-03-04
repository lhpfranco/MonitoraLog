package com.empresa.monitoraLog.domain;

import java.net.InetAddress;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.empresa.monitoraLog.util.DataUtil;

public class StackTrace {
	
	private Date   date;
	private String serverAddress;
	private String applicationName;
	private String stackTrace;
	private String exceptionType;
	private String exceptionMessage;
	private String exceptionPackage;
	private String exceptionClass;
	private String exceptionMethod;
	private String exceptionLine;
	
	private String linhasDoStackTrace[];
	
	public StackTrace(String stackTrace) throws Exception {
		this.setStackTrace(stackTrace);
		this.setApplicationName("AppTeste");
		this.setDate();
		this.setServerAddress(InetAddress.getLocalHost().toString());
		this.setLinhasDoStackTrace(this.getStackTrace());
		this.setExceptionTypeFromStackTrace();
		this.setExceptionMessageFromStackTrace();
		this.setExceptionFileInfoFromStackTrace("br.gov.sp.");
	}
	
	
	
	private void setExceptionTypeFromStackTrace() {
		
		for (String linha : linhasDoStackTrace) {
			if (linha.matches(".*\\sR\\s{1}\\w.*")){
				Pattern pattern = Pattern.compile("(?<=R\\s{1})(.*?)(?=:)");

				Matcher matcher = pattern.matcher(linha);
				
				if (matcher.find()) {
				    this.setExceptionType(matcher.group(1));
				    break;
				}
			}
		}
	}
	

	private void setExceptionMessageFromStackTrace() {
		/*
		 * PADRÃO DE RECONHECIMENTO: MENSAGEM DE EXCEPTION
		 * PRIMEIRA LINHA DO TRACE, APÓS, QUINTO CARACTER DOIS PONTOS ':'
		 * 
		 */
		for (String linha : linhasDoStackTrace) {
			if (linha.matches(".*\\sR\\s{1}\\w.*")){
				String linhaSplit[] = linha.split(":");
				if(linhaSplit.length == 5) {
					this.setExceptionMessage(linhaSplit[linhaSplit.length-1]);
					break;
				}else if(linhaSplit.length > 5){
					String strMsgExcption = "";
					for(int i=4; i < linhaSplit.length; i++ ) {
						
						if(i==4) 
							strMsgExcption = linhaSplit[i] + ":";
						
						else if(i == linhaSplit.length - 1)
							strMsgExcption = strMsgExcption + linhaSplit[i];
						
						else
							strMsgExcption = strMsgExcption + linhaSplit[i] + ":";
							
						
					}
					this.setExceptionMessage(strMsgExcption);
					System.out.println("*************************************************");
					System.out.println("*************************************************");
					System.out.println("*************************************************");
					System.out.println(strMsgExcption);


					break;
				}
			}
		}
	}
	
		
	private void setExceptionFileInfoFromStackTrace(String prefixoDoPacote) {
		for (String linha : linhasDoStackTrace) {
			
			/*
			 * PROCURA PELA PRIMEIRA LINHA QUE CONTÉM O PREFIXO DOS PACOTES DA APLICACAO
			 * EX.: br.gov.sp.
			 */
			if (linha.matches(".*\\sat\\s{1}" + prefixoDoPacote + "\\w.*")){
				
				
				/*
				 * ISOLA A STRING QUE CONTÉM APENAS O pacote.classe.metodo
				 * EX.: br.gov.sp.connection.BoContexto.disconnectFromMF
				 */
				Pattern pattern = Pattern.compile("(?<=.*\\sat\\s{1})(.*?)(?=\\()");
				Matcher matcher = pattern.matcher(linha);
				
				if (matcher.find()) {
					
					String localizacaoDaException[] = matcher.group(1).split("\\.");
					
					/*
					 * O MÉTODO É O VALOR DA ÚLTIMA POSICAO DENTRO DO PADRAO DE LOG
					 */
					this.setExceptionMethod(localizacaoDaException[localizacaoDaException.length-1]);
					
					/*
					 * A CALSSE É O VALOR DA PENULTIMA POSICAO DENTRO DO PADRAO DE LOG
					 */
					this.setExceptionClass(localizacaoDaException[localizacaoDaException.length-2]);
					
					/*
					 * O PACOTE É O VALOR ENTRE A ANTIPENULTIMA E A PRIMEIRA POSICAO DENTRO DO PADRAO DE LOG
					 */
					String sufixoPacote = "";
					for(int i=localizacaoDaException.length-3; i > 2; i--) {
						if(i==localizacaoDaException.length-3)
							sufixoPacote = localizacaoDaException[i];
						else
							sufixoPacote = localizacaoDaException[i] + "." + sufixoPacote;
					}
					
					this.setExceptionPackage(prefixoDoPacote+sufixoPacote);
					
					
					/*
					 * ISOLA A STRING QUE CONTÉM APENAS O NOME DO ARQUIVO:NUMERO DA LINHA
					 * EX.: ProducaoSIAFEMLogin.java:77
					 */
					pattern = Pattern.compile("(?<=.*\\w\\({1})(.*?)(?=\\))");
					matcher = pattern.matcher(linha);
					
					if (matcher.find()) {
						String numeroDaLinha[] = matcher.group(1).split(":");
						this.setExceptionLine(numeroDaLinha[1]);
					}
					
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*");
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
					System.out.println("linha : " + linha);
					System.out.println("Pacote: " + this.getExceptionPackage());
					System.out.println("Classe: " + this.getExceptionClass());
					System.out.println("Metodo: " + this.getExceptionMethod());
					System.out.println("Linha : " + this.getExceptionLine());

				    break;
				}
			}
		}

		
		
	}
	
	
	private void setLinhasDoStackTrace (String stackTrace) {
		linhasDoStackTrace = stackTrace.split(System.getProperty("line.separator"));
	}
	
	
	
	private void setDate() throws Exception {
		
		if(this.stackTrace != null && !this.stackTrace.equals("")) {
			DataUtil dataUtil = new DataUtil();
			if(this.stackTrace.length() > 23)
				this.date = dataUtil.converteStringMilisegundosToDate(this.stackTrace.substring(1, 22));
			
		}
	}
	
	
	public Date getDate() {
		return date;
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
	
	
	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}
	
	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String message) {
		this.exceptionMessage = message;
	}

	public String getExceptionPackage() {
		return exceptionPackage;
	}

	public void setExceptionPackage(String _package) {
		this.exceptionPackage = _package;
	}

	public String getExceptionClass() {
		return exceptionClass;
	}

	public void setExceptionClass(String _class) {
		this.exceptionClass = _class;
	}

	public String getExceptionMethod() {
		return exceptionMethod;
	}

	public void setExceptionMethod(String method) {
		this.exceptionMethod = method;
	}

	public String getExceptionLine() {
		return exceptionLine;
	}

	public void setExceptionLine(String line) {
		this.exceptionLine = line;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "\n[STACKTRACE][date:" + this.getDate() + "][serverAddress:" + this.getServerAddress() + "][appName:" + this.getApplicationName();
		
	}
	
}
