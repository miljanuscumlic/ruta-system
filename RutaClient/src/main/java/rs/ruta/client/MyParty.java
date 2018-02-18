package rs.ruta.client;

import java.math.BigDecimal;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.*;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.*;
import rs.ruta.common.DeregistrationNotice;
import rs.ruta.common.InstanceFactory;

@XmlRootElement(name = "MyParty", namespace = "urn:rs:ruta:client")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({CatalogueType.class}) //this solves the issue JAXB context not seeing the CatatalogueType
public class MyParty extends BusinessParty
{
	@XmlElement(name = "Password")
	private String password;
	@XmlElement(name = "Username")
	private String username;
	@XmlElement(name = "SecretKey")
	private String secretKey; // for SOAP message digest encryption
	/**
	 * MyParty data retrieved from the CDR service.
	 */
	@XmlElement(name = "MyFollowingParty")
	private BusinessParty myFollowingParty;
	/**
	 * List of all following parties of MyParty. Should always be accessed with {@link #getFollowingParty(String)} method call.
	 */
	@XmlElement(name = "FollowingParty")
	private List<BusinessParty> followingParties;
	//	@XmlElement(name = "BusinessPartner")
	/**
	 * Helper list containing only business partners of MyParty.
	 * Should always be accessed with {@link #getBusinessPartners()} method call.
	 */
	@XmlTransient
	private List<BusinessParty> businessPartners;
	/**
	 * Helper list containing only following parties that are not business partners of MyParty.
	 * Should always be accessed with {@link #getOtherParties()} method call.
	 */
	@XmlTransient
	private List<BusinessParty> otherParties;
	/**
	 * List of unfollowed parties from the CDR service.
	 * Should always be accessed with {@link #getArchivedParties()} method call.
	 */
	@XmlElement(name = "ArchivedParty")
	private List<BusinessParty> archivedParties;
	/**
	 * List of deregistered parties from the CDR service.
	 * Should always be accessed with {@link #getArchivedParties()} method call.
	 */
	@XmlElement(name = "DeregisteredParty")
	private List<BusinessParty> deregisteredParties;
	@XmlElement(name = "PartySearch")
	private List<Search<PartyType>> partySearches;
	@XmlElement(name = "CatalogueSearch")
	private List<Search<CatalogueType>> catalogueSearches;
	@XmlElement(name = "DirtyCatalogue")
	private boolean dirtyCatalogue;
	@XmlElement(name = "DirtyMyParty")
	private boolean dirtyMyParty;
	@XmlElement(name = "InsertMyCatalogue")
	private boolean insertMyCatalogue; // true when catalogue should be inserted in the CDR i.e. deposit for the first time
	@XmlElement(name = "SearchNumber")
	private long searchNumber;
	@XmlElement(name = "CatalogueID")
	protected long catalogueID;
	@XmlElement(name = "CatalogueDeletionID")
	protected long catalogueDeletionID;
	@XmlElement(name = "CatalogueIssueDate")
	protected XMLGregorianCalendar catalogueIssueDate;
	@XmlAttribute(name = "JAXBVersion")
	private String jaxb; // version of the MyParty class

	public MyParty()
	{
		super();
		setFollowing(true);
		dirtyCatalogue = dirtyMyParty = insertMyCatalogue = true;
		username = password = secretKey = null;
		followingParties = businessPartners = otherParties = archivedParties = deregisteredParties = null;
		searchNumber = catalogueID = catalogueDeletionID = 0;
		catalogueIssueDate = null;
		jaxb = Client.getVersion().getJaxbVersion();
	}

	/**Returns ID of the latest created Catalogue Document.
	 * @return catalogue ID
	 */
	public long getCatalogueID()
	{
		return catalogueID;
	}

	public void setCatalogueID(long catalogueID)
	{
		this.catalogueID = catalogueID;
	}

	/**Returns next ID for the newly created Catalogue Document.
	 * @return next catalogue ID
	 */
	public long nextCatalogueID()
	{
		return ++catalogueID;
	}

	/**Returns ID of the latest created Catalogue Deletion Document.
	 * @return catalogue deletion ID
	 */
	public long getCatalogueDeletionID()
	{
		return catalogueDeletionID;
	}

	public void setCatalogueDeletionID(long catalogueDeletionID)
	{
		this.catalogueDeletionID = catalogueDeletionID;
	}

	/**Gets issue date of the latest catalogue.
	 * @return catalogue issue date
	 */
	public XMLGregorianCalendar getCatalogueIssueDate()
	{
		return catalogueIssueDate;
	}

