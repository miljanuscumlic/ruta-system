package rs.ruta.server;

import javax.xml.ws.WebFault;

//WebFault annotation is mandatory so I can include detail element inside the SOAPFault
/**UserException is thrown when thare is a problem with the user data sent from the client side.
 *
 */
@WebFault
public class UserException extends DetailException
{
	private static final long serialVersionUID = -4563032326263109358L;

	public UserException(String detail)
	{
		super(detail);
	}

	public UserException(String message, Throwable cause)
	{
		super(message, cause);
	}
}