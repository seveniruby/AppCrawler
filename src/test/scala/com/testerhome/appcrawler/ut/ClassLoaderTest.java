package com.testerhome.appcrawler.ut;

import org.junit.jupiter.api.Test;

public class ClassLoaderTest {
    @Test
    void getResourceTest(){

        System.out.println(getClass().getResource("/allure.properties").getPath());
        System.out.println(getClass().getResource("/allure.properties").getFile());
    }
}
