package com.testerhome.appcrawler.diff;

import com.testerhome.appcrawler.data.PathElementStore;

public class Report {
	private boolean showCancel=false;
	private String title="AppCrawler";
	private String master="";
	private String candidate="";
	private String reportDir="";
	private PathElementStore store=new PathElementStore();

	public PathElementStore loadResult() {
		return store;
	}
	
	public boolean getShowCancel() {
		return this.showCancel;
	}	
	
	public void setShowCancel(boolean str) {
		this.showCancel=str;
	}	
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String str) {
		this.title=str;
	}
	
	public String getMaster() {
		return this.master;
	}
	
	public void setMaster(String str) {
		this.master=str;
	}

	public String getCandidate() {
		return this.candidate;
	}	
	
	public void setCandidate(String str) {
		this.candidate=str;
	}
	
	public String getReportDir() {
		return this.reportDir;
	}
	
	public void setReportDir(String str) {
		this.reportDir=str;
	}
}
