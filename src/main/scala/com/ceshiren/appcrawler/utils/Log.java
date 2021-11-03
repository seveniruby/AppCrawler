package com.ceshiren.appcrawler.utils;

import org.apache.log4j.*;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class Log {
    public static Logger log=Logger.getLogger(Log.class);
    static PatternLayout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %p [%c{1}.%L.%M] %m%n");



    public static Logger initLog(){
        BasicConfigurator.configure();
        log.setLevel(Level.TRACE);

        if (log.getAppender("console") == null) {
            ConsoleAppender console = new ConsoleAppender();
            console.setName("console");
            console.setWriter(new OutputStreamWriter(System.out));
            console.setLayout(layout);
            log.addAppender(console);
        } else {
            log.info("console already exist");
        }
        log.setAdditivity(false);
        return log;
    }
    public static Logger initLog(String path){
        initLog();

        try {
            FileAppender fileAppender = new FileAppender(layout, path, false);
            log.addAppender(fileAppender);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return log;
    }

}
