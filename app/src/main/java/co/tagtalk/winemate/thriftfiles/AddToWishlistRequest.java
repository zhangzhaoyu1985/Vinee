/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package co.tagtalk.winemate.thriftfiles;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2017-04-02")
public class AddToWishlistRequest implements org.apache.thrift.TBase<AddToWishlistRequest, AddToWishlistRequest._Fields>, java.io.Serializable, Cloneable, Comparable<AddToWishlistRequest> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("AddToWishlistRequest");

  private static final org.apache.thrift.protocol.TField USER_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("userId", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField WINE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("wineId", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField ENABLED_FIELD_DESC = new org.apache.thrift.protocol.TField("enabled", org.apache.thrift.protocol.TType.BOOL, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new AddToWishlistRequestStandardSchemeFactory());
    schemes.put(TupleScheme.class, new AddToWishlistRequestTupleSchemeFactory());
  }

  public int userId; // required
  public int wineId; // required
  public boolean enabled; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    USER_ID((short)1, "userId"),
    WINE_ID((short)2, "wineId"),
    ENABLED((short)3, "enabled");

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
        case 1: // USER_ID
          return USER_ID;
        case 2: // WINE_ID
          return WINE_ID;
        case 3: // ENABLED
          return ENABLED;
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
  private static final int __USERID_ISSET_ID = 0;
  private static final int __WINEID_ISSET_ID = 1;
  private static final int __ENABLED_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.USER_ID, new org.apache.thrift.meta_data.FieldMetaData("userId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.WINE_ID, new org.apache.thrift.meta_data.FieldMetaData("wineId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.ENABLED, new org.apache.thrift.meta_data.FieldMetaData("enabled", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(AddToWishlistRequest.class, metaDataMap);
  }

  public AddToWishlistRequest() {
  }

  public AddToWishlistRequest(
    int userId,
    int wineId,
    boolean enabled)
  {
    this();
    this.userId = userId;
    setUserIdIsSet(true);
    this.wineId = wineId;
    setWineIdIsSet(true);
    this.enabled = enabled;
    setEnabledIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public AddToWishlistRequest(AddToWishlistRequest other) {
    __isset_bitfield = other.__isset_bitfield;
    this.userId = other.userId;
    this.wineId = other.wineId;
    this.enabled = other.enabled;
  }

  public AddToWishlistRequest deepCopy() {
    return new AddToWishlistRequest(this);
  }

  @Override
  public void clear() {
    setUserIdIsSet(false);
    this.userId = 0;
    setWineIdIsSet(false);
    this.wineId = 0;
    setEnabledIsSet(false);
    this.enabled = false;
  }

  public int getUserId() {
    return this.userId;
  }

  public AddToWishlistRequest setUserId(int userId) {
    this.userId = userId;
    setUserIdIsSet(true);
    return this;
  }

  public void unsetUserId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __USERID_ISSET_ID);
  }

  /** Returns true if field userId is set (has been assigned a value) and false otherwise */
  public boolean isSetUserId() {
    return EncodingUtils.testBit(__isset_bitfield, __USERID_ISSET_ID);
  }

  public void setUserIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __USERID_ISSET_ID, value);
  }

  public int getWineId() {
    return this.wineId;
  }

  public AddToWishlistRequest setWineId(int wineId) {
    this.wineId = wineId;
    setWineIdIsSet(true);
    return this;
  }

  public void unsetWineId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __WINEID_ISSET_ID);
  }

  /** Returns true if field wineId is set (has been assigned a value) and false otherwise */
  public boolean isSetWineId() {
    return EncodingUtils.testBit(__isset_bitfield, __WINEID_ISSET_ID);
  }

  public void setWineIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __WINEID_ISSET_ID, value);
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public AddToWishlistRequest setEnabled(boolean enabled) {
    this.enabled = enabled;
    setEnabledIsSet(true);
    return this;
  }

  public void unsetEnabled() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ENABLED_ISSET_ID);
  }

  /** Returns true if field enabled is set (has been assigned a value) and false otherwise */
  public boolean isSetEnabled() {
    return EncodingUtils.testBit(__isset_bitfield, __ENABLED_ISSET_ID);
  }

  public void setEnabledIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ENABLED_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case USER_ID:
      if (value == null) {
        unsetUserId();
      } else {
        setUserId((Integer)value);
      }
      break;

    case WINE_ID:
      if (value == null) {
        unsetWineId();
      } else {
        setWineId((Integer)value);
      }
      break;

    case ENABLED:
      if (value == null) {
        unsetEnabled();
      } else {
        setEnabled((Boolean)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case USER_ID:
      return getUserId();

    case WINE_ID:
      return getWineId();

    case ENABLED:
      return isEnabled();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case USER_ID:
      return isSetUserId();
    case WINE_ID:
      return isSetWineId();
    case ENABLED:
      return isSetEnabled();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof AddToWishlistRequest)
      return this.equals((AddToWishlistRequest)that);
    return false;
  }

  public boolean equals(AddToWishlistRequest that) {
    if (that == null)
      return false;

    boolean this_present_userId = true;
    boolean that_present_userId = true;
    if (this_present_userId || that_present_userId) {
      if (!(this_present_userId && that_present_userId))
        return false;
      if (this.userId != that.userId)
        return false;
    }

    boolean this_present_wineId = true;
    boolean that_present_wineId = true;
    if (this_present_wineId || that_present_wineId) {
      if (!(this_present_wineId && that_present_wineId))
        return false;
      if (this.wineId != that.wineId)
        return false;
    }

    boolean this_present_enabled = true;
    boolean that_present_enabled = true;
    if (this_present_enabled || that_present_enabled) {
      if (!(this_present_enabled && that_present_enabled))
        return false;
      if (this.enabled != that.enabled)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_userId = true;
    list.add(present_userId);
    if (present_userId)
      list.add(userId);

    boolean present_wineId = true;
    list.add(present_wineId);
    if (present_wineId)
      list.add(wineId);

    boolean present_enabled = true;
    list.add(present_enabled);
    if (present_enabled)
      list.add(enabled);

    return list.hashCode();
  }

  @Override
  public int compareTo(AddToWishlistRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

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
    lastComparison = Boolean.valueOf(isSetWineId()).compareTo(other.isSetWineId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetWineId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.wineId, other.wineId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetEnabled()).compareTo(other.isSetEnabled());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEnabled()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.enabled, other.enabled);
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
    StringBuilder sb = new StringBuilder("AddToWishlistRequest(");
    boolean first = true;

    sb.append("userId:");
    sb.append(this.userId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("wineId:");
    sb.append(this.wineId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("enabled:");
    sb.append(this.enabled);
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

  private static class AddToWishlistRequestStandardSchemeFactory implements SchemeFactory {
    public AddToWishlistRequestStandardScheme getScheme() {
      return new AddToWishlistRequestStandardScheme();
    }
  }

  private static class AddToWishlistRequestStandardScheme extends StandardScheme<AddToWishlistRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, AddToWishlistRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // USER_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.userId = iprot.readI32();
              struct.setUserIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // WINE_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.wineId = iprot.readI32();
              struct.setWineIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // ENABLED
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.enabled = iprot.readBool();
              struct.setEnabledIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, AddToWishlistRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(USER_ID_FIELD_DESC);
      oprot.writeI32(struct.userId);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(WINE_ID_FIELD_DESC);
      oprot.writeI32(struct.wineId);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(ENABLED_FIELD_DESC);
      oprot.writeBool(struct.enabled);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class AddToWishlistRequestTupleSchemeFactory implements SchemeFactory {
    public AddToWishlistRequestTupleScheme getScheme() {
      return new AddToWishlistRequestTupleScheme();
    }
  }

  private static class AddToWishlistRequestTupleScheme extends TupleScheme<AddToWishlistRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, AddToWishlistRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetUserId()) {
        optionals.set(0);
      }
      if (struct.isSetWineId()) {
        optionals.set(1);
      }
      if (struct.isSetEnabled()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetUserId()) {
        oprot.writeI32(struct.userId);
      }
      if (struct.isSetWineId()) {
        oprot.writeI32(struct.wineId);
      }
      if (struct.isSetEnabled()) {
        oprot.writeBool(struct.enabled);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, AddToWishlistRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.userId = iprot.readI32();
        struct.setUserIdIsSet(true);
      }
      if (incoming.get(1)) {
        struct.wineId = iprot.readI32();
        struct.setWineIdIsSet(true);
      }
      if (incoming.get(2)) {
        struct.enabled = iprot.readBool();
        struct.setEnabledIsSet(true);
      }
    }
  }

}

