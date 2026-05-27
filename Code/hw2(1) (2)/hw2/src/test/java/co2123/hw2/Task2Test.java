package co2123.hw2;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class Task2Test {

    public Class<?> loadClass(String name) {
        ClassLoader classLoader = this.getClass().getClassLoader();

        try {
            Class<?> aClass = classLoader.loadClass(name);
            return aClass;
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

    @Test
    public void classesAndFieldsCreated() throws Exception {
        Map<String, String> c = GenerateTasks.getConcepts();

        Class<?> parent = loadClass("co2123.hw2.model." + c.get("Parent"));
        assertNotNull(parent, "Class " + "co2123.hw2.model." + c.get("Parent") + " not found");

        // fields
        assertField(parent, "private", "int", c.get("pf1"));
        assertField(parent, "private", "java.lang.String", c.get("pf2"));
        assertField(parent, "private", "java.util.List", c.get("children"));
        assertField(parent, "private", "co2123.hw2.model." + c.get("Child"), c.get("pf4"));


        Class<?> child = loadClass("co2123.hw2.model." + c.get("Child"));
        assertNotNull(child, "Class " + "co2123.hw2.model." + c.get("Child") + " not found");
        assertField(child, "private", "java.lang.String", c.get("cf1"));
        assertField(child, "private", "java.util.List", c.get("parents"));
        assertField(child, "private", "java.util.List", c.get("grandchildren"));
        assertField(child, "private", "co2123.hw2.model." + c.get("Grandchild"), c.get("cf4"));

        Class<?> grandchild = loadClass("co2123.hw2.model." + c.get("Grandchild"));
        assertNotNull(grandchild, "Class " + "co2123.hw2.model." + c.get("Grandchild") + " not found");
        assertField(grandchild, "private", "int", c.get("gf1"));
        assertField(grandchild, "private", "int", c.get("gf2"));

        //check toString() prints out the correct information
        String parentString = parent.getDeclaredConstructor().newInstance().toString().toLowerCase().replaceAll("\\s+","");
        assertTrue(c.get("pf1") + " missing from " + c.get("Parent") + " toString().", parentString.contains(c.get("pf1").toLowerCase().replaceAll("\\s+","")));
        assertTrue(c.get("pf2") + " missing from " + c.get("Parent") + " toString().", parentString.contains(c.get("pf2").toLowerCase().replaceAll("\\s+","")));
        assertTrue(c.get("children") + " missing from " + c.get("Parent") + " toString().", parentString.contains(c.get("children").toLowerCase().replaceAll("\\s+","")));
        assertTrue(c.get("pf4") + " missing from " + c.get("Parent") + " toString().", parentString.contains(c.get("pf4").toLowerCase().replaceAll("\\s+","")));

        String childString = child.getDeclaredConstructor().newInstance().toString().toLowerCase().replaceAll("\\s+","");
        assertTrue(c.get("cf1") + " missing from " + c.get("Child") + " toString().", childString.contains(c.get("cf1").toLowerCase().replaceAll("\\s+","")));
        assertFalse(c.get("parents") + " shouldn't be in the toString() method of " + c.get("Child") + ".", childString.contains(c.get("parents").toLowerCase().replaceAll("\\s+","")));
        assertTrue(c.get("grandchildren") + " missing from " + c.get("Child") + " toString().", childString.contains(c.get("grandchildren").toLowerCase().replaceAll("\\s+","")));
        assertTrue(c.get("cf1") + " missing from " + c.get("Child") + " toString().", childString.contains(c.get("cf1").toLowerCase().replaceAll("\\s+","")));

        String grandchildString = grandchild.getDeclaredConstructor().newInstance().toString().toLowerCase().replaceAll("\\s+","");
        assertTrue(c.get("gf1") + " missing from " + c.get("Grandchild") + " toString().", grandchildString.contains(c.get("gf1").toLowerCase().replaceAll("\\s+","")));
        assertTrue(c.get("gf2") + " missing from " + c.get("Grandchild") + " toString().", grandchildString.contains(c.get("gf2").toLowerCase().replaceAll("\\s+","")));
    }

    @Autowired
    private MockMvc mockMvc; //if there is an error, it's likely an IntelliJ IDEA glitch if the code runs


    //NOTE: Only run this once you have at least one of each object stored in your database and the list pages working
    //Checks your toString method is correctly formatted
    @Test
    void checkToStringPrintOut() throws Exception{
        Map<String, String> c = GenerateTasks.getConcepts();

        MvcResult result = this.mockMvc.perform(get("/list"+c.get("Parent"))).andReturn();
        String content = result.getModelAndView().toString();
        //System.out.println("Content: " + content);

        //checking parent class' toString()
        Pattern pattern = Pattern.compile("[^A-Z]" + c.get("pf1") + "\\s*=\\s*" + "\\d", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        boolean matchFound = matcher.find();
        Assertions.assertTrue(matchFound);

        //checking child class' toString()
        pattern = Pattern.compile("[^A-Z]" + c.get("cf1") + "\\s*=\\s*'" + "[A-Z\\d ]*'", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(content);
        matchFound = matcher.find();
        Assertions.assertTrue(matchFound);

        //checking grandchild class' toString()
        pattern = Pattern.compile("[^A-Z]" + c.get("gf1") + "\\s*=\\s*" + "\\d", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(content);
        matchFound = matcher.find();
        Assertions.assertTrue(matchFound);
    }

    private void assertField(Class<?> parent, String modifier, String type, String name) throws Exception {
        Field f = parent.getDeclaredField(name);
        assertNotNull(f);
        assertEquals(type, f.getType().getName());
        assertEquals(Modifier.PRIVATE, f.getModifiers());
    }

}

