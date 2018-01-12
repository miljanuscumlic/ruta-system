package rs.ruta.common.datamapper;

import java.util.*;
import java.util.concurrent.*;

import javax.xml.bind.JAXBElement;

import org.exist.xmldb.XQueryService;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import rs.ruta.common.SearchCriterion;
import rs.ruta.common.ObjectFactory;
import rs.ruta.common.CatalogueSearchCriterion;
import rs.ruta.common.Followers;

public class FollowersXmlMapper extends XmlMapper<Followers>
{
	final private static String collectionPath = "/followers";
	final private static String objectPackageName = Followers.class.getPackage().getName(); //"rs.ruta.server.data";
	final private static String queryNameSearchFollowers = "search-following.xq"; // MMM: not defined yet
	//MMM: This map should be some kind of most recently used collection bounded in size
	private Map<String, Followers> loadedFollowers;

	public FollowersXmlMapper(ExistConnector connector) throws DetailException
	{
		super(connector);
		loadedFollowers = new ConcurrentHashMap<String, Followers>();
	}

	@Override
	protected String getCollectionPath() { return collectionPath; }
	@Override
	protected String getObjectPackageName() { return objectPackageName; }

	@Override
	protected void clearCachedObjects()
	{
		loadedFollowers.clear();
	}

	@Override
	protected Followers getCachedObject(String id)
	{
		return loadedFollowers.get(id);
	}

	@Override
	protected Followers removeCachedObject(String id)
	{
		return loadedFollowers.remove(id);
	}

	@Override
	protected void putCacheObject(String id, Followers object)
	{
		loadedFollowers.put(id, object);
	}

	@Override
	protected JAXBElement<Followers> getJAXBElement(Followers object)
	{
		return new ObjectFactory().createFollowers(object);
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return Followers.class;
	}

	@Override
	protected String getSearchQueryName()
	{
		return queryNameSearchFollowers;
	}

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
	public ArrayList<Followers> findAll() throws DetailException
	{
		ArrayList<Followers> followings = new ArrayList<>();
		String ids[] = listAllDocumentIDs();
		for(String id : ids)
		{
			final Followers f = find(trimID(id));
			if(f != null)
				followings.add(f);
		}
		return followings.size() > 0 ? followings : null;
	}

/*	@Override
	public String update(String username, Followers object) throws DetailException
	{
		return insert(username, object);
	}*/

	@Override
	protected String prepareQuery(SearchCriterion criterion, XQueryService queryService) throws DatabaseException
	{
		CatalogueSearchCriterion sc = (CatalogueSearchCriterion) criterion;
		String partyName = sc.getPartyName();
		String partyCompanyID = sc.getPartyCompanyID();
		String partyClassCode = sc.getPartyClassCode();
		String partyCity = sc.getPartyCity();
		String partyCountry = sc.getPartyCountry();
		boolean partyAll = sc.isPartyAll();

		String query = openDocument(getQueryPath(), queryNameSearchFollowers);
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
		String query = openDocument(getQueryPath(), queryName);
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
