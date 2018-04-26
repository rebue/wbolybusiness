package com.wboly.system.sys.pojo;

/**
 * 消息类
 * @author Sea
 *
 */
public class Messge {
	
	private  Boolean bool;
	private  String title;
	private  String content;
	
	public Messge( Boolean bool,String title,String content){
		this.bool=bool;
		this.title=title;
		this.content=content;
	}

	public Boolean getBool() {
		return bool;
	}

	public void setBool(Boolean bool) {
		this.bool = bool;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
