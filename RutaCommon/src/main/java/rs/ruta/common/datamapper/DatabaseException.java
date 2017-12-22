package rs.ruta.common.datamapper;

import javax.xml.ws.WebFault;

//WebFault annotation is mandatory so I can include detail element inside the SOAPFault
/**DatabaseException is thrown when there is a problem with the database connectivy or some other issue
 * in regard to calling the database.
 * @author miljan
 */
@WebFault
public class DatabaseException extends DetailException
{
	private static final long serialVersionUID = -4563032326263109358L;

	public DatabaseException(String detail)
	{
		super(detail);
	}

	public DatabaseException(String message, Throwable cause)
	{
		super(message, cause);
	}

}