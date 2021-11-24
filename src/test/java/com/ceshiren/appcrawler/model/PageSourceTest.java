package com.ceshiren.appcrawler.model;

import com.ceshiren.appcrawler.utils.Log;
import com.ceshiren.appcrawler.utils.XPathUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.ceshiren.appcrawler.utils.Log.log;

class PageSourceTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void fromXML() throws IOException {
        PageSource source = new PageSource();

        String path = "src/test/scala/com/ceshiren/appcrawler/ut/miniprogram.xml";
        String content = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        Document doc = XPathUtil.toDocument(content);
        source.fromDocument(doc);
        log.info(source.getNodeListByKey("(//*[@package!=''])[1]").headOption().get().get("package"));
    }

    @Test
    void fromJSON() {
    }

    @Test
    void getNodeListByKey() {
    }

    @Test
    void demo() {
        PageSource page = new PageSource();
        Log.setLogFilePath("/tmp/1.log");
        log.trace("trace");
        log.debug("debug");
        log.info("info");
        page.demo();
    }
}