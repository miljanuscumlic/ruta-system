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

/**Temporary {@link XmlMapper} class until I develop complete solution for the local database collection organisation.
 */
public class MyPartyExistMapper extends XmlMapper<MyParty>
{
	private Client client;
	final private static String collectionPath = "/my-party";
	final private static String objectPackageName = "rs.ruta.client";
	final private static String queryNameSearchParty = "search-my-party.xq"; //MMM: not defined
	private Map<String, MyParty> loadedParties;

	public MyPartyExistMapper(Client client, ExistConnector connector) throws DetailException
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
	public MyParty find(String id) throws DetailException
	{
		MyParty party = loadedParties.get(id);
		if(party == null)
		{
			party =  super.find(id);
			if(party != null)
				loadedParties.put((String) id,  party);
		}
		return party;
	}*/

	@Override
	protected void clearCachedObjects()
	{
		loadedParties.clear();
	}

	@Override
	protected MyParty getCachedObject(String id)
	{
		return loadedParties.get(id);
	}

	@Override
	protected void putCacheObject(String id, MyParty object)
	{
		loadedParties.put(id, object);
	}

	@Override
	protected JAXBElement<MyParty> getJAXBElement(MyParty object)
	{
		QName _Party_QNAME = new QName("urn:rs:ruta:client", "MyParty");
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

	//MMM: this could be more elegantly solved if the MyParty object has an id field - then just retrieve it
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
