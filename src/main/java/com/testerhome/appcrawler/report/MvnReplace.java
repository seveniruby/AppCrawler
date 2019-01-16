package com.testerhome.appcrawler.report;

import com.testerhome.appcrawler.AppCrawler;
import org.junit.jupiter.api.Test;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.*;
import java.util.Properties;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

public class MvnReplace {
    public static void runTest() {
        try {
            FileInputStream is = new FileInputStream("src/main/resources/allure.properties");
            Properties pro = new Properties();
            pro.load(is);
            pro.setProperty("allure.results.directory", AppCrawler.crawler().conf().resultDir()+ "/allure-results");
            OutputStream fos = new FileOutputStream("src/main/resources/allure.properties");
            pro.store(fos,"update");
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(
                            selectPackage("com.testerhome.appcrawler.report"),
                            selectClass(AllureTest.class)
                    )
                    .filters(
                            includeClassNamePatterns(".*Tests")
                    )
                    .build();

            Launcher launcher = LauncherFactory.create();

            // Register a listener of your choice
            TestExecutionListener listener = new SummaryGeneratingListener();
            launcher.registerTestExecutionListeners(listener);

            launcher.execute(request);


            Runtime runtime = Runtime.getRuntime();

            Process process = runtime.exec("cmd /k cd " + AppCrawler.crawler().conf().resultDir() + " && allure generate -o " + "allureReport");

            if (process != null) {
                process.getOutputStream().close();
            }

            InputStream in = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String tmp = null;
            while ((tmp = br.readLine()) != null) {
                System.out.println(tmp);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
