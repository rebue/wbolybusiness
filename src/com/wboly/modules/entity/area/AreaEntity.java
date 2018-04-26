package com.wboly.modules.entity.area;

import java.util.ArrayList;
import java.util.List;

public class AreaEntity {
	
	private String provinceName,cityName,areaName,shopName;
	
	private int provinceId,cityId,areaId,shopId;
	
	private List<AreaEntity> listNodes = new ArrayList<AreaEntity>();
	

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	public List<AreaEntity> getListNodes() {
		return listNodes;
	}

	public void setListNodes(List<AreaEntity> listNodes) {
		this.listNodes = listNodes;
	}

	public AreaEntity() {
	}

	public AreaEntity(String provinceName, String cityName, String areaName, String shopName, int provinceId,
			int cityId, int areaId, int shopId, List<AreaEntity> listNodes) {
		this.provinceName = provinceName;
		this.cityName = cityName;
		this.areaName = areaName;
		this.shopName = shopName;
		this.provinceId = provinceId;
		this.cityId = cityId;
		this.areaId = areaId;
		this.shopId = shopId;
		this.listNodes = listNodes;
	}

	
}
