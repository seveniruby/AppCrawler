package com.testerhome.appcrawler;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

import scala.Predef;
import scala.Tuple2;
import scala.collection.JavaConverters;
import scala.collection.JavaConverters$;
import scala.collection.immutable.Map;
class SuiteToClassTest {

    @Test
    void genTestCaseClass() {
        HashMap<String, Object> map=new HashMap<>();
        map.put("uri", "xxxxxx");
        map.put("name", "demo");

        SuiteToClass$.MODULE$.genTestCaseClass2(
                "A",
                "com.testerhome.appcrawler.TemplateTestCase",
                map,
                "/tmp/suitetoclass");

        SuiteToClass$.MODULE$.genTestCaseClass2(
                "B",
                "com.testerhome.appcrawler.TemplateTestCase",
                map,
                "/tmp/suitetoclass");
    }
}