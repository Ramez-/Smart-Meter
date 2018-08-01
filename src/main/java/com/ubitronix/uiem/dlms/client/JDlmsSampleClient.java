package com.ubitronix.uiem.dlms.client;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.AuthenticationMechanism;
import org.openmuc.jdlms.ConnectionBuilder;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.HexConverter;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.RawMessageData;
import org.openmuc.jdlms.RawMessageListener;
import org.openmuc.jdlms.ResponseTimeoutException;
import org.openmuc.jdlms.SecuritySuite;
import org.openmuc.jdlms.SecuritySuite.SecuritySuiteBuilder;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.SerialConnectionBuilder;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.SnObjectInfo;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.openmuc.jdlms.datatypes.CosemDateFormat;
import org.openmuc.jdlms.datatypes.CosemDateFormat.Field;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.CosemDateTime.ClockStatus;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.openmuc.jdlms.interfaceclass.InterfaceClass;
import org.openmuc.jdlms.interfaceclass.attribute.AssociationSnAttribute;
import org.openmuc.jdlms.interfaceclass.attribute.ClockAttribute;
import org.openmuc.jdlms.interfaceclass.attribute.DemandRegisterAttribute;
import org.openmuc.jdlms.interfaceclass.attribute.ExtendedRegisterAttribute;
import org.openmuc.jdlms.interfaceclass.attribute.ProfileGenericAttribute;
import org.openmuc.jdlms.interfaceclass.attribute.RegisterAttribute;
import org.openmuc.jdlms.settings.client.ReferencingMethod;

import register.Register;

public class JDlmsSampleClient {
	private static final Log logger = LogFactory.getLog(JDlmsSampleClient.class.getName());

	private ConnectionBuilder<?> connectionBuilder = null;
	private DlmsConnection dlmsConnection = null;
	private boolean initializeSnObjects = false;

	// added by me
	private ArrayList<String> shortNames = new ArrayList<String>();

	public JDlmsSampleClient(boolean useSerialPort, String serialPort, String ip, Integer port, Integer clientAddress,
			String password, Integer logicalDeviceAddress, Integer physicalDeviceAddress, boolean initializeSnObjects)
			throws Exception {
		this.initializeSnObjects = initializeSnObjects;
		if (useSerialPort) {
			SerialConnectionBuilder serialConnectionBuilder = new SerialConnectionBuilder(serialPort);
			connectionBuilder = serialConnectionBuilder;
			serialConnectionBuilder.setBaudRateChangeTime(300);
			serialConnectionBuilder.enableHandshake();
			serialConnectionBuilder.setBaudRate(2400);
		} else {
			InetAddress inetAddress = InetAddress.getByName(ip);
			TcpConnectionBuilder tcpConnectionBuilder = new TcpConnectionBuilder(inetAddress);
			connectionBuilder = tcpConnectionBuilder;
			tcpConnectionBuilder.setPort(port);
			tcpConnectionBuilder.useHdlc();

			tcpConnectionBuilder.setRawMessageListener(new RawMessageListener() {
				public void messageCaptured(RawMessageData paramRawMessageData) {
					logger.trace("Received raw message with " + "\n\t Source " + paramRawMessageData.getMessageSource()
							+ "\n\t Message "
							+ (paramRawMessageData.getMessage() != null
									? HexConverter.toHexString(paramRawMessageData.getMessage())
									: null)
							+ "\n\t APDU " + paramRawMessageData.getApdu());
				}
			});
		}

		connectionBuilder.setClientId(clientAddress);
		connectionBuilder.setLogicalDeviceId(logicalDeviceAddress);
		connectionBuilder.setPhysicalDeviceAddress(physicalDeviceAddress);
		connectionBuilder.setReferencingMethod(ReferencingMethod.SHORT);
		// connectionBuilder.setReferencingMethod(ReferencingMethod.LOGICAL);
		connectionBuilder.setResponseTimeout(60000);

		if (password != null) {
			SecuritySuiteBuilder securityBuilder = SecuritySuite.builder();
			securityBuilder.setAuthenticationMechanism(AuthenticationMechanism.LOW);
			securityBuilder.setPassword(password.getBytes(StandardCharsets.US_ASCII));
			SecuritySuite securitySuite = securityBuilder.build();
			connectionBuilder.setSecuritySuite(securitySuite);
		}

		// if SnOjbectMapping is not set it is retrieved when first accessing snObjects
		if (initializeSnObjects) {
			// ATTENTION: The subsequent mapping is only true for SWiBi L&G E650
			Map<ObisCode, SnObjectInfo> snObjectMapping = new LinkedHashMap<ObisCode, SnObjectInfo>();
			// put 6000 instead of 7392
			SnObjectInfo value = new SnObjectInfo(7392, 3, 0);
			ObisCode key = new ObisCode("1.1.1.8.0.255");
			snObjectMapping.put(key, value);

			value = new SnObjectInfo(2992, 3, 0);
			key = new ObisCode("1.1.1.8.1.255");
			snObjectMapping.put(key, value);

			value = new SnObjectInfo(3160, 3, 0);
			key = new ObisCode("1.1.1.8.2.255");
			snObjectMapping.put(key, value);

			value = new SnObjectInfo(11200, 8, 0);
			key = new ObisCode("0.0.1.0.0.255");
			snObjectMapping.put(key, value);

			connectionBuilder.setSnObjectMapping(snObjectMapping);
		}
	}

