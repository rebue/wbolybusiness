package com.wboly.modules.entity.area;

import java.util.ArrayList;
import java.util.List;

public class AllAreaEntity {
	
	private String provinceName,cityName,areaName,streetName;
	
	private int provinceId,cityId,areaId,streetId;
	
	private List<AllAreaEntity> listNodes = new ArrayList<AllAreaEntity>();

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

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
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

	public int getStreetId() {
		return streetId;
	}

	public void setStreetId(int streetId) {
		this.streetId = streetId;
	}

	public List<AllAreaEntity> getListNodes() {
		return listNodes;
	}

	public void setListNodes(List<AllAreaEntity> listNodes) {
		this.listNodes = listNodes;
	}

	public AllAreaEntity(String provinceName, String cityName, String areaName, String streetName, int provinceId,
			int cityId, int areaId, int streetId, List<AllAreaEntity> listNodes) {
		this.provinceName = provinceName;
		this.cityName = cityName;
		this.areaName = areaName;
		this.streetName = streetName;
		this.provinceId = provinceId;
		this.cityId = cityId;
		this.areaId = areaId;
		this.streetId = streetId;
		this.listNodes = listNodes;
	}
	
	public AllAreaEntity() {
	}

	@Override
	public String toString() {
		return "AllAreaEntity [provinceName=" + provinceName + ", cityName=" + cityName + ", areaName=" + areaName
				+ ", streetName=" + streetName + ", provinceId=" + provinceId + ", cityId=" + cityId + ", areaId="
				+ areaId + ", streetId=" + streetId + ", listNodes=" + listNodes + "]";
	}
	
}
