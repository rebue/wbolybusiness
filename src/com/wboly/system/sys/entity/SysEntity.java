package com.wboly.system.sys.entity;

import java.io.Serializable;

public class SysEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int start;//起始行数
	private int limit;//分页页大小
	
	public int getStart() {
		return start;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setStart(int start) {
		this.start = start;
	}
}
