package com.ceshiren.appcrawler;

import com.ceshiren.appcrawler.Crawler;
import com.ceshiren.appcrawler.data.AbstractElement;
import com.ceshiren.appcrawler.plugin.Plugin;

public class DemoPlugin extends Plugin {
    @Override
    public void init(Crawler crawler) {
        System.out.println("demo init");
        log().debug("demo init");
    }

    @Override
    public void beforeElementAction(AbstractElement element) {
        log().info(element);

    }
}
