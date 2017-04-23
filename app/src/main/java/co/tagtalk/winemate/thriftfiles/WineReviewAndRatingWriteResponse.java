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
public class WineReviewAndRatingWriteResponse implements org.apache.thrift.TBase<WineReviewAndRatingWriteResponse, WineReviewAndRatingWriteResponse._Fields>, java.io.Serializable, Cloneable, Comparable<WineReviewAndRatingWriteResponse> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("WineReviewAndRatingWriteResponse");

  private static final org.apache.thrift.protocol.TField IS_SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField("isSuccess", org.apache.thrift.protocol.TType.BOOL, (short)1);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new WineReviewAndRatingWriteResponseStandardSchemeFactory());
    schemes.put(TupleScheme.class, new WineReviewAndRatingWriteResponseTupleSchemeFactory());
  }

  public boolean isSuccess; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    IS_SUCCESS((short)1, "isSuccess");

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
        case 1: // IS_SUCCESS
          return IS_SUCCESS;
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
  private static final int __ISSUCCESS_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.IS_SUCCESS, new org.apache.thrift.meta_data.FieldMetaData("isSuccess", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(WineReviewAndRatingWriteResponse.class, metaDataMap);
  }

  public WineReviewAndRatingWriteResponse() {
  }

  public WineReviewAndRatingWriteResponse(
    boolean isSuccess)
  {
    this();
    this.isSuccess = isSuccess;
    setIsSuccessIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public WineReviewAndRatingWriteResponse(WineReviewAndRatingWriteResponse other) {
    __isset_bitfield = other.__isset_bitfield;
    this.isSuccess = other.isSuccess;
  }

  public WineReviewAndRatingWriteResponse deepCopy() {
    return new WineReviewAndRatingWriteResponse(this);
  }

  @Override
  public void clear() {
    setIsSuccessIsSet(false);
    this.isSuccess = false;
  }

  public boolean isIsSuccess() {
    return this.isSuccess;
  }

  public WineReviewAndRatingWriteResponse setIsSuccess(boolean isSuccess) {
    this.isSuccess = isSuccess;
    setIsSuccessIsSet(true);
    return this;
  }

  public void unsetIsSuccess() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ISSUCCESS_ISSET_ID);
  }

  /** Returns true if field isSuccess is set (has been assigned a value) and false otherwise */
  public boolean isSetIsSuccess() {
    return EncodingUtils.testBit(__isset_bitfield, __ISSUCCESS_ISSET_ID);
  }

  public void setIsSuccessIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ISSUCCESS_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case IS_SUCCESS:
      if (value == null) {
        unsetIsSuccess();
      } else {
        setIsSuccess((Boolean)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case IS_SUCCESS:
      return isIsSuccess();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case IS_SUCCESS:
      return isSetIsSuccess();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof WineReviewAndRatingWriteResponse)
      return this.equals((WineReviewAndRatingWriteResponse)that);
    return false;
  }

  public boolean equals(WineReviewAndRatingWriteResponse that) {
    if (that == null)
      return false;

    boolean this_present_isSuccess = true;
    boolean that_present_isSuccess = true;
    if (this_present_isSuccess || that_present_isSuccess) {
      if (!(this_present_isSuccess && that_present_isSuccess))
        return false;
      if (this.isSuccess != that.isSuccess)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_isSuccess = true;
    list.add(present_isSuccess);
    if (present_isSuccess)
      list.add(isSuccess);

    return list.hashCode();
  }

  @Override
  public int compareTo(WineReviewAndRatingWriteResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetIsSuccess()).compareTo(other.isSetIsSuccess());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIsSuccess()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.isSuccess, other.isSuccess);
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
    StringBuilder sb = new StringBuilder("WineReviewAndRatingWriteResponse(");
    boolean first = true;

    sb.append("isSuccess:");
    sb.append(this.isSuccess);
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

  private static class WineReviewAndRatingWriteResponseStandardSchemeFactory implements SchemeFactory {
    public WineReviewAndRatingWriteResponseStandardScheme getScheme() {
      return new WineReviewAndRatingWriteResponseStandardScheme();
    }
  }

  private static class WineReviewAndRatingWriteResponseStandardScheme extends StandardScheme<WineReviewAndRatingWriteResponse> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, WineReviewAndRatingWriteResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // IS_SUCCESS
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.isSuccess = iprot.readBool();
              struct.setIsSuccessIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, WineReviewAndRatingWriteResponse struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(IS_SUCCESS_FIELD_DESC);
      oprot.writeBool(struct.isSuccess);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class WineReviewAndRatingWriteResponseTupleSchemeFactory implements SchemeFactory {
    public WineReviewAndRatingWriteResponseTupleScheme getScheme() {
      return new WineReviewAndRatingWriteResponseTupleScheme();
    }
  }

  private static class WineReviewAndRatingWriteResponseTupleScheme extends TupleScheme<WineReviewAndRatingWriteResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, WineReviewAndRatingWriteResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetIsSuccess()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetIsSuccess()) {
        oprot.writeBool(struct.isSuccess);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, WineReviewAndRatingWriteResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        struct.isSuccess = iprot.readBool();
        struct.setIsSuccessIsSet(true);
      }
    }
  }

}

