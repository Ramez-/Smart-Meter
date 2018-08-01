package register;

import java.io.IOException;

import exceptions.InValidDataTypeException;

public interface IntegerRegister extends Register{
	
	Integer readInteger() throws IOException, InValidDataTypeException;

}
