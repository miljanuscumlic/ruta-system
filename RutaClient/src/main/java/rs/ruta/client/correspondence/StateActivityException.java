package rs.ruta.client.correspondence;

/**
 * {@code StateActivityException} is a class of those exceptions thrown when illegal state transition is
 * requested or some other error has happen during states's activity. It can be thrown when method that is
 * not allowed to be executed in the current state is invoked.
 * <p>Using {@link RuntimeException} for avoiding dependancy on implementation exceptions. This helps in
 * decoupling.</p>
 */
public class StateActivityException extends RuntimeException
{
	private static final long serialVersionUID = -796297690163802321L;

	public StateActivityException()
	{
		super(Messages.getString("StateActivityException.0")); 
	}

	public StateActivityException(String message)
	{
		super(message);
	}

	public StateActivityException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public StateActivityException(Throwable cause)
	{
		super(cause);
	}
}