package monitoraLog;

	
import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.file.tail.ApacheCommonsFileTailingMessageProducer;
import org.springframework.integration.file.tail.FileTailingMessageProducerSupport;
import org.springframework.integration.file.tail.FileTailingMessageProducerSupport.FileTailingEvent;
import org.springframework.messaging.Message;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

	/**
	 * @author Gary Russell
	 * @author Gavin Gray
	 * @author Artem Bilan
	 * @author Ali Shahbour
	 *
	 * @since 3.0
	 */
	public class FilerTailerTest {
		
		private QueueChannel outChannel;
		private volatile long pollingDelay;
		private volatile boolean end;
		
		private String dir;

		private String fileToTailName;
		private File fileToTail;
		
		private String resourceDir;
		private String fileWithTracesToAppend;
		private File fileToAppend;
		
		ApacheCommonsFileTailingMessageProducer adapter;
		ApplicationEventPublisher eventPublisher;

		@Before
		public void init(){
			outChannel 		= new QueueChannel();
			pollingDelay 	= 1000;
			end 			= false;
			//WINDOWS
			//private final String dir = "C:\\home\\ique\\Desenvolvimento e Pesquisas\\textfiles\\logs websphere\\20171201\\SiafNet1\\";
			//LINUX
			dir 			= "/home/ique/Desenvolvimento e Pesquisas/textfiles/logs websphere/20171201/SiafNet1/";
			fileToTailName 	= "SystemErr.log";
			fileToTail 		= new File(dir, fileToTailName);
			
			resourceDir 			= "src/test/java/resources/";
			fileWithTracesToAppend	= "tracesToAppend.txt";
			fileToAppend 			= new File(resourceDir, fileWithTracesToAppend);
		}
		
		
		@Test
		public void deveriaEncontrarArquivo() throws Exception {
			BufferedReader bufferedReaderTracesToAppend = new BufferedReader(new FileReader(fileToAppend));
		}

		@Test
		public void testApache() throws Exception {
			
			adapter = new ApacheCommonsFileTailingMessageProducer();
			
			adapter.setPollingDelay(pollingDelay);
			adapter.setEnd(end);
			adapter.setFile(new File(dir,fileToTailName));
			adapter.setOutputChannel(outChannel);
			ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
			taskScheduler.afterPropertiesSet();
			adapter.setTaskScheduler(taskScheduler);
			adapter.afterPropertiesSet();
			eventPublisher = new ApplicationEventPublisher() {
				@Override
				public void publishEvent(Object message) {
					if (eventPublisher != null) {
						FileTailingEvent event = new FileTailingEvent(this, (String) message, fileToTail);
						eventPublisher.publishEvent(event);
					}
					else {
						System.out.println("No publisher for event: " + message);
					}
				}
				@Override
				public void publishEvent(ApplicationEvent arg0) {
				}
			};
			adapter.setApplicationEventPublisher(eventPublisher);
			adapter.start();
			
			BufferedReader bufferedReaderTracesToAppend = new BufferedReader(new FileReader(fileToAppend));
			FileOutputStream foo = new FileOutputStream(fileToTail);
			
			String line = "";
			
			while ((line = bufferedReaderTracesToAppend.readLine()) != null) {
				foo.write((line + "\n").getBytes());
			}
				
			foo.flush();
			foo.close();
			
			boolean isThisTheEndOfTailedTraces = true;
			int actualQueueSize = 2;
			
			while (isThisTheEndOfTailedTraces) {
				assertEquals(actualQueueSize, outChannel.getQueueSize());
				actualQueueSize--;
				
				Message<?> message = outChannel.receive();
				
				if(message==null)
					isThisTheEndOfTailedTraces = false;
				else {
					assert(message.getPayload().toString().contains("SystemErr"));
					System.out.println("payload: " + message.getPayload());
				}
			}
		}
		
		protected  void publish(String message) {
		}


//		@Test
//		@TailAvailable
//		public void canRecalculateCommandWhenFileOrOptionsChanged() throws IOException {
//			File firstFile = File.createTempFile("first", ".txt");
//			String firstOptions = "-f options";
//			File secondFile = File.createTempFile("second", ".txt");
//			String secondOptions = "-f newoptions";
//			OSDelegatingFileTailingMessageProducer adapter = new OSDelegatingFileTailingMessageProducer();
//			adapter.setFile(firstFile);
//			adapter.setOptions(firstOptions);
//
//			adapter.setOutputChannel(new QueueChannel());
//			adapter.setTailAttemptsDelay(500);
//			adapter.setBeanFactory(mock(BeanFactory.class));
//			adapter.afterPropertiesSet();
//
//			adapter.start();
//			assertEquals("tail " + firstOptions + " " + firstFile.getAbsolutePath(), adapter.getCommand());
//			adapter.stop();
//
//			adapter.setFile(secondFile);
//			adapter.start();
//			assertEquals("tail " + firstOptions + " " + secondFile.getAbsolutePath(), adapter.getCommand());
//			adapter.stop();
//
//			adapter.setOptions(secondOptions);
//			adapter.start();
//			assertEquals("tail " + secondOptions + " " + secondFile.getAbsolutePath(), adapter.getCommand());
//			adapter.stop();
//		}

//		@Test
//		public void testIdleEvent() throws Exception {
//			ApacheCommonsFileTailingMessageProducer adapter = new ApacheCommonsFileTailingMessageProducer();
//
//			ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
//			taskScheduler.afterPropertiesSet();
//			adapter.setTaskScheduler(taskScheduler);
//
//			CountDownLatch idleCountDownLatch = new CountDownLatch(1);
//			CountDownLatch fileExistCountDownLatch = new CountDownLatch(1);
//
//			adapter.setApplicationEventPublisher(event -> {
//				if (event instanceof FileTailingIdleEvent) {
//					idleCountDownLatch.countDown();
//				}
//				if (event instanceof FileTailingEvent) {
//					FileTailingEvent fileTailingEvent = (FileTailingEvent) event;
//					if (fileTailingEvent.getMessage().contains("File not found")) {
//						fileExistCountDownLatch.countDown();
//					}
//				}
//			});
//
//			File file = spy(new File(this.testDir, "foo"));
//			file.delete();
//			adapter.setFile(file);
//
//			adapter.setOutputChannel(new NullChannel());
//			adapter.setIdleEventInterval(10);
//			adapter.afterPropertiesSet();
//			adapter.start();
//
//			boolean noFile = fileExistCountDownLatch.await(10, TimeUnit.SECONDS);
//			assertTrue("file does not exist event did not emit ", noFile);
//			boolean noEvent = idleCountDownLatch.await(100, TimeUnit.MILLISECONDS);
//			assertFalse("event should not emit when no file exit", noEvent);
//			verify(file, atLeastOnce()).exists();
//
//			file.createNewFile();
//			boolean eventRaised = idleCountDownLatch.await(10, TimeUnit.SECONDS);
//			assertTrue("idle event did not emit", eventRaised);
//			adapter.stop();
//			file.delete();
//		}

//		private void testGuts(FileTailingMessageProducerSupport adapter, String field)
//				throws Exception {
//			this.adapter = adapter;
//			ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
//			taskScheduler.afterPropertiesSet();
//			adapter.setTaskScheduler(taskScheduler);
//			final List<FileTailingEvent> events = new ArrayList<FileTailingEvent>();
////			adapter.setApplicationEventPublisher(event -> {
////				FileTailingEvent tailEvent = (FileTailingEvent) event;
////				logger.debug(event);
////				events.add(tailEvent);
////			});
//			adapter.setFile(new File(testDir, "foo"));
//			QueueChannel outputChannel = new QueueChannel();
//			adapter.setOutputChannel(outputChannel);
//			adapter.setTailAttemptsDelay(500);
//			adapter.setBeanFactory(mock(BeanFactory.class));
//			adapter.afterPropertiesSet();
//			File file = new File(testDir, "foo");
//			File renamed = new File(testDir, "bar");
//			file.delete();
//			renamed.delete();
//			adapter.start();
//			waitForField(adapter, field);
//			FileOutputStream foo = new FileOutputStream(file);
//			for (int i = 0; i < 50; i++) {
//				foo.write(("hello" + i + "\n").getBytes());
//			}
//			foo.flush();
//			foo.close();
//			for (int i = 0; i < 50; i++) {
//				Message<?> message = outputChannel.receive(10000);
//				assertNotNull("expected a non-null message", message);
//				assertEquals("hello" + i, message.getPayload());
//			}
//			file.renameTo(renamed);
//			file = new File(testDir, "foo");
//			foo = new FileOutputStream(file);
//			if (adapter instanceof ApacheCommonsFileTailingMessageProducer) {
//				Thread.sleep(1000);
//			}
//			for (int i = 50; i < 100; i++) {
//				foo.write(("hello" + i + "\n").getBytes());
//			}
//			foo.flush();
//			foo.close();
//			for (int i = 50; i < 100; i++) {
//				Message<?> message = outputChannel.receive(10000);
//				assertNotNull("expected a non-null message", message);
//				assertEquals("hello" + i, message.getPayload());
//				assertEquals(file, message.getHeaders().get(FileHeaders.ORIGINAL_FILE));
//				assertEquals(file.getName(), message.getHeaders().get(FileHeaders.FILENAME));
//			}
//
//			assertThat(events.size(), greaterThanOrEqualTo(1));
//		}

		private void waitForField(FileTailingMessageProducerSupport adapter, String field) throws Exception {
			int n = 0;
			DirectFieldAccessor accessor = new DirectFieldAccessor(adapter);
			while (n < 100) {
				if (accessor.getPropertyValue(field) == null) {
					Thread.sleep(100);
				}
				else {
					return;
				}
			}
			fail("adapter failed to start");
		}

	}


