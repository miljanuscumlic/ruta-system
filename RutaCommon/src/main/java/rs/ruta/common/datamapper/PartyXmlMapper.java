package rs.ruta.common.datamapper;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

import javax.xml.bind.JAXBElement;

import org.exist.xmldb.XQueryService;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ObjectFactory;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.SearchCriterion;
import rs.ruta.common.CatalogueSearchCriterion;

public class PartyXmlMapper extends XmlMapper<PartyType>
{
	final private static String collectionPath = "/party";
	final private static String objectPackageName = "oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21";
	final private static String queryNameSearchParty = "search-party.xq";
	//MMM: This map should be some kind of most recently used collection bounded in size
	private Map<String, PartyType> loadedParties;

	public PartyXmlMapper(ExistConnector connector) throws DetailException
	{
		super(connector);
		loadedParties = new ConcurrentHashMap<String, PartyType>();
	}

	@Override
	protected String getCollectionPath() { return collectionPath; }
	@Override
	protected String getObjectPackageName() { return objectPackageName; }

/*	@Override
	public PartyType find(String id) throws DetailException
	{
		PartyType party = loadedParties.get(id);
		if(party == null)
		{
			party =  super.find(id);
			if(party != null)
				loadedParties.put((String) id,  party);
		}
		return party;
	}*/

