package com.testerhome.appcrawler.report;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class LoadYamlTest {

    @Test
    void loadYaml() throws FileNotFoundException {
        LoadYaml loadYaml = new LoadYaml();
        loadYaml.loadYaml();
    }
}