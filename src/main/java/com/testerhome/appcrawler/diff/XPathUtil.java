package com.testerhome.appcrawler.diff;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import com.testerhome.appcrawler.URIElement;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

public class XPathUtil {
	
	public static List<String> xpathExpr= Arrays.asList("name", "label", "value", "resource-id", "content-desc", "class", "text", "index");
	
	public static List<String> xpathList = Arrays.asList("//*[contains(name(), 'Text')]", "//*[contains(name(), 'Image')]", "//*[contains(name(), 'Button')]");
	
	//用例对比
	public static Map<String,String> checkDom(String mDom, String cDom, String keyd) throws Exception  {
		
//		if(keyd.equals("com.xueqiu.android.雪球.Start(Start)_depth=2")){
//			System.out.println("");
//		}
		
		Map<String, URIElement> melements=getListFromDom(mDom);
		Map<String, URIElement> celements=getListFromDom(cDom);
//		if(keyd.equals("com.xueqiu.android.雪球.Start(Start)_depth=2")){
//			for (URIElement entry : celements.values()) {
//				if(entry.tag.equals("android.widget.TextView"))
//					System.out.println(entry.text);
//			}
//		}
		Map<String,String> keys = new HashMap<String,String>();
		HashSet<String> keySet = new HashSet<String>();
		boolean flag = true;
		
		if(melements==null && celements==null) 
			flag = false;
		if(flag&&melements==null)
		{
			keySet.addAll(celements.keySet());
			Iterator<String> it = keySet.iterator();  
			while (it.hasNext()) {
				String key = it.next();
				URIElement c=celements.get(key);
				keys.put("", c.toString());
				}

			flag = false;
		}
		if(flag&&celements==null)
		{
			keySet.addAll(melements.keySet());
			Iterator<String> it = keySet.iterator();  
			while (it.hasNext()) {
				String key = it.next();
				URIElement m=melements.get(key);
				keys.put(m.toString(),"" );
				}

			flag = false;
		}
		if(flag){
			keySet.addAll(melements.keySet());
			keySet.addAll(celements.keySet());
			Iterator<String> it = keySet.iterator();  
			while (it.hasNext()) {
				String key = it.next();
				URIElement m=melements.get(key);
				URIElement c=celements.get(key);
				if(m==null&&c==null) flag = false;
				else {
					if(m==null||c==null) {
						if (m==null) keys.put("", c.xpath());
						else keys.put(m.toString(), "");
					}
					else {
						if(!(m.name().equals(c.name())&&m.id().equals(c.id())&&m.xpath().equals(c.xpath())&&m.text().equals(c.text()))) {
							keys.put(m.toString(), c.toString());
						}
					}					
				}				
			} 
		}
		return keys;			
	}
	
