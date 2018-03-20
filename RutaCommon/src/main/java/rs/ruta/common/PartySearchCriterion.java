package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *Class represents a {@code SearchCriterion} for {@link PartyType party} searches.
 */
//@XmlRootElement(name = "PartySearchCriterion", namespace = "urn:rs:ruta:common")
@XmlType(name = "PartySearchCriterion")
@XmlAccessorType(XmlAccessType.FIELD)
public class PartySearchCriterion extends SearchCriterion
{
	@XmlElement(name = "PartyName")
	private String partyName;
	@XmlElement(name = "PartyCompanyID")
	private String partyCompanyID;
	@XmlElement(name = "PartyClassCode")
	private String partyClassCode;
	@XmlElement(name = "PartyCity")
	private String partyCity;
	@XmlElement(name = "PartyCountry")
	private String partyCountry;
	@XmlElement(name = "PartyAll")
	private boolean partyAll;

	public PartySearchCriterion()
	{
		partyName = partyCompanyID = partyClassCode = partyCity = partyCountry = null;
		partyAll = false;
	}

	public String getPartyName()
	{
		return partyName;
	}

	public void setPartyName(String partyName)
	{
		this.partyName = partyName;
	}


	public String getPartyCompanyID()
	{
		return partyCompanyID;
	}


	public void setPartyCompanyID(String partyCompanyID)
	{
		this.partyCompanyID = partyCompanyID;
	}


	public String getPartyClassCode()
	{
		return partyClassCode;
	}


	public void setPartyClassCode(String partyClassCode)
	{
		this.partyClassCode = partyClassCode;
	}


	public String getPartyCity()
	{
		return partyCity;
	}


	public void setPartyCity(String partyCity)
	{
		this.partyCity = partyCity;
	}


	public String getPartyCountry()
	{
		return partyCountry;
	}


	public void setPartyCountry(String partyCountry)
	{
		this.partyCountry = partyCountry;
	}

	public boolean isPartyAll()
	{
		return partyAll;
	}

	public void setPartyAll(boolean partyAll)
	{
		this.partyAll = partyAll;
	}

	@Override
	public PartySearchCriterion nullEmptyFields()
	{
		if("".equals(partyName))
			partyName = null;
		if("".equals(partyCompanyID))
			partyCompanyID = null;
		if("".equals(partyClassCode))
			partyClassCode = null;
		if("".equals(partyCity))
			partyCity = null;
		if("".equals(partyCountry))
			partyCountry = null;
		return this;
	}

}
