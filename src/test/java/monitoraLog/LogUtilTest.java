package monitoraLog;


import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;

import com.empresa.monitoraLog.util.LogUtil;

@SpringBootTest
public class LogUtilTest {
	
	@Rule
	public OutputCapture output = new OutputCapture();

	@Test
	public void testLogger() {
		LogUtil.info("LogUtilTest", "Testando LOG4j com jUnit");
		this.output.equals("Testando LOG4j com jUnit");
	}

}
