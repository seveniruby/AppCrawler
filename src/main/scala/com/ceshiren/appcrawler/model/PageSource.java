package com.ceshiren.appcrawler.model;

import com.ceshiren.appcrawler.utils.XPathUtil;
import org.w3c.dom.Document;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;

import java.util.Arrays;

import static com.ceshiren.appcrawler.utils.Log.log;

public class PageSource {
    String raw;
    Document currentPageDom;
    private List<Map<String, Object>> nodeListAll;

    public void fromDocument(Document document) {
        this.currentPageDom = document;
    }

    public void fromXML(String xmlSource) {

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

    public List<Map<String, Object>> getNodeListAll(){
        if(nodeListAll==null) {
            nodeListAll = XPathUtil.getNodeListByXPath("//*", currentPageDom);
        }
        return nodeListAll;
    }

}
