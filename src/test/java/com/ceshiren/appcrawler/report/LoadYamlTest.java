package com.ceshiren.appcrawler.report;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

class LoadYamlTest {

    @Test
    void loadYaml() throws FileNotFoundException {
        LoadYaml loadYaml = new LoadYaml();
        loadYaml.loadYaml();
    }
}