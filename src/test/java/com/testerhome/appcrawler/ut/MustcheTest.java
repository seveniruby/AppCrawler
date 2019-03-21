package com.testerhome.appcrawler.ut;


import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class MustcheTest {

    @Test
    void demo() throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("template.mustache");
        mustache.execute(new PrintWriter(System.out), new Context()).flush();
    }

    @Test
    void path(){
        System.out.println(System.getenv("PATH"));

        for(String path :System.getenv("PATH").split(File.pathSeparator)){
            String allurePath=path+File.separator+"allure";
            System.out.println(allurePath);
            System.out.println(new File(allurePath).exists());
        }


    }

    @Test
    void exec() throws IOException {
        Runtime.getRuntime().exec("allure2");
    }
}
