package com.ceshiren.appcrawler.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumDDT {
    WebDriver driver;
    WebElement currentElement;
    By currentBy;

    public void chrome() {
        driver = new ChromeDriver();
    }

    public void get(String url) {
        driver.get(url);
    }

    public WebElement find(By by) {
        currentElement = driver.findElement(by);
        return currentElement;
    }

    public By id(String value) {
        currentBy = By.id(value);
        return currentBy;
    }

    public void click() {
        driver.findElement(currentBy).click();
    }

    public void click(By by) {
        driver.findElement(by).click();
    }

    public void sendKeys(String text) {
        driver.findElement(currentBy).sendKeys(text);
    }

    public String attribute(String attributeName) {
        return driver.findElement(currentBy).getAttribute(attributeName);
    }
}