	//从dom中获取一组元素
	private static Map<String, URIElement> getListFromDom(String dom) throws XPathExpressionException, SAXException, ParserConfigurationException, IOException{
		
		if (dom.isEmpty()) return null;
		
		List<URIElement> elements = new ArrayList<URIElement>();
		NodeList nodeList = null;
		StringReader sr = new StringReader(dom);
		InputSource is = new InputSource(sr);
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
		
		for (int x=0;x<xpathList.size();x++) {
			String xpath = xpathList.get(x);
			XPath xPath = XPathFactory.newInstance().newXPath();
			XPathExpression xpathExp =xPath.compile(xpath);
			if (xpath.matches("string(.*)") ||xpath.matches(".*/@[^/]*")) {
				//nodeString=(String) xpathExp.evaluate(document, XPathConstants.STRING);
				return null;   
			} 
			else nodeList=(NodeList) xpathExp.evaluate(document, XPathConstants.NODESET);
			
			if(nodeList.getLength()>0) {
				for(int i=0;i<nodeList.getLength();i++) {
					URIElement temp = new URIElement();
					Node node = (Node) nodeList.item(i);
					Map<String,String> nodeMap= new HashMap<String,String>();
					nodeMap.put("name", "");
					nodeMap.put("value", "");
					nodeMap.put("label", "");
					nodeMap.put("x", "0");
					nodeMap.put("y", "0");
					nodeMap.put("width", "0");
					nodeMap.put("height", "0");
					nodeMap.put("name()", node.getNodeName());
					nodeMap.put("innerText",node.getTextContent().trim());
					
					NamedNodeMap nodeAttributes = node.getAttributes();
					for(int j=0;j<nodeAttributes.getLength();j++) {
						Node attr = nodeAttributes.item(j);
						nodeMap.put(attr.getNodeName(), attr.getNodeValue());
					}
					
					if (nodeMap.containsKey("resource-id")) {
						nodeMap.put("name", nodeMap.get("resource-id"));
			            if (nodeMap.containsKey("text")) {
			            	nodeMap.put("value", nodeMap.get("text"));
			            }
			            if (nodeMap.containsKey("content-desc")) {
			            	nodeMap.put("label", nodeMap.get("content-desc"));
			            }
					}
					
					if (nodeMap.containsKey("href")) {
			            if (nodeMap.containsKey("id")) {
			            	nodeMap.put("name", nodeMap.get("id"));
			            }
			            if (nodeMap.containsKey("name")) {
			            	nodeMap.put("label", nodeMap.get("name"));
			            }
		            	nodeMap.put("value", nodeMap.get("innerText"));
					}
					List<Map<String, String>> attributesList=getAttributesFromNode(node);
					nodeMap.put("depth", String.valueOf(attributesList.size()));
					nodeMap.put("ancestor", getAncestorFromAttributes(attributesList));
					nodeMap.put("xpath", getXPathFromAttributes(attributesList));
					if((nodeMap.getOrDefault("visible", "") == "true" &&
				            nodeMap.getOrDefault("enabled", "") == "true" &&
				            nodeMap.getOrDefault("valid", "") == "true")) {
						nodeMap.put("valid", "true");
					}
					else nodeMap.put("valid", "false");

					if (!nodeMap.get("xpath").isEmpty() && nodeMap.get("value").toString().length()<50)
					{
						temp.tag_$eq(nodeMap.getOrDefault("name()", ""));
						temp.id_$eq(nodeMap.getOrDefault("name", ""));
						temp.name_$eq(nodeMap.getOrDefault("label", ""));
						temp.text_$eq(nodeMap.getOrDefault("value", ""));
						temp.instance_$eq(nodeMap.getOrDefault("instance", ""));
						temp.depth_$eq(nodeMap.getOrDefault("depth", ""));
						temp.xpath_$eq(nodeMap.getOrDefault("xpath", ""));
						if(nodeMap.containsKey("bounds")){
				        	String[] rect=nodeMap.get("bounds").toString().split(",");
							temp.x_$eq(Integer.parseInt(rect[0].replace("[","")));
							temp.y_$eq(Integer.parseInt(rect[1].split("\\]")[0]));
							temp.width_$eq(Integer.parseInt(rect[1].split("\\[")[1]));
							temp.height_$eq(Integer.parseInt(rect[2].replace("]","")));
				          }
						temp.ancestor_$eq(nodeMap.getOrDefault("ancestor", ""));
						temp.selected_$eq(nodeMap.getOrDefault("selected", "false"));
						temp.valid_$eq(nodeMap.getOrDefault("valid", "true"));
						elements.add(temp);
					}
				}
			}
		}
		if(!elements.isEmpty())	{
			Map<String, URIElement> elementsMap = new HashMap<String, URIElement>();
			for(int i=0;i<elements.size();i++) {
				elementsMap.put(elements.get(i).xpath(), elements.get(i));
			}
			return elementsMap;
		}
		else return null;
	}
	
	//获得一个结点的所有祖先属性列表，递归
	private static List<Map<String, String>> getAttributesFromNode(Node node){
		List<Map<String, String>> attributesList =new ArrayList<Map<String, String>>();
		attributesList=getParent(attributesList,node);
		return attributesList;
	}
	
	//获得一个结点的所有祖先属性列表，递归
	private static List<Map<String, String>> getParent(List<Map<String, String>> attributesList,Node node) {
		if (node.hasAttributes()) {
			NamedNodeMap attributes = node.getAttributes();
			Map<String, String> attributeMap = new HashMap<String,String>();
			for(int i=0;i<attributes.getLength();i++) {
				Node temp = (Node) attributes.item(i);
				attributeMap.put(temp.getNodeName(), temp.getNodeValue());
			}
			attributeMap.put("name()", node.getNodeName());
			attributeMap.put("innerText", node.getTextContent().trim());
			attributesList.add(attributeMap);
		}
		if (node.getParentNode() != null) {
			attributesList=getParent(attributesList,node.getParentNode());
	      }
		return attributesList;
	}
	
