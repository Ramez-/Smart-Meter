package register;

import java.io.IOException;
import java.util.ArrayList;

import org.openmuc.jdlms.datatypes.DataObject;

import com.ubitronix.uiem.dlms.client.JDlmsSampleClient;

import exceptions.ConnectionException;
import exceptions.InValidDataTypeException;
import exceptions.RegisterNotFoundException;
import meter.Meter;
import meter.MeterFactory;
import protocol.DLMSProtocolFactory;

public class IntegerRegisterFactory implements IntegerRegister {
	
	private String registerID;
	private RegisterUnit registerUnit;
	private Meter meter;
	private JDlmsSampleClient client;
	
	private DLMSProtocolFactory dpf;
	
	public IntegerRegisterFactory(String registerID, Meter meter, JDlmsSampleClient client) {
		this.registerID = registerID;	
		this.meter = meter;
		dpf = new DLMSProtocolFactory();
		this.client = client;
	}
	

	@Override
	public String getRegisterID() {
		return registerID;
	}

	@Override
	public RegisterType getRegisterType() {
		return RegisterType.Integer;
	}

	@Override
	public RegisterUnit getRegisterUnit() {
		return dpf.getRegisterUnit(registerID);
	}

	@Override
	public void open() throws ConnectionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws ConnectionException {
		// TODO Auto-generated method stub
		
	}

	// attributes may have more than one dataObject at least one is identifier the dataObject is returned
	@Override
	public ArrayList<DataObject> read() throws ConnectionException, IOException {
		ArrayList<DataObject> attributesList = client.readRegister(registerID);
		// prints two attriuites one with 2 elments and other with double 10
//		for(DataObject attribute : attributesList) {
//			// better than cheking the type because the only available module is integer
//			if(attribute.getType().toString().contains("DOUBLE")) {
//				return attribute.getRawValue();
//			}
//		}
			return attributesList;
	}

	@Override
	public Meter getMeter() {
		return meter;
	}

	@Override
	public Integer readInteger() throws IOException, InValidDataTypeException {
		ArrayList<DataObject> attributesList = client.readRegister(registerID);
		// prints two attriuites one with 2 elments and other with double 10
		for(DataObject attribute : attributesList) {
			// better than checking the type because the only available module is number
			// contains because sometimes it is long
			if(attribute.getType().toString().contains("INTEGER")) {
				return (Integer) attribute.getRawValue();
			}
		}
		throw new InValidDataTypeException("The register doesn't contain integer value");
	}


	@Override
	public double timeReads() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public double readDouble() throws IOException, InValidDataTypeException {
		// TODO Auto-generated method stub
		return 0;
	}


	public double timeReads1() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public boolean readBoolean() throws IOException, InValidDataTypeException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public long readLongInteger() throws IOException, InValidDataTypeException, InterruptedException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public String readString() throws IOException, InValidDataTypeException {
		// TODO Auto-generated method stub
		return null;
	}

}
