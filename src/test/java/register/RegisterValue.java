package register;

import exceptions.InValidDataTypeException;

public interface RegisterValue {
	Integer getInteger() throws InValidDataTypeException;

	Double getDouble() throws InValidDataTypeException;

	String getString() throws InValidDataTypeException;
}
