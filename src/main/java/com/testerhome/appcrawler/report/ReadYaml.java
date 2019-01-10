package com.testerhome.appcrawler.report;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.util.Map;

public class ReadYaml {

    public Map convert2Map(String path) throws Exception {
        Yaml yaml = new Yaml();
        Map map = (Map) yaml.load(new FileInputStream(path));
        return map;
    }
}
