package com.ceshiren.appcrawler.model;

import com.ceshiren.appcrawler.utils.XPathUtil;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.w3c.dom.Document;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;

import java.util.Arrays;

import static com.ceshiren.appcrawler.utils.Log.log;

public class PageSource {
    private String xmlSource;
    private Document currentPageDom;
    private List<Map<String, Object>> nodeListAll;
    private XmlMapper mapper = new XmlMapper();

    public static PageSource getPagefromDocument(Document document) {
        PageSource pageSource = new PageSource();
        pageSource.fromDocument(document);
        return pageSource;
    }

    public static PageSource getPagefromXML(String xmlSource) {
        PageSource pageSource = null;
        try {
            pageSource = new PageSource();
            pageSource.fromXML(xmlSource);
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return pageSource;
    }


    public void fromDocument(Document document) {
        currentPageDom = document;
    }

    public void fromXML(String xmlSource) {
        this.xmlSource = xmlSource;
        this.currentPageDom=XPathUtil.toDocument(xmlSource);
    }

    public String toXML() {
        return XPathUtil.toPrettyXML(xmlSource);
    }

    public void fromJSON(String jsonSource) {

    }


    public void demo() {
        log.trace("page source java demo");
    }

    public List<Map<String, Object>> getNodeListByKey(String key) {
        String[] regexPatternList = new String[]{"/.*", "\\(.*", "string\\(/.*\\)"};
        if (Arrays.stream(regexPatternList).anyMatch(key::matches)) {
            return XPathUtil.getNodeListByXPath(key, currentPageDom);
        } else if (key.startsWith("^") || key.contains(".*")) {
            return getNodeListAll().filter(node -> node.get("name").toString().matches(key) ||
                    node.get("label").toString().matches(key) ||
                    node.get("value").toString().matches(key));
        } else {
            return getNodeListAll().filter(node -> node.get("name").toString().contains(key) ||
                    node.get("label").toString().contains(key) ||
                    node.get("value").toString().contains(key));
        }
    }

    public List<Map<String, Object>> getNodeListAll() {
        if (nodeListAll == null) {
            nodeListAll = XPathUtil.getNodeListByXPath("//*", currentPageDom);
        }
        return nodeListAll;
    }

}
