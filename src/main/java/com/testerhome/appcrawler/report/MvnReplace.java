package com.testerhome.appcrawler.report;

import com.testerhome.appcrawler.AppCrawler;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.util.Properties;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

public class MvnReplace {

    public void runTest(){
        Properties pro = new Properties();
        pro.setProperty("allure.results.directory", AppCrawler.crawler().conf().resultDir() + "/allure-results");
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
    }
}
