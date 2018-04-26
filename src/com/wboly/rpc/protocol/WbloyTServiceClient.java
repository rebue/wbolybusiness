package com.wboly.rpc.protocol;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;

public class WbloyTServiceClient extends TServiceClient {

	public TProtocol iprot_;
	public TProtocol oprot_;

	public WbloyTServiceClient(TProtocol iprot, TProtocol oprot) {
		super(iprot, oprot);

	}

	public WbloyTServiceClient(TProtocol prot) {
		this(prot, prot);
	}

	public WbloyTServiceClient() {
		super(null);
	}

}
