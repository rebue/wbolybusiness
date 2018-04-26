package com.wboly.rpc.entity;

public class RpcEntity {

	private String service_ip;
	private String service_port;

	public RpcEntity() {

	}  

	public String getService_ip() {
		return service_ip;
	}

	public void setService_ip(String service_ip) {
		this.service_ip = service_ip;
	}

	public String getService_port() {
		return service_port;
	}

	public void setService_port(String service_port) {
		this.service_port = service_port;
	}

}
