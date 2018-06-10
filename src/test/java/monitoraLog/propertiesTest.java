package monitoraLog;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:application.properties")
public class propertiesTest {
	
	@Value( "${package.naming}" )
	private String pckNaming;
	
//	@Test
//	public void deveriaRetornarValorPropriedade() {
//		assertEquals("br.sp.gov", pckNaming);
//	}

}
