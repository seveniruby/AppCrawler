package com.ceshiren.appcrawler.report;

import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

class Junit5ReportTest {

    @Test
    void genTestCase() throws Exception {
        String path = "D:\\AppCrawler\\src\\test\\java\\com\\testerhome\\appcrawler\\report\\generateClass";
        Junit5Report junit5Report = new Junit5Report();
        junit5Report.genTestCase(path);
    }

    //todo:ExecuteTest
    @Test
    void runTestCase() throws Exception {
        String namespace = "com.ceshiren.appcrawler.report.generateClass";
        Junit5Report junit5Report = new Junit5Report();
        junit5Report.runTestCase(namespace);
    }

    //todo:Console Launcher
    @Test
    void runTestCase1() throws Exception {
        //todo:带参
        String command = "java -jar junit-platform-console-standalone-1.4.0.jar";
        Process process = Runtime.getRuntime().exec(command);
        BufferedInputStream bis = new BufferedInputStream(
                process.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(bis));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

        process.waitFor();
        if (process.exitValue() != 0) {
            System.out.println("error!");
        }

        bis.close();
        br.close();
    }

}

