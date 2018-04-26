package com.wboly.rpc.Client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import com.wboly.rpc.entity.MyOrderNoEntity;
import com.wboly.rpc.entity.RpcEntity;
import com.wboly.rpc.service.MyorderNoService;
import com.wboly.system.sys.util.SpringUtil;

/**
 * 我的订单客户端
 * 
 * @author dwh
 *
 */
public class MyOrderRPCClient extends BaseRpcClient {

	private TMultiplexedProtocol tMultiplexedProtocol;

	public MyorderNoService.Client client;

	// private TTransport transport;

	@Override
	public void close() {
		/* super.transport.close(); */
		transport.close();
	}

	public MyOrderRPCClient() {
		RpcEntity obj = (RpcEntity) SpringUtil.getInstace().getBean("rpcEntity");
		transport = new TFramedTransport(
				new TSocket(obj.getService_ip(), Integer.parseInt(obj.getService_port()), 100 * 1000));
		TProtocol protocol = new TCompactProtocol(transport);
		TMultiplexedProtocol tmp = new TMultiplexedProtocol(protocol, "MyorderNoService");
		client = new MyorderNoService.Client(tmp);
		try {
			transport.open();
		} catch (TTransportException e) {
			e.printStackTrace();
		}
		/*
		 * try { ApplicationContext context = new
		 * ClassPathXmlApplicationContext("spring-rpc.xml"); RpcEntity obj = (RpcEntity)
		 * context.getBean("rpcEntity"); this.transport = new
		 * TSocket(obj.getService_ip(), Integer.parseInt(obj.getService_port()));
		 * transport.open(); TBinaryProtocol protocol = new
		 * TBinaryProtocol(super.transport);
		 * 
		 * TMultiplexedProtocol mp1 = new TMultiplexedProtocol(protocol,
		 * "MyorderNoService"); MyorderNoService.Client client = new
		 * MyorderNoService.Client(mp1); this.client = client; } catch (TException x) {
		 * x.printStackTrace(); }
		 */
	}

	public TMultiplexedProtocol gettMultiplexedProtocol() {
		return tMultiplexedProtocol;
	}

	public static void main(String[] args) {
		MyOrderRPCClient myOrderClient = new MyOrderRPCClient();
		MyOrderNoEntity entity = new MyOrderNoEntity();
		try {
			myOrderClient.client.myOrder(entity);
			myOrderClient.close();
		} catch (TException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onComplete(Object arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(Exception arg0) {
		// TODO Auto-generated method stub

	}

}