	/**Sets catalogue issue date to current date.
	 * @return set catalogue issue date
	 */
	public XMLGregorianCalendar setCatalogueIssueDate()
	{
		this.catalogueIssueDate = InstanceFactory.getDate();
		return catalogueIssueDate;
	}

	/**Sets Catalogue issue date to {@code null}. Usually this is done after the Catalogue is deleted from the CDR.
	 */
	public void removeCatalogueIssueDate()
	{
		this.catalogueIssueDate = null;
	}

	/**Tells if the Catalogue is in the CDR.
	 * @return true is CDR has My Party's Catalogue, false otherwise
	 */
	public boolean isCatalogueInCDR()
	{
		return catalogueIssueDate != null ? true : false;
	}

	/**Returns next ID for the newly created CatalogueDeletion Document.
	 * @return next catalogue deletion ID
	 */
	public long nextCatalogueDeletionID()
	{
		return ++catalogueDeletionID;
	}

	/**Gets the copy of MyParty previously retrieved from the CDR service.
	 * @return MyParty object or {@code null}
	 */
	public BusinessParty getMyFollowingParty()
	{
		return myFollowingParty;
	}

	public void setMyFollowingParty(BusinessParty myFollowingParty)
	{
		this.myFollowingParty = myFollowingParty;
	}

	/**Gets the {@code List} of all parties that are bussines partners of MyParty.
	 * @return list of business partners
	 */
	public List<BusinessParty> getBusinessPartners()
	{
		if(businessPartners == null)
			businessPartners = new ArrayList<BusinessParty>();
		businessPartners = getFollowingParties().stream().filter(party -> party.isPartner()).collect(Collectors.toList());
		return businessPartners;
	}

	/**Gets the {@code List} of all parties that are not bussines partners of MyParty. Those parties are just
	 * followed by MyParty.
	 * @return list of followed parties that are not business partners
	 */
	public List<BusinessParty> getOtherParties()
	{
		if(otherParties == null)
			otherParties = new ArrayList<BusinessParty>();
		otherParties = getFollowingParties().stream().filter(party -> ! party.isPartner()).collect(Collectors.toList());
		return otherParties;
	}

	public List<BusinessParty> getArchivedParties()
	{
		if(archivedParties == null)
			archivedParties = new ArrayList<>();
		return archivedParties;
	}

	public void setArchivedParties(List<BusinessParty> archivedParties)
	{
		this.archivedParties = archivedParties;
	}

	/**Gets the archived party with passed Party ID.
	 * @param id archived party's ID
	 * @return archived party or {@code null} if there is no party with specified ID in the list of
	 * archived parties
	 */
	public BusinessParty getArchivedParty(String id)
	{
		try
		{
			return getArchivedParties().stream().filter(party -> id.equals(party.getCoreParty().getPartyID())).findFirst().get();
		}
		catch(NoSuchElementException e) //if there is no party with passed id
		{
			return null;
		}
	}

	/**Checks if the Party is archived. Check is based on the Party ID only extracted from passed Party argument.
	 * So different Party objects with the same Party ID will have the same result.
	 * @param party party in check
	 * @return true if Party is archived, false otherwise
	 */
	public boolean checkArchivedParty(BusinessParty party)
	{
		String partyID = party.getPartyID();
		return getArchivedParty(partyID) != null ? true : false;
	}

	/**Adds to archived party list and removes it from archived list if it is in it.
	 * @param party party to add
	 */
	public void addArchivedParty(BusinessParty party)
	{
		if(party != null)
		{
			party.setArchived(true);
			getArchivedParties().add(party);
		}
	}

	/**Removes party from the list of archived parties.
	 * @param party party to be removed
	 */
	public void removeArchivedParty(BusinessParty party)
	{
		if(party != null)
		{
			//MMM: TODO can be removed if there are no correspondence/documents with the party
			getArchivedParties().remove(party);
			party.setArchived(false);
		}
	}

	public List<BusinessParty> getDeregisteredParties()
	{
		if(deregisteredParties == null)
			deregisteredParties = new ArrayList<>();
		return deregisteredParties;
	}

	public void setDeregisteredParties(List<BusinessParty> deregisteredParties)
	{
		this.deregisteredParties = deregisteredParties;
	}

	/**Gets the deregistered party with passed Party ID.
	 * @param id deregistered party's ID
	 * @return Deregistered party or {@code null} if there is no party with specified ID in the list of
	 * Deregistered parties
	 */
	public BusinessParty getDeregisteredParty(String id)
	{
		try
		{
			return getDeregisteredParties().stream().filter(party -> id.equals(party.getCoreParty().getPartyID())).findFirst().get();
		}
		catch(NoSuchElementException e) //if there is no party with passed id
		{
			return null;
		}
	}

