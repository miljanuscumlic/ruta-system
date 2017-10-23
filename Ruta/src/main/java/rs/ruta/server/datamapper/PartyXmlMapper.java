package rs.ruta.server.datamapper;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

import javax.xml.bind.JAXBElement;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ObjectFactory;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import rs.ruta.common.SearchCriterion;
import rs.ruta.server.DatabaseException;
import rs.ruta.server.DetailException;

public class PartyXmlMapper extends XmlMapper
{
	final private static String docPrefix = ""; // "party";
	final private static String collectionPath = "/ruta/parties";
	final private static String deletedCollectionPath = "/ruta/deleted/parties";
	final private static String objectPackageName = "oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21";
	//MMM: This map should be some kind of most recently used collection bounded in size
	private Map<Object, PartyType> loadedParties;

	public PartyXmlMapper() throws DatabaseException
	{
		super();
		loadedParties = new ConcurrentHashMap<Object, PartyType>();
	}

	@Override
	public String getCollectionPath() { return collectionPath; }
	@Override
	public String getDocumentPrefix() { return docPrefix; }
	@Override
	public String getDeletedBaseCollectionPath() { return deletedCollectionPath; }
	@Override
	public String getObjectPackageName() { return objectPackageName; }

	@Override
	public PartyType find(String id) throws DetailException
	{
		PartyType party = loadedParties.get(id);
		if(party == null)
		{
			party = (PartyType) super.find(id);
			if(party != null)
				loadedParties.put(id, (PartyType) party);
		}
		return party;
	}

	@Override
	public <T extends DSTransaction> void insert(Object party, Object id, T transaction) throws DetailException
	{
		setPartyID((PartyType) party, (String) id);
		super.insert(party, id, transaction);
		loadedParties.put(id, (PartyType) party);
	}

	@Override
	public <T extends DSTransaction> String insert(Object party, T transaction) throws DetailException
	{
		Collection collection = null;
		String id = null;
		try
		{
			collection = getCollection();
			id = createID(collection);
			insert(party, id, transaction);
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
				logger.error("Exception is: ", e);;
			}
		}
		return id;
	}

	@Override
	public <T extends DSTransaction> void delete(Object id, T transaction) throws DetailException
	{
		super.delete(id, transaction);
		loadedParties.remove(id);
	}

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

	@Override
	protected JAXBElement<PartyType> getJAXBElement(Object object)
	{
		JAXBElement<PartyType> partyElement = (new ObjectFactory()).createParty((PartyType) object);
		return partyElement;
	}

	@Override
	public Class<?> getObjectClass()
	{
		return PartyType.class;
	}

	//MMM: This is temporary here. It should be part of the client-common.jar
	public static <T, U> U getPropertyOrNull(T property, Function<? super T, ? extends U> extractor)
	{
		return property != null ? extractor.apply(property) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<?> findAll() throws DetailException
	{
		ArrayList<PartyType> parties;
		parties = (ArrayList<PartyType>) super.findAll();
		if (parties != null)
			for(PartyType t : parties)
				loadedParties.put(getID(t), t);
		return parties;
	}

	@Override
	public <T extends DSTransaction> void update(Object party, Object id, T transaction) throws DetailException
	{
		super.update(party, id, transaction);
		loadedParties.put(id, (PartyType) party);
	}

	@Override
	public String getSearchQueryName()
	{
		return queryNameSearchParty;
	}

	/**Sends search request to the super class and converts search result represented as
	 * list of <code>Object</code>s to list of <code>PartyType</code>s
	 * @param criterion search criterion
	 * @return search result as list of <code>PartyType</code>s
	 * @throws DetailException if search query could not be processed
	 */
	public List<PartyType> searchParty(SearchCriterion criterion) throws DetailException
	{
		List<PartyType> partyResult = null;
		List<Object> objectResult = super.search(criterion);
		if (objectResult != null)
		{
			partyResult = new ArrayList<>();
			for(Object object : objectResult)
				partyResult.add((PartyType) object);
		}
		return partyResult;
	}
}
