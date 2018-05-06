package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "RutaVersion")
@XmlType(name = "RutaVersion")
@XmlAccessorType(XmlAccessType.NONE)
public class RutaVersion
{
	@XmlTransient
	private static String[] kinds = {"Client", "Service"};
	@XmlElement(name = "Side")
	private String side;
	@XmlElement(name = "Version")
	private String version;
	@XmlElement(name = "JAXBVersion")
	private String jaxbVersion;
	@XmlElement(name = "Weblink")
	private String weblink;

	public RutaVersion() { }

	public RutaVersion(String side, String version, String jaxbVersion, String weblink)
	{
		this.version = version;
		this.side = side;
		this.jaxbVersion = jaxbVersion;
		this.weblink = weblink;
	}

	public String getSide()
	{
		return side;
	}

	public void setSide(String side)
	{
		this.side = side;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getJaxbVersion()
	{
		return jaxbVersion;
	}

	public void setJaxbVersion(String jaxbVersion)
	{
		this.jaxbVersion = jaxbVersion;
	}

	public String getWeblink()
	{
		return weblink;
	}

	public void setWeblink(String weblink)
	{
		this.weblink = weblink;
	}

	public static String[] getKinds()
	{
		return kinds;
	}

}
