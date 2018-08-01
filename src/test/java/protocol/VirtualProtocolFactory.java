package protocol;

import java.lang.reflect.Field;
import java.util.ArrayList;

import protocol.DLMSProtocolFactory.RegisterInfo;
import register.RegisterType;
import register.RegisterUnit;

public class VirtualProtocolFactory implements VirtualProtocol {

	String protocolName;
	ArrayList<RegisterInfo> registerInfo = new ArrayList<RegisterInfo>();

	public VirtualProtocolFactory(String protocolName) throws IllegalArgumentException, IllegalAccessException {
		this.protocolName = protocolName;
		getParameters();
	}

	@Override
	public String getName() {
		return protocolName;
	}

	public void getParameters() throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = VirtualProtocol.class.getFields();
		for (int i = 0; i < fields.length; i += 3) {
			// I think the obj that is passed to get method is just the type that I think it
			// will return it can always be those fields
			RegisterInfo registerTemp = new RegisterInfo(fields[i].get(POSITIVE_ACTIVE_ENERGY_TOTAL_ID).toString(),
					(RegisterUnit) fields[i + 1].get(POSITIVE_ACTIVE_ENERGY_TOTAL_UNIT),
					(RegisterType) fields[i + 2].get(POSITIVE_ACTIVE_ENERGY_TOTAL_TYPE));
			registerInfo.add(registerTemp);
		}
	}

	public RegisterUnit getRegisterUnit(String registerID) {
		for (RegisterInfo register : registerInfo) {
			if (register.ID.equals(registerID)) {
				return register.unit;
			}
		}
		return null; // this should't be called because it is called from register
	}

	public RegisterType getRegisterType(String registerID) {
		for (RegisterInfo register : registerInfo) {
			if (register.ID.equals(registerID)) {
				return register.type;
			}
		}
		return null; // this should't be called because it is called from register
	}

	class RegisterInfo {
		String ID;
		RegisterUnit unit;
		RegisterType type;

		public RegisterInfo(String ID, RegisterUnit unit, RegisterType type) {
			this.ID = ID;
			this.unit = unit;
			this.type = type;
		}

	}

}
