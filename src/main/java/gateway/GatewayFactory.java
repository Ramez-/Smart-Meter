package gateway;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import exceptions.GatewayNotFoundException;
import exceptions.InValidConfigFileException;
import exceptions.MeterNotFoundException;
import meter.Meter;
import protocol.Protocol;

public class GatewayFactory {

	public static Gateway getGateway() throws GatewayNotFoundException, Exception, InValidConfigFileException {
		DefaultGateway dg = new DefaultGateway();
		return dg;
	}

	// creating gateway from gateway config file
	public static Gateway getGateway(String gatewayID) throws GatewayNotFoundException {
		return null;
	}

}
