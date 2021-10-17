package com.ceshiren.appcrawler.report;

import com.ceshiren.appcrawler.URIElementStore;
import com.ceshiren.appcrawler.data.AbstractElementStore;
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
