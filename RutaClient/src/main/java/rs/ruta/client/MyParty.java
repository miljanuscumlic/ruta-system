package rs.ruta.client;

import java.math.BigDecimal;
import java.util.*;

import javax.xml.bind.annotation.*;

import oasis.names.specification.ubl.schema.xsd.catalogue_2.*;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;

@XmlRootElement(name = "MyParty", namespace = "urn:rs:ruta:client")
@XmlAccessorType(XmlAccessType.FIELD)
public class MyParty extends BusinessParty
{
	@XmlElement(name = "BusinessPartner")
//	@XmlTransient
	private List<BusinessParty> businessPartners;
	@XmlElement(name = "FollowingPartner")
//	@XmlTransient
	private List<BusinessParty> followingParties;
//	@XmlElement(name = "FollowerPartners")
	@XmlTransient
	private List<BusinessParty> followerParties;

	@XmlElement(name = "DirtyCatalogue")
	private boolean dirtyCatalogue; // MMM: this property is also saved as the preference

	public MyParty()
	{
		super();
		setFollowing(true);
	}

	public List<BusinessParty> getBusinessPartners()
	{
		if (businessPartners == null)
			businessPartners = new ArrayList<BusinessParty>();
		return businessPartners;
	}

	public void setBusinessPartners(List<BusinessParty> businessPartners)
	{
		this.businessPartners = businessPartners;
	}

	public List<BusinessParty> getFollowingParties()
	{
		if (followingParties == null)
			followingParties = new ArrayList<BusinessParty>();
		return followingParties;
	}

	public void setFollowingParties(List<BusinessParty> followingParties)
	{
		this.followingParties = followingParties;
	}

	public List<BusinessParty> getFollowerParties()
	{
		if (followerParties == null)
			followerParties = new ArrayList<BusinessParty>();
		return followerParties;
	}

	public void setFollowerParties(List<BusinessParty> followerParties)
	{
		this.followerParties = followerParties;
	}

	public boolean isDirtyCatalogue()
	{
		return dirtyCatalogue;
	}

	public void setDirtyCatalogue(Boolean dirty)
	{
		dirtyCatalogue = dirty;
	}



	public void setDirtyCatalogue(boolean dirtyCatalogue)
	{
		this.dirtyCatalogue = dirtyCatalogue;
	}

	@Override
	public void setProductName(int index, String value)
	{
		ItemType item = getMyProducts().get(index);
		if(item.getName() == null)
			item.setName(new NameType());
		if(hasCellValueChanged(item.getName().getValue(), value))
			item.getName().setValue(value);
	}

	@Override
	public void setProductDescription(int index, String value)
	{
		ItemType item = getMyProducts().get(index);
		List<DescriptionType> descriptions = item.getDescription();
		if(descriptions.size() == 0)
			descriptions.add(new DescriptionType());
		if(hasCellValueChanged(descriptions.get(0).getValue(), value))
			descriptions.get(0).setValue(value);
	}

	@Override
	public void setProductID(int index, String value)
	{
		ItemType item = getMyProducts().get(index);
		if(item.getSellersItemIdentification() == null)
			item.setSellersItemIdentification(new ItemIdentificationType());
		if(item.getSellersItemIdentification().getID() == null)
			item.getSellersItemIdentification().setID(new IDType());
		if(hasCellValueChanged(item.getSellersItemIdentification().getID().getValue(), value))
			item.getSellersItemIdentification().getID().setValue(value);
	}

	@Override
	public void setProductBarcode(int index, String value)
	{
		ItemType item = getMyProducts().get(index);
		if(item.getSellersItemIdentification() == null)
			item.setSellersItemIdentification(new ItemIdentificationType());
		if(item.getSellersItemIdentification().getBarcodeSymbologyID() == null)
			item.getSellersItemIdentification().setBarcodeSymbologyID(new BarcodeSymbologyIDType());
		if(hasCellValueChanged(item.getSellersItemIdentification().getBarcodeSymbologyID().getValue(), value))
			item.getSellersItemIdentification().getBarcodeSymbologyID().setValue(value);

	}

	@Override
	public void setProductPackSizeNumeric(int index, String value)
	{
		ItemType item = getMyProducts().get(index);
		if(item.getPackSizeNumeric() == null)
			item.setPackSizeNumeric(new PackSizeNumericType());
		if(hasCellValueChanged(item.getPackSizeNumeric().getValue().toString(), value))
			item.getPackSizeNumeric().setValue(BigDecimal.valueOf(Long.parseLong(value)));
	}

	@Override
	public void setProductCommodityCode(int index, String value)
	{
		ItemType item = getMyProducts().get(index);
		List<CommodityClassificationType> commodities = item.getCommodityClassification();
		if(commodities.size() == 0)
			commodities.add(new CommodityClassificationType());
		if(commodities.get(0).getCommodityCode() == null)
			commodities.get(0).setCommodityCode(new CommodityCodeType());
		if(hasCellValueChanged(commodities.get(0).getCommodityCode().getValue(), value))
			commodities.get(0).getCommodityCode().setValue(value);;
	}

	@Override
	public void setProductItemClassificationCode(int index, String value)
	{
		ItemType item = getMyProducts().get(index);
		List<CommodityClassificationType> commodities = item.getCommodityClassification();
		if(commodities.size() == 0)
			commodities.add(new CommodityClassificationType());
		if(commodities.get(0).getItemClassificationCode() == null)
			commodities.get(0).setItemClassificationCode(new ItemClassificationCodeType());
		if(hasCellValueChanged(commodities.get(0).getItemClassificationCode().getValue(), value))
			commodities.get(0).getItemClassificationCode().setValue(value);;
	}

	/**Generates Catalogue Document from Items in the Product table.
	 * @return catalogue
	 */
	public CatalogueType createCatalogueTypeObject()
	{
		//forming Catalogue document
		CatalogueType catalogue = new CatalogueType();
		IDType catID = new IDType();
		catID.setValue(String.valueOf(catalogueID++));
		catalogue.setID(catID);
		IssueDateType date = new IssueDateType();
		date.setValue(InstanceFactory.getDate());
		catalogue.setIssueDate(date);

		// MMM: insert here Provider Party and Receiver Party

		int cnt = 0;
		for(ItemType prod : getMyProducts())
		{
			CatalogueLineType catLine = new CatalogueLineType();
			IDType catLineID = new IDType();
			catLineID.setValue(catID.getValue() + "-" + cnt++);
			catLine.setID(catLineID);
			catLine.setItem(prod);
			catalogue.getCatalogueLine().add(catLine);
		}
		return catalogue;
	}

	/**Checks if the cell value has changed. If it has changed dirtyCatalogue field is set to true
	 * @param oldOne old value of the cell
	 * @param newOne new value of the cell
	 * @return true if the values differs, false otherwise
	 */
	private <T> boolean hasCellValueChanged(T oldOne, T newOne)
	{
		boolean changed = ! newOne.equals(oldOne);
		dirtyCatalogue = dirtyCatalogue || changed;
		return changed;
	}

	public void addFollowingParty(BusinessParty party)
	{
		if(! followingParties.contains(party)) // MMM: this check should be based on some unique number e.g. party ID from the CDR database
			getFollowingParties().add(party);
	}

}