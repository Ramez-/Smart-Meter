package protocol;

import java.lang.reflect.Field;
import java.util.ArrayList;

import exceptions.RegisterNotFoundException;
import register.RegisterType;
import register.RegisterUnit;

public class DLMSProtocolFactory implements DLMSProtocol {
	ArrayList<RegisterInfo> registerInfo = new ArrayList<RegisterInfo>();

	@Override
	public String getName() {
		return "DLMS/COSEM";
	}

	/*
	 * get parameters of the DLMSCOSEM interface with order put the data in
	 * arraylist regiseterInfo
	 */
	public void getParameters() throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = DLMSProtocol.class.getFields();
		for (int i = 0; i < fields.length; i += 3) {
			// I think the obj that is passed to get method is just the type that I think it
			// will return it can always be those fields
			RegisterInfo registerTemp = new RegisterInfo(fields[i].get(INSTANTANEOUS_POWER_ID).toString(),
					(RegisterUnit) fields[i + 1].get(INSTANTANEOUS_POWER_UNIT),
					(RegisterType) fields[i + 2].get(INSTANTANEOUS_POWER_TYPE));
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
