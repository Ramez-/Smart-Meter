package register;

import java.io.IOException;
import java.util.ArrayList;

import org.openmuc.jdlms.datatypes.DataObject;

import com.ubitronix.uiem.dlms.client.JDlmsSampleClient;

import exceptions.ConnectionException;
import exceptions.InValidDataTypeException;
import meter.Meter;
import protocol.DLMSProtocolFactory;

public class DoubleRegisterFactory extends RegisterFactory implements Register {


	private String registerID;
	private JDlmsSampleClient client;

	
	public DoubleRegisterFactory(String registerID, Meter meter, JDlmsSampleClient client) throws IOException {
		super(registerID, meter, client);
	}
	
	public double readDouble() throws IOException, InValidDataTypeException {
		ArrayList<DataObject> attributesList = client.readRegister(registerID);
		for (DataObject attribute : attributesList) {
			if (attribute.getType().toString().contains("DOUBLE")) {
				
				return (Double) attribute.getRawValue();
			}
		}
		throw new InValidDataTypeException("The register doesn't contain integer value");
	}

}
