package gateway;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.opencsv.CSVReader;
import com.ubitronix.uiem.dlms.client.JDlmsSampleClient;

import exceptions.InValidConfigFileException;
import exceptions.MeterNotFoundException;
import meter.Meter;
import meter.MeterFactory;
import protocol.Protocol;

public class DefaultGateway implements Gateway {

	// meter ID in config file, protocol, serial number, manufacturer, port, client
	// address, logic address, physical address, allow write
	ArrayList<Meter> metersList;

	public DefaultGateway() throws Exception, InValidConfigFileException {
		metersList = metersFromConfigFile();
	}

	@Override
	public ArrayList<Meter> getAllMetersByProtocol(Protocol protocol) {
		ArrayList<Meter> arrList = new ArrayList<Meter>();
		for (Meter m : metersList) {
			if (m.getProtocol().getName() == protocol.getName()) {
				arrList.add(m);
			}
		}
		return arrList;
	}

	@Override
	public Meter getMeter(String meterID) throws MeterNotFoundException {
		ArrayList<Meter> arrList = new ArrayList<Meter>();
		for (Meter m : metersList) {
			if (m.getMeterID().equals(meterID)) {
				return m;
			}
		}
		throw new MeterNotFoundException("no meter found");
	}

	@Override
	public ArrayList<Meter> getAllMeters() {
		return metersList;
	}

	@Override
	public ArrayList<Protocol> getAllSupportedProtocols() {
		ArrayList<Protocol> arrList = new ArrayList<Protocol>();
		for (Meter m : metersList) {
			arrList.add(m.getProtocol());
		}
		return arrList;
	}

	private ArrayList<Meter> metersFromConfigFile() throws Exception, InValidConfigFileException {
		Scanner scanner = new Scanner(new File("file.csv"));

		Meter newMeter = null;
		ArrayList<String[]> metersConfig = new ArrayList<String[]>();
		ArrayList<Meter> metersFromConfig = new ArrayList<Meter>();

		while (scanner.hasNext()) {
			String x = scanner.nextLine();
			String[] meterConfig = x.split(",");
			metersConfig.add(meterConfig);
		}

		for (int i = 0; i < metersConfig.size(); i++) {
			newMeter = null;
			if (metersConfig.get(i)[1].contains("DLMS")) {
				newMeter = new MeterFactory(metersConfig.get(i)[1], metersConfig.get(i)[2], metersConfig.get(i)[3],
						metersConfig.get(i)[4], metersConfig.get(i)[5], metersConfig.get(i)[6], metersConfig.get(i)[7],
						metersConfig.get(i)[8]);
			} else {
				if (metersConfig.get(i)[1].contains("ANSI")) {
					newMeter = new MeterFactory(metersConfig.get(i)[1], metersConfig.get(i)[2], metersConfig.get(i)[3],
							metersConfig.get(i)[4], metersConfig.get(i)[5], metersConfig.get(i)[8]);
				}
			}
			if (newMeter == null) {
				throw new InValidConfigFileException("Error in config file");
			}
			metersFromConfig.add(newMeter);
		}
		return metersFromConfig;
	}

}
