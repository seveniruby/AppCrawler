package com.testerhome.appcrawler.diff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;

import com.testerhome.appcrawler.ElementInfo;
import com.testerhome.appcrawler.URIElement;
import com.testerhome.appcrawler.URIElementStore;
import com.testerhome.appcrawler.data.AbstractElementStore;
import org.yaml.snakeyaml.Yaml;
import scala.collection.JavaConverters;

public class DataObject 
{

	//对读取到的yaml文件进行格式化
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public URIElementStore fromYaml(String yamlUrl) throws FileNotFoundException{
		Yaml yaml = new Yaml();
		File dumpFile = new File(yamlUrl);
		Map<String, Map> map =(Map<String, Map>)yaml.load(new FileInputStream(dumpFile));
		Map<String, ElementInfo> eleStore = map.get("elementStore");
		URIElementStore store = new URIElementStore();
		Iterator iter = eleStore.entrySet().iterator(); 
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object val = entry.getValue();
			entry.setValue(formatElementInfo(val));
		}
		store.elementStore_$eq(JavaConverters.mapAsScalaMap(eleStore));
		return store;	
	}

	//格式化存储ElementInfo
	@SuppressWarnings({ "rawtypes", "unchecked"})
	private ElementInfo formatElementInfo(Object elemInfoStream) {
		Yaml yaml = new Yaml();
		Map<String, String> map =(Map<String, String>)yaml.load(yaml.dump(elemInfoStream));
		Iterator iter = map.entrySet().iterator(); 
		ElementInfo ei = new ElementInfo();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key=entry.getKey();
			Object val=entry.getValue();
			if(key.equals("reqDom")) {ei.reqDom_$eq(val.toString());}
			if(key.equals("resDom")) {ei.resDom_$eq(val.toString());}
			if(key.equals("reqHash")) {ei.reqHash_$eq(val.toString());}
			if(key.equals("resHash")) {ei.resHash_$eq(val.toString());}
			if(key.equals("reqImg")) {ei.reqImg_$eq(val.toString());}
			if(key.equals("resImg")) {ei.resImg_$eq(val.toString());}
			if(key.equals("clickedIndex")) {ei.clickedIndex_$eq(Integer.valueOf(val.toString()));}
			if(key.equals("action")) {
				switch (val.toString()){
					case "CLICKED" : ei.action_$eq(AbstractElementStore.Status.CLICKED);break;
					case "SKIPPED" : ei.action_$eq(AbstractElementStore.Status.SKIPPED);break;
				}
			}
			if(key.equals("element")) {
				ei.element_$eq(formatElement(val));
				}
			
		}
		return ei;
	}
	
	//格式化存储uriElement
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private URIElement formatElement(Object elemStream) {
		Yaml yaml = new Yaml();
		Map<String, String> map =(Map<String, String>)yaml.load(yaml.dump(elemStream));
		Iterator iter = map.entrySet().iterator(); 
		URIElement el = new URIElement();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key=entry.getKey();
			Object val=entry.getValue();
			if(key.equals("url")) {el.url_$eq(val.toString());}
			if(key.equals("tag")) {el.tag_$eq(val.toString());}
			if(key.equals("id")) {el.id_$eq(val.toString());}
			if(key.equals("name")) {el.name_$eq(val.toString());}
			if(key.equals("text")) {el.text_$eq(val.toString());}
			if(key.equals("instance")) {el.instance_$eq(val.toString());}
			if(key.equals("depth")) {el.depth_$eq(val.toString());}
			if(key.equals("valid")) {el.valid_$eq(val.toString());}
			if(key.equals("selected")) {el.selected_$eq(val.toString());}
			if(key.equals("xpath")) {el.xpath_$eq(val.toString());}
			if(key.equals("ancestor")) {el.ancestor_$eq(val.toString());}
			if(key.equals("x")) {el.x_$eq(Integer.valueOf(val.toString()));}
			if(key.equals("y")) {el.y_$eq(Integer.valueOf(val.toString()));}
			if(key.equals("width")) {el.width_$eq(Integer.valueOf(val.toString()));}
			if(key.equals("height")) {el.height_$eq(Integer.valueOf(val.toString()));}
			if(key.equals("action")) {el.action_$eq(val.toString());}
		}
		return el;
	}
}
