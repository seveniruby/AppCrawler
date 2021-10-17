package com.ceshiren.appcrawler.ut;

import com.ceshiren.appcrawler.AppCrawler;
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
