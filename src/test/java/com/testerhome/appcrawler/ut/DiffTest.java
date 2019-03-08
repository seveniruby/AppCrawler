package com.testerhome.appcrawler.ut;

import com.testerhome.appcrawler.AppCrawler;
import com.testerhome.appcrawler.diff.CrawlerDiff;
import org.junit.jupiter.api.Test;

public class DiffTest {

    @Test
    public void testDiff(){

        AppCrawler.main(new String[]{
                "--report","./eles/report",
                "--master","./eles/elements1.yml",
                "--candidate","./eles/elements2.yml"
        });
    }
}
