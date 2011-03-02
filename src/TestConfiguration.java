import org.junit.*;
import static org.junit.Assert.*;

public class TestConfiguration {
	@Test
	public void parsingCommandLineArguments() {
		String[] args = { "-f", "hello.txt", "-v", "--version" };
		Configuration c = new Configuration();
		c.processArguments(args);
		assertEquals("hello.txt", c.getFileName());
		assertFalse(c.isDebuggingEnabled());
		assertFalse(c.isWarningsEnabled());
		assertTrue(c.isVerbose());
		assertTrue(c.shouldShowVersion());

		c = new Configuration();
		try {
			c.processArguments(new String[] {"-f"});
		} catch (InvalidArgumentException expected) {
			fail("Should've failed");
		}
	}
}