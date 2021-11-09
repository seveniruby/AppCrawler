package com.ceshiren.appcrawler.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class Log {
    public static Logger log = LogManager.getLogger(Log.class);
    private static LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
    private static Configuration configuration = logContext.getConfiguration();
    private static LoggerConfig loggerConfig = configuration.getLoggerConfig(Log.class.getName());
    private static FileAppender fileAppender;

    public static Logger setLogFilePath(String path) {
        fileAppender = FileAppender.newBuilder()
                .withFileName(path)
                .withAdvertise(false)
                .withAppend(false)
                .setName("CrawlerLog")
                .setLayout(PatternLayout.newBuilder().withPattern("%d{yyyy-MM-dd HH:mm:ss} %p [%C{1}.%L.%M] %m%n").build())
                .build();
        fileAppender.start();
        loggerConfig.addAppender(fileAppender, Level.TRACE, null);
        logContext.updateLoggers();
        return log;
    }

    public static void setLevel(Level level) {
        for (String name : loggerConfig.getAppenders().keySet()) {
            loggerConfig.removeAppender(name);
        }
//        loggerConfig.setLevel(level);
        loggerConfig.addAppender(configuration.getAppender("STDOUT"), level, null);
        if(fileAppender!=null) {
            loggerConfig.addAppender(fileAppender, Level.TRACE, null);
        }
        logContext.updateLoggers();
    }

}
