package monitoraLog;

import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.*;

public class RegexTest {

	
	
	@Test
	public void deveria_reconhecer_padrao_do_inicio_do_stackTrace() {
		String line = "[30/11/17 09:10:18:660 BRST] 00000c53 SystemErr     R br.gov.sp.exceptions.ValidacaoMainFrameException: * - TRANSACAO INEXISTENTE";
		
		Pattern pattern = Pattern.compile("(?<=R\\s{1})(.*?)(?=\\.)(.*?)(?=:)");

		Matcher matcher = pattern.matcher(line);
		
		assertTrue(matcher.find());

	}
}
