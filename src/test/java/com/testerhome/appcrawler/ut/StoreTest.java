package com.testerhome.appcrawler.ut;

import com.testerhome.appcrawler.AppCrawler;
import com.testerhome.appcrawler.TData;
import com.testerhome.appcrawler.data.AbstractElement;
import com.testerhome.appcrawler.data.AbstractElementFactory;
import com.testerhome.appcrawler.data.AbstractElementStore;
import org.junit.jupiter.api.Test;

public class StoreTest {

    @Test
    public void test2(){
        AppCrawler.factory_$eq(AbstractElementFactory.factorySwitch(true));
        AbstractElementStore store = AppCrawler.factory().generateElementStore();
        AbstractElement element1 = AppCrawler.factory().generateElement("a", "b", "c",
                "d", "e", "", "", "", "",
                "", "", 0, 0, 0, 0, "");
        store.saveElement(element1);
        store.setElementClicked(element1);
        store.saveReqDom("<1></1>");
        store.saveResDom("<1_res></1_res>");

        AbstractElement element2 = AppCrawler.factory().generateElement("aa", "bb", "cc",
                "dd", "ee","","","","",
                "","",0,0,0,0,"");
        store.saveElement(element2);
        store.setElementSkip(element2);

        AbstractElement element3 = AppCrawler.factory().generateElement("aaa", "bbb", "ccc",
                "ddd", "eee","","","","",
                "","",0,0,0,0,"");
        store.saveElement(element3);
        store.setElementClicked(element3);
        store.saveReqDom("<2></2>");
        store.saveResDom("<2_res></2_res>");

        AbstractElement element4 = AppCrawler.factory().generateElement("aaaa", "bbbb", "cccc",
                "dddd", "eeee","","","","",
                "","",0,0,0,0,"");
        store.saveElement(element4);
        store.setElementClicked(element4);
        store.saveReqDom("<3></3>");
        store.saveResDom("<3_res></3_res>");


        AbstractElement element5 = AppCrawler.factory().generateElement("aaaaa", "bbbbb", "ccccc",
                "ddddd", "eeeee","","","","",
                "","",0,0,0,0,"");
        store.saveElement(element5);
        store.setElementClicked(element5);
        store.saveReqDom("<4></4>");
        store.saveResDom("<4_res></4_res>");

        AppCrawler.log().info(store);
        String str = TData.toYaml(store);
        AppCrawler.log().info(str);

    }
}
