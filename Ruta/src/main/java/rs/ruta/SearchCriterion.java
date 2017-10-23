package rs.ruta;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "SearchCriterion", namespace = "urn:rs:ruta:client")
@XmlType(name = "SearchCriterion")
@XmlAccessorType(XmlAccessType.FIELD)
//MMM: should be part of the common project
public class SearchCriterion
{
	@XmlElement(name="PartyName")
	private String partyName;
	@XmlElement(name="PartyCompanyID")
	private String partyCompanyID;
	@XmlElement(name="PartyClassCode")
	private String partyClassCode;
	@XmlElement(name="PartyCity")
	private String partyCity;
	@XmlElement(name="PartyCountry")
	private String partyCountry;
	@XmlElement(name="PartyAll")
	private boolean partyAll;

	@XmlElement(name="ItemName")
	private String itemName;
	@XmlElement(name="ItemBarcode")
	private String itemBarcode;
	@XmlElement(name="ItemCommCode")
	private String itemCommCode;
	@XmlElement(name="ItemAll")
	private boolean itemAll;

	public SearchCriterion()
	{
		partyName = partyCompanyID = partyClassCode = partyCity =
				partyCountry = itemName = itemBarcode = itemCommCode = null;
		partyAll = itemAll = false;
	}

	public boolean isCatalogueSearched()
	{
		return itemName != null || itemBarcode != null || itemCommCode != null;
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


	public String getItemName()
	{
		return itemName;
	}


	public void setItemName(String itemName)
	{
		this.itemName = itemName;
	}


	public String getItemBarcode()
	{
		return itemBarcode;
	}


	public void setItemBarcode(String itemBarcode)
	{
		this.itemBarcode = itemBarcode;
	}


	public String getItemCommCode()
	{
		return itemCommCode;
	}


	public void setItemCommCode(String itemCommCode)
	{
		this.itemCommCode = itemCommCode;
	}


	public boolean isPartyAll()
	{
		return partyAll;
	}


	public void setPartyAll(boolean partyAll)
	{
		this.partyAll = partyAll;
	}


	public boolean isItemAll()
	{
		return itemAll;
	}


	public void setItemAll(boolean itemAll)
	{
		this.itemAll = itemAll;
	}

}
