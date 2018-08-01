package com.ubitronix.uiem.execution.adapter.dlms.cosem.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SnObjectInfo;
import org.openmuc.jdlms.datatypes.CosemDateFormat;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.CosemDateFormat.Field;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.openmuc.jdlms.interfaceclass.attribute.ClockAttribute;
import org.openmuc.jdlms.interfaceclass.attribute.DemandRegisterAttribute;
import org.openmuc.jdlms.interfaceclass.attribute.ExtendedRegisterAttribute;
import org.openmuc.jdlms.interfaceclass.attribute.RegisterAttribute;

public class RegisterReadingMapper {
  private Map<ObisCode, SnObjectInfo> snObjectMapping;

  public RegisterReadingMapper(Map<ObisCode, SnObjectInfo> snObjectMapping) {
    super();
    this.snObjectMapping = snObjectMapping;
  }

  public List <AttributeAddress> buildRegisterReadingRequest(ObisCode obisCode) throws IOException {
    SnObjectInfo info = this.snObjectMapping.get(obisCode);
    
    if (info == null) {
      throw new RuntimeException("Unsupported register " + obisCode.asShortObisCodeString());
    }
    
    // TODO Read scaler unit and multiplier only once with snObjectMapping when initializing the device
    List <AttributeAddress> attributeList = new ArrayList<AttributeAddress>();
    if (info.getClassId() == InterfaceClass.REGISTER.id()) {
      attributeList.addAll(buildClockTimeReadingRequest());      
      attributeList.add(new AttributeAddress(RegisterAttribute.SCALER_UNIT, obisCode));
      // TODO remove all deprecated methods to not use the enums but the concrete values
      // Above deprecated attribute for scaler unit might look like this
      // attributeList.add(new AttributeAddress(3, obisCode, 3));
      attributeList.add(new AttributeAddress(RegisterAttribute.VALUE, obisCode));
    }
    else if (info.getClassId() == InterfaceClass.EXTENDED_REGISTER.id()) {
      attributeList.add(new AttributeAddress(ExtendedRegisterAttribute.SCALER_UNIT, obisCode));
      attributeList.add(new AttributeAddress(ExtendedRegisterAttribute.VALUE, obisCode));
      attributeList.add(new AttributeAddress(ExtendedRegisterAttribute.CAPTURE_TIME, obisCode));
      attributeList.add(new AttributeAddress(ExtendedRegisterAttribute.STATUS, obisCode));
    }
    else if (info.getClassId() == InterfaceClass.DEMAND_REGISTER.id()) {
      attributeList.add(new AttributeAddress(DemandRegisterAttribute.SCALER_UNIT, obisCode));
      attributeList.add(new AttributeAddress(DemandRegisterAttribute.CURRENT_AVERAGE_VALUE, obisCode));
      attributeList.add(new AttributeAddress(DemandRegisterAttribute.LAST_AVERAGE_VALUE, obisCode));
      attributeList.add(new AttributeAddress(DemandRegisterAttribute.CAPTURE_TIME, obisCode));
      attributeList.add(new AttributeAddress(DemandRegisterAttribute.PERIOD, obisCode));
      attributeList.add(new AttributeAddress(DemandRegisterAttribute.STATUS, obisCode));
      attributeList.add(new AttributeAddress(DemandRegisterAttribute.START_TIME_CURRENT, obisCode));
      attributeList.add(new AttributeAddress(DemandRegisterAttribute.PERIOD, obisCode));
      attributeList.add(new AttributeAddress(DemandRegisterAttribute.NUMBER_OF_PERIODS, obisCode));
    }
    else {
      throw new RuntimeException("Register with obis " + obisCode.asShortObisCodeString() + " is not an register");
    }
    
    return attributeList;
  }

  // SCALER_UNIT is a complex type including the the multiplier (0 for 1.0, -1 for 0.1, -2 for 0.01, 1 for 10.0 etc) as 1st element
  // and unit as 2nd element 
  // Mapping siehe https://github.com/Gurux/Gurux.DLMS.Net/blob/master/Development/Enums/Unit.cs
  // e.g. 30 is ActiveEnergy Wh
  private Integer getUnit(GetResult res) {
    // List<DataObject> (DataObject.Type.TRUCTURE)
    List<DataObject> objectList = res.getResultData().getValue();
    // Integer (DataObject.Type.ENUMERATE)
    return objectList.get(1).getValue();
 }
  
  // SCALER_UNIT is a complex type including the the multiplier (0 for 1.0, -1 for 0.1, -2 for 0.01, 1 for 10.0 etc) as 1st element
  // and unit as 2nd element 
  private Integer getScaler(GetResult res) {
    // List<DataObject> (DataObject.Type.TRUCTURE)
    List<DataObject> objectList = res.getResultData().getValue();
    // BYTE (DataObject.Type.INTEGER)
    Byte scaler = objectList.get(0).getValue();
    return scaler.intValue();
  }
  
  private Long getValue(GetResult res) {
    // LONG (DataObject.Type.LONG64)
    return res.getResultData().getValue();
  }
  
  public Date getDate(GetResult res) {
    DataObject resultData = res.getResultData();
    if (resultData.isCosemDateFormat()) {
      CosemDateFormat value = resultData.getValue();
      
      Calendar cal = Calendar.getInstance();
      cal.clear();
      cal.set(value.get(Field.YEAR), value.get(Field.MONTH), value.get(Field.DAY_OF_MONTH), value.get(Field.HOUR), value.get(Field.MINUTE), value.get(Field.SECOND));
      
      return cal.getTime();
    }
    else if (resultData.getType() == Type.OCTET_STRING) {
      byte[] octetString = resultData.getValue();
      CosemDateTime value = CosemDateTime.decode(octetString);
      
      Calendar cal = Calendar.getInstance();
      cal.clear();
      cal.set(value.get(Field.YEAR), value.get(Field.MONTH), value.get(Field.DAY_OF_MONTH), value.get(Field.HOUR), value.get(Field.MINUTE), value.get(Field.SECOND));
      
      return cal.getTime();
    }
    
    throw new RuntimeException("Unable to decode GetResult for ClockAttribute.TIME with Type  " + resultData.getType());
  }

  
  public List <AttributeAddress> buildClockTimeReadingRequest() throws IOException {
    ObisCode obisCode = ObisCodeMapper.getClockTimeObisCode();
    SnObjectInfo info = this.snObjectMapping.get(obisCode);
    
    if (info == null) {
      throw new RuntimeException("Unsupported register " + obisCode.asShortObisCodeString());
    }
    
    List <AttributeAddress> attributeList = new ArrayList<AttributeAddress>();
    if (info.getClassId() == InterfaceClass.CLOCK.id()) {
      attributeList.add(new AttributeAddress(ClockAttribute.TIME, ObisCodeMapper.getClockTimeObisCode()));      
    }
    else {
      throw new RuntimeException("Register with obis " + obisCode.asShortObisCodeString() + " is not supported");
    }
    
    return attributeList;
  }
}
