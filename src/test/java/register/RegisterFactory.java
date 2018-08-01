package register;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ubitronix.uiem.dlms.client.JDlmsSampleClient;

import exceptions.ConnectionException;
import exceptions.InValidDataTypeException;
import meter.Meter;
import protocol.DLMSProtocolFactory;
import protocol.VirtualProtocol;
import protocol.VirtualProtocolFactory;

public class RegisterFactory implements Register {

	private String registerID;
	private RegisterUnit registerUnit;
	private Meter meter;
	private JDlmsSampleClient client;
	private DLMSProtocolFactory dpf;
	private VirtualProtocolFactory virtualProtocol;
	private ArrayList<DataObject> attributesList;
	private String location;

	// DLMS/COSEM constructor, the instance variables related to ANSI meter will be
	// null in case that it is DLMS/COSEM protocol
	public RegisterFactory(String registerID, Meter meter, JDlmsSampleClient client) throws IOException {
		this.registerID = registerID;
		this.meter = meter;
		dpf = new DLMSProtocolFactory();
		this.client = client;
		registerUnit = dpf.getRegisterUnit(registerID);
		if (client != null) {
			attributesList = client.readRegister(registerID);
		}
	}

	// ANSI
	public RegisterFactory(String registerID, Meter meter, String location, String registerUnit)
			throws IOException, IllegalArgumentException, IllegalAccessException {
		virtualProtocol = new VirtualProtocolFactory("ANSI");
		this.registerID = registerID;
		this.meter = meter;
		this.location = location;
		this.registerUnit = RegisterUnit.valueOf(registerUnit);
	}

	@Override
	public String getRegisterID() {
		return registerID;
	}

	// you get the type just from the config file
	@Override
	public RegisterType getRegisterType() throws IOException {
		if (client != null) {
			ArrayList<DataObject> attributesList = client.readRegister(registerID);
			for (DataObject attribute : attributesList) {
				if (attribute.getType().toString().contains("DOUBLE")) {
					return RegisterType.Double;
				} else {
					if (attribute.getType().toString().contains("Integer")) {
						return RegisterType.Integer;
					} else if (attribute.getType().toString().contains("Long")) {
						return RegisterType.Long;
					} else if (attribute.getType().toString().contains("Boolean")) {
						return RegisterType.Boolean;
					}
				}
			}
			// case it couldn't be any data type will be casted to string
			return RegisterType.String;
		} else {
			return virtualProtocol.getRegisterType(registerID);
		}
	}

	@Override
	public RegisterUnit getRegisterUnit() {
		return registerUnit;
	}

	@Override
	public void open() throws ConnectionException, IOException, InterruptedException {
		if (client != null) {
			;
		} else {
			Thread.sleep(18000);
			Runtime.getRuntime().exec("cmd /c start \"\" " + location + "/ReadBillingData.bat");
		}

	}

	@Override
	public void close() throws ConnectionException {
		// TODO Auto-generated method stub

	}

	// returns arrayList of dataObject, in case of DLMS/COSEM could be structure
	// In case of Ansi returns one value which is the value of the read in
	// DataObject format
	@Override
	public ArrayList<DataObject> read()
			throws ConnectionException, IOException, ParserConfigurationException, SAXException, InterruptedException {
		if (client != null) {
			return attributesList;
		}
		Thread.sleep(18000);
		Runtime.getRuntime().exec("cmd /c start \"\" " + location + "/ReadBillingData.bat");
		ArrayList<DataObject> ansiValue = new ArrayList<DataObject>();
		ansiValue.add(getRegisterValueANSI());
		return ansiValue;
	}

	@Override
	public Meter getMeter() {
		return meter;
	}

	// works only for DLMS/COSEM
	@Override
	public double timeReads() throws IOException {
		if (client != null) {
			return client.measureReads();
		}
		// can return exception instead
		return -1;
	}

	// in the new structure remove classes IntegerRegister and DoubleRegister and so
	// on

