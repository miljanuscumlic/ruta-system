package rs.ruta;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import rs.ruta.client.Client;
import rs.ruta.client.MyParty;

public class ClientSignatureHandler implements SOAPHandler<SOAPMessageContext>
{
	private MyParty myParty;

	public ClientSignatureHandler(MyParty myParty)
	{
		this.myParty = myParty;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext mCtx)
	{
		Boolean outbound = (Boolean) mCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		//process the message only if it is outbound and not RegisterUser message
		if(outbound)
		{
			SOAPMessage message = mCtx.getMessage();
			if(!isRegisterUser(message))
			{
				try
				{
					SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
					if(envelope.getHeader() == null)
						envelope.addHeader();
					SOAPHeader header = envelope.getHeader();
					QName qname = new QName("http://ruta.rs/credentials", "Credentials");
					header.addHeaderElement(qname);
					String timestamp = getTimestamp();
					String username = myParty.getUsername();
					String secretKey = myParty.getSecretKey();
					String signature = getSignature(username, timestamp, getBytes(secretKey));
					Node firstChild = header.getFirstChild();
					append(firstChild, "Username", username);
					append(firstChild, "Timestamp", timestamp);
					append(firstChild, "Signature" , signature);
					message.saveChanges();
				}
				catch (SOAPException e)
				{
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	private void append(Node node, String name, String text)
	{
		Element element = node.getOwnerDocument().createElement(name);
		element.setTextContent(text);
		node.appendChild(element);
	}

	@Override
	public boolean handleFault(SOAPMessageContext context)
	{
		return true;
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

	private byte[] getBytes(String str)
	{
		try
		{
			return str.getBytes("UTF-8");
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private String getTimestamp()
	{
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Belgrade"));
		return dateFormat.format(calendar.getTime());
	}

	private String getSignature(String username, String timestamp, byte[] secretBytes)
	{
		try
		{
			String toSign = username + timestamp;
			byte[] toSignBytes = getBytes(toSign);
			Mac signer = Mac.getInstance("HmacSHA256");
			SecretKeySpec keySpec = new SecretKeySpec(secretBytes, "HmacSHA256");
			signer.init(keySpec);
			signer.update(toSignBytes);
			byte[] signBytes = signer.doFinal();
			String signature = new String(Base64.encodeBase64(signBytes));
			return signature;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private boolean isRegisterUser(SOAPMessage message)
	{
		boolean registerUser = false;
		try
		{
			SOAPBody body = message.getSOAPBody();
			Node node = body.getFirstChild();
			registerUser = node.getNodeName().contains("RegisterUser");
		}
		catch (SOAPException e)
		{
			throw new RuntimeException(e);
		}
		return registerUser;
	}
}
