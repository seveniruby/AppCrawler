package com.ceshiren.appcrawler.report;

import com.ceshiren.appcrawler.plugin.report.Report;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectDirectory;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

public class Junit5Report extends Report {

    private Map map;
    private Set<String> set = new HashSet<>();
    //1.读取.yml文件中的activity
    public Junit5Report() throws Exception {
        ReadYaml readYaml = new ReadYaml();
        map = readYaml.convert2Map("E://elements.yml");
        Map mapAct = (Map)map.get("elementStore");
        for(Object activity : mapAct.keySet()){
            //截至目前，set中存储的是activity的名字，接下来开始第二步
            set.add(activity.toString().split("\\.")[3]);
        }
    }

    @Override
    public void genTestCase(String resultDir) {
        super.genTestCase(resultDir);
        for(String activityName : set){
            System.out.println(activityName);
            try {
                new GenerateClassByJavassist(activityName, resultDir);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("AllureTemplate的子类生成完毕...");
    }

    @Override
    public void runTestCase(String namespace) {
        super.runTestCase(namespace);
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectDirectory("D:\\AppCrawler\\src\\test\\java\\com\\ElementInfo\\appcrawler\\report\\generateClass"),
                        selectPackage("com.ceshiren.appcrawler.report.generateClass")
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

        // Do something with the TestExecutionSummary.
        System.out.println("开始时间：" + summary.getTimeStarted());
        System.out.println("结束时间：" + summary.getTimeFinished());
        System.out.println("失败个数：" + summary.getTotalFailureCount());
        System.out.println("成功个数：" + summary.getTestsSucceededCount());

    }

}
