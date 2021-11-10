package com.ceshiren.appcrawler.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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