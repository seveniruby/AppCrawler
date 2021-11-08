package com.ceshiren.appcrawler.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class Log {
    public static Logger log = LogManager.getLogger(Log.class);

    public static Logger initLog(String path) {
        System.setProperty("logFilename", path);
        LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
        logContext.reconfigure();

        return log;
    }

}
