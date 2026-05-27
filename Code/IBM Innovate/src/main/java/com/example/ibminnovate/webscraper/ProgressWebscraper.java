package com.example.ibminnovate.webscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class ProgressWebscraper {
    public static void main(String[] args) {
        String url = "https://skills.yourlearning.ibm.com/learning/completed";
        try {
            // Fetch the page
            Document document = Jsoup.connect(url)
                    .cookie("SESSION", "YOUR_SESSION_COOKIE") // Replace with actual session cookie
                    .get();

            // Extract completed courses
            Elements courseElements = document.select(".completed-course"); // Adjust selector
            for (Element courseElement : courseElements) {
                String courseName = courseElement.select(".course-name").text(); // Adjust selector
                System.out.println("Completed Course: " + courseName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
