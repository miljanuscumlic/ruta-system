package rs.ruta.common.datamapper;

import javax.xml.ws.WebFault;

//WebFault annotation is mandatory so I can include detail element inside the SOAPFault
/**DataManipulationException is thrown when there is a problem with the database connectivy or some other issue
 * in regard to calling the database.
 * @author miljan
 */
@WebFault
public class DataManipulationException extends DetailException
{
	private static final long serialVersionUID = -4563032326263109358L;

	public DataManipulationException(String detail)
	{
		super(detail);
	}

	public DataManipulationException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
