package rs.ruta.server.datamapper;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User
{
	@XmlElement
	private String username;
	@XmlElement
	private String password;

	public User() { username = password = null; }

	public String getUsername()	{ return username; }

	public void setUsername(String username) { this.username = username; }

	public String getPassword() { return password; }

	public void setPassword(String password) { this.password = password; }



}
