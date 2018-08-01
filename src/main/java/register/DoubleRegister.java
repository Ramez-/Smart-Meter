package register;

import java.io.IOException;

import exceptions.InValidDataTypeException;

public interface DoubleRegister extends Register {
	
	double readDouble() throws IOException, InValidDataTypeException;

}
