package com.testerhome.appcrawler.report;

import com.testerhome.appcrawler.AppCrawler;
import com.testerhome.appcrawler.CommonLog;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import scala.App;

import java.io.*;
import java.util.Properties;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.*;

public class MvnReplace {

    public static void setPro() throws Exception {

        System.out.println("setpro");
        Properties pro = new Properties();
        pro.load(MvnReplace.class.getResourceAsStream("/allure.properties"));

        if(AppCrawler.crawler().conf().resultDir().isEmpty()) {
            AppCrawler.crawler().conf().resultDir_$eq(".");
        }
        System.out.println(AppCrawler.crawler().conf().resultDir());
        pro.setProperty("allure.results.directory", AppCrawler.crawler().conf().resultDir() + "/allure-results");
        FileOutputStream out = new FileOutputStream(MvnReplace.class.getResource("/allure.properties").getPath());
        pro.store(out, "new file");

    }

    public static void runTest() throws Exception {
        System.out.println("runtest");
        setPro();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectMethod("com.testerhome.appcrawler.report.AllureTest", "dynamicTestsExample")
                )
                .build();

        Launcher launcher = LauncherFactory.create();

        // Register a listener of your choice
        TestExecutionListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);

        launcher.execute(request);
    }

    public static boolean isExist() {
        for (String path : System.getenv("path").split(File.pathSeparator)) {
            String allurePath = path + File.separator + "allure";
            if (new File(allurePath).exists())
                return true;
        }
        return false;
    }

    public static void executeCommand(String command) throws Exception {

        Runtime r = Runtime.getRuntime();
        Process p = r.exec(command);
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String inline;
        while ((inline = br.readLine()) != null) {
            System.out.println(inline);
        }
        br.close();
    }
}