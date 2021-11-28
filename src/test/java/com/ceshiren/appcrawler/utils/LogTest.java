package com.ceshiren.appcrawler.utils;

import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.Test;

class LogTest {

    @Test
    void setLogFilePath() {
        Log.log.trace("trace");
        Log.log.info("demo");
        Log.log.info(Log.log.getLevel());
        Log.log.info(Log.log.getName());
        Log.setLogFilePath("/tmp/1.log");
        Log.log.trace("trace");
        Log.log.info("info");
        Log.log.trace("trace");
        Log.log.info(Log.log.getLevel());
        Log.log.info(Log.log.getName());
    }

    @Test
    void setLevel() {
        Log.log.trace("trace");
        Log.log.debug("debug");
        Log.log.info("info");
        Log.setLevel(Level.DEBUG);
        Log.setLogFilePath("/tmp/1.log");
        Log.log.trace("trace2");
        Log.log.debug("debug2");
        Log.log.info("info2");
    }
}