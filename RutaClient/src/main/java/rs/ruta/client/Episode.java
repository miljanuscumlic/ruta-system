package rs.ruta.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Episode", namespace = "http://ruta.rs/episode")
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
