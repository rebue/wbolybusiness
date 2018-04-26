package com.wboly.rpc.protocol;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

public abstract class WbloyBaseTProtocol extends TProtocol {

	protected WbloyBaseTProtocol(TTransport trans) {
		super(trans);
	}
}
