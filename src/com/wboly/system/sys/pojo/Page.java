package com.wboly.system.sys.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class Page {

	private int limit = 0;
	@SuppressWarnings("rawtypes")
	private List Rows;
	private int Total;
	private int start=0;
	private int pageNo=1;

	/**
	 * Web  jquery_ui获取分页数据
	 * @param request
	 */
	@SuppressWarnings("rawtypes")
	public Page(HttpServletRequest request){
		
		//注入行数
		try {
			String limit =request.getParameter("limit");
			if(limit!=null&&!"".equals(limit)){
				this.limit=Integer.parseInt(limit.trim());
			}else{
				this.limit=10;
			}
		} catch (NumberFormatException e) {
			this.limit=10;
		}
		
		try {
			String pageNo=request.getParameter("pageNo");
			if(pageNo!=null&&!"".equals(pageNo)){
				this.pageNo=Integer.parseInt(pageNo.trim());
			}else{
				this.pageNo=1;
			}
		} catch (NumberFormatException e) {
			this.pageNo=1;
		}
		
		this.start=(this.pageNo-1)*this.limit;
		//注入总数
		this.Total=0;
		//注入
		this.Rows=new ArrayList();
	}

	/**
	 * 构造函数
	 * @param limit
	 * @param start
	 * @param total
	 * @param rows
	 */
	@SuppressWarnings("rawtypes")
	public Page(int limit,int pageNo,int total,List rows){
		this.limit=limit;
		this.pageNo=pageNo;
		this.Total=total;
		this.Rows=rows;
	}
	
	public Page(){}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@SuppressWarnings("rawtypes")
	public List getRows() {
		return Rows;
	}

	@SuppressWarnings("rawtypes")
	public void setRows(List rows) {
		Rows = rows;
	}

	public int getTotal() {
		return Total;
	}

	public void setTotal(int total) {
		Total = total;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}
}
