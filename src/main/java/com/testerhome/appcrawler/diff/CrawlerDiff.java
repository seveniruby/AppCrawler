package com.testerhome.appcrawler.diff;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import javax.imageio.ImageIO;

import com.testerhome.appcrawler.ElementInfo;
import com.testerhome.appcrawler.URIElementStore;
import org.yaml.snakeyaml.Yaml;
import scala.collection.JavaConverters;

@SuppressWarnings("unchecked")
public class CrawlerDiff {
	private static URIElementStore masterStore;
	private static URIElementStore candidateStore;
	public static void startDiff(String masterPath, String candidatePath, String reportPath) throws Exception {
		//初始化路径参数
		String master=masterPath;
		String candidate=candidatePath;
		String reportDir=reportPath;

		diffSuite(master,candidate,reportDir);
	}

	
	//Diff的主要逻辑流程
	@SuppressWarnings({"rawtypes"})
	private static void diffSuite(String master,String candidate,String reportDir) throws Exception{
		Report report = new Report();
		report.setMaster(master);
		report.setCandidate(candidate);
		report.setReportDir(reportDir);
		Map<String,Map<String,Map<String,List<Map<String,String>>>>> checkRes= new HashMap<String,Map<String,Map<String,List<Map<String,String>>>>>();
		
		//创建输出文件diff.yml
		String strPath = report.getReportDir();  
		File file = new File(strPath);  
		if(!file.exists()) file.mkdirs();
		file = new File(strPath+"\\diff.yml");
		file.createNewFile();

		//获取两个elementStore
		DataObject dataObject = new DataObject();
		masterStore = dataObject.fromYaml(report.getMaster());
		candidateStore = dataObject.fromYaml(report.getCandidate());
		List<ElementInfo> masterList = JavaConverters.seqAsJavaList(masterStore.elementStoreMap().values().toList());
		List<ElementInfo> candidateList= JavaConverters.seqAsJavaList(candidateStore.elementStoreMap().values().toList());

		//遍历脚本中涉及界面的URL
		List<String> URLs = new ArrayList();
		URLs.addAll(getURLs(masterList));
		URLs.addAll(getURLs(candidateList));
		URLs = removeDuplicate(URLs);
		
		//获取两组界面Suite信息
		Map<String,List<String>> masterSuites = getSuites(JavaConverters.mapAsJavaMap(masterStore.elementStoreMap()),URLs);
		Map<String,List<String>> candidateSuites = getSuites(JavaConverters.mapAsJavaMap(candidateStore.elementStoreMap()),URLs);

		//对每一个界面展开对比
		Iterator<String> urlHead = URLs.iterator();
		while(urlHead.hasNext())
		{
			Map<String,List<Map<String,String>>> falseList = new HashMap<String,List<Map<String,String>>>();
			Map<String,List<Map<String,String>>> trueList = new HashMap<String,List<Map<String,String>>>();		
			List<String> keys = new ArrayList();
			
			//获取url对应的keys
			String url = urlHead.next();
			keys.addAll(masterSuites.get(url));
			keys.addAll(candidateSuites.get(url));
			keys = removeDuplicate(keys);
	
			//针对每个key进行遍历
			Iterator<String> keyHead = keys.iterator();
			while(keyHead.hasNext()){
				String key = keyHead.next();
				List<Map<String,String>> checkOut= new ArrayList<Map<String,String>>();

				ElementInfo masterInfo = JavaConverters.mapAsJavaMap(masterStore.elementStoreMap()).get(key);
				ElementInfo candidateInfo = JavaConverters.mapAsJavaMap(candidateStore.elementStoreMap()).get(key);

				String mDom = masterInfo==null ? "" : masterInfo.resDom();
				String cDom = candidateInfo==null ? "" : candidateInfo.resDom();

				Map<String,String> temp = new HashMap<String,String>();
				temp = XPathUtil.checkDom(mDom,cDom,key);
				checkOut.add(temp);
				Map<String,String> temp2 = new HashMap<String,String>();

				temp2.put("mResImg", masterInfo == null ? "" : masterInfo.getResImg());
				temp2.put("mReqImg", masterInfo == null ? "" : masterInfo.getReqImg());

				temp2.put("cResImg", candidateInfo == null ? "" : candidateInfo.getResImg());
				temp2.put("cReqImg", candidateInfo == null ? "" : candidateInfo.getReqImg());

				checkOut.add(0,temp2);
				if(temp.isEmpty()) trueList.put(key,checkOut);
				else falseList.put(key,checkOut);
			}
			Map<String,Map<String,List<Map<String,String>>>> tMap = new HashMap<String,Map<String,List<Map<String,String>>>>();
			tMap.put("diff", falseList);
			tMap.put("same", trueList);
			checkRes.put(url, tMap);
		}
		
		File file2 = new File(strPath+"\\diff.yml");
		Writer out = new FileWriter(file2);
		Yaml ya = new Yaml();
		ya.dump(checkRes, out);
	}
	
	//获得所有界面URL
	private static List<String> getURLs (List<ElementInfo> tempList) {
		List<String> URLs = new ArrayList<String>();
		Iterator<ElementInfo> einfo = tempList.iterator();
		while(einfo.hasNext()){
			ElementInfo next = einfo.next();
			if(!next.element().getUrl().isEmpty()) URLs.add(next.element().getUrl());
		}
		URLs = removeDuplicate(URLs);
		return URLs;
	}

	//获得所有界面和其对应的用例key列表
	@SuppressWarnings("rawtypes")
	private static Map<String,List<String>> getSuites (Map<String,ElementInfo> elementStroe, List<String> URLs) {
		Map<String,List<String>> suites = new HashMap<String, List<String>>();
		Iterator<String> urlHead = URLs.iterator();
		while(urlHead.hasNext()){
			String url = urlHead.next();
			List<String> keys = new ArrayList<String>();
			Iterator<?> storeHead = elementStroe.entrySet().iterator();
			while(storeHead.hasNext()){
				Map.Entry store = (Map.Entry) storeHead.next();
				ElementInfo info = (ElementInfo)store.getValue();
				if(info.element().getUrl().equals(url)){
					String key = store.getKey().toString();
					keys.add(key);
					//elementStroe.remove(key);
				}
			}
			suites.put(url, keys);
		}
		return suites;
	}

	private static void drawSquare(String path,int x,int y,int len,int hei) throws IOException
	{
		BufferedImage image = ImageIO.read(new File(path));
		Graphics g = image.getGraphics();
		g.setColor(Color.RED);//画笔颜色
		g.drawRect(x, y, len, hei);//矩形框(原点x坐标，原点y坐标，矩形的长，矩形的宽)
		FileOutputStream out = new FileOutputStream("path");//输出图片的地址
		ImageIO.write(image, "jpeg", out);
	}
	
	//List去重
	private static List<String> removeDuplicate(List<String> list) {
		HashSet<String> h = new HashSet<String>(list);
		list.clear();
		list.addAll(h);
		return list;
	}
}
