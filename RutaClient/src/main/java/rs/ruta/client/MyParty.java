package rs.ruta.client;

import java.math.BigDecimal;
import java.util.*;

import javax.xml.bind.annotation.*;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.*;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.*;

@XmlRootElement(name = "MyParty", namespace = "urn:rs:ruta:client")
@XmlAccessorType(XmlAccessType.FIELD)
public class MyParty extends BusinessParty
{
	@XmlElement(name = "Password")
	private String password;
	@XmlElement(name = "Username")
	private String username;
	@XmlElement(name = "SecretKey")
	private String secretKey; // for SOAP message digest encryption
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
	@XmlElement(name = "DirtyMyParty")
	private boolean dirtyMyParty;
	@XmlElement(name = "InsertMyCatalogue")
	private boolean insertMyCatalogue; // true when catalogue should be inserted in the CDR i.e. deposit first time


	public MyParty()
	{
		super();
		setFollowing(true);
		dirtyCatalogue = false; //when first created My Catalogue is empty and therefore as nonexisting it is syncronized with the CDR service
		dirtyMyParty = insertMyCatalogue = true;
		username = password = secretKey = null;
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

	/**Checks if the party is completely registered with the CDR, i.e. if party has assigned unique Party ID.
	 * @return true if party is completely registered with the CDR service, otherwise false
	 */
	public boolean isRegisteredWithCDR()
	{
		//return secretKey != null ? true : false;
		return getCoreParty().getPartyID() == null ? false : true;
	}

	/**Checks if the party has secret key.
	 * @return true if party has secret key, otherwise false
	 */
	public boolean hasSecretKey()
	{
		return secretKey != null ? true : false;
	}

	public boolean isDirtyCatalogue()
	{
		return dirtyCatalogue;
	}

	public void setDirtyCatalogue(Boolean dirty)
	{
		dirtyCatalogue = dirty;
	}

	//MMM: maybe this method is not neccessery, because of the above one which might be mandatory because of the JAXB serialization
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
	public void setProductPackSizeNumeric(int index, BigDecimal value)
	{
		ItemType item = getMyProducts().get(index);
		if(item.getPackSizeNumeric() == null)
			item.setPackSizeNumeric(new PackSizeNumericType());
		if(hasCellValueChanged(item.getPackSizeNumeric().getValue(), value))
		{
			if(value.doubleValue() == 0)
				value = null;
			item.getPackSizeNumeric().setValue(value);
		}
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
	 * @param receiverParty receiver Party of the Catalogue
	 * @return catalogue
	 */
	public CatalogueType createCatalogue(Party receiverParty)
	{
		//forming Catalogue document
		CatalogueType catalogue = new CatalogueType();
		IDType catID = new IDType();
		String strID = String.valueOf(nextCatalogueID());
		catID.setValue(strID);
		catalogue.setID(catID);
		UUIDType catUUID = new UUIDType();
		catUUID.setValue(getCatalogueUUID());
		catalogue.setUUID(catUUID);
		IssueDateType date = new IssueDateType();
		date.setValue(setCatalogueIssueDate());
		catalogue.setIssueDate(date);

		catalogue.setProviderParty((PartyType) getCoreParty());
		catalogue.setReceiverParty((PartyType) receiverParty);
		// MMM: insert here Receiver Party (CDR)

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

	/**Generates Catalogue Deletion Document.
	 * @return catalogue deletion object
	 */
	public CatalogueDeletionType createCatalogueDeletion(Party CDRParty)
	{
		//creating Catalogue Deletion document
		CatalogueDeletionType catalogueDeletion = new CatalogueDeletionType();
		String strID = String.valueOf(nextCatalogueDeletionID());
		IDType catDelID = new IDType(strID);
		catalogueDeletion.setID(catDelID);
		UUIDType catDelUUID = new UUIDType(getCatalogueDeletionUUID());
		catalogueDeletion.setUUID(catDelUUID);
		catalogueDeletion.setIssueDate(InstanceFactory.getDate());
		catalogueDeletion.setDeletedCatalogueReference(getCatalogueReference());
		catalogueDeletion.setProviderParty((PartyType) getCoreParty());
		catalogueDeletion.setReceiverParty((PartyType) CDRParty);
		return catalogueDeletion;
	}

	/**Creates object representing reference to the latest created Catalogue Document.
	 * @return object representing reference to the lateset catalogue document
	 */
	private CatalogueReferenceType getCatalogueReference()
	{
		CatalogueReferenceType catRef = new CatalogueReferenceType();
		catRef.setID(String.valueOf(getCatalogueID()));
		catRef.setUUID(getCatalogueUUID());
		catRef.setIssueDate(getCatalogueIssueDate());
		return catRef;
	}

	/**Gets String representing UUID of the lastly created Catalogue Document.
	 * @return UUID of lastly created Catalogue as String
	 */
	private String getCatalogueUUID()
	{
		return getCoreParty().getPartyID()+ "CAT" + getCatalogueID();
	}

	/**Gets String representing UUID of the lastly created CatalogueDeletion Document.
	 * @return UUID of lastly created CatalogueDeletion as String
	 */
	private String getCatalogueDeletionUUID()
	{
		return getCoreParty().getPartyID()+ "CDL" + getCatalogueDeletionID();
	}


	/**Checks if the cell value has changed. If it has changed dirtyCatalogue field is set to true.
	 * @param oldOne old value of the cell
	 * @param newOne new value of the cell
	 * @return true if the values differs, false otherwise
	 */
	private <T> boolean hasCellValueChanged(T oldOne, T newOne)
	{
		boolean changed = false;
		if(newOne != null)
		{
			if(newOne instanceof String && newOne.toString().equals("") && oldOne == null)
				changed = false;
			changed = ! newOne.equals(oldOne);
		}
		dirtyCatalogue = dirtyCatalogue || changed;
		return changed;
	}

	public void addFollowingParty(BusinessParty party)
	{
		if(! followingParties.contains(party)) // MMM: this check should be based on some unique number e.g. party ID from the CDR database
			getFollowingParties().add(party);
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * @return the dirtyMyParty
	 */
	public boolean isDirtyMyParty()
	{
		return dirtyMyParty;
	}

	/**Sets the value of the boolean flag designating whether the Party object has been changed recently.
	 * @param dirtyMyParty boolean value to be set
	 */
	public void setDirtyMyParty(boolean dirtyMyParty)
	{
		this.dirtyMyParty = dirtyMyParty;
	}

	/**Returns secret key used for the SOAP message digest.
	 * @return the sekretKey or <code>null</code> if it is not defined.
	 */
	public String getSecretKey()
	{
		return "".equals(secretKey) ? null : secretKey;
	}

	/**Sets the value of the Party's secret key used for the SOAP message digest encryption and decryption.
	 * @param secretKey boolean value
	 */
	public void setSecretKey(String secretKey)
	{
		this.secretKey = secretKey;
	}

	/**Gets the boolean value that tells if the Catalogue should be inserted in or updated with the CDR service.
	 * @return true if Catalogue should be deposited in the CDR service for the first time i.e inserted,
	 *  false otherwise i.e. updated
	 */
	public boolean isInsertMyCatalogue()
	{
		return insertMyCatalogue;
	}

	/**Sets the value of the field that tells if the Catalogue should be inserted in or updated with the CDR service.
	 * @param insertMyCatalogue true if Catalogue should be deposited in the CDR service for the first time i.e inserted,
	 *  false otherwise i.e. updated
	 */
	public void setInsertMyCatalogue(boolean insertMyCatalogue)
	{
		this.insertMyCatalogue = insertMyCatalogue;
	}

}