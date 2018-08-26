package com.testerhome.appcrawler;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.TapOptions;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.ElementOption;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public class AppiumTouchAction {
    TouchAction action;
    int width;
    int height;
    public AppiumTouchAction(AppiumDriver driver){
        action=new TouchAction(driver);
    }
    public AppiumTouchAction(AppiumDriver driver, int width, int height){
        action=new TouchAction(driver);
        this.width=width;
        this.height=height;
    }
    public AppiumTouchAction swipe(Double startX, Double startY, Double endX, Double endY){
        action.press(
                PointOption.point((int)(width*startX), (int)(height*startY)))
                .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(1)))
                .moveTo(PointOption.point((int)(width*endX), (int)(height*endY)))
                .release()
                .perform();
        return this;
    }

    public AppiumTouchAction tap(WebElement currentElement){
        action.tap(
                TapOptions.tapOptions().withElement(ElementOption.element(currentElement))
        ).perform();
        return this;
    }
}
