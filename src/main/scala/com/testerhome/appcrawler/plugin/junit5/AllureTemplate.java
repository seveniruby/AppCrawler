package com.testerhome.appcrawler.plugin.junit5;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class AllureTemplate {
    @TestFactory
    Collection<DynamicTest> AllTestCases() {
        ArrayList<DynamicTest> arrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            arrayList.add(dynamicTest(String.format("testcase %s ", i), () -> {
                        assertTrue(false);
                    }
            ));
        }

        return arrayList;
    }
}