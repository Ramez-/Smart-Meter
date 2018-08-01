package exceptions;

public class GatewayNotFoundException extends Throwable {

	public GatewayNotFoundException(Exception e) {
		super(e.getMessage());
	}
}
