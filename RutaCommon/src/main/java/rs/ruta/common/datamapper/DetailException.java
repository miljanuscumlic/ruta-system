package rs.ruta.common.datamapper;

/**
 * DetailException describes the detail of the exception that is thrown. Its FaultInfo
 * field that contains the detail description is later incorporated in the RutaException.
 */
public class DetailException extends Exception
{
	private static final long serialVersionUID = 6518894458611117725L;

	private FaultInfo detail;

	public DetailException(String message)
	{
		detail = new FaultInfo();
		detail.setDetail(message);
	}

	public DetailException(String message, Throwable cause)
	{
		super(cause);
		detail = new FaultInfo();
		detail.setDetail(message);
	}

	@Override
	public String getMessage()
	{
		return detail.getDetail();
	}

	public FaultInfo getFaultInfo()
	{
		return detail;
	}
}