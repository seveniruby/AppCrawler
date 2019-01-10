package com.testerhome.appcrawler.report;

import com.testerhome.appcrawler.CrawlerConf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {

    // todo : 不使用mvn
    public void start(String resultDir){
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("cmd /c cd C:/Users/hbh/Documents/project/gitTest/AITesting/forked5/AppCrawler && mvn clean -Dtest=AllureTest test");

            if(process != null){
                process.getOutputStream().close();
            }

            InputStream in = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String tmp = null;
            while ((tmp = br.readLine()) != null) {
                System.out.println(tmp);
            }

            runtime.exec("cmd /k cd C:/Users/hbh/Documents/project/gitTest/AITesting/forked5/AppCrawler/"+resultDir +" && allure generate -o " + "allureReport");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
