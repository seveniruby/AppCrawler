package com.ceshiren.appcrawler.ut;

import com.ceshiren.appcrawler.report.ReadYaml;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Junit5TemplateTest {
    Map map;
    Set<String> set = new HashSet<>();
    //1.读取.yml文件中的activity
    public Junit5TemplateTest() throws Exception {
        ReadYaml readYaml = new ReadYaml();
        map = readYaml.convert2Map("E://elements.yml");
        Map mapAct = (Map)map.get("elementStore");
        for(Object activity : mapAct.keySet()){
            //截至目前，set中存储的是activity的名字，接下来开始第二步
            set.add(activity.toString().split("\\.")[3]);
        }
    }

    //2.利用javassist动态生成extends AllureTemplate的子类，pageName作为参数传入
    @Test
    public void test() throws Exception {
        for(String activityName : set){
            System.out.println(activityName);
            new GenerateClassTest(activityName);
        }
        System.out.println("AllureTemplate的子类生成完毕...");
    }
}
