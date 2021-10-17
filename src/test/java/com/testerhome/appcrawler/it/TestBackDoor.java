package com.ceshiren.appcrawler.it;

import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumDriver;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

public class TestBackDoor {

    private String APP = "/Users/seveniruby/temp/appcrawler/TheApp-v1.8.1.apk";

    private AppiumDriver driver;

    @Before
    public void setUp() throws IOException {
        DesiredCapabilities caps = new DesiredCapabilities();

        caps.setCapability("platformName", "Android");
        caps.setCapability("deviceName", "Android Emulator");
        caps.setCapability("automationName", "Espresso");
        caps.setCapability("app", APP);
        driver = new AppiumDriver(new URL("http://localhost:4723/wd/hub"), caps);
    }

    @After
    public void tearDown() {
        try {
            driver.quit();
        } catch (Exception ign) {}
    }

    @Test
    public void testBackdoor() throws InterruptedException {
        Thread.sleep(3000);
        ImmutableMap<String, Object> scriptArgs = ImmutableMap.of(
                "target", "application",
                "methods", Arrays.asList(ImmutableMap.of(
                        "name", "raiseToast",
                        "args", Arrays.asList(ImmutableMap.of(
                                "value", "Hello from the test script by 咪咕阅读 & 霍格沃兹测试学院!",
                                "type", "String"
                        ))
                ))
        );


        ImmutableMap<String, Object> scriptArgs2 = ImmutableMap.of(
                "target", "application",
                "methods", Arrays.asList(ImmutableMap.of(
                        "name", "getJSMainModuleName"
                ))
        );


        for(int i=0;i<100;i++) {

            System.out.println(driver.executeScript("mobile: backdoor", scriptArgs2));
            System.out.println(driver.executeScript("mobile: backdoor", scriptArgs));
            Thread.sleep(1000);
        }
        try { Thread.sleep(2000); } catch (Exception ign) {} // pause to allow visual verification
    }

}