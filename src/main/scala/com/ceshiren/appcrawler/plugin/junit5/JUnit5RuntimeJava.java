package com.ceshiren.appcrawler.plugin.junit5;

import com.ceshiren.appcrawler.Report;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectDirectory;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

public class JUnit5RuntimeJava extends Report {
    @Override
    public void genTestCase(String resultDir) {
        super.genTestCase(resultDir);
    }

    @Override
    public void runTestCase(String namespace) {

        //todo: https://junit.org/junit5/docs/current/user-guide/#running-tests-console-launcher
        //todo: junit的console运行方式 Runtime.getRuntime().exec("java -jar junit-platform-console-standalone-1.4.2.jar -d xxxx ")
        //todo: java -javaagent xxxx.jar  -jar xxxx.jar
        //todo: api方式
        super.runTestCase(namespace);

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectDirectory(""),
                        selectPackage("com.example.mytests")
                )
                .filters(
                        includeClassNamePatterns(".*Tests")
                )
                .build();

        Launcher launcher = LauncherFactory.create();

// Register a listener of your choice
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);

        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();


    }
}