	public static void main(String[] args) throws IOException {
	 JDlmsSampleClient client = null;
	 try {
	 logger.info("Create JDlmsSampleClient");
	 // serial
	 client = new JDlmsSampleClient(true, "COM3", null, null, 16, null, 1, 0,false);
	
	 // serial
	 // client = new JDlmsSampleClient(true, "COM5", null, null, 16, null, 1, 0,
	 // false);
	 // EMH 3078450 HDLC address is last 4 digits of serialnumber + 256 = 8706
	 // client = new JDlmsSampleClient(true, "COM5", null, null, 16, null, 1,
//	 1206,
	 // false);
	
	 // For Hercules IEC Readout
	 // af 3f 35 30 b7 b2 35 b7 b1 33 21 8d 0a SWiBi (Serialnumber 50725713 =>
//	 HDLC
	 // address last 4 digits + 1000 = 6713)
	 // af 3f 39 36 b2 b2 b4 36 b8 b2 21 8d 0a Steweag (Serialnumber 96224682 =>
//	 =>
	 // HDLC address last 4 digits + 1000 = 5682)
	
	 // Cascading
	 // Configuration RS485 both as Master or SWiBi as Master and Stewag as Slave,
	 // Baudrate 2400 fix, without Access Protection for IEC and DLMS, 10 ms dlms
	 // Response delay, IEC Intercharacter Timeout 5s)
	 // Configuration CS+ SWiBi as Master and Steweag as Slave, Initial protocol
	 // autodetect, Baudrate 2400 fix, without Access Protection for IEC and DLMS,
//	 10
	 // ms dlms Response delay, IEC Intercharacter Timeout 5s)
	 // if only one device is attached, no cascading, then we do not need the
	 // serialnumber, else we need the serialnumber to identify the device => best
	 // practice is to always use the serialnumber/HDLC address := last 4 digits +
	 // 1000
	
	 // Access level
	 // ClientAddress (Client ID) controls the access level
	 // 16 is public access for all device types
	 // 32 (manufacturer specific) is low level security for SWiBi and password
	 // default 00000000 to e.g. set DateTime or retrieve Eventlog
	
	 // net
	 // client = new JDlmsSampleClient(false, null, "10.130.129.201", 5200, 32,
	 // "00000000", 1, 6713, false);
	
	 // client = new JDlmsSampleClient(false, null, "10.130.129.201", 5200, 16,
//	 null,
	 // 1, 0, false);
	 // client = new JDlmsSampleClient(false, null, "10.130.129.201", 5200, 16,
//	 null,
	 // 1, 6713, false);
	 // client = new JDlmsSampleClient(false, null, "10.130.129.201", 5200, 16,
//	 null,
	 // 1, 5682, false);
	
	 client.serializeAttributeList("1.1.1.8.0.255");
	
	 logger.info("--------------------------------------------------- Initialize Connection");
	 client.initializeConnection();
	
	 logger.info("--------------------------------------------------- Read definitions");
	 client.readDefinitions();
	 logger.info("--------------------------------------------------- Read definitions finished");
	
	// logger.info("--------------------------------------------------- Read eventnumber");
	// client.readEventNumber();
	// logger.info("--------------------------------------------------- Read eventfinished");
	//
	// logger.info("--------------------------------------------------- ReadDateTime");
	// client.readDateTime();
	// logger.info("--------------------------------------------------- ReadDateTime finished");
	//
	// logger.info("--------------------------------------------------- SetDateTime");
	// client.setDateTime(Calendar.getInstance());
	// logger.info("--------------------------------------------------- Set DateTime finished");
	
	 logger.info("--------------------------------------------------- Readregister hello world");
	 // this is replaced by power of the dlms meter that I have strutuer of two
	 // elments is returned however it always return zero which is wired
	 // client.readRegister("1.1.1.8.0.255");
	 client.readRegister("1.1.16.7.0.255");
	
	 logger.info("--------------------------------------------------- Readregister finished");
	
//	 logger.info("--------------------------------------------------- Readregister this one");
//	 client.readRegister("1.1.1.6.1.255");
//	 logger.info("--------------------------------------------------- Readregister finished");
	
	 // creating calender from time till another time for billing
	// Calendar from = Calendar.getInstance();
	// from.set(Calendar.YEAR, 2017);
	// from.set(Calendar.MONTH, 1);
	// from.set(Calendar.DAY_OF_MONTH, 1);
	// // months is zero based
	// Calendar to = Calendar.getInstance();
	// to.set(Calendar.YEAR, 2017);
	// to.set(Calendar.MONTH, 4);
	// to.set(Calendar.DAY_OF_MONTH, 1);
	
	 // readHisotrical is just reading a certain register with count, I don't know
//	 what does count represent.
//	// int count = 10;
//	// logger.info("--------------------------------------------------- Read
//	 historical billing data with count = "
//	// + count);
//	// client.readHistoricalBillingData(count);
//	// logger.info("--------------------------------------------------- Read
//	 historical billing data finished");
//	// // getting billing from certain time
//	// logger.info("--------------------------------------------------- Read
//	 historical billing data with from = "
//	// + from.getTime() + " and to = " + to.getTime());
//	// client.readHistoricalBillingData(from, to);
//	// logger.info("--------------------------------------------------- Read
//	 historical billing data finished");
	//
	// logger.info(
	// "--------------------------------------------------- Read load profile data
//	 with count = " + count);
//	// client.readLoadProfileData(count);
//	// logger.info("--------------------------------------------------- Read load
//	 profile data finished");
//	// logger.info("--------------------------------------------------- Read load
//	 profile data with from = "
//	// + from.getTime() + " and to = " + to.getTime());
//	// client.readLoadProfileData(from, to);
//	// logger.info("--------------------------------------------------- Read load
//	 profile data finished");
//	//
//	// logger.info("--------------------------------------------------- Read
//	 Eventlog with count = " + count);
//	// client.readEventLog(count);
//	// logger.info("--------------------------------------------------- Read
//	 Eventlog finished");
//	// logger.info("--------------------------------------------------- Read
//	 Eventlog with from = "
//	// + from.getTime() + " and to = " + to.getTime());
//	// client.readEventLog(from, to);
//	// logger.info("--------------------------------------------------- Read
//	 Eventlog finished");
//	
//	 // measure time for reading element from smart meter
//	// logger.info("--------------------------------------------------- Measure
//	 duration");
//	// client.measureReads();
//	// logger.info("--------------------------------------------------- Measure
//	 duration finished");
	 } catch (Throwable t) {
	 t.printStackTrace();
	 } finally {
	 if (client != null) {
	 client.close();
	 }
	 }
	 }

