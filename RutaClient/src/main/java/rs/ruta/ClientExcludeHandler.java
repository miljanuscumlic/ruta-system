package rs.ruta;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;
//MMM: not used anymore; incorporated in ClientSignatureHandler
public class ClientExcludeHandler implements LogicalHandler<LogicalMessageContext>
{

	public ClientExcludeHandler() {	}

	/* Puts ("RegisterUser", true) in MessageContext map if the message is registerUser,
	 * that means clientHashHandler, next handler in chain, will skip this message.
	 * In a case of the registerUser method, user still don't have a secret key, which is necessary in the
	 * logic of the clientHashHandler. That's why it must be excluded from message processing.
	 * Otherwise if messages of other types were encountered method returns true and clientHashHandler
	 * will process these messages.
	 * @see javax.xml.ws.handler.Handler#handleMessage(javax.xml.ws.handler.MessageContext)
	 */
	@Override
	public boolean handleMessage(LogicalMessageContext lmCtx)
	{
		Boolean outbound = (Boolean) lmCtx.get(LogicalMessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if(outbound)
		{
			LogicalMessage message = lmCtx.getMessage();
			try
			{
				JAXBContext jaxbCtx = JAXBContext.newInstance("rs.ruta.server");
				JAXBElement<?> payload = (JAXBElement<?>) message.getPayload(jaxbCtx);
				if(payload != null)
				{
					Object webMethod = payload.getValue();
					if(webMethod.toString().contains("RegisterUser"))
						lmCtx.put("RegisterUser", true); // put key/value pair in MessageContex map
				}
			}
			catch (JAXBException e)
			{
				throw new RuntimeException(e);
			}
		}
		return true;
	}

	@Override
	public boolean handleFault(LogicalMessageContext context)
	{
		return true;
	}

	@Override
	public void close(MessageContext context) {	}

}
