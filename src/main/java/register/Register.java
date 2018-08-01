package register;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.openmuc.jdlms.datatypes.DataObject;
import org.xml.sax.SAXException;

import exceptions.ConnectionException;
import exceptions.InValidDataTypeException;
import meter.Meter;

public interface Register {

	public String getRegisterID();

	public RegisterType getRegisterType() throws IOException;

	public RegisterUnit getRegisterUnit();

	public void open() throws ConnectionException, IOException, InterruptedException;

	public void close() throws ConnectionException;

	// changed register value to dataObject to make it retrun dataObject which is general of DLMS/COSEM
	public ArrayList<DataObject> read() throws ConnectionException, IOException, ParserConfigurationException, SAXException, InterruptedException;

	public Meter getMeter();
	
	public double timeReads() throws IOException;
	
	public double readDouble() throws IOException, InValidDataTypeException, InterruptedException;
	
	public Integer readInteger() throws IOException, InValidDataTypeException, ParserConfigurationException, SAXException, InterruptedException;
	
	public boolean readBoolean() throws IOException, InValidDataTypeException, InterruptedException;
	
	public long readLongInteger() throws IOException, InValidDataTypeException, InterruptedException;
	
	public String readString() throws IOException, InValidDataTypeException;
	
	

}
