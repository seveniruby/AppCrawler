package com.testerhome.appcrawler.report;

import com.testerhome.appcrawler.URIElementStore;
import com.testerhome.appcrawler.data.AbstractElementStore;
import com.testerhome.appcrawler.data.SimpleElementStore;
import org.ho.yaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;

public class LoadYaml {
    public void loadYaml() throws FileNotFoundException {
        File dumpFile = new File("E://elements-test.yml");
        //SimpleElementStore simpleElementStore = (SimpleElementStore) Yaml.load(dumpFile);
        AbstractElementStore simpleElementStore = Yaml.loadType(dumpFile, URIElementStore.class);
        System.out.println(simpleElementStore);
        //System.out.println(simpleElementStore.elementStore());
    }
}