	/**Checks if the Party is Deregistered. Check is based on the Party ID only extracted from passed Party argument.
	 * So different Party objects with the same Party ID will have the same result.
	 * @param party party in check
	 * @return true if Party is Deregistered, false otherwise
	 */
	public boolean checkDeregisteredParty(BusinessParty party)
	{
		String partyID = party.getPartyID();
		return getDeregisteredParty(partyID) != null ? true : false;
	}

	/**Adds to deregisterd party list and removes it from archived list if it is in it.
	 * @param party party to add
	 */
	public void addDeregisteredParty(BusinessParty party)
	{
		if(party != null)
		{
			party.setDeregistered(true);
			getDeregisteredParties().add(party);

			if(checkArchivedParty(party))
			{	//This way it is removed the object with the same Party ID, not just
				//the same object like in this call: getArchivedParties().remove(party)
				getArchivedParties().remove(getArchivedParty(party.getPartyID()));
			}
		}
	}

	/**Removes party from the list of Deregistered parties.
	 * @param party party to be removed
	 */
	public void removeDeregisteredParty(BusinessParty party)
	{
		if(party != null)
		{
			//MMM: TODO can be removed if there are no correspondence/documents with the party
			getDeregisteredParties().remove(party);
			party.setDeregistered(false);
		}
	}


	/**Gets the {@code List} of all parties that are followed by MyParty.
	 * @return list of all followed parties
	 */
	public List<BusinessParty> getFollowingParties()
	{
		if (followingParties == null)
			followingParties = new ArrayList<BusinessParty>();
		return followingParties;
	}

	public void setFollowingParties(List<BusinessParty> followingParties)
	{
		this.followingParties = followingParties;
		businessPartners = null;
		otherParties = null;
	}

	/**Gets the following party with passed ID.
	 * @param id following party's ID
	 * @return following party or {@code null} if there is no party with specified ID in the
	 * list of following parties
	 */
	public BusinessParty getFollowingParty(String id)
	{
		try
		{
			return getFollowingParties().stream().filter(party -> id.equals(party.getCoreParty().getPartyID())).findFirst().get();
		}
		catch(NoSuchElementException e) //if there is no party with passed id
		{
			return null;
		}
	}

	/**Adds party to the list of following parties and one appropriate helper list while removes it
	 * from the other. If party is present in the list, this method overwrites it.
	 * @param party following party
	 */
	public void addFollowingParty(BusinessParty party)
	{
		if(party != null)
		{
			party.setFollowing(true);
			//removal is necessary when party is already in the list, but its partner status has been changed
			//so this method is called because Party should be moved to the other helper list. Cumbersome.
			getFollowingParties().remove(party); //if exists MMM: better to use Sets then?
			getFollowingParties().add(party);
			if(party.isPartner())
			{
				getBusinessPartners().add(party);
				getOtherParties().remove(party);
			}
			else
			{
				getBusinessPartners().remove(party);
				getOtherParties().add(party);
			}
			if(checkArchivedParty(party))
			{	//when following party that is archived, object referenced in the archived list is
				//different than the one passed to this method. This way ine the list it is removed the
				//object with the same Party ID, not just the same object like in this call:
				//getArchivedParties().remove(party)
				getArchivedParties().remove(getArchivedParty(party.getPartyID()));
			}
		}
	}

	/**Adds party to the list of following parties, and sets it as a business partner in regard with
	 * the {@code partner} argument.
	 * @param party following party to be added
	 * @param partner whether the party is a business partner
	 */
	public void addFollowingParty(PartyType party, boolean partner)
	{
		if(party != null)
		{
			BusinessParty newParty = new BusinessParty();
			newParty.setCoreParty(party);
			newParty.setPartner(partner);
			addFollowingParty(newParty);
		}
	}

	/**Removes party from the list of following parties.
	 * @param party party to be removed
	 */
	public void removeFollowingParty(BusinessParty party)
	{
		if(party != null)
		{
			getFollowingParties().remove(party);
			if(party.isPartner())
				getBusinessPartners().remove(party);
			else
				getOtherParties().remove(party);
			party.setPartner(false);
			party.setFollowing(false);
		}
	}

