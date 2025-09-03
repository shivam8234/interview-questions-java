import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class MainTest {

    @Test
    public void test1() throws IOException {
        String testInput = "/test_input_001.txt";
        int result = Main.processInputFile(testInput);
        assertEquals(1, result);
    }

    @Test
    public void test2() throws IOException {
        String testInput = "/test_input_002.txt";
        int result = Main.processInputFile(testInput);
        assertEquals(214, result);
    }
}
