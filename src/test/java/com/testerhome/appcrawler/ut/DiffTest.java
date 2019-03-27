package com.testerhome.appcrawler.ut;

import com.testerhome.appcrawler.AppCrawler;
import com.testerhome.appcrawler.diff.CrawlerDiff;
import org.junit.jupiter.api.Test;

public class DiffTest {

    @Test
    public void testDiff(){

        AppCrawler.main(new String[]{
                "--report","/tmp/xueQiu400/reportDiff",
                "--master","/tmp/xueQiu400/20190315164643/elements.yml",
                "--candidate","/tmp/xueQiu400/20190315161950/elements.yml"
        });
    }
}
