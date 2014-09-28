package com.climate.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class NewsArticle {
	public String content;
	public String title;
	public String short_url;
	public String expanded_url;
	public Long downloaded_time;
	public Set<String> tweets;
	
	public NewsArticle(String title, String content, String short_url) 
	{
		this.content = content;
		this.title = title;
		this.short_url = short_url;
		this.expanded_url = null;
		this.tweets = new HashSet<String>();
		this.downloaded_time = System.currentTimeMillis();
	}
	
	public NewsArticle(String title, String content, String short_url, String expanded_url) 
	{
		this.content = content;
		this.title = title;
		this.short_url = short_url;
		this.expanded_url = expanded_url;
		this.tweets = new HashSet<String>();
		this.downloaded_time = System.currentTimeMillis();
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getShort_url() {
		return short_url;
	}

	public void setShort_url(String short_url) {
		this.short_url = short_url;
	}

	public String getExpanded_url() {
		return expanded_url;
	}

	public void setExpanded_url(String expanded_url) {
		this.expanded_url = expanded_url;
	}

	
}

