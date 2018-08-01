package com.ubitronix.uiem.dlms.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.SnObjectInfo;
import org.openmuc.jdlms.datatypes.CosemDateFormat;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.CosemDateFormat.Field;
import org.openmuc.jdlms.datatypes.CosemDateTime.ClockStatus;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.openmuc.jdlms.interfaceclass.attribute.ClockAttribute;

import com.ubitronix.uiem.execution.adapter.dlms.cosem.util.RegisterReadingMapper;

public class TestClient {
  private static final Log LOG = LogFactory.getLog(TestClient.class.getName());
  
  private static String IP_ADDRESS = "10.130.129.201";
  private static Integer PORT = 5200;
  private static Integer CLIENT_ADDRESS = 32;
  private static String PASSWORD = "00000000";
  private static Integer LOGICAL_DEVICE_ADDRESS = 1;
  private static Integer PHYSICAL_DEVICE_ADDRESS = 6713;

  // ATTENTION: The subsequent mapping is only true for SWiBi L&G E650
  private static Map<ObisCode, SnObjectInfo> SN_OBJECT_MAPPING = new LinkedHashMap<ObisCode, SnObjectInfo>();
  
  static {
    SnObjectInfo value = new SnObjectInfo(7392, 3, 0);
    ObisCode key = new ObisCode("1-1:1.8.0");
    SN_OBJECT_MAPPING.put(key, value);
    
    value = new SnObjectInfo(2992, 3, 0);
    key = new ObisCode("1-1:1.8.1");
    SN_OBJECT_MAPPING.put(key, value);
    
    value = new SnObjectInfo(3160, 3, 0);
    key = new ObisCode("1-1:1.8.2");
    SN_OBJECT_MAPPING.put(key, value);

    value = new SnObjectInfo(11200, 8, 0);
    key = new ObisCode("0-0:1.0.0");
    SN_OBJECT_MAPPING.put(key, value);
  }

  public void testOpenConnection() throws Exception {
    LOG.info("Running testOpenConnection");
    DlmsClient client = new DlmsClient(IP_ADDRESS, PORT, CLIENT_ADDRESS, PASSWORD, LOGICAL_DEVICE_ADDRESS, PHYSICAL_DEVICE_ADDRESS, SN_OBJECT_MAPPING);
    
    try {
      client.openConnection();
    }
    finally {
      if (client != null) {
        client.closeConnection();
      }
    }
  }
  
  public void testReadForwardActiveEnergyTotal() throws Exception {
    LOG.info("Running testReadForwardActiveEnergyTotal");
    DlmsClient client = new DlmsClient(IP_ADDRESS, PORT, CLIENT_ADDRESS, PASSWORD, LOGICAL_DEVICE_ADDRESS, PHYSICAL_DEVICE_ADDRESS, SN_OBJECT_MAPPING);
    
    try {
      client.openConnection();

      ObisCode obisCode = new ObisCode("1-1:1.8.0");
      LOG.info("Reading register with OBIS code " + obisCode.asShortObisCodeString());
      RegisterReadingMapper mapper = new RegisterReadingMapper(SN_OBJECT_MAPPING);
      List<AttributeAddress> attributeAddressList = mapper.buildRegisterReadingRequest(obisCode);
      
      List<GetResult> getResultList = client.get(attributeAddressList);
      SnObjectInfo info = SN_OBJECT_MAPPING.get(obisCode);
      
      if (info == null) {
        throw new RuntimeException("Unsupported register " + obisCode.asShortObisCodeString());
      }

      Integer scaler;
      Integer unit;
      Long value;
      Date date;
      if (info.getClassId() == InterfaceClass.REGISTER.id()) {
        date = getDate(getResultList.get(0));
        scaler = getScaler(getResultList.get(1));
        unit = getUnit(getResultList.get(1));
        value = getValue(getResultList.get(2));
      }
      else {
        throw new RuntimeException("Register with obis " + obisCode.asShortObisCodeString() + " is not an register");
      }

      LOG.info("Got date " + date);
      LOG.info("Got value " + new BigDecimal(value).scaleByPowerOfTen(scaler).toPlainString());
      
      Assert.assertNotNull(value);
    }
    finally {
      if (client != null) {
        client.closeConnection();
      }
    }
  }

