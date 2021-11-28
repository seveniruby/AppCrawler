package com.ceshiren.appcrawler.driver;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.ceshiren.appcrawler.utils.Log.log;

class BaseDDTTest {

    @Test
    void shell() throws IOException, InterruptedException {
        BaseDDT ddt = new BaseDDT();
        log.info(ddt.shell("ls"));
    }
}