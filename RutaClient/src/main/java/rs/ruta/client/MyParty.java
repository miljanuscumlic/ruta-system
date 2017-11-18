package rs.ruta.client;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.annotation.*;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.*;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.*;

@XmlRootElement(name = "MyParty", namespace = "urn:rs:ruta:client")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({CatalogueType.class}) //this solves the issue JAXB context not seeing the CatatalogueType
public class MyParty extends BusinessParty
{
/*	@XmlAttribute(required = false)
	private static final String VERSION = Client.getVersion().getJaxbVersion();*/
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
	@XmlElement(name = "PartySearch")
	private List<Search<PartyType>> partySearches;
	@XmlElement(name = "CatalogueSearch")
	private List<Search<CatalogueType>> catalogueSearches;
	@XmlElement(name = "DirtyCatalogue")
	private boolean dirtyCatalogue; // MMM: this property is also saved as the preference
	@XmlElement(name = "DirtyMyParty")
	private boolean dirtyMyParty;
	@XmlElement(name = "InsertMyCatalogue")
	private boolean insertMyCatalogue; // true when catalogue should be inserted in the CDR i.e. deposit for the first time
	@XmlElement(name = "SearchNumber")
	private long searchNumber;

	public MyParty()
	{
		super();
		setFollowing(true);
		dirtyCatalogue = true;
		dirtyMyParty = insertMyCatalogue = true;
		username = password = secretKey = null;
		searchNumber = 0;
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

	public List<Search<PartyType>> getPartySearches()
	{
		if(partySearches == null)
			partySearches = new ArrayList<Search<PartyType>>();
		return partySearches;
	}

	public void setPartySearches(List<Search<PartyType>> partySearches)
	{
		this.partySearches = partySearches;
	}

	public List<Search<CatalogueType>> getCatalogueSearches()
	{
		if(catalogueSearches == null)
			catalogueSearches = new ArrayList<Search<CatalogueType>>();
		return catalogueSearches;
	}

	public void setCatalogueSearches(List<Search<CatalogueType>> catalogueSearches)
	{
		this.catalogueSearches = catalogueSearches;
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
	public void setProductKeywords(int index, List<KeywordType> value)
	{
		ItemType item = getMyProducts().get(index);
		item.setKeyword(value);
	}

	@Override
	public void setProductKeywords(int index, String value)
	{
		ItemType item = getMyProducts().get(index);
		List<KeywordType> keywords =
				Stream.of(value.split("( )*[,;]+")).map(keyword -> new KeywordType(keyword)).collect(Collectors.toList());
		item.setKeyword(keywords);
	}

	/**Generates {@link CatalogueType} document from items in the Product table. Method returns {@code null} if Catalogue
	 * does not have any item or some item has no name or is empty string.
	 * @param receiverParty receiver Party of the Catalogue
	 * @return catalogue or {@code null}
	 */
	public CatalogueType createCatalogue(Party receiverParty)
	{
		CatalogueType catalogue = new CatalogueType();
		int cnt = 0;
		ArrayList<ItemType> myProducts = getMyProducts();
		if(checkProductNames(myProducts) && myProducts.size() != 0)
		{
			//populating Catalogue document
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

			for(ItemType prod : myProducts)
			{
				CatalogueLineType catLine = new CatalogueLineType();
				IDType catLineID = new IDType();
				catLineID.setValue(catID.getValue() + "-" + cnt++);
				catLine.setID(catLineID);
				catLine.setItem(prod);
				catalogue.getCatalogueLine().add(catLine);
			}
		}
		else
			catalogue = null;

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

	/**Creates object of {@code CatalogueReferenceType} representing reference to the latest created {@code CatalogueType} document.
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

	private boolean checkProductNames(ArrayList<ItemType> myProducts)
	{
		boolean ok = true;
		for(ItemType prod : myProducts)
		{
			String productName = prod.getNameValue();
			if (productName == null || productName.equals(""))
			{
				ok = false;
				break;
			}
		}
		return ok;
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
		if(party != null && !followingParties.contains(party)) // MMM: this check should be based on some unique number e.g. party ID from the CDR database
			getFollowingParties().add(party);
	}

	public void removeFollowingParty(BusinessParty party)
	{
		if(party != null && followingParties.contains(party)) // MMM: this check should be based on some unique number e.g. party ID from the CDR database
			getFollowingParties().remove(party);
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

	/**Get the boolean value telling if the {@code Party} data are in sync with the CDR service.
	 * @return true if the {@code Party} is in sync with the CDR service
	 */
	public boolean isDirtyMyParty()
	{
		return dirtyMyParty;
	}

	/**Sets the value of the boolean flag designating whether the {@code Party} object has been changed
	 * since it is retrieved from the database or updated with the CDR service.
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

	public long getSearchNumber()
	{
		return searchNumber;
	}

	public void setSearchNumber(long searchNumber)
	{
		this.searchNumber = searchNumber;
	}

	/**Adds My Party to the list of following parties.
	 */
	public void followMyself()
	{
		BusinessParty myPartyCopy = new BusinessParty();
		//Party coreParty = InstanceFactory.newInstance(getCoreParty());
		Party coreParty = getCoreParty().clone();
		myPartyCopy.setCoreParty(coreParty);
		//addFollowingParty(myPartyCopy);
		getFollowingParties().add(0, myPartyCopy);
	}

	/**Removes My Party from the list of following parties.
	 */
	public void unfollowMyself()
	{
		//removeFollowingParty(followingParties.get(0));
		getFollowingParties().remove(0);
	}

	/**Updates My Party in the list of the following parties.
	 */
	public void updateMyself()
	{
		unfollowMyself();
		followMyself();
	}
}