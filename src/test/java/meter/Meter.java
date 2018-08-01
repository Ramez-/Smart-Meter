package meter;

import java.io.IOException;
import java.util.ArrayList;

import exceptions.NotWritableMeterException;
import exceptions.RegisterNotFoundException;
import protocol.Protocol;
import register.Register;

public interface Meter {

	public ArrayList<Register> getAllRegisters();

	public String getManufacturer();

	public Protocol getProtocol();

	public Register getRegister(String registerID) throws RegisterNotFoundException;

	public String getMeterID();

	public boolean hasRegister(String registerID);

	public void openConnection() throws Exception;

	public void closeConnection() throws IOException;

	public boolean isWritable();

	public void breakerClose() throws IOException, NotWritableMeterException;

	public void breakerOpen() throws IOException, NotWritableMeterException;

	public void breakerUnlock() throws IOException, NotWritableMeterException;

	public void disableManualDisconnect() throws IOException, NotWritableMeterException;

	public void enableManualDisconnect() throws IOException, NotWritableMeterException;

}
