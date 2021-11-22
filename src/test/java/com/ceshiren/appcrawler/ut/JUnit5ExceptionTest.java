package com.ceshiren.appcrawler.ut;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JUnit5ExceptionTest {

    static int index=0;
    @BeforeAll
    static void beforeAll(){
        System.out.println("before all");
    }
    @BeforeEach
    void beforeEach() throws Exception {
        System.out.println("before each");
        System.out.println(this);
        System.out.println(index);
        if(index==2) {
            throw new Exception("after each exception");
        }
    }
    @Test
    void case1() throws Exception {
        System.out.println("case1");
        index+=1;
        System.out.println(index);
        assertTrue(true);
    }

    @Test
    void case2() throws Exception {
        System.out.println("case2");
        index+=1;
        System.out.println(index);
        throw new Exception("ddd");
    }

    @Test
    void case3() throws Exception {
        System.out.println("case3");
        index+=1;
        System.out.println(index);
        assertTrue(true);
    }

    @AfterEach
    void afterEach() throws Exception {
        System.out.println("after each");
    }

    @AfterAll
    static void afterAll(){
        System.out.println("after all");
    }
}
