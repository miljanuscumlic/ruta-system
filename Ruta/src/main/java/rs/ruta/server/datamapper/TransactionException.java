package rs.ruta.server.datamapper;

import rs.ruta.server.DetailException;

public class TransactionException extends DetailException
{
	private static final long serialVersionUID = 2990859306801475293L;

	public TransactionException(String message)
	{
		super(message);
	}

	public TransactionException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
