package com.wboly.rpc.entity;

/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;

@SuppressWarnings({"rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2016-12-09")
public class SystemConfigEntity implements org.apache.thrift.TBase<SystemConfigEntity, SystemConfigEntity._Fields>, java.io.Serializable, Cloneable, Comparable<SystemConfigEntity> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("SystemConfigEntity");

  private static final org.apache.thrift.protocol.TField CONFIG_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("configName", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField CONFIG_VALUE_FIELD_DESC = new org.apache.thrift.protocol.TField("configValue", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField DESCRIBE_FIELD_DESC = new org.apache.thrift.protocol.TField("describe", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField SHOP_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("shopId", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField PROVINCE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("provinceId", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField CITY_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("cityId", org.apache.thrift.protocol.TType.STRING, (short)6);
  private static final org.apache.thrift.protocol.TField AREA_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("areaId", org.apache.thrift.protocol.TType.STRING, (short)7);
  private static final org.apache.thrift.protocol.TField STREET_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("streetId", org.apache.thrift.protocol.TType.STRING, (short)8);
  private static final org.apache.thrift.protocol.TField SHOP_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("shopName", org.apache.thrift.protocol.TType.STRING, (short)9);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new SystemConfigEntityStandardSchemeFactory());
    schemes.put(TupleScheme.class, new SystemConfigEntityTupleSchemeFactory());
  }

  public String configName; // required
  public String configValue; // required
  public String describe; // required
  public String shopId; // required
  public String provinceId; // required
  public String cityId; // required
  public String areaId; // required
  public String streetId; // required
  public String shopName; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    CONFIG_NAME((short)1, "configName"),
    CONFIG_VALUE((short)2, "configValue"),
    DESCRIBE((short)3, "describe"),
    SHOP_ID((short)4, "shopId"),
    PROVINCE_ID((short)5, "provinceId"),
    CITY_ID((short)6, "cityId"),
    AREA_ID((short)7, "areaId"),
    STREET_ID((short)8, "streetId"),
    SHOP_NAME((short)9, "shopName");

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
        case 1: // CONFIG_NAME
          return CONFIG_NAME;
        case 2: // CONFIG_VALUE
          return CONFIG_VALUE;
        case 3: // DESCRIBE
          return DESCRIBE;
        case 4: // SHOP_ID
          return SHOP_ID;
        case 5: // PROVINCE_ID
          return PROVINCE_ID;
        case 6: // CITY_ID
          return CITY_ID;
        case 7: // AREA_ID
          return AREA_ID;
        case 8: // STREET_ID
          return STREET_ID;
        case 9: // SHOP_NAME
          return SHOP_NAME;
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
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.CONFIG_NAME, new org.apache.thrift.meta_data.FieldMetaData("configName", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.CONFIG_VALUE, new org.apache.thrift.meta_data.FieldMetaData("configValue", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.DESCRIBE, new org.apache.thrift.meta_data.FieldMetaData("describe", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.SHOP_ID, new org.apache.thrift.meta_data.FieldMetaData("shopId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PROVINCE_ID, new org.apache.thrift.meta_data.FieldMetaData("provinceId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.CITY_ID, new org.apache.thrift.meta_data.FieldMetaData("cityId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.AREA_ID, new org.apache.thrift.meta_data.FieldMetaData("areaId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.STREET_ID, new org.apache.thrift.meta_data.FieldMetaData("streetId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.SHOP_NAME, new org.apache.thrift.meta_data.FieldMetaData("shopName", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(SystemConfigEntity.class, metaDataMap);
  }

  public SystemConfigEntity() {
  }

  public SystemConfigEntity(
    String configName,
    String configValue,
    String describe,
    String shopId,
    String provinceId,
    String cityId,
    String areaId,
    String streetId,
    String shopName)
  {
    this();
    this.configName = configName;
    this.configValue = configValue;
    this.describe = describe;
    this.shopId = shopId;
    this.provinceId = provinceId;
    this.cityId = cityId;
    this.areaId = areaId;
    this.streetId = streetId;
    this.shopName = shopName;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public SystemConfigEntity(SystemConfigEntity other) {
    if (other.isSetConfigName()) {
      this.configName = other.configName;
    }
    if (other.isSetConfigValue()) {
      this.configValue = other.configValue;
    }
    if (other.isSetDescribe()) {
      this.describe = other.describe;
    }
    if (other.isSetShopId()) {
      this.shopId = other.shopId;
    }
    if (other.isSetProvinceId()) {
      this.provinceId = other.provinceId;
    }
    if (other.isSetCityId()) {
      this.cityId = other.cityId;
    }
    if (other.isSetAreaId()) {
      this.areaId = other.areaId;
    }
    if (other.isSetStreetId()) {
      this.streetId = other.streetId;
    }
    if (other.isSetShopName()) {
      this.shopName = other.shopName;
    }
  }

  public SystemConfigEntity deepCopy() {
    return new SystemConfigEntity(this);
  }

  @Override
  public void clear() {
    this.configName = null;
    this.configValue = null;
    this.describe = null;
    this.shopId = null;
    this.provinceId = null;
    this.cityId = null;
    this.areaId = null;
    this.streetId = null;
    this.shopName = null;
  }

  public String getConfigName() {
    return this.configName;
  }

  public SystemConfigEntity setConfigName(String configName) {
    this.configName = configName;
    return this;
  }

  public void unsetConfigName() {
    this.configName = null;
  }

  /** Returns true if field configName is set (has been assigned a value) and false otherwise */
  public boolean isSetConfigName() {
    return this.configName != null;
  }

  public void setConfigNameIsSet(boolean value) {
    if (!value) {
      this.configName = null;
    }
  }

  public String getConfigValue() {
    return this.configValue;
  }

  public SystemConfigEntity setConfigValue(String configValue) {
    this.configValue = configValue;
    return this;
  }

  public void unsetConfigValue() {
    this.configValue = null;
  }

  /** Returns true if field configValue is set (has been assigned a value) and false otherwise */
  public boolean isSetConfigValue() {
    return this.configValue != null;
  }

  public void setConfigValueIsSet(boolean value) {
    if (!value) {
      this.configValue = null;
    }
  }

  public String getDescribe() {
    return this.describe;
  }

  public SystemConfigEntity setDescribe(String describe) {
    this.describe = describe;
    return this;
  }

  public void unsetDescribe() {
    this.describe = null;
  }

  /** Returns true if field describe is set (has been assigned a value) and false otherwise */
  public boolean isSetDescribe() {
    return this.describe != null;
  }

  public void setDescribeIsSet(boolean value) {
    if (!value) {
      this.describe = null;
    }
  }

  public String getShopId() {
    return this.shopId;
  }

  public SystemConfigEntity setShopId(String shopId) {
    this.shopId = shopId;
    return this;
  }

  public void unsetShopId() {
    this.shopId = null;
  }

  /** Returns true if field shopId is set (has been assigned a value) and false otherwise */
  public boolean isSetShopId() {
    return this.shopId != null;
  }

  public void setShopIdIsSet(boolean value) {
    if (!value) {
      this.shopId = null;
    }
  }

  public String getProvinceId() {
    return this.provinceId;
  }

  public SystemConfigEntity setProvinceId(String provinceId) {
    this.provinceId = provinceId;
    return this;
  }

  public void unsetProvinceId() {
    this.provinceId = null;
  }

  /** Returns true if field provinceId is set (has been assigned a value) and false otherwise */
  public boolean isSetProvinceId() {
    return this.provinceId != null;
  }

  public void setProvinceIdIsSet(boolean value) {
    if (!value) {
      this.provinceId = null;
    }
  }

  public String getCityId() {
    return this.cityId;
  }

  public SystemConfigEntity setCityId(String cityId) {
    this.cityId = cityId;
    return this;
  }

  public void unsetCityId() {
    this.cityId = null;
  }

  /** Returns true if field cityId is set (has been assigned a value) and false otherwise */
  public boolean isSetCityId() {
    return this.cityId != null;
  }

  public void setCityIdIsSet(boolean value) {
    if (!value) {
      this.cityId = null;
    }
  }

  public String getAreaId() {
    return this.areaId;
  }

  public SystemConfigEntity setAreaId(String areaId) {
    this.areaId = areaId;
    return this;
  }

  public void unsetAreaId() {
    this.areaId = null;
  }

  /** Returns true if field areaId is set (has been assigned a value) and false otherwise */
  public boolean isSetAreaId() {
    return this.areaId != null;
  }

  public void setAreaIdIsSet(boolean value) {
    if (!value) {
      this.areaId = null;
    }
  }

  public String getStreetId() {
    return this.streetId;
  }

  public SystemConfigEntity setStreetId(String streetId) {
    this.streetId = streetId;
    return this;
  }

  public void unsetStreetId() {
    this.streetId = null;
  }

  /** Returns true if field streetId is set (has been assigned a value) and false otherwise */
  public boolean isSetStreetId() {
    return this.streetId != null;
  }

  public void setStreetIdIsSet(boolean value) {
    if (!value) {
      this.streetId = null;
    }
  }

  public String getShopName() {
    return this.shopName;
  }

  public SystemConfigEntity setShopName(String shopName) {
    this.shopName = shopName;
    return this;
  }

  public void unsetShopName() {
    this.shopName = null;
  }

  /** Returns true if field shopName is set (has been assigned a value) and false otherwise */
  public boolean isSetShopName() {
    return this.shopName != null;
  }

  public void setShopNameIsSet(boolean value) {
    if (!value) {
      this.shopName = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case CONFIG_NAME:
      if (value == null) {
        unsetConfigName();
      } else {
        setConfigName((String)value);
      }
      break;

    case CONFIG_VALUE:
      if (value == null) {
        unsetConfigValue();
      } else {
        setConfigValue((String)value);
      }
      break;

    case DESCRIBE:
      if (value == null) {
        unsetDescribe();
      } else {
        setDescribe((String)value);
      }
      break;

    case SHOP_ID:
      if (value == null) {
        unsetShopId();
      } else {
        setShopId((String)value);
      }
      break;

    case PROVINCE_ID:
      if (value == null) {
        unsetProvinceId();
      } else {
        setProvinceId((String)value);
      }
      break;

    case CITY_ID:
      if (value == null) {
        unsetCityId();
      } else {
        setCityId((String)value);
      }
      break;

    case AREA_ID:
      if (value == null) {
        unsetAreaId();
      } else {
        setAreaId((String)value);
      }
      break;

    case STREET_ID:
      if (value == null) {
        unsetStreetId();
      } else {
        setStreetId((String)value);
      }
      break;

    case SHOP_NAME:
      if (value == null) {
        unsetShopName();
      } else {
        setShopName((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case CONFIG_NAME:
      return getConfigName();

    case CONFIG_VALUE:
      return getConfigValue();

    case DESCRIBE:
      return getDescribe();

    case SHOP_ID:
      return getShopId();

    case PROVINCE_ID:
      return getProvinceId();

    case CITY_ID:
      return getCityId();

    case AREA_ID:
      return getAreaId();

    case STREET_ID:
      return getStreetId();

    case SHOP_NAME:
      return getShopName();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case CONFIG_NAME:
      return isSetConfigName();
    case CONFIG_VALUE:
      return isSetConfigValue();
    case DESCRIBE:
      return isSetDescribe();
    case SHOP_ID:
      return isSetShopId();
    case PROVINCE_ID:
      return isSetProvinceId();
    case CITY_ID:
      return isSetCityId();
    case AREA_ID:
      return isSetAreaId();
    case STREET_ID:
      return isSetStreetId();
    case SHOP_NAME:
      return isSetShopName();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof SystemConfigEntity)
      return this.equals((SystemConfigEntity)that);
    return false;
  }

  public boolean equals(SystemConfigEntity that) {
    if (that == null)
      return false;

    boolean this_present_configName = true && this.isSetConfigName();
    boolean that_present_configName = true && that.isSetConfigName();
    if (this_present_configName || that_present_configName) {
      if (!(this_present_configName && that_present_configName))
        return false;
      if (!this.configName.equals(that.configName))
        return false;
    }

    boolean this_present_configValue = true && this.isSetConfigValue();
    boolean that_present_configValue = true && that.isSetConfigValue();
    if (this_present_configValue || that_present_configValue) {
      if (!(this_present_configValue && that_present_configValue))
        return false;
      if (!this.configValue.equals(that.configValue))
        return false;
    }

    boolean this_present_describe = true && this.isSetDescribe();
    boolean that_present_describe = true && that.isSetDescribe();
    if (this_present_describe || that_present_describe) {
      if (!(this_present_describe && that_present_describe))
        return false;
      if (!this.describe.equals(that.describe))
        return false;
    }

    boolean this_present_shopId = true && this.isSetShopId();
    boolean that_present_shopId = true && that.isSetShopId();
    if (this_present_shopId || that_present_shopId) {
      if (!(this_present_shopId && that_present_shopId))
        return false;
      if (!this.shopId.equals(that.shopId))
        return false;
    }

    boolean this_present_provinceId = true && this.isSetProvinceId();
    boolean that_present_provinceId = true && that.isSetProvinceId();
    if (this_present_provinceId || that_present_provinceId) {
      if (!(this_present_provinceId && that_present_provinceId))
        return false;
      if (!this.provinceId.equals(that.provinceId))
        return false;
    }

    boolean this_present_cityId = true && this.isSetCityId();
    boolean that_present_cityId = true && that.isSetCityId();
    if (this_present_cityId || that_present_cityId) {
      if (!(this_present_cityId && that_present_cityId))
        return false;
      if (!this.cityId.equals(that.cityId))
        return false;
    }

    boolean this_present_areaId = true && this.isSetAreaId();
    boolean that_present_areaId = true && that.isSetAreaId();
    if (this_present_areaId || that_present_areaId) {
      if (!(this_present_areaId && that_present_areaId))
        return false;
      if (!this.areaId.equals(that.areaId))
        return false;
    }

    boolean this_present_streetId = true && this.isSetStreetId();
    boolean that_present_streetId = true && that.isSetStreetId();
    if (this_present_streetId || that_present_streetId) {
      if (!(this_present_streetId && that_present_streetId))
        return false;
      if (!this.streetId.equals(that.streetId))
        return false;
    }

    boolean this_present_shopName = true && this.isSetShopName();
    boolean that_present_shopName = true && that.isSetShopName();
    if (this_present_shopName || that_present_shopName) {
      if (!(this_present_shopName && that_present_shopName))
        return false;
      if (!this.shopName.equals(that.shopName))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_configName = true && (isSetConfigName());
    list.add(present_configName);
    if (present_configName)
      list.add(configName);

    boolean present_configValue = true && (isSetConfigValue());
    list.add(present_configValue);
    if (present_configValue)
      list.add(configValue);

    boolean present_describe = true && (isSetDescribe());
    list.add(present_describe);
    if (present_describe)
      list.add(describe);

    boolean present_shopId = true && (isSetShopId());
    list.add(present_shopId);
    if (present_shopId)
      list.add(shopId);

    boolean present_provinceId = true && (isSetProvinceId());
    list.add(present_provinceId);
    if (present_provinceId)
      list.add(provinceId);

    boolean present_cityId = true && (isSetCityId());
    list.add(present_cityId);
    if (present_cityId)
      list.add(cityId);

    boolean present_areaId = true && (isSetAreaId());
    list.add(present_areaId);
    if (present_areaId)
      list.add(areaId);

    boolean present_streetId = true && (isSetStreetId());
    list.add(present_streetId);
    if (present_streetId)
      list.add(streetId);

    boolean present_shopName = true && (isSetShopName());
    list.add(present_shopName);
    if (present_shopName)
      list.add(shopName);

    return list.hashCode();
  }

  @Override
  public int compareTo(SystemConfigEntity other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetConfigName()).compareTo(other.isSetConfigName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetConfigName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.configName, other.configName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetConfigValue()).compareTo(other.isSetConfigValue());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetConfigValue()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.configValue, other.configValue);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDescribe()).compareTo(other.isSetDescribe());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDescribe()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.describe, other.describe);
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
    lastComparison = Boolean.valueOf(isSetProvinceId()).compareTo(other.isSetProvinceId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetProvinceId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.provinceId, other.provinceId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetCityId()).compareTo(other.isSetCityId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCityId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.cityId, other.cityId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAreaId()).compareTo(other.isSetAreaId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAreaId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.areaId, other.areaId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetStreetId()).compareTo(other.isSetStreetId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStreetId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.streetId, other.streetId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetShopName()).compareTo(other.isSetShopName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetShopName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.shopName, other.shopName);
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
    StringBuilder sb = new StringBuilder("SystemConfigEntity(");
    boolean first = true;

    sb.append("configName:");
    if (this.configName == null) {
      sb.append("null");
    } else {
      sb.append(this.configName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("configValue:");
    if (this.configValue == null) {
      sb.append("null");
    } else {
      sb.append(this.configValue);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("describe:");
    if (this.describe == null) {
      sb.append("null");
    } else {
      sb.append(this.describe);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("shopId:");
    if (this.shopId == null) {
      sb.append("null");
    } else {
      sb.append(this.shopId);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("provinceId:");
    if (this.provinceId == null) {
      sb.append("null");
    } else {
      sb.append(this.provinceId);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("cityId:");
    if (this.cityId == null) {
      sb.append("null");
    } else {
      sb.append(this.cityId);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("areaId:");
    if (this.areaId == null) {
      sb.append("null");
    } else {
      sb.append(this.areaId);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("streetId:");
    if (this.streetId == null) {
      sb.append("null");
    } else {
      sb.append(this.streetId);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("shopName:");
    if (this.shopName == null) {
      sb.append("null");
    } else {
      sb.append(this.shopName);
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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class SystemConfigEntityStandardSchemeFactory implements SchemeFactory {
    public SystemConfigEntityStandardScheme getScheme() {
      return new SystemConfigEntityStandardScheme();
    }
  }

  private static class SystemConfigEntityStandardScheme extends StandardScheme<SystemConfigEntity> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, SystemConfigEntity struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // CONFIG_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.configName = iprot.readString();
              struct.setConfigNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // CONFIG_VALUE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.configValue = iprot.readString();
              struct.setConfigValueIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // DESCRIBE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.describe = iprot.readString();
              struct.setDescribeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // SHOP_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.shopId = iprot.readString();
              struct.setShopIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // PROVINCE_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.provinceId = iprot.readString();
              struct.setProvinceIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // CITY_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.cityId = iprot.readString();
              struct.setCityIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // AREA_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.areaId = iprot.readString();
              struct.setAreaIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 8: // STREET_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.streetId = iprot.readString();
              struct.setStreetIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 9: // SHOP_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.shopName = iprot.readString();
              struct.setShopNameIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, SystemConfigEntity struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.configName != null) {
        oprot.writeFieldBegin(CONFIG_NAME_FIELD_DESC);
        oprot.writeString(struct.configName);
        oprot.writeFieldEnd();
      }
      if (struct.configValue != null) {
        oprot.writeFieldBegin(CONFIG_VALUE_FIELD_DESC);
        oprot.writeString(struct.configValue);
        oprot.writeFieldEnd();
      }
      if (struct.describe != null) {
        oprot.writeFieldBegin(DESCRIBE_FIELD_DESC);
        oprot.writeString(struct.describe);
        oprot.writeFieldEnd();
      }
      if (struct.shopId != null) {
        oprot.writeFieldBegin(SHOP_ID_FIELD_DESC);
        oprot.writeString(struct.shopId);
        oprot.writeFieldEnd();
      }
      if (struct.provinceId != null) {
        oprot.writeFieldBegin(PROVINCE_ID_FIELD_DESC);
        oprot.writeString(struct.provinceId);
        oprot.writeFieldEnd();
      }
      if (struct.cityId != null) {
        oprot.writeFieldBegin(CITY_ID_FIELD_DESC);
        oprot.writeString(struct.cityId);
        oprot.writeFieldEnd();
      }
      if (struct.areaId != null) {
        oprot.writeFieldBegin(AREA_ID_FIELD_DESC);
        oprot.writeString(struct.areaId);
        oprot.writeFieldEnd();
      }
      if (struct.streetId != null) {
        oprot.writeFieldBegin(STREET_ID_FIELD_DESC);
        oprot.writeString(struct.streetId);
        oprot.writeFieldEnd();
      }
      if (struct.shopName != null) {
        oprot.writeFieldBegin(SHOP_NAME_FIELD_DESC);
        oprot.writeString(struct.shopName);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class SystemConfigEntityTupleSchemeFactory implements SchemeFactory {
    public SystemConfigEntityTupleScheme getScheme() {
      return new SystemConfigEntityTupleScheme();
    }
  }

  private static class SystemConfigEntityTupleScheme extends TupleScheme<SystemConfigEntity> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, SystemConfigEntity struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetConfigName()) {
        optionals.set(0);
      }
      if (struct.isSetConfigValue()) {
        optionals.set(1);
      }
      if (struct.isSetDescribe()) {
        optionals.set(2);
      }
      if (struct.isSetShopId()) {
        optionals.set(3);
      }
      if (struct.isSetProvinceId()) {
        optionals.set(4);
      }
      if (struct.isSetCityId()) {
        optionals.set(5);
      }
      if (struct.isSetAreaId()) {
        optionals.set(6);
      }
      if (struct.isSetStreetId()) {
        optionals.set(7);
      }
      if (struct.isSetShopName()) {
        optionals.set(8);
      }
      oprot.writeBitSet(optionals, 9);
      if (struct.isSetConfigName()) {
        oprot.writeString(struct.configName);
      }
      if (struct.isSetConfigValue()) {
        oprot.writeString(struct.configValue);
      }
      if (struct.isSetDescribe()) {
        oprot.writeString(struct.describe);
      }
      if (struct.isSetShopId()) {
        oprot.writeString(struct.shopId);
      }
      if (struct.isSetProvinceId()) {
        oprot.writeString(struct.provinceId);
      }
      if (struct.isSetCityId()) {
        oprot.writeString(struct.cityId);
      }
      if (struct.isSetAreaId()) {
        oprot.writeString(struct.areaId);
      }
      if (struct.isSetStreetId()) {
        oprot.writeString(struct.streetId);
      }
      if (struct.isSetShopName()) {
        oprot.writeString(struct.shopName);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, SystemConfigEntity struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(9);
      if (incoming.get(0)) {
        struct.configName = iprot.readString();
        struct.setConfigNameIsSet(true);
      }
      if (incoming.get(1)) {
        struct.configValue = iprot.readString();
        struct.setConfigValueIsSet(true);
      }
      if (incoming.get(2)) {
        struct.describe = iprot.readString();
        struct.setDescribeIsSet(true);
      }
      if (incoming.get(3)) {
        struct.shopId = iprot.readString();
        struct.setShopIdIsSet(true);
      }
      if (incoming.get(4)) {
        struct.provinceId = iprot.readString();
        struct.setProvinceIdIsSet(true);
      }
      if (incoming.get(5)) {
        struct.cityId = iprot.readString();
        struct.setCityIdIsSet(true);
      }
      if (incoming.get(6)) {
        struct.areaId = iprot.readString();
        struct.setAreaIdIsSet(true);
      }
      if (incoming.get(7)) {
        struct.streetId = iprot.readString();
        struct.setStreetIdIsSet(true);
      }
      if (incoming.get(8)) {
        struct.shopName = iprot.readString();
        struct.setShopNameIsSet(true);
      }
    }
  }

}

