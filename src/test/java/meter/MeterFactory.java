package meter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ubitronix.uiem.dlms.client.JDlmsSampleClient;

import exceptions.ConnectionException;
import exceptions.NotWritableMeterException;
import exceptions.RegisterNotFoundException;
import protocol.DLMSProtocol;
import protocol.DLMSProtocolFactory;
import protocol.Protocol;
import protocol.VirtualProtocol;
import protocol.VirtualProtocolFactory;
import register.IntegerRegisterFactory;
import register.Register;
import register.RegisterFactory;
import register.RegisterType;
import register.RegisterUnit;
import register.RegisterValue;

public class MeterFactory implements Meter {

	String manufacurer;
	Protocol protocol;
	String meterID;
	private JDlmsSampleClient client;
	String port;
	int clientAdress;
	int logicAdress;
	int physicalAdress;
	ArrayList<Register> registersList = new ArrayList<Register>();
	boolean writable;
//	static Meter instance;
	String location;

	// writable is not tested yet
	// several constructors for different protocols
	public MeterFactory(String protocol, String meterID, String manufacurer, String port, String clientAdress,
			String logicAdress, String physicalAdress, String writable) throws Exception {
		this.manufacurer = manufacurer;
		if (protocol.equals("DLMSCOSEM")) {
			this.protocol = new DLMSProtocolFactory();
		}

		this.meterID = meterID;
		this.clientAdress = Integer.parseInt(clientAdress);
		this.logicAdress = Integer.parseInt(logicAdress);
		this.physicalAdress = Integer.parseInt(physicalAdress.charAt(0) + "");
		this.port = port;
		this.writable = writable.equals("true") ? true : false;
	}

	// constructor for ansi smart meters
	public MeterFactory(String protocol, String meterID, String manufacurer, String port, String location,
			String writable) throws IOException, IllegalArgumentException, IllegalAccessException {
		this.meterID = meterID;
		this.writable = writable.contains("true") ? true : false;
		this.manufacurer = manufacurer;
		this.port = port;
		this.protocol = new VirtualProtocolFactory(protocol);
		this.location = location;
	}

	// DLMS/COSEM => opening connection and reading the registers definitions 
	public void openConnection() throws Exception {
		if (protocol instanceof DLMSProtocol) {
			client = new JDlmsSampleClient(true, port, null, null, clientAdress, null, logicAdress, physicalAdress,
					false);
			client.serializeAttributeList("1.1.1.8.0.255");
			final Log logger = LogFactory.getLog(JDlmsSampleClient.class.getName());
			logger.info("--------------------------------------------------- Initialize Connection");
			client.initializeConnection();

			logger.info("--------------------------------------------------- Read definitions");
			client.readDefinitions();
			logger.info("--------------------------------------------------- Read definitions finished");

			// instance = this.instance;
			for (String shortName : client.getShortNames()) {
				Meter m = new MeterFactory("DLMS/COSEM", meterID, manufacurer, port, clientAdress + "",
						logicAdress + "", physicalAdress + "", writable + "");
				registersList.add(new RegisterFactory(shortName, m, client));
			}
		}

		// if protocol is virtualProtocol then it use OBIS and will have the
		// registerName from xml
		if (protocol instanceof VirtualProtocol) {
			// el value 3'areeba mensa3et ma 3'ayrt el location
			File file = new File(location + "/results/result4.xml");
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
				// probably could be done better with reflection
				Meter m = new MeterFactory("ANSI", meterID, manufacurer, port, location, writable + "");
				registersList
						.add(new RegisterFactory(e.getAttribute("registerName"), m, location, e.getAttribute("unit")));
			}

		}

	}

	public void closeConnection() throws IOException {
		if (client != null) {
			client.close();
		}
	}

	@Override
	public ArrayList<Register> getAllRegisters() {
		return registersList;
	}

	@Override
	public String getManufacturer() {
		return manufacurer;
	}

	@Override
	public Protocol getProtocol() {
		return protocol;
	}

	@Override
	public Register getRegister(String registerID) throws RegisterNotFoundException {
		for (Register register : registersList) {
			if (register.getRegisterID().equals(registerID)) {
				return register;
			}
		}
		throw new RegisterNotFoundException("RegisterID is not found");
	}

	@Override
	public String getMeterID() {
		return meterID;
	}

	// register id is the short name, important for diagram
	@Override
	public boolean hasRegister(String registerID) {
		for (Register register : registersList) {
			if (register.getRegisterID().equals(registerID)) {
				return true;
			}
		}
		return false;
	}

	public JDlmsSampleClient getClient() {
		return client;
	}

	@Override
	public boolean isWritable() {
		return writable;
	}

	// closed => LED Blue off, smart meter is working, sound
	@Override
	public void breakerClose() throws IOException, NotWritableMeterException {
		if (!writable) {
			throw new NotWritableMeterException("Meter doesn't authorize write");
		}
		Runtime.getRuntime().exec("cmd /c start \"\" " + location + "/BreakerClose.bat");

	}

	// open => LED Blue on, Smart meter not working and smart meter locked, sound
	@Override
	public void breakerOpen() throws IOException, NotWritableMeterException {
		if (!writable) {
			throw new NotWritableMeterException("Meter doesn't authorize write");
		}
		Runtime.getRuntime().exec("cmd /c start \"\" " + location + "/BreakerOpen.bat");

	}

	// unlock => LED blinkin, Smart meter not working but customer can turn it to
	// close by button |, no sound, but sound when pressing the button
	@Override
	public void breakerUnlock() throws IOException, NotWritableMeterException {
		if (!writable) {
			throw new NotWritableMeterException("Meter doesn't authorize write");
		}
		Runtime.getRuntime().exec("cmd /c start \"\" " + location + "/BreakerUnlock.bat");

	}

	@Override
	public void disableManualDisconnect() throws IOException, NotWritableMeterException {
		if (!writable) {
			throw new NotWritableMeterException("Meter doesn't authorize write");
		}
		Runtime.getRuntime().exec("cmd /c start \"\" " + location + "/DisableManualDisconnect.bat");

	}

	@Override
	public void enableManualDisconnect() throws IOException, NotWritableMeterException {
		if (!writable) {
			throw new NotWritableMeterException("Meter doesn't authorize write");
		}
		Runtime.getRuntime().exec("cmd /c start \"\" " + location + "/EnableManualDisconnect.bat");

	}

}
