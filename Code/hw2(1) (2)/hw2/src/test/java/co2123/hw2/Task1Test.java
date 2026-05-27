package co2123.hw2;

import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static org.springframework.test.util.AssertionErrors.assertTrue;

public class Task1Test {

    @Test
    public void checkProperties() throws Exception {
        Properties p = new Properties();
        p.load(new FileReader("src/main/resources/application.properties"));
        assertTrue("Folder for JSPs not defined.", "/WEB-INF/views/".equals(p.getProperty("spring.mvc.view.prefix")));
        assertTrue("JSP extension not defined.", ".jsp".equals(p.getProperty("spring.mvc.view.suffix")));
        assertTrue("URl for the database not defined", p.getProperty("spring.datasource.url") != null);
        assertTrue("Username for the database not defined", p.getProperty("spring.datasource.username") != null);
        assertTrue("Password for the database not defined", p.getProperty("spring.datasource.password") != null);
        assertTrue("Missing/Incomplete configuration for printing out SQL to the command line with spring.jpa.show-sql.",p.getProperty("spring.jpa.show-sql").equals("true"));

        String s = p.getProperty("logging.level.org.hibernate.SQL");
        if(s != null){
            s = s.toLowerCase();
            assertTrue("Missing/Incomplete configuration for printing out SQL to the command line with logging.level.org.hibernate.SQL.",s.contains("debug"));
        } else {
            assertTrue("Missing/Incomplete configuration for printing out SQL to the command line with logging.level.org.hibernate.SQL.",p.getProperty("logging.level.org.hibernate.SQL").contains("debug"));
        }
    }

    @Test
    public void dependencies() throws Exception {
        String content = Files.readString(Paths.get("build.gradle"));
        assertTrue("Jasper libraries not loaded for JSPs.", content.contains("tomcat-embed-jasper"));
        assertTrue("JSTL library not included in build.gradle.", content.contains("jstl"));
        assertTrue("Missing a dependency for connecting to the database.", content.contains("mysql-connector"));
        assertTrue("Missing a dependency for connecting to the database.", content.contains("spring-boot-starter-data-jpa"));
    }


}