	@Override
	public ArrayList<PartyType> findAll() throws DetailException
	{
		ArrayList<PartyType> parties = new ArrayList<>();
		String ids[] = listAllDocumentIDs();
		for(String id : ids)
		{
			final PartyType party = find(trimID(id));
			if(party != null)
				parties.add(party);
		}
		return parties.size() > 0 ? parties : null;

/*		ArrayList<PartyType> parties;
		parties = (ArrayList<PartyType>) super.findAll(username);
		if (parties != null)
			for(PartyType t : parties)
				loadedParties.put(getID(t), t);
		return parties;*/
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> List<U> findGeneric(CatalogueSearchCriterion criterion) throws DetailException
	{
		ArrayList<U> searchResult;
		searchResult = (ArrayList<U>) super.findGeneric(criterion);
/*		if (searchResult != null)
			for(U t : searchResult)
				loadedParties.put(getID(t),  t);*/
		return searchResult;
	}

	@Override
	protected void clearCachedObjects()
	{
		loadedParties.clear();
	}

	@Override
	protected PartyType getCachedObject(String id)
	{
		return loadedParties.get(id);
	}

	@Override
	protected PartyType removeCachedObject(String id)
	{
		return loadedParties.remove(id);
	}

	@Override
	protected void putCacheObject(String id, PartyType object)
	{
		loadedParties.put(id, object);
	}

	@Override
	protected String doPrepareAndGetID(PartyType party, String username, DSTransaction transaction)
			throws DetailException
	{
		String id = null;
		//object that should be stored doesn't have an ID and User has no Document ID metadata set
		//MMM: this will be changed - Party will get UUID on the Client side
		if(getPartyID(party) == null && getID(username) == null)
			id = createID();
		else
		{
			id = getID(username);
			final String uuid = getUserID(username);
			setPartyID(party, uuid);
		}
		return id;
	}

	//MMM: not used anymore
/*	@Deprecated
	@Override
	public String insert(String username, PartyType party, DSTransaction transaction) throws DetailException
	{
		Collection collection = null;
		String id = null;
		try
		{	//object that should be stored doesn't have an ID and User has no Document ID metadata set
			if(getPartyID(party) == null && getID(username) == null)
			{
				collection = getCollection();
				if(collection == null)
					throw new DatabaseException("Could not retrieve the collection.");
				id = createID(collection);
			}
			else
			{
				id = getID(username);
				String uuid = (String) getUserID(username);
				setPartyID(party, uuid);
			}
			insert(party, id, transaction);
			loadedParties.put(id, party);
			return id;
		}
		catch(XMLDBException e)
		{
			throw new DatabaseException("Could not retrieve the collection or create unique ID.", e);
		}
		finally
		{
			try
			{
				if(collection != null)
					collection.close();
			}
			catch(XMLDBException e)
			{
				logger.error("Exception is ", e);;
			}
		}
	}*/

	/**Sets the unique identification number for the party.
	 * @param party
	 * @param id
	 */
	private void setPartyID(PartyType party, String id)
	{
		List<PartyIdentificationType> identifications = party.getPartyIdentification();
		if(identifications.size() == 0)
			identifications.add(new PartyIdentificationType());
		if(identifications.get(0).getID() == null)
			identifications.get(0).setID(new IDType());
		identifications.get(0).getID().setValue(id);
	}

	/**Gets the unique ID number for the party.
	 * @param party
	 * @return uuid
	 */
	private String getPartyID(PartyType party)
	{
		try
		{
			String ID = InstanceFactory.getPropertyOrNull(party.getPartyIdentification().get(0).getID(), IDType::getValue);
			return ID.equals("") ? null : ID;
		}
		catch(Exception e) { return null; }
	}

	@Override
	protected JAXBElement<PartyType> getJAXBElement(PartyType object)
	{
		return new ObjectFactory().createParty(object);
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return PartyType.class;
	}

	@Override
	protected String getSearchQueryName()
	{
		return queryNameSearchParty;
	}

	@Override
	@Deprecated //MMM: using string replacement
	protected String prepareQuery(CatalogueSearchCriterion criterion) throws DatabaseException
	{
		String query = openXmlDocument(getQueryPath(), queryNameSearchParty);
		if(query == null)
			return query;

		String partyName = criterion.getPartyName();
		String partyCompanyID = criterion.getPartyCompanyID();
		String partyClassCode = criterion.getPartyClassCode();
		String partyCity = criterion.getPartyCity();
		String partyCountry = criterion.getPartyCountry();
		boolean partyAll = criterion.isPartyAll();

		String preparedQuery = query;
		if(partyName != null)
			preparedQuery = preparedQuery.replaceFirst("party-name( )+external( )*:=( )*[(][)]",
					(new StringBuilder("party-name := '").append(partyName).append("'")).toString());
		if(partyCompanyID != null)
			preparedQuery = preparedQuery.replaceFirst("party-company-id( )+external( )*:=( )*[(][)]",
					(new StringBuilder("party-company-id := '").append(partyCompanyID).append("'")).toString());
		if(partyClassCode != null)
			preparedQuery = preparedQuery.replaceFirst("party-class-code( )+external( )*:=( )*[(][)]",
					(new StringBuilder("party-class-code := '").append(partyClassCode).append("'")).toString());
		if(partyCity != null)
			preparedQuery = preparedQuery.replaceFirst("party-city( )+external( )*:=( )*[(][)]",
					(new StringBuilder("party-city := '").append(partyCity).append("'")).toString());
		if(partyCountry != null)
			preparedQuery = preparedQuery.replaceFirst("party-country( )+external( )*:=( )*[(][)]",
					(new StringBuilder("party-country := '").append(partyCountry).append("'")).toString());
		if(!partyAll)
			preparedQuery = preparedQuery.replaceFirst("party-all( )+external( )*:=( )*true",
					(new StringBuilder("party-all := false")).toString());

		return preparedQuery;
	}

	@Override
	protected String prepareQuery(SearchCriterion criterion, XQueryService queryService) throws DatabaseException
	{
		String queryName = null;
		if(criterion.getClass() == CatalogueSearchCriterion.class)  //MMM: at this point this is superfluous
			queryName = queryNameSearchParty;
		String query = openXmlDocument(getQueryPath(), queryName);

		CatalogueSearchCriterion sc = (CatalogueSearchCriterion) criterion;
		String partyName = sc.getPartyName();
		String partyCompanyID = sc.getPartyCompanyID();
		String partyClassCode = sc.getPartyClassCode();
		String partyCity = sc.getPartyCity();
		String partyCountry = sc.getPartyCountry();
		boolean partyAll = sc.isPartyAll();

		if(query == null)
			return query;
		try
		{
			StringBuilder queryPath = new StringBuilder(getRelativeRutaCollectionPath()).append(collectionPath);
			queryService.declareVariable("path", queryPath.toString());
			if(partyName != null)
				queryService.declareVariable("party-name", partyName);
			if(partyCompanyID != null)
				queryService.declareVariable("party-company-id", partyCompanyID);
			if(partyClassCode != null)
				queryService.declareVariable("party-class-code", partyClassCode);
			if(partyCity != null)
				queryService.declareVariable("party-city", partyCity);
			if(partyCountry != null)
				queryService.declareVariable("party-country", partyCountry);
			if(!partyAll)
				queryService.declareVariable("party-all", false);
		}
		catch(XMLDBException e)
		{
			logger.error(e.getMessage(), e);
			throw new DatabaseException("Could not process the query. There has been an error in the process of its exceution.", e);
		}

		return query;
	}

	@Override
	protected String prepareQuery(String queryName, CatalogueSearchCriterion criterion) throws DatabaseException
	{
		String query = openXmlDocument(getQueryPath(), queryName);
		if(query == null)
			return query;

		String partyName = criterion.getPartyName();
		String partyCompanyID = criterion.getPartyCompanyID();
		String partyClassCode = criterion.getPartyClassCode();
		String partyCity = criterion.getPartyCity();
		String partyCountry = criterion.getPartyCountry();
		boolean partyAll = criterion.isPartyAll();

		String itemName = criterion.getItemName();
		String itemBarcode = criterion.getItemBarcode();
		String itemCommCode = criterion.getItemCommCode();
		boolean itemAll = criterion.isItemAll();

/*		String preparedQuery = query.replaceFirst("declare variable party-name external := ''",
				"declare variable party-name external := " + partyName);*/

		String preparedQuery = query.replaceFirst("([$]party-name external := ')'", "$1" + partyName + "'");
		return preparedQuery;
	}

}
