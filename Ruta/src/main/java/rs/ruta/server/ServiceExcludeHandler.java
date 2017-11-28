package rs.ruta.server;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Node;
//MMM: this class could be a part of ServiceSignatureHandler as it is on the client side
public class ServiceExcludeHandler implements SOAPHandler<SOAPMessageContext>
{

	public ServiceExcludeHandler() { }

	@Override
	public boolean handleMessage(SOAPMessageContext mCtx)
	{
		Boolean outbound = (Boolean) mCtx.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if(!outbound)
		{
			try
			{
				SOAPMessage message = mCtx.getMessage();
				SOAPBody body = message.getSOAPBody();
				Node node = body.getFirstChild();
				String webMethod  = node.getNodeName();
				if(webMethod.contains("RegisterUser"))
				{
					mCtx.put("RegisterUser", true); // put key/value pair in MessageContext map
					Node arg0 = node.getFirstChild(); // first argument
					if(arg0 != null)
						mCtx.put("Username", arg0.getTextContent());
				}
			}
			catch (SOAPException e)
			{
				throw new RuntimeException(e);
			}
		}
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context)
	{
		return false;
	}

	@Override
	public void close(MessageContext context)
	{
	}

	@Override
	public Set<QName> getHeaders()
	{
		return null;
	}
}
