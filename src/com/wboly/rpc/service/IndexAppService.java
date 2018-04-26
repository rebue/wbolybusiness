package com.wboly.rpc.service;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TTupleProtocol;
/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.server.AbstractNonblockingServer.AsyncFrameBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wboly.rpc.entity.IndexAppEntity;
import com.wboly.rpc.protocol.WbloyTMultiplexedProtocol;

@SuppressWarnings({ "cast", "rawtypes", "serial", "unchecked","unused" })
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2016-07-14")
public class IndexAppService {

	public interface Iface {

		public Map<String, List<IndexAppEntity>> indexAppAll(IndexAppEntity entity) throws org.apache.thrift.TException;

	}

	public interface AsyncIface {

		public void indexAppAll(IndexAppEntity entity, org.apache.thrift.async.AsyncMethodCallback resultHandler)
				throws org.apache.thrift.TException;

	}

	public static class Client extends org.apache.thrift.TServiceClient implements Iface {
		public static class Factory implements org.apache.thrift.TServiceClientFactory<Client> {
			public Factory() {
			}

			public Client getClient(org.apache.thrift.protocol.TProtocol prot) {
				return new Client(prot);
			}

			public Client getClient(org.apache.thrift.protocol.TProtocol iprot,
					org.apache.thrift.protocol.TProtocol oprot) {
				return new Client(iprot, oprot);
			}
		}

		public Client(org.apache.thrift.protocol.TProtocol prot) {
			super(prot, prot);
		}

		public Client(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
			super(iprot, oprot);
		}

		public Client(WbloyTMultiplexedProtocol wbloyTMultiplexedProtocol, String serviceName) {
			this(wbloyTMultiplexedProtocol, wbloyTMultiplexedProtocol);
			this.iprot_ = (TProtocol) wbloyTMultiplexedProtocol;
			wbloyTMultiplexedProtocol.serviceName = serviceName;

		}

		public Map<String, List<IndexAppEntity>> indexAppAll(IndexAppEntity entity)
				throws org.apache.thrift.TException {
			send_indexAppAll(entity);
			return recv_indexAppAll();
		}

		public void send_indexAppAll(IndexAppEntity entity) throws org.apache.thrift.TException {
			indexAppAll_args args = new indexAppAll_args();
			args.setEntity(entity);
			sendBase("indexAppAll", args);
		}

