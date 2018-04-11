package rs.ruta.client.correspondence;

/**
 * {@code StateTransitionException} is a class of those exceptions thrown when illegal state transition is
 * acquired. It can be a cese when method that is not allowed to be executed in the current state is invoked.
 * <p>Using {@link RuntimeException} for avoiding dependancy on implementation exceptions. This helps in
 * decoupling.</p>
 */
public class StateTransitionException extends RuntimeException
{
	private static final long serialVersionUID = -796297690163802321L;

	public StateTransitionException()
	{
		super("Illegal state transition acquired");
	}

	public StateTransitionException(String message)
	{
		super(message);
	}

	public StateTransitionException(String message, Throwable cause)
	{
		super(message, cause);
	}
}