	public double readDouble() throws IOException, InValidDataTypeException, InterruptedException {
		if (client != null) {
			for (DataObject attribute : attributesList) {
				if (attribute.getType().toString().contains("DOUBLE")) {
					return new Double(attribute.getRawValue().toString());
				}
			}
		} else {
			Runtime.getRuntime().exec("cmd /c start \"\" " + location + "/ReadBillingData.bat");
			Thread.sleep(18000);
			try {
				if (this.getRegisterType().toString().equals("Double")) {
					return new Double((Double) getRegisterValueANSI().getRawValue());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		throw new InValidDataTypeException("The register doesn't contain double value");
	}

	@Override
	public Integer readInteger() throws IOException, InValidDataTypeException, ParserConfigurationException,
			SAXException, InterruptedException {
		// if using DLMS/COSEM
		if (client != null) {
			for (DataObject attribute : attributesList) {
				if (attribute.getType().toString().contains("Integer")) {
					return new Integer(attribute.getRawValue().toString());
				}
			}

		} else {
			Runtime.getRuntime().exec("cmd /c start \"\" " + location + "/ReadBillingData.bat");
			Thread.sleep(18000);
			try {
				if (this.getRegisterType().toString().equals("Integer")) {
					return new Integer((Integer) getRegisterValueANSI().getRawValue());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		throw new InValidDataTypeException("The register doesn't contain integer value");
	}

	// to be tested
	@Override
	public boolean readBoolean() throws IOException, InValidDataTypeException, InterruptedException {
		if (client != null) {
			for (DataObject attribute : attributesList) {

				if (attribute.getType().toString().contains("Boolean")) {
					return (Boolean) attribute.getRawValue();
				}
			}

		} else {

			Runtime.getRuntime().exec("cmd /c start \"\" " + location + "/ReadBillingData.bat");
			Thread.sleep(18000);
			try {
				if (this.getRegisterType().toString().equals("Integer")) {
					return getRegisterValueANSI().getRawValue().toString() == "1" ? true : false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		throw new InValidDataTypeException("The register doesn't contain boolean value");
	}

	// to be tested
	@Override
	public long readLongInteger() throws IOException, InValidDataTypeException, InterruptedException {
		// if using DLMS/COSEM
		if (client != null) {
			for (DataObject attribute : attributesList) {
				if (attribute.getType().toString().contains("Long")) {
					return new Long(attribute.getRawValue().toString());
				}
			}

		} else {
			Runtime.getRuntime().exec("cmd /c start \"\" " + location + "/ReadBillingData.bat");
			Thread.sleep(18000);
			try {
				if (this.getRegisterType().toString().equals("Long")) {
					return new Long((Long) getRegisterValueANSI().getRawValue());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		throw new InValidDataTypeException("The register doesn't contain integer value");
	}

	@Override
	public String readString() throws IOException, InValidDataTypeException {
		// if using DLMS/COSEM
		if (client != null) {
			for (DataObject attribute : attributesList) {
				if (attribute.getType().toString().contains("String")) {
					return attribute.getRawValue().toString();
				}
			}
		}
		throw new InValidDataTypeException(
				"The register doesn't contain String value or protocol not supporting strings");
	}

	// read xml till finding this register, then read its value based on wheather it
	// is integer or double create dataobject that can be
	// used in reading methods
	public DataObject getRegisterValueANSI() throws IOException, ParserConfigurationException, SAXException {
		File file = new File("C:/MST/Bin/results/result4.xml");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = (Document) documentBuilder.parse(file);
		NodeList nList = document.getElementsByTagName("measurement");

		for (int i = 0; i < nList.getLength(); i++) {
			if (!nList.item(i).getParentNode().getParentNode().getNodeName().equals("adhocRead")) {
				break;
			}
			Node n = nList.item(i);
			Element e = (Element) n;
			DataObject dataObject = DataObject.newNullData();
			if (e.getAttribute("registerName").equals(registerID)) {
				String type = this.getRegisterType().toString();
				// this is actually implemented as double, I guess double from integer doesn't
				// matter a lot
				if (type.contains("Integer")) {
					// need a way to pause till the data is read by the smart meter
					return dataObject.newInteger32Data(Integer.parseInt(e.getAttribute("value")));
				} else if (type.contains("Double")) {
					return dataObject.newFloat64Data(Double.parseDouble(e.getAttribute("value")));
				} else if (type.contains("Boolean")) {
					return dataObject.newBoolData(e.getAttribute("value") == "1" ? true : false);
				} else if (type.contains("String")) {
					// getBytes?
					return dataObject.newUtf8StringData(e.getAttribute("value").getBytes());
				}
			}
		}
		return null;
	}

}
