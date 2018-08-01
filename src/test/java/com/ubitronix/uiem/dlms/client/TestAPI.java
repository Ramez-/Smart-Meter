package com.ubitronix.uiem.dlms.client;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import exceptions.ConnectionException;
import exceptions.GatewayNotFoundException;
import exceptions.InValidConfigFileException;
import exceptions.InValidDataTypeException;
import exceptions.MeterNotFoundException;
import exceptions.NotWritableMeterException;
import gateway.Gateway;
import gateway.GatewayFactory;
import meter.Meter;
import protocol.DLMSProtocolFactory;
import protocol.VirtualProtocolFactory;
import register.DoubleRegister;
import register.IntegerRegister;
import register.Register;

public class TestAPI {

	public static void testANSI() throws GatewayNotFoundException, Exception, InValidConfigFileException,
			MeterNotFoundException, NotWritableMeterException, InValidDataTypeException, ConnectionException {
		Gateway gateway = GatewayFactory.getGateway();
		Meter meter = gateway.getMeter("UBI26001034");
		meter.openConnection();
		meter.getAllRegisters().get(0).open();
//		System.out.println(meter.getAllRegisters().get(0).readDouble());
//		System.out.println(meter.getAllRegisters().get(0).read());
//		System.out.println(meter.getAllRegisters().get(0).getRegisterUnit());
//		System.out.println(meter.getAllRegisters().get(0).getRegisterID());
//		System.out.println(meter.getAllRegisters().get(0).getMeter().getMeterID());
		
		
		// returns structure data object, double is represented by float 64
		//System.out.println(meter.getAllRegisters().get(0).read());
//		System.out.println(meter.getAllRegisters().get(0).getRegisterType());
				
				
		// close breaker
//		if (meter.isWritable()) {
//			meter.breakerClose();
//		}

//		System.out.println(meter.getAllRegisters().size());
//		System.out.println(meter.getRegister("pulseInputChannelOneTotal").getRegisterType());
		
		// meter id and meter type
//		for (int i = 0; i < meter.getAllRegisters().size(); i++) {
//			System.out.println(meter.getAllRegisters().get(i).getRegisterID() + " " + meter.getAllRegisters().get(i).getRegisterType());
//		}
		
		
		meter.getAllRegisters().get(0).close();

		meter.closeConnection();
	}

	public static void testDLMS() throws GatewayNotFoundException, Exception, InValidConfigFileException,
			MeterNotFoundException, ConnectionException, InValidDataTypeException {
		Gateway gateway = GatewayFactory.getGateway();
		Meter meter = gateway.getMeter("96224681");
		meter.openConnection();
		System.out.println(meter.hasRegister("1.1.16.7.0.255"));
		System.out.println(meter.getRegister("1.1.16.7.0.255").getRegisterType());
		meter.getRegister("1.1.16.7.0.255").open();
		System.out.println(meter.getRegister("1.1.16.7.0.255").read());
		System.out.println(meter.getRegister("1.1.16.7.0.255").readDouble());
		meter.getRegister("1.1.16.7.0.255").close();
		meter.closeConnection();
	}

	public static void main(String[] args) throws GatewayNotFoundException, MeterNotFoundException, Exception,
			ConnectionException, InValidDataTypeException, InValidConfigFileException, NotWritableMeterException {

		Gateway g = GatewayFactory.getGateway();
		// one meter use DLMS/COSEM
		System.out.println(g.getAllMetersByProtocol(new DLMSProtocolFactory()).size());
		
		
		
//		testANSI();
		testDLMS();

	}
}
