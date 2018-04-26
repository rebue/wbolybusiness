package com.wboly.rpc.protocol;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolDecorator;

public class WbloyTMultiplexedProtocol extends TProtocolDecorator {
	public static final String SEPARATOR = ":";
	public String serviceName;

	public WbloyTMultiplexedProtocol(TProtocol protocol, String serviceName) {
		super(protocol);
		serviceName = this.serviceName;
	}

	public WbloyTMultiplexedProtocol(WbloyTBinaryProtocol wbloyTBinaryProtocol) {
		super(wbloyTBinaryProtocol);
	}

	public WbloyTMultiplexedProtocol() {
		super(null);
	}

	public void writeMessageBegin(TMessage tMessage) throws TException {
		if ((tMessage.type == 1) || (tMessage.type == 4)) {
			super.writeMessageBegin(
					new TMessage(this.serviceName + ":" + tMessage.name, tMessage.type, tMessage.seqid));
		} else {
			super.writeMessageBegin(tMessage);
		}
	}

}
