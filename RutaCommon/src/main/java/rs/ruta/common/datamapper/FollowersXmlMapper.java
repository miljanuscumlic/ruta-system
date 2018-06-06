package rs.ruta.common.datamapper;

import java.util.*;
import java.util.concurrent.*;

import javax.xml.bind.JAXBElement;

import org.exist.xmldb.XQueryService;
import org.xmldb.api.base.XMLDBException;

import rs.ruta.common.SearchCriterion;
import rs.ruta.common.ObjectFactory;
import rs.ruta.common.Associates;
import rs.ruta.common.FollowersSearchCriterion;

public class FollowersXmlMapper extends XmlMapper<Associates>
{
	final private static String collectionPath = "/followers";
	final private static String objectPackageName = "rs.ruta.common";
	final private static String queryNameSearchFollowers = "search-followers.xq";
	//MMM: This map should be some kind of most recently used collection bounded in size
	private Map<String, Associates> loadedFollowers;

	public FollowersXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
		loadedFollowers = new ConcurrentHashMap<String, Associates>();
	}

	@Override
	protected String getCollectionPath() { return collectionPath; }
	@Override
	protected String getObjectPackageName() { return objectPackageName; }

	@Override
	public void clearCache()
	{
		loadedFollowers.clear();
	}

	@Override
	protected Associates getCachedObject(String id)
	{
		return loadedFollowers.get(id);
	}

	@Override
	protected Associates removeCachedObject(String id)
	{
		return loadedFollowers.remove(id);
	}

	@Override
	protected void putCachedObject(String id, Associates object)
	{
		loadedFollowers.put(id, object);
	}

	@Override
	protected JAXBElement<Associates> getJAXBElement(Associates object)
	{
		return new ObjectFactory().createAssociates(object);
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return Associates.class;
	}

	@Override
	protected String getSearchQueryName()
	{
		return queryNameSearchFollowers;
	}

	@Override
	public String update(String username, Associates followers) throws DetailException
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
	public ArrayList<Associates> findAll() throws DetailException
	{
		ArrayList<Associates> followings = new ArrayList<>();
		String ids[] = listAllDocumentIDs();
		for(String id : ids)
		{
			final Associates f = find(trimID(id));
			if(f != null)
				followings.add(f);
		}
		return followings.size() > 0 ? followings : null;
	}

	@Override
	protected void delete(String id, DSTransaction transaction) throws DetailException
	{
		//deletes ID entry in Associates documents of parties that the party with passed id is following
		//"search-following.xq" retrieves list of followers objects
		final FollowersSearchCriterion criterion = new FollowersSearchCriterion();
		final String userID = getUserIDByID(id);
		criterion.setFollowerID(userID);
		final ArrayList<Associates> followersList = findMany(criterion);
		//delete myself from all objects from the list and update all objects
		for(Associates followers : followersList)
		{
			synchronized(followers) //MMM: might be done in few threads
			{	//there is no need to delete followerID from its own followers object because if there is a case
				//of this kind of deletion that means that the followers document would be deleted altogether very
				//soon, and there is no need for this step
				if(! userID.equals(followers.getPartyID()))
				{
					followers.removeAssociate(userID);
					update(null, followers, transaction);
				}
			}
		}
		super.delete(id, transaction);
	}

	@Override
	protected String update(String username, Associates followers, DSTransaction transaction) throws DetailException
	{
		synchronized(followers)
		{
			return super.update(null, followers, transaction);
		}
	}

	@Override
	protected String doPrepareAndGetID(Associates followers, String username, DSTransaction transaction)
			throws DetailException
	{
		return getIDByUserID(followers.getPartyID());
	}

	@Override
	protected String prepareQuery(SearchCriterion criterion, XQueryService queryService) throws DatabaseException
	{

		String query = null;
		if(criterion.getClass() == FollowersSearchCriterion.class)
			query = openXmlDocument(getQueryPath(), queryNameSearchFollowers);
		if(query != null)
		{
			FollowersSearchCriterion sc = (FollowersSearchCriterion) criterion;
			String followerID = sc.getFollowerID();
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
				throw new DatabaseException("Could not process the query. There has been an error in the process of its execution.", e);
			}
		}
		return query;
	}
}