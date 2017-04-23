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
public class Address implements org.apache.thrift.TBase<Address, Address._Fields>, java.io.Serializable, Cloneable, Comparable<Address> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Address");

  private static final org.apache.thrift.protocol.TField PROVINCE_FIELD_DESC = new org.apache.thrift.protocol.TField("province", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField CITY_FIELD_DESC = new org.apache.thrift.protocol.TField("city", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField STREET_FIELD_DESC = new org.apache.thrift.protocol.TField("street", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField ZIP_CODE_FIELD_DESC = new org.apache.thrift.protocol.TField("zipCode", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField PHONE_NUMBER_FIELD_DESC = new org.apache.thrift.protocol.TField("phoneNumber", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField EMAIL_FIELD_DESC = new org.apache.thrift.protocol.TField("email", org.apache.thrift.protocol.TType.STRING, (short)6);
  private static final org.apache.thrift.protocol.TField FULL_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("fullName", org.apache.thrift.protocol.TType.STRING, (short)7);
  private static final org.apache.thrift.protocol.TField COUNTRY_FIELD_DESC = new org.apache.thrift.protocol.TField("country", org.apache.thrift.protocol.TType.STRING, (short)8);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new AddressStandardSchemeFactory());
    schemes.put(TupleScheme.class, new AddressTupleSchemeFactory());
  }

  public String province; // required
  public String city; // required
  public String street; // required
  public String zipCode; // required
  public String phoneNumber; // required
  public String email; // required
  public String fullName; // required
  public String country; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    PROVINCE((short)1, "province"),
    CITY((short)2, "city"),
    STREET((short)3, "street"),
    ZIP_CODE((short)4, "zipCode"),
    PHONE_NUMBER((short)5, "phoneNumber"),
    EMAIL((short)6, "email"),
    FULL_NAME((short)7, "fullName"),
    COUNTRY((short)8, "country");

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
        case 1: // PROVINCE
          return PROVINCE;
        case 2: // CITY
          return CITY;
        case 3: // STREET
          return STREET;
        case 4: // ZIP_CODE
          return ZIP_CODE;
        case 5: // PHONE_NUMBER
          return PHONE_NUMBER;
        case 6: // EMAIL
          return EMAIL;
        case 7: // FULL_NAME
          return FULL_NAME;
        case 8: // COUNTRY
          return COUNTRY;
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
    tmpMap.put(_Fields.PROVINCE, new org.apache.thrift.meta_data.FieldMetaData("province", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.CITY, new org.apache.thrift.meta_data.FieldMetaData("city", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.STREET, new org.apache.thrift.meta_data.FieldMetaData("street", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ZIP_CODE, new org.apache.thrift.meta_data.FieldMetaData("zipCode", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PHONE_NUMBER, new org.apache.thrift.meta_data.FieldMetaData("phoneNumber", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.EMAIL, new org.apache.thrift.meta_data.FieldMetaData("email", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.FULL_NAME, new org.apache.thrift.meta_data.FieldMetaData("fullName", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.COUNTRY, new org.apache.thrift.meta_data.FieldMetaData("country", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Address.class, metaDataMap);
  }

  public Address() {
  }

  public Address(
    String province,
    String city,
    String street,
    String zipCode,
    String phoneNumber,
    String email,
    String fullName,
    String country)
  {
    this();
    this.province = province;
    this.city = city;
    this.street = street;
    this.zipCode = zipCode;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.fullName = fullName;
    this.country = country;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Address(Address other) {
    if (other.isSetProvince()) {
      this.province = other.province;
    }
    if (other.isSetCity()) {
      this.city = other.city;
    }
    if (other.isSetStreet()) {
      this.street = other.street;
    }
    if (other.isSetZipCode()) {
      this.zipCode = other.zipCode;
    }
    if (other.isSetPhoneNumber()) {
      this.phoneNumber = other.phoneNumber;
    }
    if (other.isSetEmail()) {
      this.email = other.email;
    }
    if (other.isSetFullName()) {
      this.fullName = other.fullName;
    }
    if (other.isSetCountry()) {
      this.country = other.country;
    }
  }

  public Address deepCopy() {
    return new Address(this);
  }

  @Override
  public void clear() {
    this.province = null;
    this.city = null;
    this.street = null;
    this.zipCode = null;
    this.phoneNumber = null;
    this.email = null;
    this.fullName = null;
    this.country = null;
  }

  public String getProvince() {
    return this.province;
  }

  public Address setProvince(String province) {
    this.province = province;
    return this;
  }

  public void unsetProvince() {
    this.province = null;
  }

  /** Returns true if field province is set (has been assigned a value) and false otherwise */
  public boolean isSetProvince() {
    return this.province != null;
  }

  public void setProvinceIsSet(boolean value) {
    if (!value) {
      this.province = null;
    }
  }

  public String getCity() {
    return this.city;
  }

  public Address setCity(String city) {
    this.city = city;
    return this;
  }

  public void unsetCity() {
    this.city = null;
  }

  /** Returns true if field city is set (has been assigned a value) and false otherwise */
  public boolean isSetCity() {
    return this.city != null;
  }

  public void setCityIsSet(boolean value) {
    if (!value) {
      this.city = null;
    }
  }

  public String getStreet() {
    return this.street;
  }

  public Address setStreet(String street) {
    this.street = street;
    return this;
  }

  public void unsetStreet() {
    this.street = null;
  }

  /** Returns true if field street is set (has been assigned a value) and false otherwise */
  public boolean isSetStreet() {
    return this.street != null;
  }

  public void setStreetIsSet(boolean value) {
    if (!value) {
      this.street = null;
    }
  }

  public String getZipCode() {
    return this.zipCode;
  }

  public Address setZipCode(String zipCode) {
    this.zipCode = zipCode;
    return this;
  }

  public void unsetZipCode() {
    this.zipCode = null;
  }

  /** Returns true if field zipCode is set (has been assigned a value) and false otherwise */
  public boolean isSetZipCode() {
    return this.zipCode != null;
  }

  public void setZipCodeIsSet(boolean value) {
    if (!value) {
      this.zipCode = null;
    }
  }

  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  public Address setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  public void unsetPhoneNumber() {
    this.phoneNumber = null;
  }

  /** Returns true if field phoneNumber is set (has been assigned a value) and false otherwise */
  public boolean isSetPhoneNumber() {
    return this.phoneNumber != null;
  }

  public void setPhoneNumberIsSet(boolean value) {
    if (!value) {
      this.phoneNumber = null;
    }
  }

  public String getEmail() {
    return this.email;
  }

  public Address setEmail(String email) {
    this.email = email;
    return this;
  }

  public void unsetEmail() {
    this.email = null;
  }

  /** Returns true if field email is set (has been assigned a value) and false otherwise */
  public boolean isSetEmail() {
    return this.email != null;
  }

  public void setEmailIsSet(boolean value) {
    if (!value) {
      this.email = null;
    }
  }

  public String getFullName() {
    return this.fullName;
  }

  public Address setFullName(String fullName) {
    this.fullName = fullName;
    return this;
  }

  public void unsetFullName() {
    this.fullName = null;
  }

  /** Returns true if field fullName is set (has been assigned a value) and false otherwise */
  public boolean isSetFullName() {
    return this.fullName != null;
  }

  public void setFullNameIsSet(boolean value) {
    if (!value) {
      this.fullName = null;
    }
  }

  public String getCountry() {
    return this.country;
  }

  public Address setCountry(String country) {
    this.country = country;
    return this;
  }

  public void unsetCountry() {
    this.country = null;
  }

  /** Returns true if field country is set (has been assigned a value) and false otherwise */
  public boolean isSetCountry() {
    return this.country != null;
  }

  public void setCountryIsSet(boolean value) {
    if (!value) {
      this.country = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case PROVINCE:
      if (value == null) {
        unsetProvince();
      } else {
        setProvince((String)value);
      }
      break;

    case CITY:
      if (value == null) {
        unsetCity();
      } else {
        setCity((String)value);
      }
      break;

    case STREET:
      if (value == null) {
        unsetStreet();
      } else {
        setStreet((String)value);
      }
      break;

    case ZIP_CODE:
      if (value == null) {
        unsetZipCode();
      } else {
        setZipCode((String)value);
      }
      break;

    case PHONE_NUMBER:
      if (value == null) {
        unsetPhoneNumber();
      } else {
        setPhoneNumber((String)value);
      }
      break;

    case EMAIL:
      if (value == null) {
        unsetEmail();
      } else {
        setEmail((String)value);
      }
      break;

    case FULL_NAME:
      if (value == null) {
        unsetFullName();
      } else {
        setFullName((String)value);
      }
      break;

    case COUNTRY:
      if (value == null) {
        unsetCountry();
      } else {
        setCountry((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case PROVINCE:
      return getProvince();

    case CITY:
      return getCity();

    case STREET:
      return getStreet();

    case ZIP_CODE:
      return getZipCode();

    case PHONE_NUMBER:
      return getPhoneNumber();

    case EMAIL:
      return getEmail();

    case FULL_NAME:
      return getFullName();

    case COUNTRY:
      return getCountry();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case PROVINCE:
      return isSetProvince();
    case CITY:
      return isSetCity();
    case STREET:
      return isSetStreet();
    case ZIP_CODE:
      return isSetZipCode();
    case PHONE_NUMBER:
      return isSetPhoneNumber();
    case EMAIL:
      return isSetEmail();
    case FULL_NAME:
      return isSetFullName();
    case COUNTRY:
      return isSetCountry();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Address)
      return this.equals((Address)that);
    return false;
  }

  public boolean equals(Address that) {
    if (that == null)
      return false;

    boolean this_present_province = true && this.isSetProvince();
    boolean that_present_province = true && that.isSetProvince();
    if (this_present_province || that_present_province) {
      if (!(this_present_province && that_present_province))
        return false;
      if (!this.province.equals(that.province))
        return false;
    }

    boolean this_present_city = true && this.isSetCity();
    boolean that_present_city = true && that.isSetCity();
    if (this_present_city || that_present_city) {
      if (!(this_present_city && that_present_city))
        return false;
      if (!this.city.equals(that.city))
        return false;
    }

    boolean this_present_street = true && this.isSetStreet();
    boolean that_present_street = true && that.isSetStreet();
    if (this_present_street || that_present_street) {
      if (!(this_present_street && that_present_street))
        return false;
      if (!this.street.equals(that.street))
        return false;
    }

    boolean this_present_zipCode = true && this.isSetZipCode();
    boolean that_present_zipCode = true && that.isSetZipCode();
    if (this_present_zipCode || that_present_zipCode) {
      if (!(this_present_zipCode && that_present_zipCode))
        return false;
      if (!this.zipCode.equals(that.zipCode))
        return false;
    }

    boolean this_present_phoneNumber = true && this.isSetPhoneNumber();
    boolean that_present_phoneNumber = true && that.isSetPhoneNumber();
    if (this_present_phoneNumber || that_present_phoneNumber) {
      if (!(this_present_phoneNumber && that_present_phoneNumber))
        return false;
      if (!this.phoneNumber.equals(that.phoneNumber))
        return false;
    }

    boolean this_present_email = true && this.isSetEmail();
    boolean that_present_email = true && that.isSetEmail();
    if (this_present_email || that_present_email) {
      if (!(this_present_email && that_present_email))
        return false;
      if (!this.email.equals(that.email))
        return false;
    }

    boolean this_present_fullName = true && this.isSetFullName();
    boolean that_present_fullName = true && that.isSetFullName();
    if (this_present_fullName || that_present_fullName) {
      if (!(this_present_fullName && that_present_fullName))
        return false;
      if (!this.fullName.equals(that.fullName))
        return false;
    }

    boolean this_present_country = true && this.isSetCountry();
    boolean that_present_country = true && that.isSetCountry();
    if (this_present_country || that_present_country) {
      if (!(this_present_country && that_present_country))
        return false;
      if (!this.country.equals(that.country))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_province = true && (isSetProvince());
    list.add(present_province);
    if (present_province)
      list.add(province);

    boolean present_city = true && (isSetCity());
    list.add(present_city);
    if (present_city)
      list.add(city);

    boolean present_street = true && (isSetStreet());
    list.add(present_street);
    if (present_street)
      list.add(street);

    boolean present_zipCode = true && (isSetZipCode());
    list.add(present_zipCode);
    if (present_zipCode)
      list.add(zipCode);

    boolean present_phoneNumber = true && (isSetPhoneNumber());
    list.add(present_phoneNumber);
    if (present_phoneNumber)
      list.add(phoneNumber);

    boolean present_email = true && (isSetEmail());
    list.add(present_email);
    if (present_email)
      list.add(email);

    boolean present_fullName = true && (isSetFullName());
    list.add(present_fullName);
    if (present_fullName)
      list.add(fullName);

    boolean present_country = true && (isSetCountry());
    list.add(present_country);
    if (present_country)
      list.add(country);

    return list.hashCode();
  }

  @Override
  public int compareTo(Address other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetProvince()).compareTo(other.isSetProvince());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetProvince()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.province, other.province);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetCity()).compareTo(other.isSetCity());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCity()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.city, other.city);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetStreet()).compareTo(other.isSetStreet());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStreet()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.street, other.street);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetZipCode()).compareTo(other.isSetZipCode());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetZipCode()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.zipCode, other.zipCode);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPhoneNumber()).compareTo(other.isSetPhoneNumber());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPhoneNumber()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.phoneNumber, other.phoneNumber);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetEmail()).compareTo(other.isSetEmail());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEmail()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.email, other.email);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetFullName()).compareTo(other.isSetFullName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFullName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.fullName, other.fullName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetCountry()).compareTo(other.isSetCountry());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCountry()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.country, other.country);
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
    StringBuilder sb = new StringBuilder("Address(");
    boolean first = true;

    sb.append("province:");
    if (this.province == null) {
      sb.append("null");
    } else {
      sb.append(this.province);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("city:");
    if (this.city == null) {
      sb.append("null");
    } else {
      sb.append(this.city);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("street:");
    if (this.street == null) {
      sb.append("null");
    } else {
      sb.append(this.street);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("zipCode:");
    if (this.zipCode == null) {
      sb.append("null");
    } else {
      sb.append(this.zipCode);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("phoneNumber:");
    if (this.phoneNumber == null) {
      sb.append("null");
    } else {
      sb.append(this.phoneNumber);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("email:");
    if (this.email == null) {
      sb.append("null");
    } else {
      sb.append(this.email);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("fullName:");
    if (this.fullName == null) {
      sb.append("null");
    } else {
      sb.append(this.fullName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("country:");
    if (this.country == null) {
      sb.append("null");
    } else {
      sb.append(this.country);
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

  private static class AddressStandardSchemeFactory implements SchemeFactory {
    public AddressStandardScheme getScheme() {
      return new AddressStandardScheme();
    }
  }

  private static class AddressStandardScheme extends StandardScheme<Address> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Address struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // PROVINCE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.province = iprot.readString();
              struct.setProvinceIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // CITY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.city = iprot.readString();
              struct.setCityIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // STREET
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.street = iprot.readString();
              struct.setStreetIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // ZIP_CODE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.zipCode = iprot.readString();
              struct.setZipCodeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // PHONE_NUMBER
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.phoneNumber = iprot.readString();
              struct.setPhoneNumberIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // EMAIL
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.email = iprot.readString();
              struct.setEmailIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // FULL_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.fullName = iprot.readString();
              struct.setFullNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 8: // COUNTRY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.country = iprot.readString();
              struct.setCountryIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, Address struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.province != null) {
        oprot.writeFieldBegin(PROVINCE_FIELD_DESC);
        oprot.writeString(struct.province);
        oprot.writeFieldEnd();
      }
      if (struct.city != null) {
        oprot.writeFieldBegin(CITY_FIELD_DESC);
        oprot.writeString(struct.city);
        oprot.writeFieldEnd();
      }
      if (struct.street != null) {
        oprot.writeFieldBegin(STREET_FIELD_DESC);
        oprot.writeString(struct.street);
        oprot.writeFieldEnd();
      }
      if (struct.zipCode != null) {
        oprot.writeFieldBegin(ZIP_CODE_FIELD_DESC);
        oprot.writeString(struct.zipCode);
        oprot.writeFieldEnd();
      }
      if (struct.phoneNumber != null) {
        oprot.writeFieldBegin(PHONE_NUMBER_FIELD_DESC);
        oprot.writeString(struct.phoneNumber);
        oprot.writeFieldEnd();
      }
      if (struct.email != null) {
        oprot.writeFieldBegin(EMAIL_FIELD_DESC);
        oprot.writeString(struct.email);
        oprot.writeFieldEnd();
      }
      if (struct.fullName != null) {
        oprot.writeFieldBegin(FULL_NAME_FIELD_DESC);
        oprot.writeString(struct.fullName);
        oprot.writeFieldEnd();
      }
      if (struct.country != null) {
        oprot.writeFieldBegin(COUNTRY_FIELD_DESC);
        oprot.writeString(struct.country);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class AddressTupleSchemeFactory implements SchemeFactory {
    public AddressTupleScheme getScheme() {
      return new AddressTupleScheme();
    }
  }

  private static class AddressTupleScheme extends TupleScheme<Address> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Address struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetProvince()) {
        optionals.set(0);
      }
      if (struct.isSetCity()) {
        optionals.set(1);
      }
      if (struct.isSetStreet()) {
        optionals.set(2);
      }
      if (struct.isSetZipCode()) {
        optionals.set(3);
      }
      if (struct.isSetPhoneNumber()) {
        optionals.set(4);
      }
      if (struct.isSetEmail()) {
        optionals.set(5);
      }
      if (struct.isSetFullName()) {
        optionals.set(6);
      }
      if (struct.isSetCountry()) {
        optionals.set(7);
      }
      oprot.writeBitSet(optionals, 8);
      if (struct.isSetProvince()) {
        oprot.writeString(struct.province);
      }
      if (struct.isSetCity()) {
        oprot.writeString(struct.city);
      }
      if (struct.isSetStreet()) {
        oprot.writeString(struct.street);
      }
      if (struct.isSetZipCode()) {
        oprot.writeString(struct.zipCode);
      }
      if (struct.isSetPhoneNumber()) {
        oprot.writeString(struct.phoneNumber);
      }
      if (struct.isSetEmail()) {
        oprot.writeString(struct.email);
      }
      if (struct.isSetFullName()) {
        oprot.writeString(struct.fullName);
      }
      if (struct.isSetCountry()) {
        oprot.writeString(struct.country);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Address struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(8);
      if (incoming.get(0)) {
        struct.province = iprot.readString();
        struct.setProvinceIsSet(true);
      }
      if (incoming.get(1)) {
        struct.city = iprot.readString();
        struct.setCityIsSet(true);
      }
      if (incoming.get(2)) {
        struct.street = iprot.readString();
        struct.setStreetIsSet(true);
      }
      if (incoming.get(3)) {
        struct.zipCode = iprot.readString();
        struct.setZipCodeIsSet(true);
      }
      if (incoming.get(4)) {
        struct.phoneNumber = iprot.readString();
        struct.setPhoneNumberIsSet(true);
      }
      if (incoming.get(5)) {
        struct.email = iprot.readString();
        struct.setEmailIsSet(true);
      }
      if (incoming.get(6)) {
        struct.fullName = iprot.readString();
        struct.setFullNameIsSet(true);
      }
      if (incoming.get(7)) {
        struct.country = iprot.readString();
        struct.setCountryIsSet(true);
      }
    }
  }

}
