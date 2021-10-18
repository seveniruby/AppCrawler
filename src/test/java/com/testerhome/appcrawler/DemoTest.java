package com.ceshiren.appcrawler;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class DemoTest {

    public String dom;


    @Test
    public void hello() {
        System.out.println("hello");

    }

    @Test
    public void report() {

        //AppCrawler.main();
    }

    @TestFactory
    Collection<DynamicTest> dynamicTestsFromCollection() {
        return Arrays.asList(
                dynamicTest("1st dynamic test", () -> assertTrue(false)),
                dynamicTest("2nd dynamic test", () -> assertEquals(4, 3))
        );
    }
}