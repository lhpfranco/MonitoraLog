package com.empresa.monitoraLog.util;

import java.text.SimpleDateFormat;
import java.sql.Date;

public class DataUtil {

	
	public Date converteStringMilisegundosToDate(String inDataString) throws Exception{
		
		String diaMes 			= inDataString.substring(0, 6);
		String ano 				= inDataString.substring(6, 8);
		String espacoMaisHora 	= inDataString.substring(8, inDataString.length());
		
		ano = "20" + ano;
		
		SimpleDateFormat sdfIn = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		Date novaData = new Date(sdfIn.parse(diaMes+ano+espacoMaisHora).getTime());
		return novaData;

	}
}
