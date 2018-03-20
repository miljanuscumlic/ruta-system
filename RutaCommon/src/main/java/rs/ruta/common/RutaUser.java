package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

/**
 *Defines the user of the {@code Ruta application} with his log-in credentials.
 */
@XmlRootElement(name = "RutaUser", namespace = "urn:rs:ruta:common")
@XmlAccessorType(XmlAccessType.FIELD)
public class RutaUser
{
	@XmlElement(name = "username")
	private String username;
	@XmlElement(name = "password")
	private String password;
	@XmlElement(name = "secretKey")
	private String secretKey;

	public RutaUser() { username = password = secretKey = null; }

	public RutaUser(String username, String password, String sekretkey)
	{
		this.username = username;
		this.password = password;
		this.secretKey = sekretkey;
	}

	public String getUsername()	{ return username; }

	public void setUsername(String username) { this.username = username; }

	public String getPassword() { return password; }

	public void setPassword(String password) { this.password = password; }

	public String getSecretKey() { return secretKey; }

	public void setSecretKey(String secretKey) { this.secretKey = secretKey; }

}