	/**Checks if the Party is followed. Check is based on the Party ID only extracted from passed Party argument.
	 *  So different Party objects with the same Party ID will have the same result.
	 * @param party party in check
	 * @return true if Party is followed, false otherwise
	 */
	public boolean checkFollowingParty(BusinessParty party)
	{
		String partyID = party.getPartyID();
		return getFollowingParty(partyID) != null ? true : false;
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

	//MMM: maybe this method is not necessary, because of the above one which might be mandatory because of the JAXB serialization ???
	public void setDirtyCatalogue(boolean dirtyCatalogue)
	{
		this.dirtyCatalogue = dirtyCatalogue;
	}

	@Override
	public void setProductName(int index, String value)
	{
		ItemType item = getProducts().get(index);
		if(item.getName() == null)
			item.setName(new NameType());
		if(hasCellValueChanged(item.getName().getValue(), value))
			item.getName().setValue(value);
	}

	@Override
	public void setProductDescription(int index, String value)
	{
		ItemType item = getProducts().get(index);
		List<DescriptionType> descriptions = item.getDescription();
		if(descriptions.size() == 0)
			descriptions.add(new DescriptionType());
		if(hasCellValueChanged(descriptions.get(0).getValue(), value))
			descriptions.get(0).setValue(value);
	}

	@Override
	public void setProductID(int index, String value)
	{
		ItemType item = getProducts().get(index);
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
		ItemType item = getProducts().get(index);
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
		ItemType item = getProducts().get(index);
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
		ItemType item = getProducts().get(index);
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
		ItemType item = getProducts().get(index);
		item.setKeyword(value);
	}

	@Override
	public void setProductKeywords(int index, String value)
	{
		ItemType item = getProducts().get(index);
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
		ArrayList<ItemType> myProducts = getProducts();
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
	 * @param insertMyCatalogue true if Catalogue should be deposited in the CDR service for the first time i.e. inserted,
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

	/**
	 * Adds My Party to the list of following parties.
	 */
	public void followMyself()
	{
		BusinessParty myPartyCopy = clone(); //new BusinessParty();
		//Catalogue would be retrieved from CDR on next Get New Documents call invocation
		myPartyCopy.setCatalogue(null);
		myFollowingParty = myPartyCopy;
	}

	/**
	 * Removes My Party from the list of following parties.
	 */
	public void unfollowMyself()
	{
		//removeFollowingParty(followingParties.get(0));
		myFollowingParty = null;
		/*		List<BusinessParty> followings = getFollowingParties();

		if(followings.size() != 0 && followings.get(0).getCoreParty().getSimpleName().equals(this.getCoreParty().getSimpleName()))
			getFollowingParties().remove(0);*/
	}

	/**
	 * Updates My Party in the list of the following parties.
	 */
	public void updateMyself()
	{
		unfollowMyself();
		followMyself();
	}

	@Override
	public void removeProduct(int row)
	{
		super.removeProduct(row);
		dirtyCatalogue = true;
	}

	@Override
	public void addProduct(ItemType item)
	{
		super.addProduct(item);
		dirtyCatalogue = true;
	}

	//MMM: because of view update should this method be placed in Client class???
	void placeDocBoxCatalogue(CatalogueType catalogue, String docID)
	{
		PartyType provider = catalogue.getProviderParty();
		if(sameParties(this, provider))
		{
			getMyFollowingParty().setCatalogue(catalogue);
			getMyFollowingParty().setProducts(catalogue);
			//MMM: update the view (tree view, table....) ; bolding the name of the node in tree view
		}
		else
		{
			for(BusinessParty bParty: getFollowingParties())
				if(sameParties(bParty, provider))
				{
					bParty.setCatalogue(catalogue);
					bParty.setProducts(catalogue); //MMM: this is gonna be dealt with because this is double storeage of the same data
					//MMM: update the view (tree view, table....) ; bolding the name of the node in tree view
					break;
				}
		}

	}

	public void placeDocBoxParty(PartyType party, String docID)
	{
		if(sameParties(this, party))
			getMyFollowingParty().setCoreParty(party);
		else
			for(BusinessParty bParty: getFollowingParties())
			{
				if(sameParties(bParty, party))
				{
					bParty.setCoreParty(party);
					break;
				}
			}
	}

	public void placeDocBoxCatalogueDeletion(CatalogueDeletionType catDeletion, String docID)
	{
		PartyType provider = catDeletion.getProviderParty();
		if(sameParties(this, provider))
		{
			getMyFollowingParty().setCatalogue(null);
			getMyFollowingParty().clearProducts();
		}
		else
			for(BusinessParty bParty: getFollowingParties())
			{
				if(sameParties(bParty, provider))
				{
					bParty.setCatalogue(null);
					bParty.clearProducts();
					break;
				}
			}
	}

	public void placeDocBoxDeregistrationNotice(DeregistrationNotice notice, String docID)
	{
		PartyType party = notice.getParty();
		for(BusinessParty bParty: getFollowingParties())
			if(sameParties(bParty, party))
			{
				removeFollowingParty(bParty);
				addDeregisteredParty(bParty);
				break;
			}
	}

}