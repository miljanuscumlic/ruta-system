package rs.ruta.common;

import javax.xml.ws.WebFault;

import rs.ruta.common.datamapper.DetailException;

//WebFault annotation is mandatory so I can include detail element inside the SOAPFault
/**DocumentException is thrown when is tried to distribute the document which type is not
 * applicable to the distribution among parties.
 * @author miljan
 */
@WebFault
public class DocumentException extends DetailException
{
	private static final long serialVersionUID = -4563032326263109358L;

	public DocumentException(String detail)
	{
		super(detail);
	}

	public DocumentException(String message, Throwable cause)
	{
		super(message, cause);
	}
}