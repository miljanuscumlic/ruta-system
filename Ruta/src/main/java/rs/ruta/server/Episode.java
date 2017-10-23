package rs.ruta.server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Episode")
@XmlAccessorType(XmlAccessType.FIELD)
public class Episode
{
	@XmlElement(name = "Yes")
	public boolean yes;

	public boolean isYes()
	{
		return yes;
	}

	public void setYes(boolean yes)
	{
		this.yes = yes;
	}
}
