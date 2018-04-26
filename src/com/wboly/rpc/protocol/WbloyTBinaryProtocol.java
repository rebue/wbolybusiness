package com.wboly.rpc.protocol;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import com.wboly.rpc.pool.ConnectionManager;

public class WbloyTBinaryProtocol extends TBinaryProtocol {

	public TProtocol iprot_;

	public TProtocol oprot_;

	public WbloyTBinaryProtocol(TTransport trans) {
		super(trans);
	}

	public WbloyTBinaryProtocol(ConnectionManager connectionManager) {
		this(connectionManager.getSocket());
	}

}
