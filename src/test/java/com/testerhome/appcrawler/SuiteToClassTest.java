package com.testerhome.appcrawler;

import com.testerhome.appcrawler.plugin.scalatest.SuiteToClass$;
import javassist.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;


class SuiteToClassTest {

    @Test
    void genTestCaseClass() {
        HashMap<String, Object> map=new HashMap<>();
        map.put("uri", "xxxxxx");
        map.put("name", "demo");

        SuiteToClass$.MODULE$.genTestCaseClass2(
                "A",
                "com.testerhome.appcrawler.plugin.scalatest.TemplateTestCase",
                map,
                "/tmp/suitetoclass");

        SuiteToClass$.MODULE$.genTestCaseClass2(
                "",
                "com.testerhome.appcrawler.plugin.scalatest.TemplateTestCase",
                map,
                "/tmp/suitetoclass");
    }


    @Test
    void genClass() throws CannotCompileException, IOException {
        ClassPool pool=ClassPool.getDefault();
        CtClass clazz=pool.getOrNull("com.testerhome.appcrawler.plugin.allure.AlllureTemplate");
        System.out.println(clazz);

        CtClass a=pool.makeClass("AA4Test");

        CtConstructor init=new CtConstructor(null, a);
        init.setBody("System.out.println(\"AAAA\");");

        a.setSuperclass(clazz);

        Arrays.stream(a.getMethods()).forEach(m->{
            System.out.println(m.getName());
            System.out.println(m.getMethodInfo().getDescriptor());
            System.out.println(m.getMethodInfo().getName());
            System.out.println(m.getMethodInfo().getAttributes());
            try {
                System.out.println(m.getAnnotations());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        });
        //a.addConstructor(init);
        a.writeFile("/tmp/aa");



    }
}