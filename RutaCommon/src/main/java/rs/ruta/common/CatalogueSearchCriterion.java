package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *Class represents a {@code SearchCriterion} for {@link CatalogueType catalogue} searches.
 */
@XmlType(name = "CatalogueSearchCriterion")
@XmlAccessorType(XmlAccessType.NONE)
public class CatalogueSearchCriterion extends SearchCriterion
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

	@XmlElement(name = "ItemName")
	private String itemName;
	@XmlElement(name = "ItemDescription")
	private String itemDescription;
	@XmlElement(name = "ItemBarcode")
	private String itemBarcode;
	@XmlElement(name = "ItemCommCode")
	private String itemCommCode;
	@XmlElement(name = "ItemKeyword")
	private String itemKeyword;
	@XmlElement(name = "ItemAll")
	private boolean itemAll;

	public CatalogueSearchCriterion()
	{
		partyName = partyCompanyID = partyClassCode = partyCity =
				partyCountry = itemName = itemDescription = itemBarcode = itemCommCode = itemKeyword = null;
		partyAll = itemAll = false;
	}

	/**
	 * Checks whether the search is supposed to query Catalogues. If not the Parties are only queried.
	 * @return true if Catalogues should be queried.
	 */
	public boolean isCatalogueSearchedFor()
	{
		return itemName != null || itemDescription != null || itemBarcode != null || itemCommCode != null || itemKeyword != null;
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

	public String getItemDescription()
	{
		return itemDescription;
	}

	public void setItemDescription(String itemDescription)
	{
		this.itemDescription = itemDescription;
	}

	public String getItemKeyword()
	{
		return itemKeyword;
	}

	public void setItemKeyword(String itemKeyword)
	{
		this.itemKeyword = itemKeyword;
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

	@Override
	public CatalogueSearchCriterion nullEmptyFields()
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
		if("".equals(itemName))
			itemName = null;
		if("".equals(itemDescription))
			itemDescription = null;
		if("".equals(itemBarcode))
			itemBarcode = null;
		if("".equals(itemCommCode))
			itemCommCode = null;
		if("".equals(itemKeyword))
			itemKeyword = null;
		return this;
	}

}