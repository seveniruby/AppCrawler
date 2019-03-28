package com.testerhome.appcrawler.diff;

import scala.collection.JavaConverters;
import com.testerhome.appcrawler.ElementInfo;
import com.testerhome.appcrawler.URIElementStore;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

@SuppressWarnings("unchecked")
public class CrawlerDiff {
	public static URIElementStore masterStore;
	public static URIElementStore candidateStore;
	public static Report report = new Report();

	//Diff的主要逻辑流程
	@SuppressWarnings({"rawtypes", "resource" })
	public static void diffSuite(String master,String candidate,String reportDir)throws Exception{

		report.setMaster(master);
		report.setCandidate(candidate);
		report.setReportDir(reportDir);		
		Map<String,Map<String,List<String>>> yaml= new HashMap<String,Map<String,List<String>>>();
		
		//创建结果文件diff.yml
		String strPath = report.getReportDir();  
		File file = new File(strPath);  
		if(!file.exists()) file.mkdirs();
		file = new File(strPath+"/diff.yml");
		file.createNewFile();

		//获取两个elementStore
		DataObject dataObject = new DataObject();
		masterStore = dataObject.fromYaml(report.getMaster());
		candidateStore = dataObject.fromYaml(report.getCandidate());
		List<ElementInfo> masterList = JavaConverters.seqAsJavaList(masterStore.elementStore().values().toList());
		List<ElementInfo> candidateList= JavaConverters.seqAsJavaList(candidateStore.elementStore().values().toList());
		//遍历脚本中涉及界面的URL，检测两次遍历中涉及的界面是否一致
		List<String> masterURLs = getURLs(masterList);
		List<String> candidateURLs = getURLs(candidateList);
		
		//获取两组界面Suite信息
		Map<String,List<String>> masterSuites = getSuites(JavaConverters.mapAsJavaMap(masterStore.elementStore()),masterURLs);
		Map<String,List<String>> candidateSuites = getSuites(JavaConverters.mapAsJavaMap(candidateStore.elementStore()),candidateURLs);
		
		//找出两边主体界面是否又不同，进行处理
		List<String> URLs = new ArrayList();
		if(equalList(masterURLs,candidateURLs)) URLs=masterURLs;
		else {
			URLs.addAll(masterURLs);
			URLs.retainAll(candidateURLs);
			masterURLs.removeAll(URLs);
			candidateURLs.removeAll(URLs);
			if(!masterURLs.isEmpty()) {
				Iterator<String> urlHead = masterURLs.iterator();
				while(urlHead.hasNext()){
					Map<String,List<String>> tMap = new HashMap<String,List<String>>();
					String url = urlHead.next();
					List<String> keys= masterSuites.get(url);
					tMap.put("false", keys);
					yaml.put(url, tMap);
				}
			}
			if(!candidateURLs.isEmpty()) {
				Iterator<String> urlHead = candidateURLs.iterator();
				while(urlHead.hasNext()){
					Map<String,List<String>> tMap = new HashMap<String,List<String>>();
					String url = urlHead.next();
					List<String> keys= candidateSuites.get(url);
					tMap.put("false", keys);
					yaml.put(url, tMap);
				}
			}
		}

		
		//对每一个界面展开对比
		Iterator<String> urlHead = URLs.iterator();
		while(urlHead.hasNext()){
			List<String> falseList = new ArrayList<String>();
			List<String> trueList = new ArrayList<String>();
			String url = urlHead.next();
			List<String> masterkeys= masterSuites.get(url);
			List<String> candidatekeys= candidateSuites.get(url);
			
			List<String> keys = new ArrayList();
			if(equalList(masterkeys,candidatekeys)) keys=masterkeys;
			else {
				keys.addAll(masterkeys);
				keys.retainAll(candidatekeys);
				masterkeys.removeAll(keys);
				candidatekeys.removeAll(keys);
				if(!masterkeys.isEmpty()) falseList.addAll(masterkeys);
				if(!candidatekeys.isEmpty()) falseList.addAll(candidatekeys);
			}
	
			Iterator<String> keyHead = keys.iterator();
			while(keyHead.hasNext()){
				String key = keyHead.next();
				Boolean flag = XPathUtil.checkDom(JavaConverters.mapAsJavaMap(masterStore.elementStore()).get(key).resDom(),JavaConverters.mapAsJavaMap(candidateStore.elementStore()).get(key).resDom());
				if(flag) trueList.add(key);
				else falseList.add(key);
			}
			Map<String,List<String>> tMap = new HashMap<String,List<String>>();
			tMap.put("false", falseList);
			tMap.put("true", trueList);
			yaml.put(url, tMap);
		}
		
		//清空diff输出文件
		FileWriter fileWriter1 = new FileWriter(file);
		fileWriter1.write(""); 
		fileWriter1.close();
		//写入diff输出文件
		FileWriter fileWriter = new FileWriter(file,true);
		Iterator<?> ahead = yaml.entrySet().iterator();
		while(ahead.hasNext()){
			Map.Entry a = (Map.Entry) ahead.next();
			String akey = a.getKey().toString();
			Map<String,List<String>> avalue = (Map<String,List<String>>)a.getValue();
			fileWriter.write(akey+":\n");
			Iterator<?> bhead = avalue.entrySet().iterator();
			while(bhead.hasNext()){
				Map.Entry b = (Map.Entry) bhead.next();
				String bkey = b.getKey().toString();
				List<String> bvalue = (List<String>)b.getValue();
				fileWriter.write("  "+bkey+":\n"); 
				Iterator<String> c = bvalue.iterator();
				while(c.hasNext()){
					String key = c.next();
					fileWriter.write("    - "+key+"\n");
				}
			}
		}
		fileWriter.close();
		
	}
	
	//获得所有界面URL
	public static List<String> getURLs (List<ElementInfo> tempList) {
		List<String> URLs = new ArrayList<String>();
		Iterator<ElementInfo> einfo = tempList.iterator();
		while(einfo.hasNext()){
			ElementInfo next = einfo.next();
			System.out.println(next + "=========");
			URLs.add(next.element().getUrl());
		}
		URLs = removeDuplicate(URLs);
		return URLs;
	}

	//获得所有界面和其对应的用例key列表
	@SuppressWarnings("rawtypes")
	public static Map<String,List<String>> getSuites (Map<String,ElementInfo> elementStroe, List<String> URLs) {
		Map<String,List<String>> suites = new HashMap();
		Iterator<String> urlHead = URLs.iterator();
		while(urlHead.hasNext()){
			String url = urlHead.next();
			//Suite suite = new Suite();
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

	//判断List是否相等
	public static boolean equalList(List<String> list1, List<String> list2) {
		if (list1.size() != list2.size())
			return false;
		for (Object object : list1) {
			if (!list2.contains(object))
				return false;
		}
		return true;
	}

	//List去重
	public static List<String> removeDuplicate(List<String> list) {
		HashSet<String> h = new HashSet<String>(list);
		list.clear();
		list.addAll(h);
		return list;
	}
}