	public void initializeConnection() throws IOException {
		dlmsConnection = connectionBuilder.build();
		if (!initializeSnObjects) {
			// might retrieve this information at first, this is also done for the first
			// read request for snObjects if snObject mapping is not available
			// this might be set before building the connection
			// problem here is that we only get baseName, classId, version but e.g. no
			// scaler unit for register classes
			// so best would be to once read the definition including all the necessary
			// attributes and store them an always set them before creating the connection
			// (@see readDefinitions)
			SnObjectInfo.retrieveLnSnMappingFrom(dlmsConnection);
		}
	}

	public void close() throws IOException {
		if (dlmsConnection != null) {
			dlmsConnection.close();
		}
	}

	private List<GetResult> readObis(String... obisStrings) throws IOException {
		List<AttributeAddress> attributeList = new ArrayList<AttributeAddress>();
		for (String obisString : obisStrings) {
			attributeList.add(new AttributeAddress(AssociationSnAttribute.OBJECT_LIST, obisString));
			Map<ObisCode, SnObjectInfo> snObjects = SnObjectInfo.retrieveLnSnMappingFrom(dlmsConnection);
			ObisCode code = new ObisCode(obisString);
			if (!snObjects.containsKey(code)) {
				logger.info("Device does not support " + obisString);
				return null;
			}
		}

		// the order of the elements in the result corresponds to the order of the
		// elements in the request
		List<GetResult> resultList = dlmsConnection.get(attributeList);
		for (int i = 0; i < resultList.size(); i++) {
			GetResult result = resultList.get(i);
			if (result.getResultCode() == AccessResultCode.SUCCESS) {
				DataObject resultData = result.getResultData();
				logger.info("Request: " + obisStrings[i] + ", Result: " + resultData.toString());
			} else {
				logger.info("Get was not successfull for Request: " + obisStrings[i] + ", resultCode: "
						+ result.getResultCode());
			}
		}

		return resultList;
	}

