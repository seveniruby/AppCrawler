package com.ceshiren.appcrawler.driver;

import static com.ceshiren.appcrawler.utils.Log.log;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BaseDDTTest {

    @Test
    void shell() throws IOException, InterruptedException {
        BaseDDT ddt = new BaseDDT();
        log.info(ddt.shell("ls"));
    }
}