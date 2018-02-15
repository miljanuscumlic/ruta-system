package rs.ruta.common.datamapper;

import java.util.*;
import java.util.concurrent.*;

import javax.xml.bind.JAXBElement;

import org.exist.xmldb.XQueryService;
import org.xmldb.api.base.XMLDBException;

import rs.ruta.common.SearchCriterion;
import rs.ruta.common.ObjectFactory;
import rs.ruta.common.Followers;
import rs.ruta.common.FollowersSearchCriterion;

public class FollowersXmlMapper extends XmlMapper<Followers>
{
	final private static String collectionPath = "/followers";
	final private static String objectPackageName = "rs.ruta.common";
	final private static String queryNameSearchFollowers = "search-followers.xq";
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

	@Override
	public String update(String username, Followers followers) throws DetailException
	{
		synchronized(followers)
		{
			return super.update(username, followers);
		}
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

	@Override
	protected void delete(String id, DSTransaction transaction) throws DetailException
	{
		//deletes ID entry in Followers documents of parties that the party with id is following
		//"search-following.xq" retrieves list of followers objects
		final FollowersSearchCriterion criterion = new FollowersSearchCriterion();
		final String userID = getUserIDByID(id);
		criterion.setFollowerID(userID);
		final ArrayList<Followers> followersList = findMany(criterion);
		//delete myself from all objects from the list and update all objects
		for(Followers followers : followersList)
		{
			synchronized(followers) //MMM: may be done in few threads
			{
				followers.remove(userID);
				update(null, followers, transaction);
			}
		}

		super.delete(id, transaction);

		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//TODO inform all followers that party is deregistered (e.g. send to their DocBox some kind of document - Application Response???)
	}

	@Override
	protected String update(String username, Followers followers, DSTransaction transaction) throws DetailException
	{
		synchronized(followers)
		{
			return super.update(null, followers, transaction);
		}
	}

	@Override
	protected String doPrepareAndGetID(Followers followers, String username, DSTransaction transaction)
			throws DetailException
	{
		return getIDByUserID(followers.getPartyID());
	}

	@Override
	protected String prepareQuery(SearchCriterion criterion, XQueryService queryService) throws DatabaseException
	{
		FollowersSearchCriterion sc = (FollowersSearchCriterion) criterion;
		String followerID = sc.getFollowerID();

		String query = openXmlDocument(getQueryPath(), queryNameSearchFollowers);
		if(query == null)
			return query;
		try
		{
			StringBuilder queryPath = new StringBuilder(getRelativeRutaCollectionPath()).append(collectionPath);
			queryService.declareVariable("path", queryPath.toString());
			if(followerID != null)
				queryService.declareVariable("follower-id", followerID);
		}
		catch(XMLDBException e)
		{
			logger.error(e.getMessage(), e);
			throw new DatabaseException("Could not process the query. There has been an error in the process of its exceution.", e);
		}
		return query;
	}
}