  public void testReadDateTime() throws Exception {
    LOG.info("Running testReadDateTime");
    DlmsClient client = new DlmsClient(IP_ADDRESS, PORT, CLIENT_ADDRESS, PASSWORD, LOGICAL_DEVICE_ADDRESS, PHYSICAL_DEVICE_ADDRESS, SN_OBJECT_MAPPING);
    
    try {
      client.openConnection();

      LOG.info("Reading Clock Time");
      RegisterReadingMapper mapper = new RegisterReadingMapper(SN_OBJECT_MAPPING);
      List<AttributeAddress> attributeAddressList = mapper.buildClockTimeReadingRequest();
      
      List<GetResult> getResultList = client.get(attributeAddressList);
      Assert.assertNotNull(getResultList);
      Assert.assertTrue(!getResultList.isEmpty());
      
      Date date = mapper.getDate(getResultList.get(0));
      LOG.info("Got value " + date);
    }
    finally {
      if (client != null) {
        client.closeConnection();
      }
    }
  }

  public void testSetDateTime() throws Exception {
    LOG.info("Running testReadDateTime");
    DlmsClient client = new DlmsClient(IP_ADDRESS, PORT, CLIENT_ADDRESS, PASSWORD, LOGICAL_DEVICE_ADDRESS, PHYSICAL_DEVICE_ADDRESS, SN_OBJECT_MAPPING);
    
    try {
      client.openConnection();

      LOG.info("Setting Clock Time");
      Set<ClockStatus> statusSet = ClockStatus.clockStatusFrom(Byte.MAX_VALUE);
      ClockStatus[] statusList = statusSet.toArray(new ClockStatus[statusSet.size()]);
      
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.HOUR_OF_DAY, -2);
      
      CosemDateTime dateTime = new CosemDateTime(calendar.get(Calendar.YEAR), 
          calendar.get(Calendar.MONTH) + 1,
          calendar.get(Calendar.DAY_OF_MONTH),
          calendar.get(Calendar.HOUR_OF_DAY),
          calendar.get(Calendar.MINUTE),
          calendar.get(Calendar.SECOND),
          calendar.getTimeZone().getRawOffset()/1000/60,
          statusList);
      
      List<SetParameter> setParameterList = new ArrayList<SetParameter>();
      setParameterList.add(new SetParameter(new AttributeAddress(ClockAttribute.TIME, "0.0.1.0.0.255"), DataObject.newDateTimeData(dateTime)));
      
      List<AccessResultCode> resultCodeList = client.set(setParameterList);
      
      Assert.assertTrue(AccessResultCode.SUCCESS.equals(resultCodeList.get(0)));
    }
    finally {
      if (client != null) {
        client.closeConnection();
      }
    }
  }

  public void testRetrieveSnObjectMapping() throws Exception {
    LOG.info("Running testRetrieveSnObjectMapping");
    DlmsClient client = new DlmsClient(IP_ADDRESS, PORT, CLIENT_ADDRESS, PASSWORD, LOGICAL_DEVICE_ADDRESS, PHYSICAL_DEVICE_ADDRESS, SN_OBJECT_MAPPING);
    
    try {
      client.openConnection();
      Map<ObisCode, SnObjectInfo> snObjects = client.retrieveSnObjectMapping();
      for (ObisCode obisCode : snObjects.keySet()) {
        SnObjectInfo snObjectInfo = snObjects.get(obisCode);
        LOG.info("obisCode: " + obisCode.asShortObisCodeString() + ", snAddress: " + snObjectInfo.getBaseName() + ", classId: " + snObjectInfo.getClassId()
            + ", version: " + snObjectInfo.getVersion()
            + ", methodOffset: " + (snObjectInfo.getFirstMethodIdOffsetPair() != null ? snObjectInfo.getFirstMethodIdOffsetPair().toString() : null));
      }
    }
    finally {
      if (client != null) {
        client.closeConnection();
      }
    }
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
  
  
}
