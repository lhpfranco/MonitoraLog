package com.empresa.monitoraLog.domain;

import java.io.Serializable;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.empresa.monitoraLog.util.DateUtil;

import lombok.Getter;
import lombok.Setter;


public class StackTrace implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private @Getter Date date;
	private @Getter @Setter String serverAddress;
	private @Getter @Setter String applicationName;
	private @Getter @Setter String stackTrace;
	private @Getter @Setter String exceptionType;
	private @Getter @Setter String exceptionMessage;
	private @Getter @Setter String exceptionPackage;
	private @Getter @Setter String exceptionClass;
	private @Getter @Setter String exceptionMethod;
	private @Getter @Setter String exceptionLine;
	
	private @Getter String linhasDoStackTrace[];
	
	public StackTrace(String serverAddress, String applicationName, String packageNaming, String stackTrace) throws Exception {
		setAllPossibleNullToEmpty();
		setDate(stackTrace);
		setServerAddress(serverAddress);
		setApplicationName(applicationName);
		setStackTrace(stackTrace);
		setLinhasDoStackTrace(getStackTrace());
		setExceptionTypeFromStackTrace(linhasDoStackTrace);
		setExceptionMessageFromStackTrace(linhasDoStackTrace);
		setExceptionClassInfoFromStackTrace(packageNaming);
	}
	
	
	private void setExceptionTypeFromStackTrace(String linhasDoTrace[]) {
		for (String linha : linhasDoTrace) {
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
	

	private void setExceptionMessageFromStackTrace(String linhasDoTrace[]) {
		/*
		 * PADRÃO DE RECONHECIMENTO: MENSAGEM DE EXCEPTION
		 * PRIMEIRA LINHA DO TRACE, APÓS O QUINTO CARACTER DOIS PONTOS ':'
		 * 
		 */
		for (String linha : linhasDoTrace) {
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
					this.setExceptionMessage(linhaSplit[linhaSplit.length-1]);
					break;
				}
			}
		}
		if(getExceptionMessage() != null && !getExceptionMessage().equals(""))
			setExceptionMessage(getExceptionMessage().trim());
	}
	
		
	private void setExceptionClassInfoFromStackTrace(String prefixoDoPacote) {
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
					 * O MÉTODO ONDE A EXCEPTION OCORREU É O VALOR DA ÚLTIMA POSICAO DENTRO DO PADRAO DE LOG
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
					
				    break;
				}
			}
		}
	}
	
	
	private void setLinhasDoStackTrace (String stackTrace) {
		linhasDoStackTrace = stackTrace.split(System.getProperty("line.separator"));
	}
	
	
	private void setDate(String trace) throws Exception {
		if(trace != null && !trace.equals("")) {
			DateUtil dataUtil = new DateUtil();
			if(trace.length() > 23)
				this.date = dataUtil.converteStringMilisegundosToDate(trace.substring(1, 22));
			
		}
	}
	
	
	private void setAllPossibleNullToEmpty() {
		setExceptionPackage("");
		setExceptionClass("");
		setExceptionMethod("");
		setExceptionLine("");
	}
	
	
	@Override
	public String toString() {
		return "\n[STACKTRACE]"
					+"\n[data:" 				+ this.getDate() 
					+ "]\n[servidor:" 			+ this.getServerAddress() 
					+ "]\n[NomeApp:" 			+ this.getApplicationName()
					+ "]\n[Tipo Excecao:" 		+ this.getExceptionType()
					+ "]\n[Mensagem Erro:" 		+ this.getExceptionMessage()
					+ "]\n[Pacote:" 			+ this.getExceptionPackage()
					+ "]\n[Classe:" 			+ this.getExceptionClass()
					+ "]\n[Metodo:" 			+ this.getExceptionMethod()
					+ "]\n[Linha:" 				+ this.getExceptionLine()
					+ "]\n";
	}
	
}
