package com.wboly.rpc.Client;

import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wboly.rpc.entity.RpcEntity;
import com.wboly.system.sys.util.SpringUtil;

@SuppressWarnings("rawtypes")
public abstract class BaseRpcClient implements AsyncMethodCallback {

	public abstract void close();

	protected TTransport transport;

	protected TBinaryProtocol protocol;

	private TSocket tSocket;

	public BaseRpcClient() {
		/*
		 * ApplicationContext context = new
		 * ClassPathXmlApplicationContext("spring-rpc.xml"); RpcEntity obj = (RpcEntity)
		 * context.getBean("rpcEntity");
		 */
		RpcEntity obj = (RpcEntity) SpringUtil.getInstace().getBean("rpcEntity");
		this.tSocket = new TSocket(obj.getService_ip(), Integer.parseInt(obj.getService_port()));
		this.transport = this.tSocket;
		try {
			this.transport.open();
			this.protocol = new TBinaryProtocol(transport);
		} catch (TTransportException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-rpc.xml");
		RpcEntity obj = (RpcEntity) context.getBean("rpcEntity");
		System.out.println(obj.getService_ip());
	}
}