		public Map<String, List<IndexAppEntity>> recv_indexAppAll() throws org.apache.thrift.TException {
			indexAppAll_result result = new indexAppAll_result();
			receiveBase(result, "indexAppAll");
			if (result.isSetSuccess()) {
				return result.success;
			}
			throw new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.MISSING_RESULT,
					"indexAppAll failed: unknown result");
		}

	}

	public static class AsyncClient extends org.apache.thrift.async.TAsyncClient implements AsyncIface {
		public static class Factory implements org.apache.thrift.async.TAsyncClientFactory<AsyncClient> {
			private org.apache.thrift.async.TAsyncClientManager clientManager;
			private org.apache.thrift.protocol.TProtocolFactory protocolFactory;

			public Factory(org.apache.thrift.async.TAsyncClientManager clientManager,
					org.apache.thrift.protocol.TProtocolFactory protocolFactory) {
				this.clientManager = clientManager;
				this.protocolFactory = protocolFactory;
			}

			public AsyncClient getAsyncClient(org.apache.thrift.transport.TNonblockingTransport transport) {
				return new AsyncClient(protocolFactory, clientManager, transport);
			}
		}

		public AsyncClient(org.apache.thrift.protocol.TProtocolFactory protocolFactory,
				org.apache.thrift.async.TAsyncClientManager clientManager,
				org.apache.thrift.transport.TNonblockingTransport transport) {
			super(protocolFactory, clientManager, transport);
		}

		public void indexAppAll(IndexAppEntity entity, org.apache.thrift.async.AsyncMethodCallback resultHandler)
				throws org.apache.thrift.TException {
			checkReady();
			indexAppAll_call method_call = new indexAppAll_call(entity, resultHandler, this, ___protocolFactory,
					___transport);
			this.___currentMethod = method_call;
			___manager.call(method_call);
		}

		public static class indexAppAll_call extends org.apache.thrift.async.TAsyncMethodCall {
			private IndexAppEntity entity;

			public indexAppAll_call(IndexAppEntity entity, org.apache.thrift.async.AsyncMethodCallback resultHandler,
					org.apache.thrift.async.TAsyncClient client,
					org.apache.thrift.protocol.TProtocolFactory protocolFactory,
					org.apache.thrift.transport.TNonblockingTransport transport) throws org.apache.thrift.TException {
				super(client, protocolFactory, transport, resultHandler, false);
				this.entity = entity;
			}

			public void write_args(org.apache.thrift.protocol.TProtocol prot) throws org.apache.thrift.TException {
				prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage("indexAppAll",
						org.apache.thrift.protocol.TMessageType.CALL, 0));
				indexAppAll_args args = new indexAppAll_args();
				args.setEntity(entity);
				args.write(prot);
				prot.writeMessageEnd();
			}

			public Map<String, List<IndexAppEntity>> getResult() throws org.apache.thrift.TException {
				if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
					throw new IllegalStateException("Method call not finished!");
				}
				org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(
						getFrameBuffer().array());
				org.apache.thrift.protocol.TProtocol prot = client.getProtocolFactory().getProtocol(memoryTransport);
				return (new Client(prot)).recv_indexAppAll();
			}
		}

	}

	public static class Processor<I extends Iface> extends org.apache.thrift.TBaseProcessor<I>
			implements org.apache.thrift.TProcessor {
		private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class.getName());

		public Processor(I iface) {
			super(iface, getProcessMap(
					new HashMap<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>>()));
		}

		protected Processor(I iface,
				Map<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>> processMap) {
			super(iface, getProcessMap(processMap));
		}

		private static <I extends Iface> Map<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>> getProcessMap(
				Map<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>> processMap) {
			processMap.put("indexAppAll", new indexAppAll());
			return processMap;
		}

		public static class indexAppAll<I extends Iface>
				extends org.apache.thrift.ProcessFunction<I, indexAppAll_args> {
			public indexAppAll() {
				super("indexAppAll");
			}

			public indexAppAll_args getEmptyArgsInstance() {
				return new indexAppAll_args();
			}

			protected boolean isOneway() {
				return false;
			}

			public indexAppAll_result getResult(I iface, indexAppAll_args args) throws org.apache.thrift.TException {
				indexAppAll_result result = new indexAppAll_result();
				result.success = iface.indexAppAll(args.entity);
				return result;
			}
		}

	}

	public static class AsyncProcessor<I extends AsyncIface> extends org.apache.thrift.TBaseAsyncProcessor<I> {
		private static final Logger LOGGER = LoggerFactory.getLogger(AsyncProcessor.class.getName());

		public AsyncProcessor(I iface) {
			super(iface, getProcessMap(
					new HashMap<String, org.apache.thrift.AsyncProcessFunction<I, ? extends org.apache.thrift.TBase, ?>>()));
		}

		protected AsyncProcessor(I iface,
				Map<String, org.apache.thrift.AsyncProcessFunction<I, ? extends org.apache.thrift.TBase, ?>> processMap) {
			super(iface, getProcessMap(processMap));
		}

		private static <I extends AsyncIface> Map<String, org.apache.thrift.AsyncProcessFunction<I, ? extends org.apache.thrift.TBase, ?>> getProcessMap(
				Map<String, org.apache.thrift.AsyncProcessFunction<I, ? extends org.apache.thrift.TBase, ?>> processMap) {
			processMap.put("indexAppAll", new indexAppAll());
			return processMap;
		}

		public static class indexAppAll<I extends AsyncIface>
				extends org.apache.thrift.AsyncProcessFunction<I, indexAppAll_args, Map<String, List<IndexAppEntity>>> {
			public indexAppAll() {
				super("indexAppAll");
			}

			public indexAppAll_args getEmptyArgsInstance() {
				return new indexAppAll_args();
			}

			public AsyncMethodCallback<Map<String, List<IndexAppEntity>>> getResultHandler(final AsyncFrameBuffer fb,
					final int seqid) {
				final org.apache.thrift.AsyncProcessFunction fcall = this;
				return new AsyncMethodCallback<Map<String, List<IndexAppEntity>>>() {
					public void onComplete(Map<String, List<IndexAppEntity>> o) {
						indexAppAll_result result = new indexAppAll_result();
						result.success = o;
						try {
							fcall.sendResponse(fb, result, org.apache.thrift.protocol.TMessageType.REPLY, seqid);
							return;
						} catch (Exception e) {
							LOGGER.error("Exception writing to internal frame buffer", e);
						}
						fb.close();
					}

					public void onError(Exception e) {
						byte msgType = org.apache.thrift.protocol.TMessageType.REPLY;
						org.apache.thrift.TBase msg;
						indexAppAll_result result = new indexAppAll_result();
						{
							msgType = org.apache.thrift.protocol.TMessageType.EXCEPTION;
							msg = (org.apache.thrift.TBase) new org.apache.thrift.TApplicationException(
									org.apache.thrift.TApplicationException.INTERNAL_ERROR, e.getMessage());
						}
						try {
							fcall.sendResponse(fb, msg, msgType, seqid);
							return;
						} catch (Exception ex) {
							LOGGER.error("Exception writing to internal frame buffer", ex);
						}
						fb.close();
					}
				};
			}

			protected boolean isOneway() {
				return false;
			}

			public void start(I iface, indexAppAll_args args,
					org.apache.thrift.async.AsyncMethodCallback<Map<String, List<IndexAppEntity>>> resultHandler)
					throws TException {
				iface.indexAppAll(args.entity, resultHandler);
			}
		}

	}

	public static class indexAppAll_args implements org.apache.thrift.TBase<indexAppAll_args, indexAppAll_args._Fields>,
			java.io.Serializable, Cloneable, Comparable<indexAppAll_args> {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"indexAppAll_args");

		private static final org.apache.thrift.protocol.TField ENTITY_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"entity", org.apache.thrift.protocol.TType.STRUCT, (short) 1);

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class, new indexAppAll_argsStandardSchemeFactory());
			schemes.put(TupleScheme.class, new indexAppAll_argsTupleSchemeFactory());
		}

		public IndexAppEntity entity; // required

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			ENTITY((short) 1, "entity");

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				case 1: // ENTITY
					return ENTITY;
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		// isset id assignments
		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			tmpMap.put(_Fields.ENTITY,
					new org.apache.thrift.meta_data.FieldMetaData("entity",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT,
									IndexAppEntity.class)));
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(indexAppAll_args.class, metaDataMap);
		}

		public indexAppAll_args() {
		}

		public indexAppAll_args(IndexAppEntity entity) {
			this();
			this.entity = entity;
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public indexAppAll_args(indexAppAll_args other) {
			if (other.isSetEntity()) {
				this.entity = new IndexAppEntity(other.entity);
			}
		}

		public indexAppAll_args deepCopy() {
			return new indexAppAll_args(this);
		}

		@Override
		public void clear() {
			this.entity = null;
		}

		public IndexAppEntity getEntity() {
			return this.entity;
		}

		public indexAppAll_args setEntity(IndexAppEntity entity) {
			this.entity = entity;
			return this;
		}

		public void unsetEntity() {
			this.entity = null;
		}

		/**
		 * Returns true if field entity is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetEntity() {
			return this.entity != null;
		}

		public void setEntityIsSet(boolean value) {
			if (!value) {
				this.entity = null;
			}
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			case ENTITY:
				if (value == null) {
					unsetEntity();
				} else {
					setEntity((IndexAppEntity) value);
				}
				break;

			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			case ENTITY:
				return getEntity();

			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			case ENTITY:
				return isSetEntity();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof indexAppAll_args)
				return this.equals((indexAppAll_args) that);
			return false;
		}

		public boolean equals(indexAppAll_args that) {
			if (that == null)
				return false;

			boolean this_present_entity = true && this.isSetEntity();
			boolean that_present_entity = true && that.isSetEntity();
			if (this_present_entity || that_present_entity) {
				if (!(this_present_entity && that_present_entity))
					return false;
				if (!this.entity.equals(that.entity))
					return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			List<Object> list = new ArrayList<Object>();

			boolean present_entity = true && (isSetEntity());
			list.add(present_entity);
			if (present_entity)
				list.add(entity);

			return list.hashCode();
		}

		@Override
		public int compareTo(indexAppAll_args other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(other.getClass().getName());
			}

			int lastComparison = 0;

			lastComparison = Boolean.valueOf(isSetEntity()).compareTo(other.isSetEntity());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetEntity()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.entity, other.entity);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("indexAppAll_args(");
			boolean first = true;

			sb.append("entity:");
			if (this.entity == null) {
				sb.append("null");
			} else {
				sb.append(this.entity);
			}
			first = false;
			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
			// check for sub-struct validity
			if (entity != null) {
				entity.validate();
			}
		}

		private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class indexAppAll_argsStandardSchemeFactory implements SchemeFactory {
			public indexAppAll_argsStandardScheme getScheme() {
				return new indexAppAll_argsStandardScheme();
			}
		}

		private static class indexAppAll_argsStandardScheme extends StandardScheme<indexAppAll_args> {

			public void read(org.apache.thrift.protocol.TProtocol iprot, indexAppAll_args struct)
					throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					case 1: // ENTITY
						if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
							struct.entity = new IndexAppEntity();
							struct.entity.read(iprot);
							struct.setEntityIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
						}
						break;
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot, indexAppAll_args struct)
					throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				if (struct.entity != null) {
					oprot.writeFieldBegin(ENTITY_FIELD_DESC);
					struct.entity.write(oprot);
					oprot.writeFieldEnd();
				}
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class indexAppAll_argsTupleSchemeFactory implements SchemeFactory {
			public indexAppAll_argsTupleScheme getScheme() {
				return new indexAppAll_argsTupleScheme();
			}
		}

		private static class indexAppAll_argsTupleScheme extends TupleScheme<indexAppAll_args> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot, indexAppAll_args struct)
					throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
				BitSet optionals = new BitSet();
				if (struct.isSetEntity()) {
					optionals.set(0);
				}
				oprot.writeBitSet(optionals, 1);
				if (struct.isSetEntity()) {
					struct.entity.write(oprot);
				}
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot, indexAppAll_args struct)
					throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
				BitSet incoming = iprot.readBitSet(1);
				if (incoming.get(0)) {
					struct.entity = new IndexAppEntity();
					struct.entity.read(iprot);
					struct.setEntityIsSet(true);
				}
			}
		}

	}

	public static class indexAppAll_result
			implements org.apache.thrift.TBase<indexAppAll_result, indexAppAll_result._Fields>, java.io.Serializable,
			Cloneable, Comparable<indexAppAll_result> {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"indexAppAll_result");

		private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"success", org.apache.thrift.protocol.TType.MAP, (short) 0);

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class, new indexAppAll_resultStandardSchemeFactory());
			schemes.put(TupleScheme.class, new indexAppAll_resultTupleSchemeFactory());
		}

		public Map<String, List<IndexAppEntity>> success; // required

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			SUCCESS((short) 0, "success");

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				case 0: // SUCCESS
					return SUCCESS;
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		// isset id assignments
		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			tmpMap.put(_Fields.SUCCESS, new org.apache.thrift.meta_data.FieldMetaData("success",
					org.apache.thrift.TFieldRequirementType.DEFAULT,
					new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP,
							new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING),
							new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST,
									new org.apache.thrift.meta_data.StructMetaData(
											org.apache.thrift.protocol.TType.STRUCT, IndexAppEntity.class)))));
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(indexAppAll_result.class, metaDataMap);
		}

		public indexAppAll_result() {
		}

		public indexAppAll_result(Map<String, List<IndexAppEntity>> success) {
			this();
			this.success = success;
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public indexAppAll_result(indexAppAll_result other) {
			if (other.isSetSuccess()) {
				Map<String, List<IndexAppEntity>> __this__success = new HashMap<String, List<IndexAppEntity>>(
						other.success.size());
				for (Map.Entry<String, List<IndexAppEntity>> other_element : other.success.entrySet()) {

					String other_element_key = other_element.getKey();
					List<IndexAppEntity> other_element_value = other_element.getValue();

					String __this__success_copy_key = other_element_key;

					List<IndexAppEntity> __this__success_copy_value = new ArrayList<IndexAppEntity>(
							other_element_value.size());
					for (IndexAppEntity other_element_value_element : other_element_value) {
						__this__success_copy_value.add(new IndexAppEntity(other_element_value_element));
					}

					__this__success.put(__this__success_copy_key, __this__success_copy_value);
				}
				this.success = __this__success;
			}
		}

		public indexAppAll_result deepCopy() {
			return new indexAppAll_result(this);
		}

		@Override
		public void clear() {
			this.success = null;
		}

		public int getSuccessSize() {
			return (this.success == null) ? 0 : this.success.size();
		}

		public void putToSuccess(String key, List<IndexAppEntity> val) {
			if (this.success == null) {
				this.success = new HashMap<String, List<IndexAppEntity>>();
			}
			this.success.put(key, val);
		}

		public Map<String, List<IndexAppEntity>> getSuccess() {
			return this.success;
		}

		public indexAppAll_result setSuccess(Map<String, List<IndexAppEntity>> success) {
			this.success = success;
			return this;
		}

		public void unsetSuccess() {
			this.success = null;
		}

		/**
		 * Returns true if field success is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetSuccess() {
			return this.success != null;
		}

		public void setSuccessIsSet(boolean value) {
			if (!value) {
				this.success = null;
			}
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			case SUCCESS:
				if (value == null) {
					unsetSuccess();
				} else {
					setSuccess((Map<String, List<IndexAppEntity>>) value);
				}
				break;

			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			case SUCCESS:
				return getSuccess();

			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			case SUCCESS:
				return isSetSuccess();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof indexAppAll_result)
				return this.equals((indexAppAll_result) that);
			return false;
		}

		public boolean equals(indexAppAll_result that) {
			if (that == null)
				return false;

			boolean this_present_success = true && this.isSetSuccess();
			boolean that_present_success = true && that.isSetSuccess();
			if (this_present_success || that_present_success) {
				if (!(this_present_success && that_present_success))
					return false;
				if (!this.success.equals(that.success))
					return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			List<Object> list = new ArrayList<Object>();

			boolean present_success = true && (isSetSuccess());
			list.add(present_success);
			if (present_success)
				list.add(success);

			return list.hashCode();
		}

		@Override
		public int compareTo(indexAppAll_result other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(other.getClass().getName());
			}

			int lastComparison = 0;

			lastComparison = Boolean.valueOf(isSetSuccess()).compareTo(other.isSetSuccess());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetSuccess()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.success, other.success);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("indexAppAll_result(");
			boolean first = true;

			sb.append("success:");
			if (this.success == null) {
				sb.append("null");
			} else {
				sb.append(this.success);
			}
			first = false;
			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
			// check for sub-struct validity
		}

		private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class indexAppAll_resultStandardSchemeFactory implements SchemeFactory {
			public indexAppAll_resultStandardScheme getScheme() {
				return new indexAppAll_resultStandardScheme();
			}
		}

		private static class indexAppAll_resultStandardScheme extends StandardScheme<indexAppAll_result> {

			public void read(org.apache.thrift.protocol.TProtocol iprot, indexAppAll_result struct)
					throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					case 0: // SUCCESS
						if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
							{
								org.apache.thrift.protocol.TMap _map0 = iprot.readMapBegin();
								struct.success = new HashMap<String, List<IndexAppEntity>>(2 * _map0.size);
								String _key1;
								List<IndexAppEntity> _val2;
								for (int _i3 = 0; _i3 < _map0.size; ++_i3) {
									_key1 = iprot.readString();
									{
										org.apache.thrift.protocol.TList _list4 = iprot.readListBegin();
										_val2 = new ArrayList<IndexAppEntity>(_list4.size);
										IndexAppEntity _elem5;
										for (int _i6 = 0; _i6 < _list4.size; ++_i6) {
											_elem5 = new IndexAppEntity();
											_elem5.read(iprot);
											_val2.add(_elem5);
										}
										iprot.readListEnd();
									}
									struct.success.put(_key1, _val2);
								}
								iprot.readMapEnd();
							}
							struct.setSuccessIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
						}
						break;
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot, indexAppAll_result struct)
					throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				if (struct.success != null) {
					oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
					{
						oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING,
								org.apache.thrift.protocol.TType.LIST, struct.success.size()));
						for (Map.Entry<String, List<IndexAppEntity>> _iter7 : struct.success.entrySet()) {
							oprot.writeString(_iter7.getKey());
							{
								oprot.writeListBegin(new org.apache.thrift.protocol.TList(
										org.apache.thrift.protocol.TType.STRUCT, _iter7.getValue().size()));
								for (IndexAppEntity _iter8 : _iter7.getValue()) {
									_iter8.write(oprot);
								}
								oprot.writeListEnd();
							}
						}
						oprot.writeMapEnd();
					}
					oprot.writeFieldEnd();
				}
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class indexAppAll_resultTupleSchemeFactory implements SchemeFactory {
			public indexAppAll_resultTupleScheme getScheme() {
				return new indexAppAll_resultTupleScheme();
			}
		}

		private static class indexAppAll_resultTupleScheme extends TupleScheme<indexAppAll_result> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot, indexAppAll_result struct)
					throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
				BitSet optionals = new BitSet();
				if (struct.isSetSuccess()) {
					optionals.set(0);
				}
				oprot.writeBitSet(optionals, 1);
				if (struct.isSetSuccess()) {
					{
						oprot.writeI32(struct.success.size());
						for (Map.Entry<String, List<IndexAppEntity>> _iter9 : struct.success.entrySet()) {
							oprot.writeString(_iter9.getKey());
							{
								oprot.writeI32(_iter9.getValue().size());
								for (IndexAppEntity _iter10 : _iter9.getValue()) {
									_iter10.write(oprot);
								}
							}
						}
					}
				}
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot, indexAppAll_result struct)
					throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
				BitSet incoming = iprot.readBitSet(1);
				if (incoming.get(0)) {
					{
						org.apache.thrift.protocol.TMap _map11 = new org.apache.thrift.protocol.TMap(
								org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.LIST,
								iprot.readI32());
						struct.success = new HashMap<String, List<IndexAppEntity>>(2 * _map11.size);
						String _key12;
						List<IndexAppEntity> _val13;
						for (int _i14 = 0; _i14 < _map11.size; ++_i14) {
							_key12 = iprot.readString();
							{
								org.apache.thrift.protocol.TList _list15 = new org.apache.thrift.protocol.TList(
										org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
								_val13 = new ArrayList<IndexAppEntity>(_list15.size);
								IndexAppEntity _elem16;
								for (int _i17 = 0; _i17 < _list15.size; ++_i17) {
									_elem16 = new IndexAppEntity();
									_elem16.read(iprot);
									_val13.add(_elem16);
								}
							}
							struct.success.put(_key12, _val13);
						}
					}
					struct.setSuccessIsSet(true);
				}
			}
		}

	}

}