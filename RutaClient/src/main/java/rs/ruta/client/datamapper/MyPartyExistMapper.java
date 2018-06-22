package rs.ruta.client.datamapper;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.xmldb.api.base.XMLDBException;

import rs.ruta.client.*;
import rs.ruta.common.datamapper.DSTransaction;
import rs.ruta.common.datamapper.DatabaseException;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.ExistConnector;
import rs.ruta.common.datamapper.XmlMapper;

/**
 * Temporary {@link XmlMapper} class until I develop complete solution for the local database collection organisation.
 */
@Deprecated
public class MyPartyExistMapper extends XmlMapper<MyParty>
{
	private RutaClient client;
	final private static String collectionPath = "/my-party";
	final private static String objectPackageName = "rs.ruta.client";
	final private static String queryNameSearchParty = "search-my-party.xq"; //MMM: not defined
	private Map<String, MyParty> loadedParties;

	public MyPartyExistMapper(RutaClient client, ExistConnector connector) throws DetailException
	{
		super(connector);
		loadedParties = new ConcurrentHashMap<String, MyParty>();
		this.client = client;
	}

	@Override
	protected String getCollectionPath() { return collectionPath; }
	@Override
	protected String getObjectPackageName() { return objectPackageName; }

	@Override
	public ArrayList<MyParty> findAll() throws DetailException
	{
		ArrayList<MyParty> parties = new ArrayList<>();
		String ids[] = listAllDocumentIDs();
		for(String id : ids)
		{
			final MyParty party = find(trimID(id));
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

/*	@Override
	public MyParty find(String uuid) throws DetailException
	{
		MyParty party = loadedParties.get(uuid);
		if(party == null)
		{
			party =  super.find(uuid);
			if(party != null)
				loadedParties.put((String) uuid,  party);
		}
		return party;
	}*/

	@Override
	public void clearCache()
	{
		loadedParties.clear();
	}

	@Override
	protected MyParty getCachedObject(String id)
	{
		return loadedParties.get(id);
	}

	@Override
	protected void putCachedObject(String id, MyParty object)
	{
		loadedParties.put(id, object);
	}

	@Override
	protected JAXBElement<MyParty> getJAXBElement(MyParty object)
	{
		QName _Party_QNAME = new QName("http://www.ruta.rs/ns/client", "MyParty");
		JAXBElement<MyParty> partyElement = new JAXBElement<MyParty>(_Party_QNAME, MyParty.class, client.getMyParty());
		return partyElement;
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return MyParty.class;
	}

	@Override
	public void insertAll() throws DetailException
	{
		super.insert(client.getMyParty());
	}

	//MMM: this could be more elegantly solved if the MyParty object has an uuid field - then just retrieve it
	//MMM: but this class is only temporary, so I shall no bother with elegancy
	@Override
	public String getID(MyParty object) throws DetailException
	{
		String id = null;
		String[] ids = listAllDocumentIDs();
		int cnt = ids.length;
		if(cnt == 0)
			return null;
		if(ids.length == 1)
			return trimID(ids[0]);
		else if(ids.length > 1)
			throw new DatabaseException("There is more than one object in a collection.");
		return id;
	}

	@Override
	protected String doPrepareAndGetID(MyParty object, String username, DSTransaction transaction)
			throws DetailException //MMM: not thrown with UUID implementation
	{
		String id = getID(object);
		if (id == null)
			id = createID();
		return id;
	}

}
