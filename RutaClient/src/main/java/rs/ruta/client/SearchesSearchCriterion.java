package rs.ruta.client;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlType;

import rs.ruta.common.SearchCriterion;

@XmlType(name = "SearchesSearchCriterion")
@XmlAccessorType(XmlAccessType.NONE)
public class SearchesSearchCriterion extends SearchCriterion
{
	@XmlElement(name = "Party")
	private boolean party;

	public boolean isParty()
	{
		return party;
	}

	public void setParty(boolean party)
	{
		this.party = party;
	}



}