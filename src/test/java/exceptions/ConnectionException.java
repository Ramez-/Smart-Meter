package exceptions;

public class ConnectionException extends Throwable {
	
	public ConnectionException(Exception e) {
		super(e.getMessage());
	}
}