	//从属性列表中获取一个元素的Ancestor属性
	private static String getAncestorFromAttributes(List<Map<String, String>> attributesList) {
		List<String> idList=new ArrayList<String>();
		List<String> tagList=new ArrayList<String>();
		String idString = "";
		String tagString = "";
		Iterator<Map<String, String>> attributesHead = attributesList.iterator();
		while(attributesHead.hasNext()){
			String id="";
			Map<String, String> attributes = attributesHead.next();
			if(attributes.containsKey("name")) id=attributes.get("name");
			String resourceid="";
			if(attributes.containsKey("resource-id")) resourceid=attributes.get("resource-id");
			String[] idSplit= resourceid.split("/");
			id=id+idSplit[idSplit.length-1];
			if(!id.isEmpty()) idList.add(id);
			if(attributes.containsKey("name()")) tagList.add(attributes.get("name()"));
		}
		if (idList.size()>0) {
			idString=idString+idList.get(0);
			for(int i=1;i<idList.size();i++) idString=idString+"."+idList.get(i);
		}
		if (tagList.size()>0) {
			tagString=tagString+tagList.get(0);
			for(int i=1;i<idList.size();i++) tagString=tagString+"/"+tagList.get(i);
		}
		if(tagString.isEmpty()) return idString;
		else {
			if(idString.isEmpty()) return tagString;
			else return idString+"_"+tagString;
		}
	}
	
	//确定一个元素的xpath属性
	@SuppressWarnings({ "rawtypes", "unused"})
	private static String getXPathFromAttributes(List<Map<String, String>> attributes) {
		int index = attributes.size();
		List<String> xpath= new ArrayList<String>();
		Iterator<Map<String, String>> attributesHead = attributes.iterator();
		while(attributesHead.hasNext()){
			Map<String, String> attributeMap = attributesHead.next();
			index-=1;
			for(int i=0;i<xpathExpr.size();i++) {
				if(attributeMap.get(xpathExpr.get(i))==null) 
					{attributeMap.remove(xpathExpr.get(i));}
				else {
					if(attributeMap.containsKey("path"))
						attributeMap.remove("path");
				}
			}
			if (attributeMap.get("name") == attributeMap.get("label"))  
				attributeMap.remove("name");
			if (attributeMap.get("content-desc") == attributeMap.get("resource-id")) 
				attributeMap.remove("resource-id");
			String tag = "";
			if(xpathExpr.contains("name()")&&!attributeMap.get("name()").isEmpty()) 
				tag=attributeMap.get("name()");
		    else tag="*";
			Iterator<?> head = attributeMap.entrySet().iterator();
			String xpathAttributes = "";
			while(head.hasNext()){
				String temp = "";
				Map.Entry attribut = (Map.Entry) head.next();
				String key = attribut.getKey().toString();
				String value = attribut.getValue().toString();
				boolean flag = true;
				if (key.equals("name()")) {temp="";flag=false;}
				if (key.equals("innerText")) {
					if(xpathExpr.contains("innerText") && index==0 ){
						temp="contains(text()," + "'" + value.trim().substring(0, 19).replace("\"", "\\\"") + "')";
			            }else{
			            	temp="";
			            }
					flag=false;
				}
				if (key.equals("name")) {if (value.length()>50) temp="";flag=false;}
				if (key.equals("text")) {
					if (attributeMap.get("name()").contains("Button")==false && value.length()>10)
					temp="";
					flag=false;
	          }
				if(flag) {
					if(xpathExpr.contains(key)&& !value.isEmpty()) temp="@"+key+"='"+value.replace("\"", "\\\"")+ "'";
				}
				if (!temp.isEmpty()) {
					if(xpathAttributes.isEmpty()) 
						xpathAttributes=temp;
					else {
						xpathAttributes=xpathAttributes+" and "+temp;
					}
				}
			}
			if(!xpathAttributes.isEmpty()) {
				String temp ="//"+tag+"["+xpathAttributes+"]";
				xpath.add(temp);
			}				
		}
//		if(!(xpath==null)&&xpath.size()>1) xpath.remove(0);
//		boolean shortXPath=false;
//		int k=0;
//		for(int i=0;i<xpath.size();i++) {
//			if(shortXPath==false){
//				if(xpath.get(i).contains(" and ")) k++;
//					if(k>2) shortXPath=true;
//				}
//			else {
//				xpath.remove(i);
//				i--;
//			}
//		}
		if(xpath==null) return "";
		else return xpath.toString();
	}
}
