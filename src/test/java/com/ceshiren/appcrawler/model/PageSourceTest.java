package com.ceshiren.appcrawler.model;

import com.ceshiren.appcrawler.utils.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.ceshiren.appcrawler.utils.Log.log;

class PageSourceTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void fromXML() {
    }

    @Test
    void fromJSON() {
    }

    @Test
    void getNodeListByKey() {
    }

    @Test
    void demo() {
        PageSource page = new PageSource();
        Log.initLog("/tmp/1.log");
        log.trace("trace");
        log.debug("debug");
        log.info("info");
        page.demo();
    }
}