package com.ceshiren.appcrawler.driver;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.ceshiren.appcrawler.utils.Log.log;

public class BaseDDT {
    public String shell(String cmd) throws IOException, InterruptedException {
        log.debug(cmd);
        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();
        StringBuilder r=new StringBuilder();
        String out = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
        if(!out.isEmpty()){
            r.append(out);
        }
        String err = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
        if(!err.isEmpty()){
            log.warn(err);
            r.append(err);
        }

        return r.toString();
    }

    public String format(String format, String... strings){
        return String.format(format, strings);
    }
}
