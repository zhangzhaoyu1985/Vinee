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
public class RewardRedeemResponse implements org.apache.thrift.TBase<RewardRedeemResponse, RewardRedeemResponse._Fields>, java.io.Serializable, Cloneable, Comparable<RewardRedeemResponse> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("RewardRedeemResponse");

  private static final org.apache.thrift.protocol.TField RESP_CODE_FIELD_DESC = new org.apache.thrift.protocol.TField("resp_code", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField REMAINING_POINTS_FIELD_DESC = new org.apache.thrift.protocol.TField("remainingPoints", org.apache.thrift.protocol.TType.I32, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new RewardRedeemResponseStandardSchemeFactory());
    schemes.put(TupleScheme.class, new RewardRedeemResponseTupleSchemeFactory());
  }

  /**
   * 
   * @see RewardRedeemResponseCode
   */
  public RewardRedeemResponseCode resp_code; // required
  public int remainingPoints; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     * @see RewardRedeemResponseCode
     */
    RESP_CODE((short)1, "resp_code"),
    REMAINING_POINTS((short)2, "remainingPoints");

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
        case 1: // RESP_CODE
          return RESP_CODE;
        case 2: // REMAINING_POINTS
          return REMAINING_POINTS;
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
  private static final int __REMAININGPOINTS_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.RESP_CODE, new org.apache.thrift.meta_data.FieldMetaData("resp_code", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, RewardRedeemResponseCode.class)));
    tmpMap.put(_Fields.REMAINING_POINTS, new org.apache.thrift.meta_data.FieldMetaData("remainingPoints", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(RewardRedeemResponse.class, metaDataMap);
  }

  public RewardRedeemResponse() {
  }

  public RewardRedeemResponse(
    RewardRedeemResponseCode resp_code,
    int remainingPoints)
  {
    this();
    this.resp_code = resp_code;
    this.remainingPoints = remainingPoints;
    setRemainingPointsIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public RewardRedeemResponse(RewardRedeemResponse other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetResp_code()) {
      this.resp_code = other.resp_code;
    }
    this.remainingPoints = other.remainingPoints;
  }

  public RewardRedeemResponse deepCopy() {
    return new RewardRedeemResponse(this);
  }

  @Override
  public void clear() {
    this.resp_code = null;
    setRemainingPointsIsSet(false);
    this.remainingPoints = 0;
  }

  /**
   * 
   * @see RewardRedeemResponseCode
   */
  public RewardRedeemResponseCode getResp_code() {
    return this.resp_code;
  }

  /**
   * 
   * @see RewardRedeemResponseCode
   */
  public RewardRedeemResponse setResp_code(RewardRedeemResponseCode resp_code) {
    this.resp_code = resp_code;
    return this;
  }

  public void unsetResp_code() {
    this.resp_code = null;
  }

  /** Returns true if field resp_code is set (has been assigned a value) and false otherwise */
  public boolean isSetResp_code() {
    return this.resp_code != null;
  }

  public void setResp_codeIsSet(boolean value) {
    if (!value) {
      this.resp_code = null;
    }
  }

  public int getRemainingPoints() {
    return this.remainingPoints;
  }

  public RewardRedeemResponse setRemainingPoints(int remainingPoints) {
    this.remainingPoints = remainingPoints;
    setRemainingPointsIsSet(true);
    return this;
  }

  public void unsetRemainingPoints() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __REMAININGPOINTS_ISSET_ID);
  }

  /** Returns true if field remainingPoints is set (has been assigned a value) and false otherwise */
  public boolean isSetRemainingPoints() {
    return EncodingUtils.testBit(__isset_bitfield, __REMAININGPOINTS_ISSET_ID);
  }

  public void setRemainingPointsIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __REMAININGPOINTS_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case RESP_CODE:
      if (value == null) {
        unsetResp_code();
      } else {
        setResp_code((RewardRedeemResponseCode)value);
      }
      break;

    case REMAINING_POINTS:
      if (value == null) {
        unsetRemainingPoints();
      } else {
        setRemainingPoints((Integer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case RESP_CODE:
      return getResp_code();

    case REMAINING_POINTS:
      return getRemainingPoints();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case RESP_CODE:
      return isSetResp_code();
    case REMAINING_POINTS:
      return isSetRemainingPoints();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof RewardRedeemResponse)
      return this.equals((RewardRedeemResponse)that);
    return false;
  }

  public boolean equals(RewardRedeemResponse that) {
    if (that == null)
      return false;

    boolean this_present_resp_code = true && this.isSetResp_code();
    boolean that_present_resp_code = true && that.isSetResp_code();
    if (this_present_resp_code || that_present_resp_code) {
      if (!(this_present_resp_code && that_present_resp_code))
        return false;
      if (!this.resp_code.equals(that.resp_code))
        return false;
    }

    boolean this_present_remainingPoints = true;
    boolean that_present_remainingPoints = true;
    if (this_present_remainingPoints || that_present_remainingPoints) {
      if (!(this_present_remainingPoints && that_present_remainingPoints))
        return false;
      if (this.remainingPoints != that.remainingPoints)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_resp_code = true && (isSetResp_code());
    list.add(present_resp_code);
    if (present_resp_code)
      list.add(resp_code.getValue());

    boolean present_remainingPoints = true;
    list.add(present_remainingPoints);
    if (present_remainingPoints)
      list.add(remainingPoints);

    return list.hashCode();
  }

  @Override
  public int compareTo(RewardRedeemResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetResp_code()).compareTo(other.isSetResp_code());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetResp_code()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.resp_code, other.resp_code);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRemainingPoints()).compareTo(other.isSetRemainingPoints());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRemainingPoints()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.remainingPoints, other.remainingPoints);
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
    StringBuilder sb = new StringBuilder("RewardRedeemResponse(");
    boolean first = true;

    sb.append("resp_code:");
    if (this.resp_code == null) {
      sb.append("null");
    } else {
      sb.append(this.resp_code);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("remainingPoints:");
    sb.append(this.remainingPoints);
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

  private static class RewardRedeemResponseStandardSchemeFactory implements SchemeFactory {
    public RewardRedeemResponseStandardScheme getScheme() {
      return new RewardRedeemResponseStandardScheme();
    }
  }

  private static class RewardRedeemResponseStandardScheme extends StandardScheme<RewardRedeemResponse> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, RewardRedeemResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // RESP_CODE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.resp_code = co.tagtalk.winemate.thriftfiles.RewardRedeemResponseCode.findByValue(iprot.readI32());
              struct.setResp_codeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // REMAINING_POINTS
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.remainingPoints = iprot.readI32();
              struct.setRemainingPointsIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, RewardRedeemResponse struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.resp_code != null) {
        oprot.writeFieldBegin(RESP_CODE_FIELD_DESC);
        oprot.writeI32(struct.resp_code.getValue());
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(REMAINING_POINTS_FIELD_DESC);
      oprot.writeI32(struct.remainingPoints);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class RewardRedeemResponseTupleSchemeFactory implements SchemeFactory {
    public RewardRedeemResponseTupleScheme getScheme() {
      return new RewardRedeemResponseTupleScheme();
    }
  }

  private static class RewardRedeemResponseTupleScheme extends TupleScheme<RewardRedeemResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, RewardRedeemResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetResp_code()) {
        optionals.set(0);
      }
      if (struct.isSetRemainingPoints()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetResp_code()) {
        oprot.writeI32(struct.resp_code.getValue());
      }
      if (struct.isSetRemainingPoints()) {
        oprot.writeI32(struct.remainingPoints);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, RewardRedeemResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.resp_code = co.tagtalk.winemate.thriftfiles.RewardRedeemResponseCode.findByValue(iprot.readI32());
        struct.setResp_codeIsSet(true);
      }
      if (incoming.get(1)) {
        struct.remainingPoints = iprot.readI32();
        struct.setRemainingPointsIsSet(true);
      }
    }
  }

}

