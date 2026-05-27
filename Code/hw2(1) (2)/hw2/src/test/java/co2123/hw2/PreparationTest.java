package co2123.hw2;

import co2123.hw2.GenerateTasks;
import org.junit.jupiter.api.Test;

import static org.springframework.test.util.AssertionErrors.assertNotNull;

/*
Used by Task4Test and Task6Test to generate a random string of a given length (of letters and numbers)
 */
public class PreparationTest {

    @Test
    public void userDefined() throws Exception {
        assertNotNull("No username was specified in application.properties.", GenerateTasks.getUser());
    }

}