	public ArrayList<DataObject> readRegister(String registerObisString) throws IOException {
		logger.info("Request " + registerObisString);
		Map<ObisCode, SnObjectInfo> snObjects = SnObjectInfo.retrieveLnSnMappingFrom(dlmsConnection);
		ObisCode obisCode = new ObisCode(registerObisString);
		SnObjectInfo info = snObjects.get(obisCode);
		if (info == null) {
			logger.error("Device does not support " + registerObisString);
			return null;
		}

		// depending on the class of the obis there is a different number of attributes
		// that might be requested
		// e.g. EXTENDED_REGISTER.VALUE, EXTENDED_REGISTER.SCALER_UNIT
		// e.g. REGISTER.VALUE, EXTENDED_REGISTER.SCALER_UNIT
		// SCALER_UNIT is a complex type including the unit and the multiplier (0 for
		// 1.0, -1 for 0.1, -2 for 0.01, 1 for 10.0 etc)
		List<AttributeAddress> attributeList = new ArrayList<AttributeAddress>();
		if (info.getClassId() == InterfaceClass.REGISTER.id()) {
			attributeList.add(new AttributeAddress(RegisterAttribute.SCALER_UNIT, obisCode));
			attributeList.add(new AttributeAddress(RegisterAttribute.VALUE, obisCode));
		} else if (info.getClassId() == InterfaceClass.EXTENDED_REGISTER.id()) {
			attributeList.add(new AttributeAddress(ExtendedRegisterAttribute.SCALER_UNIT, obisCode));
			attributeList.add(new AttributeAddress(ExtendedRegisterAttribute.VALUE, obisCode));
			attributeList.add(new AttributeAddress(ExtendedRegisterAttribute.CAPTURE_TIME, obisCode));
			attributeList.add(new AttributeAddress(ExtendedRegisterAttribute.STATUS, obisCode));
		} else if (info.getClassId() == InterfaceClass.DEMAND_REGISTER.id()) {
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.SCALER_UNIT, obisCode));
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.CURRENT_AVERAGE_VALUE, obisCode));
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.LAST_AVERAGE_VALUE, obisCode));
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.CAPTURE_TIME, obisCode));
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.PERIOD, obisCode));
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.STATUS, obisCode));
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.START_TIME_CURRENT, obisCode));
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.PERIOD, obisCode));
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.NUMBER_OF_PERIODS, obisCode));
		} else {
			logger.error("Register with obis " + registerObisString + " is not an register");
		}

		List<GetResult> resultList = dlmsConnection.get(attributeList);
		ArrayList<DataObject> resArray = new ArrayList<DataObject>();
		for (int i = 0; i < resultList.size(); i++) {
			GetResult result = resultList.get(i);
			if (result.getResultCode() == AccessResultCode.SUCCESS) {
				DataObject resultData = result.getResultData();
				if (resultData.isCosemDateFormat()) {
					CosemDateFormat value = resultData.getValue();
					logger.info("Year = " + value.get(Field.YEAR));
					logger.info("Month = " + value.get(Field.MONTH));
					logger.info("Day = " + value.get(Field.DAY_OF_MONTH));
					logger.info("Hour = " + value.get(Field.HOUR));
					logger.info("Minute = " + value.get(Field.MINUTE));
					logger.info("Second = " + value.get(Field.SECOND));
					logger.info("Hundredth = " + value.get(Field.HUNDREDTHS));
				}
				// should be datetime for register, e.g. 02.03.2015 01:30:00
//				else if (resultData.getType() == Type.OCTET_STRING) {
//					byte[] octetString = resultData.getValue();
//					CosemDateTime dateTime = CosemDateTime.decode(octetString);
//					logger.info("Year = " + dateTime.get(Field.YEAR));
//					logger.info("Month = " + dateTime.get(Field.MONTH));
//					logger.info("Day = " + dateTime.get(Field.DAY_OF_MONTH));
//					logger.info("Hour = " + dateTime.get(Field.HOUR));
//					logger.info("Minute = " + dateTime.get(Field.MINUTE));
//					logger.info("Second = " + dateTime.get(Field.SECOND));
//					// need to adjust with Timezone and DST
//				}
				
				resArray.add(resultData);
				logger.info("Request: ClassId " + attributeList.get(i).getClassId() + ", AttributeId "
						+ attributeList.get(i).getId() + ", Result: " + resultData.toString());
			} else {
				logger.info("Get for ClassId " + attributeList.get(i).getClassId() + ", AttributeId "
						+ attributeList.get(i).getId() + " was not successfull, resultCode: " + result.getResultCode());
			}
		}
		return resArray;
	}

	public double measureReads() throws IOException {
		List<String> obisList = new ArrayList<String>();
		obisList.add("1.1.1.8.1.255");
		obisList.add("1.1.2.8.1.255");
		obisList.add("1.1.2.8.2.255");
		obisList.add("1.1.5.8.1.255");
		obisList.add("1.1.5.8.2.255");
		obisList.add("1.1.6.8.1.255");
		obisList.add("1.1.6.8.2.255");
		obisList.add("1.1.7.8.1.255");
		obisList.add("1.1.7.8.2.255");
		obisList.add("1.1.8.8.1.255");
		obisList.add("1.1.8.8.2.255");
		obisList.add("1.1.1.8.0.255");
		obisList.add("1.1.2.8.0.255");
		obisList.add("1.1.3.8.0.255");
		obisList.add("1.1.4.8.0.255");
		obisList.add("1.1.5.8.0.255");

		Long start = System.currentTimeMillis();
		readObis(obisList.toArray(new String[obisList.size()]));
		Long end = System.currentTimeMillis();
		logger.info("Duration for read at once = " + (double) (end - start) / (double) 1000 + " seconds");

		start = System.currentTimeMillis();
		for (String obis : obisList) {
			readObis(obis);
		}
		end = System.currentTimeMillis();
		logger.info("Duration for read at once = " + (double) (end - start) / (double) 1000 + " seconds");
		return (double) (end - start) / (double) 1000;
	}

	public void readDefinitions() throws IOException {
		AttributeAddress snObjects = new AttributeAddress(AssociationSnAttribute.OBJECT_LIST,
				new ObisCode("0.0.40.0.0.255"));
		AttributeAddress snAccessRights = new AttributeAddress(AssociationSnAttribute.ACCESS_RIGHTS_LIST,
				new ObisCode("0.0.40.0.0.255"));

		GetResult objectsResult;
		GetResult accessRightsResult;
		try {
			List<GetResult> list = dlmsConnection.get(Arrays.asList(snObjects, snAccessRights));
			objectsResult = list.get(0);
			accessRightsResult = list.get(1);
		} catch (ResponseTimeoutException e) {
			System.err.println("Failed to scan: Timeout");
			return;
		}

		if (objectsResult.getResultCode() != AccessResultCode.SUCCESS) {
			System.err.printf("Failed to scan with following result code %s.\n", objectsResult.getResultCode().name());
			return;
		}
		System.out.println(accessRightsResult.getResultCode());

		List<DataObject> objectList = objectsResult.getResultData().getValue();
		System.out.printf("%-30s %6s\t %-10s %-30s\t %-30s\t\n", "InterfaceKlasse", "Id Hex", "Id Decimal", "Obis",
				"SN");
		for (DataObject objectListElement : objectList) {
			List<DataObject> struct = objectListElement.getValue();

			Number baseName = struct.get(0).getValue();
			Number classId = struct.get(1).getValue();
			Number version = struct.get(2).getValue();

			byte[] logNameBytes = struct.get(3).getValue();

			ObisCode obisCode = new ObisCode(logNameBytes);

			InterfaceClass interfaceClass = InterfaceClass.interfaceClassFor(classId.intValue());

			String interfaceClassStr;
			if (interfaceClass != InterfaceClass.UNKNOWN) {
				interfaceClassStr = String.format("%s(%d)", interfaceClass.name(), classId);
			} else {
				interfaceClassStr = classId.toString();
			}

			System.out.printf("%-30s 0x%04X\t %-10s %-30s\t %-30s\t\n", interfaceClassStr, (short) baseName.intValue(),
					Integer.parseInt(String.format("%04X", (short) baseName.intValue()), 16),
					obisCode.asShortObisCodeString(), obisCode.asDecimalString());

			shortNames.add(obisCode.asDecimalString());

			// get the full definition, either at the beginning or when we read the record
			// (e.g. scaler unit with multiplier and unit)
			// e.g. if Register then Attributes 1,2,3 (3 for the unit - see
			// RegisterAttribute)
			// e.g. if ExtendedRegister then Attributes 1,2,3,4,5 (3 for the unit - see
			// ExtendedRegisterAttribute)
			// if (interfaceClass == InterfaceClass.REGISTER) {
			// dlmsConnection.get(new AttributeAddress(interfaceClass.id(), obisCode,
			// RegisterAttribute.SCALER_UNIT.attributeId()));
			// }
			// else if (interfaceClass == InterfaceClass.EXTENDED_REGISTER) {
			// dlmsConnection.get(new AttributeAddress(interfaceClass.id(), obisCode,
			// ExtendedRegisterAttribute.SCALER_UNIT.attributeId()));
			// }
		}
	}

	private void readProfileGeneric(String obisString, Calendar from, Calendar to) throws IOException {
		logger.info("Request " + obisString);
		Map<ObisCode, SnObjectInfo> snObjects = SnObjectInfo.retrieveLnSnMappingFrom(dlmsConnection);
		ObisCode obisCode = new ObisCode(obisString);
		SnObjectInfo info = snObjects.get(obisCode);
		if (info == null) {
			logger.error("Device does not support " + obisString);
			return;
		}

		List<AttributeAddress> attributeList = new ArrayList<AttributeAddress>();
		if (info.getClassId() == InterfaceClass.PROFILE_GENERIC.id()) {
			// from
			// https://github.com/OSGP/Protocol-Adapter-DLMS/blob/f0ebdc52a8504423d5b2ea0e1fe438a1512bb6a8/osgp-protocol-adapter-dlms/src/main/java/org/osgp/adapter/protocol/dlms/domain/commands/RetrieveEventsCommandExecutor.java
			// restrict with fromDate and toDate

			// this is only for the sort criteria, means sort by time
			ObisCode clock = new ObisCode("0.0.1.0.0.255");
			SnObjectInfo clockInfo = snObjects.get(clock);
			DataObject sortDefinition = DataObject.newStructureData(
					DataObject.newUInteger16Data(clockInfo.getClassId()), // classId
					DataObject.newOctetStringData(clock.bytes()), // logical name
					DataObject.newInteger8Data((byte) ClockAttribute.TIME.attributeId()), // ClockAttribute.TIME.attributeId
					DataObject.newUInteger16Data(clockInfo.getVersion())); // version

			Set<ClockStatus> statusSet = ClockStatus.clockStatusFrom(Byte.MAX_VALUE);
			ClockStatus[] statusList = statusSet.toArray(new ClockStatus[statusSet.size()]);

			CosemDateTime fromDateTime = new CosemDateTime(from.get(Calendar.YEAR), from.get(Calendar.MONTH) + 1,
					from.get(Calendar.DAY_OF_MONTH), from.get(Calendar.HOUR_OF_DAY), from.get(Calendar.MINUTE),
					from.get(Calendar.SECOND), from.getTimeZone().getRawOffset() / 1000 / 60, statusList);
			CosemDateTime toDateTime = new CosemDateTime(to.get(Calendar.YEAR), to.get(Calendar.MONTH) + 1,
					to.get(Calendar.DAY_OF_MONTH), to.get(Calendar.HOUR_OF_DAY), to.get(Calendar.MINUTE),
					to.get(Calendar.SECOND), to.getTimeZone().getRawOffset() / 1000 / 60, statusList);
			DataObject selectedValues = DataObject.newArrayData(Collections.<DataObject>emptyList());

			DataObject accessParameter = DataObject.newStructureData(sortDefinition,
					DataObject.newDateTimeData(fromDateTime), DataObject.newDateTimeData(toDateTime), selectedValues);
			SelectiveAccessDescription selectiveAccess = new SelectiveAccessDescription(1, accessParameter);

			attributeList.add(new AttributeAddress(ProfileGenericAttribute.BUFFER, obisCode, selectiveAccess));
			// attributeList.add(new
			// AttributeAddress(ProfileGenericAttribute.CAPTURE_OBJECTS, obisCode));
			// attributeList.add(new
			// AttributeAddress(ProfileGenericAttribute.CAPTURE_PERIOD, obisCode));
			// attributeList.add(new AttributeAddress(ProfileGenericAttribute.SORT_METHOD,
			// obisCode));
			// attributeList.add(new AttributeAddress(ProfileGenericAttribute.SORT_OBJECT,
			// obisCode));
			// attributeList.add(new
			// AttributeAddress(ProfileGenericAttribute.ENTRIES_IN_USE, obisCode));
			// attributeList.add(new
			// AttributeAddress(ProfileGenericAttribute.PROFILE_ENTRIES, obisCode));
		} else {
			logger.error("Register with obis " + obisString + " is not an register");
		}

		List<GetResult> resultList = dlmsConnection.get(attributeList);
		for (int i = 0; i < resultList.size(); i++) {
			GetResult result = resultList.get(i);
			if (result.getResultCode() == AccessResultCode.SUCCESS) {
				DataObject resultData = result.getResultData();
				if (resultData.isCosemDateFormat()) {
					CosemDateFormat value = resultData.getValue();
					logger.info("Year = " + value.get(Field.YEAR));
					logger.info("Month = " + value.get(Field.MONTH));
					logger.info("Day = " + value.get(Field.DAY_OF_MONTH));
					logger.info("Hour = " + value.get(Field.HOUR));
					logger.info("Minute = " + value.get(Field.MINUTE));
					logger.info("Second = " + value.get(Field.SECOND));
					logger.info("Hundredth = " + value.get(Field.HUNDREDTHS));
				}
				// should be datetime for register, e.g. 02.03.2015 01:30:00
				else if (resultData.getType() == Type.OCTET_STRING) {
					byte[] octetString = resultData.getValue();
					CosemDateTime dateTime = CosemDateTime.decode(octetString);
					logger.info("Year = " + dateTime.get(Field.YEAR));
					logger.info("Month = " + dateTime.get(Field.MONTH));
					logger.info("Day = " + dateTime.get(Field.DAY_OF_MONTH));
					logger.info("Hour = " + dateTime.get(Field.HOUR));
					logger.info("Minute = " + dateTime.get(Field.MINUTE));
					logger.info("Second = " + dateTime.get(Field.SECOND));
					logger.info("Hundredth = " + dateTime.get(Field.HUNDREDTHS));
					// need to adjust with Timezone and DST
				}

				logger.info("Request: ClassId " + attributeList.get(i).getClassId() + ", AttributeId "
						+ attributeList.get(i).getId() + ", Result: " + resultData.toString());
			} else {
				logger.info("Get for ClassId " + attributeList.get(i).getClassId() + ", AttributeId "
						+ attributeList.get(i).getId() + " was not successfull, resultCode: " + result.getResultCode());
			}
		}
	}

	private void readProfileGeneric(String obisString, int nrOfEntries) throws IOException {
		logger.info("Request " + obisString);
		Map<ObisCode, SnObjectInfo> snObjects = SnObjectInfo.retrieveLnSnMappingFrom(dlmsConnection);
		ObisCode obisCode = new ObisCode(obisString);
		SnObjectInfo info = snObjects.get(obisCode);
		if (info == null) {
			logger.error("Device does not support " + obisString);
			return;
		}

		List<AttributeAddress> attributeList = new ArrayList<AttributeAddress>();
		if (info.getClassId() == InterfaceClass.PROFILE_GENERIC.id()) {
			// see gurux.dlms.GXDLMSClient
			// restrict with startIndex and count
			DataObject accessParameter = DataObject.newStructureData(DataObject.newUInteger32Data(0),
					DataObject.newUInteger32Data(nrOfEntries), DataObject.newUInteger16Data(1),
					DataObject.newUInteger16Data(0));
			SelectiveAccessDescription selectiveAccess = new SelectiveAccessDescription(2, accessParameter);

			attributeList.add(new AttributeAddress(ProfileGenericAttribute.BUFFER, obisCode, selectiveAccess));
			// attributeList.add(new
			// AttributeAddress(ProfileGenericAttribute.CAPTURE_OBJECTS, obisCode));
			// attributeList.add(new
			// AttributeAddress(ProfileGenericAttribute.CAPTURE_PERIOD, obisCode));
			// attributeList.add(new AttributeAddress(ProfileGenericAttribute.SORT_METHOD,
			// obisCode));
			// attributeList.add(new AttributeAddress(ProfileGenericAttribute.SORT_OBJECT,
			// obisCode));
			// attributeList.add(new
			// AttributeAddress(ProfileGenericAttribute.ENTRIES_IN_USE, obisCode));
			// attributeList.add(new
			// AttributeAddress(ProfileGenericAttribute.PROFILE_ENTRIES, obisCode));
		} else {
			logger.error("Register with obis " + obisString + " is not an register");
		}

		List<GetResult> resultList = dlmsConnection.get(attributeList);
		for (int i = 0; i < resultList.size(); i++) {
			GetResult result = resultList.get(i);
			if (result.getResultCode() == AccessResultCode.SUCCESS) {
				DataObject resultData = result.getResultData();
				if (resultData.isCosemDateFormat()) {
					CosemDateFormat value = resultData.getValue();
					logger.info("Year = " + value.get(Field.YEAR));
					logger.info("Month = " + value.get(Field.MONTH));
					logger.info("Day = " + value.get(Field.DAY_OF_MONTH));
					logger.info("Hour = " + value.get(Field.HOUR));
					logger.info("Minute = " + value.get(Field.MINUTE));
					logger.info("Second = " + value.get(Field.SECOND));
					logger.info("Hundredth = " + value.get(Field.HUNDREDTHS));
				}
				// should be datetime for register, e.g. 02.03.2015 01:30:00
				else if (resultData.getType() == Type.OCTET_STRING) {
					byte[] octetString = resultData.getValue();
					CosemDateTime dateTime = CosemDateTime.decode(octetString);
					logger.info("Year = " + dateTime.get(Field.YEAR));
					logger.info("Month = " + dateTime.get(Field.MONTH));
					logger.info("Day = " + dateTime.get(Field.DAY_OF_MONTH));
					logger.info("Hour = " + dateTime.get(Field.HOUR));
					logger.info("Minute = " + dateTime.get(Field.MINUTE));
					logger.info("Second = " + dateTime.get(Field.SECOND));
					logger.info("Hundredth = " + dateTime.get(Field.HUNDREDTHS));
					// need to adjust with Timezone and DST
				}

				logger.info("Request: ClassId " + attributeList.get(i).getClassId() + ", AttributeId "
						+ attributeList.get(i).getId() + ", Result: " + resultData.toString());
			} else {
				logger.info("Get for ClassId " + attributeList.get(i).getClassId() + ", AttributeId "
						+ attributeList.get(i).getId() + " was not successfull, resultCode: " + result.getResultCode());
			}
		}
	}

	private void readHistoricalBillingData(int nrOfEntries) throws IOException {
		readProfileGeneric("0.0.98.1.0.126", nrOfEntries);
	}

	private void readHistoricalBillingData(Calendar from, Calendar to) throws IOException {
		readProfileGeneric("0.0.98.1.0.126", from, to);
	}

	private void readLoadProfileData(int nrOfEntries) throws IOException {
		readProfileGeneric("1.0.99.1.0.255", nrOfEntries);
	}

	private void readLoadProfileData(Calendar from, Calendar to) throws IOException {
		readProfileGeneric("1.0.99.1.0.255", from, to);
	}

	private void readEventLog(int nrOfEntries) throws IOException {
		readProfileGeneric("1.0.99.98.0.255", nrOfEntries);
	}

	private void readEventLog(Calendar from, Calendar to) throws IOException {
		readProfileGeneric("1.0.99.98.0.255", from, to);
	}

	public void readEventNumber() throws IOException {
		String obisString = "0.0.96.240.12.255";
		logger.info("Request " + obisString);
		Map<ObisCode, SnObjectInfo> snObjects = SnObjectInfo.retrieveLnSnMappingFrom(dlmsConnection);
		ObisCode obisCode = new ObisCode(obisString);
		SnObjectInfo info = snObjects.get(obisCode);
		if (info == null) {
			logger.error("Device does not support " + obisString);
			return;
		}

		List<AttributeAddress> attributeList = new ArrayList<AttributeAddress>();
		attributeList.add(new AttributeAddress(RegisterAttribute.LOGICAL_NAME, obisCode));
		attributeList.add(new AttributeAddress(RegisterAttribute.SCALER_UNIT, obisCode));
		attributeList.add(new AttributeAddress(RegisterAttribute.VALUE, obisCode));

		// the order of the elements in the result corresponds to the order of the
		// elements in the request
		List<GetResult> resultList = dlmsConnection.get(attributeList);
		for (int i = 0; i < resultList.size(); i++) {
			GetResult result = resultList.get(i);
			if (result.getResultCode() == AccessResultCode.SUCCESS) {
				DataObject resultData = result.getResultData();
				logger.info("Request: " + obisString + ", Result: " + resultData.toString());
			} else {
				logger.info("Get was not successfull for Request: " + obisString + ", resultCode: "
						+ result.getResultCode());
			}
		}
	}

	private void readDateTime() throws IOException {
		String obisString = "0.0.1.0.0.255";
		logger.info("Request " + obisString);
		Map<ObisCode, SnObjectInfo> snObjects = SnObjectInfo.retrieveLnSnMappingFrom(dlmsConnection);
		ObisCode obisCode = new ObisCode(obisString);
		SnObjectInfo info = snObjects.get(obisCode);
		if (info == null) {
			logger.error("Device does not support " + obisString);
			return;
		}

		List<AttributeAddress> attributeList = new ArrayList<AttributeAddress>();
		attributeList.add(new AttributeAddress(ClockAttribute.TIME, obisCode));
		attributeList.add(new AttributeAddress(ClockAttribute.TIME_ZONE, obisCode));
		attributeList.add(new AttributeAddress(ClockAttribute.STATUS, obisCode));
		attributeList.add(new AttributeAddress(ClockAttribute.DAYLIGHT_SAVINGS_BEGIN, obisCode));
		attributeList.add(new AttributeAddress(ClockAttribute.DAYLIGHT_SAVINGS_END, obisCode));
		attributeList.add(new AttributeAddress(ClockAttribute.DAYLIGHT_SAVINGS_DEVIATION, obisCode));
		attributeList.add(new AttributeAddress(ClockAttribute.DAYLIGHT_SAVINGS_ENABLED, obisCode));
		attributeList.add(new AttributeAddress(ClockAttribute.CLOCK_BASE, obisCode));

		List<GetResult> resultList = dlmsConnection.get(attributeList);
		for (int i = 0; i < resultList.size(); i++) {
			GetResult result = resultList.get(i);
			if (result.getResultCode() == AccessResultCode.SUCCESS) {
				DataObject resultData = result.getResultData();
				if (resultData.isCosemDateFormat()) {
					CosemDateFormat value = resultData.getValue();
					logger.info("Year = " + value.get(Field.YEAR));
					logger.info("Month = " + value.get(Field.MONTH));
					logger.info("Day = " + value.get(Field.DAY_OF_MONTH));
					logger.info("Hour = " + value.get(Field.HOUR));
					logger.info("Minute = " + value.get(Field.MINUTE));
					logger.info("Second = " + value.get(Field.SECOND));
					logger.info("Hundredth = " + value.get(Field.HUNDREDTHS));
				} else if (resultData.getType() == Type.OCTET_STRING) {
					byte[] octetString = resultData.getValue();
					CosemDateTime dateTime = CosemDateTime.decode(octetString);
					logger.info("ClockStatus = " + dateTime.get(Field.CLOCK_STATUS));
					logger.info("Deviation = " + dateTime.get(Field.DEVIATION));
					logger.info("Year = " + dateTime.get(Field.YEAR));
					logger.info("Month = " + dateTime.get(Field.MONTH));
					logger.info("Day = " + dateTime.get(Field.DAY_OF_MONTH));
					logger.info("DayOfWeek = " + dateTime.get(Field.DAY_OF_WEEK));
					logger.info("Hour = " + dateTime.get(Field.HOUR));
					logger.info("Minute = " + dateTime.get(Field.MINUTE));
					logger.info("Second = " + dateTime.get(Field.SECOND));
					logger.info("Hundredths = " + dateTime.get(Field.HUNDREDTHS));
				}

				logger.info("Request: ClassId " + attributeList.get(i).getClassId() + ", AttributeId "
						+ attributeList.get(i).getId() + ", Result: " + resultData.toString());
			} else {
				logger.info("Get for ClassId " + attributeList.get(i).getClassId() + ", AttributeId "
						+ attributeList.get(i).getId() + " was not successfull, resultCode: " + result.getResultCode());
			}
		}
	}

	private void setDateTime(Calendar calendar) throws IOException {
		Set<ClockStatus> statusSet = ClockStatus.clockStatusFrom(Byte.MAX_VALUE);
		ClockStatus[] statusList = statusSet.toArray(new ClockStatus[statusSet.size()]);

		CosemDateTime dateTime = new CosemDateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
				calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.SECOND), calendar.getTimeZone().getRawOffset() / 1000 / 60, statusList);
		AccessResultCode resultCode = dlmsConnection.set(new SetParameter(
				new AttributeAddress(ClockAttribute.TIME, "0.0.1.0.0.255"), DataObject.newDateTimeData(dateTime)));
		logger.info("Result Code = " + resultCode);
	}

	public static byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++) {
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte) v;
		}
		return b;
	}

	public void serializeAttributeList(String registerObisString) throws IOException {
		Map<ObisCode, SnObjectInfo> snObjectMapping = new LinkedHashMap<ObisCode, SnObjectInfo>();
		// put 6000 instead of 7392
		SnObjectInfo value = new SnObjectInfo(7392, 3, 0);
		ObisCode key = new ObisCode("1.1.1.8.0.255");
		snObjectMapping.put(key, value);

		value = new SnObjectInfo(2992, 3, 0);
		key = new ObisCode("1.1.1.8.1.255");
		snObjectMapping.put(key, value);

		value = new SnObjectInfo(3160, 3, 0);
		key = new ObisCode("1.1.1.8.2.255");
		snObjectMapping.put(key, value);

		value = new SnObjectInfo(11200, 8, 0);
		key = new ObisCode("0.0.1.0.0.255");
		snObjectMapping.put(key, value);

		logger.info("Request " + registerObisString);
		Map<ObisCode, SnObjectInfo> snObjects = snObjectMapping;
		ObisCode obisCode = new ObisCode(registerObisString);
		SnObjectInfo info = snObjects.get(obisCode);
		if (info == null) {
			logger.error("Device does not support " + registerObisString);
			return;
		}

		// depending on the class of the obis there is a different number of attributes
		// that might be requested
		// e.g. EXTENDED_REGISTER.VALUE, EXTENDED_REGISTER.SCALER_UNIT
		// e.g. REGISTER.VALUE, EXTENDED_REGISTER.SCALER_UNIT
		// SCALER_UNIT is a complex type including the unit and the multiplier (0 for
		// 1.0, -1 for 0.1, -2 for 0.01, 1 for 10.0 etc)
		List<AttributeAddress> attributeList = new ArrayList<AttributeAddress>();
		if (info.getClassId() == InterfaceClass.REGISTER.id()) {
			attributeList.add(new AttributeAddress(RegisterAttribute.SCALER_UNIT, obisCode));
			attributeList.add(new AttributeAddress(RegisterAttribute.VALUE, obisCode));
		} else if (info.getClassId() == InterfaceClass.EXTENDED_REGISTER.id()) {
			attributeList.add(new AttributeAddress(ExtendedRegisterAttribute.SCALER_UNIT, obisCode));
			attributeList.add(new AttributeAddress(ExtendedRegisterAttribute.VALUE, obisCode));
			attributeList.add(new AttributeAddress(ExtendedRegisterAttribute.CAPTURE_TIME, obisCode));
			attributeList.add(new AttributeAddress(ExtendedRegisterAttribute.STATUS, obisCode));
		} else if (info.getClassId() == InterfaceClass.DEMAND_REGISTER.id()) {
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.SCALER_UNIT, obisCode));
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.CURRENT_AVERAGE_VALUE, obisCode));
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.LAST_AVERAGE_VALUE, obisCode));
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.CAPTURE_TIME, obisCode));
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.PERIOD, obisCode));
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.STATUS, obisCode));
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.START_TIME_CURRENT, obisCode));
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.PERIOD, obisCode));
			attributeList.add(new AttributeAddress(DemandRegisterAttribute.NUMBER_OF_PERIODS, obisCode));
		} else {
			logger.error("Register with obis " + registerObisString + " is not an register");
		}
	}

	public ArrayList<String> getShortNames() {
		return shortNames;
	}
}
