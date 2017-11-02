package rs.ruta.server;

import java.util.Arrays;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rs.ruta.server.datamapper.MapperRegistry;
import rs.ruta.server.datamapper.User;

public class ServiceSignatureHandler implements SOAPHandler<SOAPMessageContext>
{

	public ServiceSignatureHandler() { }

	@Override
	public boolean handleMessage(SOAPMessageContext mCtx)
	{
		Boolean outbound = (Boolean) mCtx.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if(!outbound)
		{
			Boolean registerUser = (Boolean) mCtx.get("RegisterUser");
			if(registerUser == null || !registerUser)
			{
				SOAPMessage message = mCtx.getMessage();
				try
				{
					SOAPHeader header = message.getSOAPHeader();
					if(header == null)
						generateFault("No header!");
					Node node = header.getFirstChild();
					NodeList nodeList = node.getChildNodes();
					if(nodeList.getLength() < 3)
						generateFault("To few header nodes!");
					String username = nodeList.item(0).getFirstChild().getNodeValue();
					String timestamp = nodeList.item(1).getFirstChild().getNodeValue();
					String signature = nodeList.item(2).getFirstChild().getNodeValue();
					if(username == null || timestamp == null || signature == null)
						generateFault("Missing header key/value pairs!");
					String secretKey = getSecretKey(username);
					if(secretKey == null)
						generateFault(username + " is not registered!");
					String localSignature = getSignature(username, timestamp, getBytes(secretKey));
					if(!verifySignatures(signature, localSignature))
						generateFault("Signatures do not matches!");
				}
				//SOAPFaultException formed in generateFault method should not be cought here
				catch(DetailException e) // might be thrown by getSecretKey
				{
					generateFault(e.getMessage());
				}
				catch(SOAPException | DOMException e)
				{
					throw new RuntimeException("SOAPException thrown.", e);
				}
			}
			else // registerUser is true
			{
				String username = (String) mCtx.get("Username");
				try
				{
					getSecretKey(username); // throws an exception if there is no user registered with this username

					//user already registered with this username
					generateFault("Username \""+ username + "\" has already been taken. Please choose another one and try again.");
				}
				catch (DetailException e)
				{
					//it's OK. User doesn't exist.
				}
			}
		}
		return true;
	}

	/**Computes signiture based on the user's username, user sent timestamp and the user's secret key.
	 * @param username user's username
	 * @param timestamp user sent timestamp
	 * @param secretBytes secret key transformed to a byte array
	 * @return signature string
	 */
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

	/**Verifies if the contents of two strings are equal.
	 * @param sig1
	 * @param sig2
	 * @return true it the contents are equal, false otherwise
	 */
	private boolean verifySignatures(String sig1, String sig2)
	{
		return Arrays.equals(sig1.getBytes(), sig2.getBytes());
	}

	/**Transforms String to a byte array.
	 * @param str string to be transformed
	 * @return byte array
	 */
	private byte[] getBytes(String str)
	{
		try
		{
			return str.getBytes("UTF-8");
		}
		catch(Exception e) { throw new RuntimeException(e); }
	}

	/**Connects to the database and retrieves the secret key metadata of the user.
	 * @param username user's username
	 * @return user's secret key or <code>null</code> if secret key is not stored for the given username
	 * @throws DetailException if there is a problem with the data store connectivity or user is not registered
	 */
	private String getSecretKey(String username) throws DetailException
	{
		return (String) MapperRegistry.getMapper(User.class).findSecretKey(username);
	}

	/**Generates SOAPFaultException that is thrown as SOAP Fault to the client. Reason for the exception is
	 * encapsulated in the SOAPFault object.
	 * @param reason reason of the SOAP Fault
	 * @throws SOAPFaultException allways thrown
	 */
	private void generateFault(String reason)
	{
		try
		{
			SOAPFault soapFault = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createFault();
			soapFault.setFaultString(reason);
			throw new SOAPFaultException(soapFault);
		}
		catch(SOAPException e)
		{
			throw new RuntimeException("SOAP Exception thrown.", e);
		}
	}

	@Override
	public boolean handleFault(SOAPMessageContext context)
	{
		return true;
	}

	@Override
	public void close(MessageContext context) {	}

	@Override
	public Set<QName> getHeaders() { return null; }

}
