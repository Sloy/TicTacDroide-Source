package com.sloy.tictacdroide.components;

import org.json.JSONException;
import org.json.JSONObject;

public class Theme {
	
	public static final String URL_MARKET = "market://details?id=";
	public static final String URL_DIRECT = "http://dl.dropbox.com/u/1587994/Repo/";

	private String name;
	
	private String author;

	private String pname;
	
	private Integer downloads;
	
	private Boolean market;

	private boolean visible;
	
	

	private JSONObject json;

	public Theme(String name, String author, String pname, Boolean market, Boolean visible, Integer downloads) {
		this.name = name;
		this.author = author;
		this.downloads = downloads;
		this.pname = pname;
		this.market = market;
		this.visible = (visible==null) ? true : visible;
		this.generateJSON();
	}

	public Theme(JSONObject json) throws JSONException {
		this(json.getString("name"), json.getString("author"), json.getString("pname"), json.getBoolean("market"), json.getBoolean("visible"), json.getInt("downloads"));
	}

	private void generateJSON(){
		json = new JSONObject();
		try {
			json.put("name", getName());
			json.put("author", getAuthor());
			json.put("pname",getPackageName());
			json.put("market",hasMarket());
			json.put("downloads",getDownloads());
			json.put("visible", isVisible().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Boolean isVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	
	public Boolean hasMarket(){
		return market;
	}

	public Integer getDownloads() {
		return downloads;
	}

	public void setDownloads(Integer downloads) {
		this.downloads = downloads;
	}

	public JSONObject getJSON() {
		if (json == null)
			generateJSON();
		return json;
	}

	public String getAuthor() {
		return author;
	}

	public String getName() {
		return name;
	}

	
	public String getPackageName() {
		return pname;
	}
	
	public String getDirectLink() {
		return URL_DIRECT+getPackageName()+".apk";
	}

	public String getMarketLink() {
		return hasMarket() ? URL_MARKET+getPackageName() : "";
	}

	public String toString() {
		return getName() + " by " + getAuthor();
	}
}