/**
 * Created by seveniruby on 2017/1/10.
 */

import org.openqa.selenium.remote.RemoteWebElement;
import io.appium.java_client.pagefactory.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindAll;

import io.appium.java_client.android.AndroidElement;
import org.openqa.selenium.remote.RemoteWebElement;
import io.appium.java_client.pagefactory.*;
import io.appium.java_client.ios.IOSElement;

import java.util.List;


public class PageObjectDemo {
    @FindBy(xpath = "")
    private List<AndroidElement> titles;

    //convinient locator
    private List<AndroidElement> scores;

    //convinient locator
    private List<AndroidElement> castings;
    //element declaration goes on
}
