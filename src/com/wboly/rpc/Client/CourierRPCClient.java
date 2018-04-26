package com.wboly.rpc.Client;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.wboly.rpc.entity.RpcEntity;
import com.wboly.rpc.service.CourierService;
import com.wboly.system.sys.util.SpringUtil;

public class CourierRPCClient extends BaseRpcClient {
	private TMultiplexedProtocol tMultiplexedProtocol;

	public CourierService.Client client;

	private TTransport transport;

	@Override
	public void close() {
		/*super.transport.close();*/
		transport.close();
	}

	public CourierRPCClient() {
		RpcEntity obj = (RpcEntity) SpringUtil.getInstace().getBean("rpcEntity");
		transport = new TFramedTransport(
				new TSocket(obj.getService_ip(), Integer.parseInt(obj.getService_port()), 100 * 1000));
		TProtocol protocol = new TCompactProtocol(transport);
		TMultiplexedProtocol tmp = new TMultiplexedProtocol(protocol, "CourierService");
		client = new CourierService.Client(tmp);
		try {
			transport.open();
		} catch (TTransportException e) {
			e.printStackTrace();
		}
		/*try {
			ApplicationContext context = new ClassPathXmlApplicationContext("spring-rpc.xml");
			RpcEntity obj = (RpcEntity) context.getBean("rpcEntity");
			this.transport = new TSocket(obj.getService_ip(), Integer.parseInt(obj.getService_port()));
			transport.open();
			TBinaryProtocol protocol = new TBinaryProtocol(super.transport);
			TMultiplexedProtocol mp1 = new TMultiplexedProtocol(protocol, "CourierService");
			CourierService.Client client = new CourierService.Client(mp1);
			this.client = client;
		} catch (TException x) {
			x.printStackTrace();
		}*/
	}

	public TMultiplexedProtocol gettMultiplexedProtocol() {
		return tMultiplexedProtocol;
	}

	@Override
	public void onComplete(Object arg0) {

	}

	@Override
	public void onError(Exception arg0) {

	}
}
