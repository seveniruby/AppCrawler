package com.ceshiren.appcrawler.it.appium;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class SettingsTest {

    @Test
    void testSettings() throws MalformedURLException, InterruptedException {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");

        AppiumDriver driver = new AppiumDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
//        driver.setSetting(Setting.WAIT_FOR_IDLE_TIMEOUT, 5000);
//        driver.setSetting(Setting.IGNORE_UNIMPORTANT_VIEWS, true);
        System.out.println(capabilities.asMap());
        driver.setSetting("enableMultiWindows", true);
//        driver.setSetting("shouldUseCompactResponses", false);

//        driver.setSetting("elementResponseAttributes", "name,text,attribute/resource-id,attribute/class");

        System.out.println(driver.getPageSource());
        driver.findElement(By.id("com.tencent.mm:id/he6")).click();
        driver.findElement(By.xpath("//*[contains(@class, 'Edit')]")).sendKeys("10086");
        driver.findElement(By.xpath("//*[contains(@text, '中国移动')]")).click();
//        Thread.sleep(5000);

        for (int i = 0; i < 5; i++) {
            System.out.println(driver.getPageSource());
            Thread.sleep(2000);
        }

        System.out.println(driver.getSettings());


    }
}
