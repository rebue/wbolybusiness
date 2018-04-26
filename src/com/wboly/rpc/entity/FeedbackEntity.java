package com.wboly.rpc.entity;

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
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.EncodingUtils;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import javax.annotation.Generated;

@SuppressWarnings({"rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2017-11-24")
public class FeedbackEntity implements org.apache.thrift.TBase<FeedbackEntity, FeedbackEntity._Fields>, java.io.Serializable, Cloneable, Comparable<FeedbackEntity> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("FeedbackEntity");

  private static final org.apache.thrift.protocol.TField ID_FIELD_DESC = new org.apache.thrift.protocol.TField("id", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField SHOP_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("shopId", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("message", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField IMG_PATH_FIELD_DESC = new org.apache.thrift.protocol.TField("imgPath", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField STATE_FIELD_DESC = new org.apache.thrift.protocol.TField("state", org.apache.thrift.protocol.TType.I32, (short)5);
  private static final org.apache.thrift.protocol.TField USER_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("userId", org.apache.thrift.protocol.TType.STRING, (short)6);
  private static final org.apache.thrift.protocol.TField DATELINE_FIELD_DESC = new org.apache.thrift.protocol.TField("dateline", org.apache.thrift.protocol.TType.I32, (short)7);
  private static final org.apache.thrift.protocol.TField USERCONTACT_FIELD_DESC = new org.apache.thrift.protocol.TField("usercontact", org.apache.thrift.protocol.TType.STRING, (short)8);
  private static final org.apache.thrift.protocol.TField TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("type", org.apache.thrift.protocol.TType.STRING, (short)9);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new FeedbackEntityStandardSchemeFactory());
    schemes.put(TupleScheme.class, new FeedbackEntityTupleSchemeFactory());
  }

  public int id; // required
  public int shopId; // required
  public String message; // required
  public String imgPath; // required
  public int state; // required
  public String userId; // required
  public int dateline; // required
  public String usercontact; // required
  public String type; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ID((short)1, "id"),
    SHOP_ID((short)2, "shopId"),
    MESSAGE((short)3, "message"),
    IMG_PATH((short)4, "imgPath"),
    STATE((short)5, "state"),
    USER_ID((short)6, "userId"),
    DATELINE((short)7, "dateline"),
    USERCONTACT((short)8, "usercontact"),
    TYPE((short)9, "type");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // ID
          return ID;
        case 2: // SHOP_ID
          return SHOP_ID;
        case 3: // MESSAGE
          return MESSAGE;
        case 4: // IMG_PATH
          return IMG_PATH;
        case 5: // STATE
          return STATE;
        case 6: // USER_ID
          return USER_ID;
        case 7: // DATELINE
          return DATELINE;
        case 8: // USERCONTACT
          return USERCONTACT;
        case 9: // TYPE
          return TYPE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
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
  private static final int __ID_ISSET_ID = 0;
  private static final int __SHOPID_ISSET_ID = 1;
  private static final int __STATE_ISSET_ID = 2;
  private static final int __DATELINE_ISSET_ID = 3;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ID, new org.apache.thrift.meta_data.FieldMetaData("id", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.SHOP_ID, new org.apache.thrift.meta_data.FieldMetaData("shopId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("message", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.IMG_PATH, new org.apache.thrift.meta_data.FieldMetaData("imgPath", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.STATE, new org.apache.thrift.meta_data.FieldMetaData("state", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.USER_ID, new org.apache.thrift.meta_data.FieldMetaData("userId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.DATELINE, new org.apache.thrift.meta_data.FieldMetaData("dateline", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.USERCONTACT, new org.apache.thrift.meta_data.FieldMetaData("usercontact", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TYPE, new org.apache.thrift.meta_data.FieldMetaData("type", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(FeedbackEntity.class, metaDataMap);
  }

  public FeedbackEntity() {
  }

  public FeedbackEntity(
    int id,
    int shopId,
    String message,
    String imgPath,
    int state,
    String userId,
    int dateline,
    String usercontact,
    String type)
  {
    this();
    this.id = id;
    setIdIsSet(true);
    this.shopId = shopId;
    setShopIdIsSet(true);
    this.message = message;
    this.imgPath = imgPath;
    this.state = state;
    setStateIsSet(true);
    this.userId = userId;
    this.dateline = dateline;
    setDatelineIsSet(true);
    this.usercontact = usercontact;
    this.type = type;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public FeedbackEntity(FeedbackEntity other) {
    __isset_bitfield = other.__isset_bitfield;
    this.id = other.id;
    this.shopId = other.shopId;
    if (other.isSetMessage()) {
      this.message = other.message;
    }
    if (other.isSetImgPath()) {
      this.imgPath = other.imgPath;
    }
    this.state = other.state;
    if (other.isSetUserId()) {
      this.userId = other.userId;
    }
    this.dateline = other.dateline;
    if (other.isSetUsercontact()) {
      this.usercontact = other.usercontact;
    }
    if (other.isSetType()) {
      this.type = other.type;
    }
  }

  public FeedbackEntity deepCopy() {
    return new FeedbackEntity(this);
  }

  @Override
  public void clear() {
    setIdIsSet(false);
    this.id = 0;
    setShopIdIsSet(false);
    this.shopId = 0;
    this.message = null;
    this.imgPath = null;
    setStateIsSet(false);
    this.state = 0;
    this.userId = null;
    setDatelineIsSet(false);
    this.dateline = 0;
    this.usercontact = null;
    this.type = null;
  }

  public int getId() {
    return this.id;
  }

  public FeedbackEntity setId(int id) {
    this.id = id;
    setIdIsSet(true);
    return this;
  }

  public void unsetId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ID_ISSET_ID);
  }

  /** Returns true if field id is set (has been assigned a value) and false otherwise */
  public boolean isSetId() {
    return EncodingUtils.testBit(__isset_bitfield, __ID_ISSET_ID);
  }

  public void setIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ID_ISSET_ID, value);
  }

  public int getShopId() {
    return this.shopId;
  }

  public FeedbackEntity setShopId(int shopId) {
    this.shopId = shopId;
    setShopIdIsSet(true);
    return this;
  }

  public void unsetShopId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __SHOPID_ISSET_ID);
  }

  /** Returns true if field shopId is set (has been assigned a value) and false otherwise */
  public boolean isSetShopId() {
    return EncodingUtils.testBit(__isset_bitfield, __SHOPID_ISSET_ID);
  }

  public void setShopIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __SHOPID_ISSET_ID, value);
  }

  public String getMessage() {
    return this.message;
  }

  public FeedbackEntity setMessage(String message) {
    this.message = message;
    return this;
  }

  public void unsetMessage() {
    this.message = null;
  }

  /** Returns true if field message is set (has been assigned a value) and false otherwise */
  public boolean isSetMessage() {
    return this.message != null;
  }

  public void setMessageIsSet(boolean value) {
    if (!value) {
      this.message = null;
    }
  }

  public String getImgPath() {
    return this.imgPath;
  }

  public FeedbackEntity setImgPath(String imgPath) {
    this.imgPath = imgPath;
    return this;
  }

  public void unsetImgPath() {
    this.imgPath = null;
  }

  /** Returns true if field imgPath is set (has been assigned a value) and false otherwise */
  public boolean isSetImgPath() {
    return this.imgPath != null;
  }

  public void setImgPathIsSet(boolean value) {
    if (!value) {
      this.imgPath = null;
    }
  }

  public int getState() {
    return this.state;
  }

  public FeedbackEntity setState(int state) {
    this.state = state;
    setStateIsSet(true);
    return this;
  }

  public void unsetState() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __STATE_ISSET_ID);
  }

  /** Returns true if field state is set (has been assigned a value) and false otherwise */
  public boolean isSetState() {
    return EncodingUtils.testBit(__isset_bitfield, __STATE_ISSET_ID);
  }

  public void setStateIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __STATE_ISSET_ID, value);
  }

  public String getUserId() {
    return this.userId;
  }

  public FeedbackEntity setUserId(String userId) {
    this.userId = userId;
    return this;
  }

  public void unsetUserId() {
    this.userId = null;
  }

  /** Returns true if field userId is set (has been assigned a value) and false otherwise */
  public boolean isSetUserId() {
    return this.userId != null;
  }

  public void setUserIdIsSet(boolean value) {
    if (!value) {
      this.userId = null;
    }
  }

  public int getDateline() {
    return this.dateline;
  }

  public FeedbackEntity setDateline(int dateline) {
    this.dateline = dateline;
    setDatelineIsSet(true);
    return this;
  }

  public void unsetDateline() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __DATELINE_ISSET_ID);
  }

  /** Returns true if field dateline is set (has been assigned a value) and false otherwise */
  public boolean isSetDateline() {
    return EncodingUtils.testBit(__isset_bitfield, __DATELINE_ISSET_ID);
  }

  public void setDatelineIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __DATELINE_ISSET_ID, value);
  }

  public String getUsercontact() {
    return this.usercontact;
  }

  public FeedbackEntity setUsercontact(String usercontact) {
    this.usercontact = usercontact;
    return this;
  }

  public void unsetUsercontact() {
    this.usercontact = null;
  }

  /** Returns true if field usercontact is set (has been assigned a value) and false otherwise */
  public boolean isSetUsercontact() {
    return this.usercontact != null;
  }

  public void setUsercontactIsSet(boolean value) {
    if (!value) {
      this.usercontact = null;
    }
  }

  public String getType() {
    return this.type;
  }

  public FeedbackEntity setType(String type) {
    this.type = type;
    return this;
  }

  public void unsetType() {
    this.type = null;
  }

  /** Returns true if field type is set (has been assigned a value) and false otherwise */
  public boolean isSetType() {
    return this.type != null;
  }

  public void setTypeIsSet(boolean value) {
    if (!value) {
      this.type = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ID:
      if (value == null) {
        unsetId();
      } else {
        setId((Integer)value);
      }
      break;

    case SHOP_ID:
      if (value == null) {
        unsetShopId();
      } else {
        setShopId((Integer)value);
      }
      break;

    case MESSAGE:
      if (value == null) {
        unsetMessage();
      } else {
        setMessage((String)value);
      }
      break;

    case IMG_PATH:
      if (value == null) {
        unsetImgPath();
      } else {
        setImgPath((String)value);
      }
      break;

    case STATE:
      if (value == null) {
        unsetState();
      } else {
        setState((Integer)value);
      }
      break;

    case USER_ID:
      if (value == null) {
        unsetUserId();
      } else {
        setUserId((String)value);
      }
      break;

    case DATELINE:
      if (value == null) {
        unsetDateline();
      } else {
        setDateline((Integer)value);
      }
      break;

    case USERCONTACT:
      if (value == null) {
        unsetUsercontact();
      } else {
        setUsercontact((String)value);
      }
      break;

    case TYPE:
      if (value == null) {
        unsetType();
      } else {
        setType((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ID:
      return getId();

    case SHOP_ID:
      return getShopId();

    case MESSAGE:
      return getMessage();

    case IMG_PATH:
      return getImgPath();

    case STATE:
      return getState();

    case USER_ID:
      return getUserId();

    case DATELINE:
      return getDateline();

    case USERCONTACT:
      return getUsercontact();

    case TYPE:
      return getType();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ID:
      return isSetId();
    case SHOP_ID:
      return isSetShopId();
    case MESSAGE:
      return isSetMessage();
    case IMG_PATH:
      return isSetImgPath();
    case STATE:
      return isSetState();
    case USER_ID:
      return isSetUserId();
    case DATELINE:
      return isSetDateline();
    case USERCONTACT:
      return isSetUsercontact();
    case TYPE:
      return isSetType();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof FeedbackEntity)
      return this.equals((FeedbackEntity)that);
    return false;
  }

  public boolean equals(FeedbackEntity that) {
    if (that == null)
      return false;

    boolean this_present_id = true;
    boolean that_present_id = true;
    if (this_present_id || that_present_id) {
      if (!(this_present_id && that_present_id))
        return false;
      if (this.id != that.id)
        return false;
    }

    boolean this_present_shopId = true;
    boolean that_present_shopId = true;
    if (this_present_shopId || that_present_shopId) {
      if (!(this_present_shopId && that_present_shopId))
        return false;
      if (this.shopId != that.shopId)
        return false;
    }

    boolean this_present_message = true && this.isSetMessage();
    boolean that_present_message = true && that.isSetMessage();
    if (this_present_message || that_present_message) {
      if (!(this_present_message && that_present_message))
        return false;
      if (!this.message.equals(that.message))
        return false;
    }

    boolean this_present_imgPath = true && this.isSetImgPath();
    boolean that_present_imgPath = true && that.isSetImgPath();
    if (this_present_imgPath || that_present_imgPath) {
      if (!(this_present_imgPath && that_present_imgPath))
        return false;
      if (!this.imgPath.equals(that.imgPath))
        return false;
    }

    boolean this_present_state = true;
    boolean that_present_state = true;
    if (this_present_state || that_present_state) {
      if (!(this_present_state && that_present_state))
        return false;
      if (this.state != that.state)
        return false;
    }

    boolean this_present_userId = true && this.isSetUserId();
    boolean that_present_userId = true && that.isSetUserId();
    if (this_present_userId || that_present_userId) {
      if (!(this_present_userId && that_present_userId))
        return false;
      if (!this.userId.equals(that.userId))
        return false;
    }

    boolean this_present_dateline = true;
    boolean that_present_dateline = true;
    if (this_present_dateline || that_present_dateline) {
      if (!(this_present_dateline && that_present_dateline))
        return false;
      if (this.dateline != that.dateline)
        return false;
    }

    boolean this_present_usercontact = true && this.isSetUsercontact();
    boolean that_present_usercontact = true && that.isSetUsercontact();
    if (this_present_usercontact || that_present_usercontact) {
      if (!(this_present_usercontact && that_present_usercontact))
        return false;
      if (!this.usercontact.equals(that.usercontact))
        return false;
    }

    boolean this_present_type = true && this.isSetType();
    boolean that_present_type = true && that.isSetType();
    if (this_present_type || that_present_type) {
      if (!(this_present_type && that_present_type))
        return false;
      if (!this.type.equals(that.type))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_id = true;
    list.add(present_id);
    if (present_id)
      list.add(id);

    boolean present_shopId = true;
    list.add(present_shopId);
    if (present_shopId)
      list.add(shopId);

    boolean present_message = true && (isSetMessage());
    list.add(present_message);
    if (present_message)
      list.add(message);

    boolean present_imgPath = true && (isSetImgPath());
    list.add(present_imgPath);
    if (present_imgPath)
      list.add(imgPath);

    boolean present_state = true;
    list.add(present_state);
    if (present_state)
      list.add(state);

    boolean present_userId = true && (isSetUserId());
    list.add(present_userId);
    if (present_userId)
      list.add(userId);

    boolean present_dateline = true;
    list.add(present_dateline);
    if (present_dateline)
      list.add(dateline);

    boolean present_usercontact = true && (isSetUsercontact());
    list.add(present_usercontact);
    if (present_usercontact)
      list.add(usercontact);

    boolean present_type = true && (isSetType());
    list.add(present_type);
    if (present_type)
      list.add(type);

    return list.hashCode();
  }

  @Override
  public int compareTo(FeedbackEntity other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetId()).compareTo(other.isSetId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.id, other.id);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetShopId()).compareTo(other.isSetShopId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetShopId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.shopId, other.shopId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMessage()).compareTo(other.isSetMessage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMessage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.message, other.message);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetImgPath()).compareTo(other.isSetImgPath());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetImgPath()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.imgPath, other.imgPath);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetState()).compareTo(other.isSetState());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetState()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.state, other.state);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUserId()).compareTo(other.isSetUserId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUserId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.userId, other.userId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDateline()).compareTo(other.isSetDateline());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDateline()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.dateline, other.dateline);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUsercontact()).compareTo(other.isSetUsercontact());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUsercontact()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.usercontact, other.usercontact);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetType()).compareTo(other.isSetType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.type, other.type);
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
    StringBuilder sb = new StringBuilder("FeedbackEntity(");
    boolean first = true;

    sb.append("id:");
    sb.append(this.id);
    first = false;
    if (!first) sb.append(", ");
    sb.append("shopId:");
    sb.append(this.shopId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("message:");
    if (this.message == null) {
      sb.append("null");
    } else {
      sb.append(this.message);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("imgPath:");
    if (this.imgPath == null) {
      sb.append("null");
    } else {
      sb.append(this.imgPath);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("state:");
    sb.append(this.state);
    first = false;
    if (!first) sb.append(", ");
    sb.append("userId:");
    if (this.userId == null) {
      sb.append("null");
    } else {
      sb.append(this.userId);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("dateline:");
    sb.append(this.dateline);
    first = false;
    if (!first) sb.append(", ");
    sb.append("usercontact:");
    if (this.usercontact == null) {
      sb.append("null");
    } else {
      sb.append(this.usercontact);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("type:");
    if (this.type == null) {
      sb.append("null");
    } else {
      sb.append(this.type);
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
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class FeedbackEntityStandardSchemeFactory implements SchemeFactory {
    public FeedbackEntityStandardScheme getScheme() {
      return new FeedbackEntityStandardScheme();
    }
  }

  private static class FeedbackEntityStandardScheme extends StandardScheme<FeedbackEntity> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, FeedbackEntity struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.id = iprot.readI32();
              struct.setIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // SHOP_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.shopId = iprot.readI32();
              struct.setShopIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // MESSAGE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.message = iprot.readString();
              struct.setMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // IMG_PATH
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.imgPath = iprot.readString();
              struct.setImgPathIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // STATE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.state = iprot.readI32();
              struct.setStateIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // USER_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.userId = iprot.readString();
              struct.setUserIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // DATELINE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.dateline = iprot.readI32();
              struct.setDatelineIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 8: // USERCONTACT
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.usercontact = iprot.readString();
              struct.setUsercontactIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 9: // TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.type = iprot.readString();
              struct.setTypeIsSet(true);
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

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, FeedbackEntity struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(ID_FIELD_DESC);
      oprot.writeI32(struct.id);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(SHOP_ID_FIELD_DESC);
      oprot.writeI32(struct.shopId);
      oprot.writeFieldEnd();
      if (struct.message != null) {
        oprot.writeFieldBegin(MESSAGE_FIELD_DESC);
        oprot.writeString(struct.message);
        oprot.writeFieldEnd();
      }
      if (struct.imgPath != null) {
        oprot.writeFieldBegin(IMG_PATH_FIELD_DESC);
        oprot.writeString(struct.imgPath);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(STATE_FIELD_DESC);
      oprot.writeI32(struct.state);
      oprot.writeFieldEnd();
      if (struct.userId != null) {
        oprot.writeFieldBegin(USER_ID_FIELD_DESC);
        oprot.writeString(struct.userId);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(DATELINE_FIELD_DESC);
      oprot.writeI32(struct.dateline);
      oprot.writeFieldEnd();
      if (struct.usercontact != null) {
        oprot.writeFieldBegin(USERCONTACT_FIELD_DESC);
        oprot.writeString(struct.usercontact);
        oprot.writeFieldEnd();
      }
      if (struct.type != null) {
        oprot.writeFieldBegin(TYPE_FIELD_DESC);
        oprot.writeString(struct.type);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class FeedbackEntityTupleSchemeFactory implements SchemeFactory {
    public FeedbackEntityTupleScheme getScheme() {
      return new FeedbackEntityTupleScheme();
    }
  }

  private static class FeedbackEntityTupleScheme extends TupleScheme<FeedbackEntity> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, FeedbackEntity struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetId()) {
        optionals.set(0);
      }
      if (struct.isSetShopId()) {
        optionals.set(1);
      }
      if (struct.isSetMessage()) {
        optionals.set(2);
      }
      if (struct.isSetImgPath()) {
        optionals.set(3);
      }
      if (struct.isSetState()) {
        optionals.set(4);
      }
      if (struct.isSetUserId()) {
        optionals.set(5);
      }
      if (struct.isSetDateline()) {
        optionals.set(6);
      }
      if (struct.isSetUsercontact()) {
        optionals.set(7);
      }
      if (struct.isSetType()) {
        optionals.set(8);
      }
      oprot.writeBitSet(optionals, 9);
      if (struct.isSetId()) {
        oprot.writeI32(struct.id);
      }
      if (struct.isSetShopId()) {
        oprot.writeI32(struct.shopId);
      }
      if (struct.isSetMessage()) {
        oprot.writeString(struct.message);
      }
      if (struct.isSetImgPath()) {
        oprot.writeString(struct.imgPath);
      }
      if (struct.isSetState()) {
        oprot.writeI32(struct.state);
      }
      if (struct.isSetUserId()) {
        oprot.writeString(struct.userId);
      }
      if (struct.isSetDateline()) {
        oprot.writeI32(struct.dateline);
      }
      if (struct.isSetUsercontact()) {
        oprot.writeString(struct.usercontact);
      }
      if (struct.isSetType()) {
        oprot.writeString(struct.type);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, FeedbackEntity struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(9);
      if (incoming.get(0)) {
        struct.id = iprot.readI32();
        struct.setIdIsSet(true);
      }
      if (incoming.get(1)) {
        struct.shopId = iprot.readI32();
        struct.setShopIdIsSet(true);
      }
      if (incoming.get(2)) {
        struct.message = iprot.readString();
        struct.setMessageIsSet(true);
      }
      if (incoming.get(3)) {
        struct.imgPath = iprot.readString();
        struct.setImgPathIsSet(true);
      }
      if (incoming.get(4)) {
        struct.state = iprot.readI32();
        struct.setStateIsSet(true);
      }
      if (incoming.get(5)) {
        struct.userId = iprot.readString();
        struct.setUserIdIsSet(true);
      }
      if (incoming.get(6)) {
        struct.dateline = iprot.readI32();
        struct.setDatelineIsSet(true);
      }
      if (incoming.get(7)) {
        struct.usercontact = iprot.readString();
        struct.setUsercontactIsSet(true);
      }
      if (incoming.get(8)) {
        struct.type = iprot.readString();
        struct.setTypeIsSet(true);
      }
    }
  }

}

