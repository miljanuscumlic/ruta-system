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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import rs.ruta.client.MyParty;

public class ClientSignatureHandler implements SOAPHandler<SOAPMessageContext>
{
	private static final String COMMON_NAMESPACE = "http://www.ruta.rs/ns/common";
	private static Logger logger = LoggerFactory.getLogger("rs.ruta.client");
	private MyParty myParty;

	public ClientSignatureHandler(MyParty myParty)
	{
		this.myParty = myParty;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext mCtx)
	{
		final Boolean outbound = (Boolean) mCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		//process the message only if it is outbound and not RegisterUser message
		if(outbound)
		{
			final SOAPMessage message = mCtx.getMessage();
			if(!isRegisterUser(message))
			{
				try
				{
					final SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
					envelope.removeNamespaceDeclaration("env");
					if(envelope.getHeader() == null)
						envelope.addHeader();
					final SOAPHeader header = envelope.getHeader();
					header.setPrefix("S");
					final QName credentialsQname = new QName(COMMON_NAMESPACE, "Credentials");
					header.addHeaderElement(credentialsQname);
					final String timestamp = getTimestamp();
					final String username = myParty.getCDRUsername();
					final String secretKey = myParty.getCDRSecretKey();
					if(username == null || secretKey == null)
						throw new RuntimeException("RutaUser is not registered with the CDR service!");
					final String signature = getSignature(username, timestamp, getBytes(secretKey));
					final Node firstChild = header.getFirstChild();
					appendElement(firstChild, "Username", username);
					appendElement(firstChild, "Timestamp", timestamp);
					appendElement(firstChild, "Signature" , signature);
					message.saveChanges();
				}
				catch (SOAPException e)
				{
					logger.error("Exception is ", e);
				}
			}
		}
		return true;
	}

	private void appendElement(Node node, String name, String text)
	{
		final Element element = node.getOwnerDocument().createElementNS(COMMON_NAMESPACE, name);
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
		final Calendar calendar = Calendar.getInstance();
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Belgrade"));
		return dateFormat.format(calendar.getTime());
	}

	private String getSignature(String username, String timestamp, byte[] secretBytes)
	{
		try
		{
			final String toSign = username + timestamp;
			byte[] toSignBytes = getBytes(toSign);
			final Mac signer = Mac.getInstance("HmacSHA256");
			final SecretKeySpec keySpec = new SecretKeySpec(secretBytes, "HmacSHA256");
			signer.init(keySpec);
			signer.update(toSignBytes);
			byte[] signBytes = signer.doFinal();
			final String signature = new String(Base64.encodeBase64(signBytes));
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
			final SOAPBody body = message.getSOAPBody();
			final Node node = body.getFirstChild();
			registerUser = node.getNodeName().contains("RegisterUser");
		}
		catch (SOAPException e)
		{
			throw new RuntimeException(e);
		}
		return registerUser;
	}
}
