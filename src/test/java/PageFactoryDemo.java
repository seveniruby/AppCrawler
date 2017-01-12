/**
 * Created by seveniruby on 2017/1/10.
 */


import io.appium.java_client.pagefactory.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;

import java.net.URL;
import java.util.concurrent.TimeUnit;


public class PageFactoryDemo {
    public static void main(){
        PageObjectDemo pageObject=new PageObjectDemo();
        RemoteWebDriver driver=new RemoteWebDriver(null);
        PageFactory.initElements(new AppiumFieldDecorator(driver,
              /*searchContext is a WebDriver or WebElement
              instance */
                        new TimeOutDuration(15, //default implicit waiting timeout for all strategies
                                TimeUnit.SECONDS)),
                pageObject //an instance of PageObject.class
        );

    }
}
