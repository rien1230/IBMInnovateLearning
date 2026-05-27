package co2123.hw2;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task3Test {

    private Class<?> loadClass(String name) {
        ClassLoader classLoader = this.getClass().getClassLoader();

        try {
            Class<?> aClass = classLoader.loadClass(name);
            return aClass;
        } catch (Throwable t) {
        }
        return null;
    }

    //Does the parent repository class exist in the correct place?
    @Test
    void parentRepositoryExists() throws IOException {
        Map<String, String> c = GenerateTasks.getConcepts();
        Class<?> clazz = loadClass("co2123.hw2.repo." + c.get("Parent")+"Repository");
        assertFalse(clazz == null);
    }

    //Does the child repository class exist in the correct place?
    @Test
    void childRepositoryExists() throws IOException {
        Map<String, String> c = GenerateTasks.getConcepts();
        Class<?> clazz = loadClass("co2123.hw2.repo." + c.get("Child")+"Repository");
        assertFalse(clazz == null);
    }

    //Does the grandchild repository class exist in the correct place?
    @Test
    void grandchildRepositoryExists() throws IOException {
        Map<String, String> c = GenerateTasks.getConcepts();
        Class<?> clazz = loadClass("co2123.hw2.repo." + c.get("Grandchild")+"Repository");
        assertFalse(clazz == null);
    }

}
