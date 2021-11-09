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

        String path = "/Users/seveniruby/Library/Containers/com.tencent.xinWeChat/Data/Library/Application Support/com.tencent.xinWeChat/2.0b4.0.9/c7118872a9eec663b3cc402e18d9d682/Message/MessageTemp/4a28969c5e85f3bf0a0b17f53353b049/File/20211027095159/342_DealReportActivity.tag=TextView.depth=13.id=text_area.text=请选择市.dom";
        String content = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        System.out.println(content.toString());
        Document doc = XPathUtil.toDocument(content.toString());
        source.fromDocument(doc);
        System.out.println(source.getNodeListByKey("(//*[@package!=''])[1]").headOption().get().get("package"));
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