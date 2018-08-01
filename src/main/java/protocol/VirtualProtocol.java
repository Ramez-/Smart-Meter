package protocol;

import register.RegisterType;
import register.RegisterUnit;

// implementation of general protocol handling 
public interface VirtualProtocol extends Protocol {

	// OBIS standard constants will be here
	public static final String POSITIVE_ACTIVE_ENERGY_TOTAL_ID = "1-0:1.8.0";
	public static final RegisterUnit POSITIVE_ACTIVE_ENERGY_TOTAL_UNIT = RegisterUnit.kWh;
	public static final RegisterType POSITIVE_ACTIVE_ENERGY_TOTAL_TYPE = RegisterType.Double;
	
	public static final String POSITIVE_ACTIVE_ENERGY_TOTAL_ID_T1 = "1-0:1.8.1";
	public static final RegisterUnit POSITIVE_ACTIVE_ENERGY_TOTAL_UNIT_T1 = RegisterUnit.kWh;
	public static final RegisterType POSITIVE_ACTIVE_ENERGY_TOTAL_TYPE_T1 = RegisterType.Double;
	
	public static final String POSITIVE_ACTIVE_ENERGY_TOTAL_ID_T2 = "1-0:1.8.2";
	public static final RegisterUnit POSITIVE_ACTIVE_ENERGY_TOTAL_UNIT_T2 = RegisterUnit.kWh;
	public static final RegisterType POSITIVE_ACTIVE_ENERGY_TOTAL_TYPE_T2 = RegisterType.Double;
	
	public static final String POSITIVE_ACTIVE_ENERGY_TOTAL_ID_T3 = "1-0:1.8.3";
	public static final RegisterUnit POSITIVE_ACTIVE_ENERGY_TOTAL_UNIT_T3 = RegisterUnit.kWh;
	public static final RegisterType POSITIVE_ACTIVE_ENERGY_TOTAL_TYPE_T3 = RegisterType.Double;
	
	public static final String POSITIVE_ACTIVE_ENERGY_TOTAL_ID_T4 = "1-0:1.8.4";
	public static final RegisterUnit POSITIVE_ACTIVE_ENERGY_TOTAL_UNIT_T4 = RegisterUnit.kWh;
	public static final RegisterType POSITIVE_ACTIVE_ENERGY_TOTAL_TYPE_T4 = RegisterType.Double;

	public static final String NEGETIVE_ACTIVE_ENERGY_TOTAL_ID = "1-0:2.8.0";
	public static final RegisterUnit NEGETIVE_ACTIVE_ENERGY_TOTAL_ID_UNIT = RegisterUnit.kvarh;
	public static final RegisterType NEGETIVE_ACTIVE_ENERGY_TOTAL_ID_TYPE = RegisterType.Integer;
	
	public static final String ABSOLUTE_ACTIVE_ENERGY_TOTAL_ID = "1-0:15.8.0";
	public static final RegisterUnit ABSOLUTE_ACTIVE_ENERGY_TOTAL_UNIT = RegisterUnit.kWh;
	public static final RegisterType ABSOLUTE_ACTIVE_ENERGY_TOTAL_TYPE = RegisterType.Double;
	
	public static final String POSITIVE_REACTIVE_ENERGY_TOTAL_ID = "1-0:3.8.0";
	public static final RegisterUnit POSITIVE_REACTIVE_ENERGY_TOTAL_UNIT = RegisterUnit.kvarh;
	public static final RegisterType POSITIVE_REACTIVE_ENERGY_TOTAL_TYPE = RegisterType.Double;
	
	
	public static final String POWER_OUTAGE_DURATION_ID = "powerOutageDurationTotal";
	public static final RegisterUnit POWER_OUTAGE_DURATION_UNIT = RegisterUnit.seconds;
	public static final RegisterType POWER_OUTAGE_DURATION_TYPE = RegisterType.Long;
	
	
	public static final String PULSE_INPUT_CHANNEL_ONE_TOTAL_ID = "pulseInputChannelOneTotal";
	public static final RegisterUnit PULSE_INPUT_CHANNEL_ONE_TOTAL_UNIT = RegisterUnit.none;
	public static final RegisterType PULSE_INPUT_CHANNEL_ONE_TOTAL_TYPE = RegisterType.Boolean;
}
