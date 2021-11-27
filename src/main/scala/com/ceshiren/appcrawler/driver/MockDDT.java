package com.ceshiren.appcrawler.driver;

import static com.ceshiren.appcrawler.utils.Log.log;
import org.openqa.selenium.By;

public class MockDDT {
    String driver;
    String currentElement;
    String currentBy;

    public void chrome() {
        log.info("driver = new ChromeDriver();");
    }

    public String find(By by) {
        log.info("currentElement = driver.findElement(by);");
        return currentElement;
    }

    public String id(String value) {
        log.info(String.format("currentBy = By.id({0});", value));
        return currentBy;
    }

    public void click(){

        log.info("currentElement.click();");

    }

    public void click(By by){
        log.info("driver.findElement(by).click();");
    }

    public String get(String attributeName){
        log.info("currentElement.getAttribute(attributeName);");
        String attributeValue = "value demo";
        return attributeValue;
    }
}
