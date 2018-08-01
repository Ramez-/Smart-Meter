package gateway;

import java.util.ArrayList;

import exceptions.MeterNotFoundException;
import meter.Meter;
import protocol.Protocol;

public interface Gateway {

	public ArrayList<Meter> getAllMetersByProtocol(Protocol protocol);

	public Meter getMeter(String meterID) throws MeterNotFoundException;

	public ArrayList<Meter> getAllMeters();

	public ArrayList<Protocol> getAllSupportedProtocols();

}
