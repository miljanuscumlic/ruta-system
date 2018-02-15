package rs.ruta.common.datamapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBElement;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

public class DatabaseTransactionMapper extends XmlMapper<DatabaseTransaction>
{
	final private static String collectionPath = "/system/transaction/operation";
	final private static String objectPackageName = "rs.ruta.common.datamapper";
	//MMM: This map should be some kind of most recently used collection bounded in size
	private Map<String, DatabaseTransaction> loadedTransactions;

	public DatabaseTransactionMapper(ExistConnector connector) throws DetailException
	{
		super(connector);
		loadedTransactions = new ConcurrentHashMap<String, DatabaseTransaction>();
	}

	@Override
	protected String getCollectionPath() { return collectionPath; }
	@Override
	protected String getObjectPackageName() { return objectPackageName; }

/*	@Override
	public DatabaseTransaction find(String id) throws DetailException
	{
		DatabaseTransaction txn = loadedTransactions.get(id);
		if(txn == null)
		{
			txn = super.find(id);
			if(txn != null)
				loadedTransactions.put(id, txn);
		}
		return txn;
	}*/

	@Override
	public ArrayList<DatabaseTransaction> findAll() throws DetailException
	{
		ArrayList<DatabaseTransaction> transactions;
		transactions = (ArrayList<DatabaseTransaction>) super.findAll();
		if (transactions != null)
		{
			if(transactions.size() >  1)
				Collections.sort(transactions, new Comparator<DatabaseTransaction>()
				{
					@Override
					public int compare(DatabaseTransaction o1, DatabaseTransaction o2)
					{
						return (int) (o1.getTimestamp() - o2.getTimestamp());
					}
				});
		}
		return transactions;
	}

	@Override
	public String insert(String username, DatabaseTransaction object) throws DetailException
	{
		return insert(username, object, null); //not using transaction when writing a transaction journal
	}

	/**Generates unique ID for objects in the scope of the passed collection. Generated ID cannot be the same
	 * as one of previously used. This method is overriden because there is no need to check if id was previously used
	 * (i.e. there was a document with the same name in the /deleted collection) because transactions are not to be backed up
	 * in the /deleted collection after they are closed.
	 * @param collection collection in which scope unique ID is created
	 * @return unique ID
	 * @throws XMLDBException trown if id cannot be created due to database connectivity issues
	 */
	@Override
	@Deprecated
	public synchronized String createCollectionID(Collection collection) throws XMLDBException
	{
		String id = trimID(collection.createId());
		return id;
	}

	@Override
	protected String doPrepareAndGetID(DatabaseTransaction tx, String username, DSTransaction transaction)
			throws DetailException
	{
		String id = getID(tx);
		if(id == null) // this is creation, not an update
		{
			id = createID();
			tx.setID(id);
		}
		return id;
	}

	@Override
	protected JAXBElement<DatabaseTransaction> getJAXBElement(DatabaseTransaction object)
	{
		return new ObjectFactory().createDatabaseTransaction(object);

	}

	@Override
	protected Class<?> getObjectClass()
	{
		return DatabaseTransaction.class;
	}

	@Override
	protected DatabaseTransaction getCachedObject(String id)
	{
		return loadedTransactions.get(id);
	}

	@Override
	protected DatabaseTransaction removeCachedObject(String id)
	{
		return loadedTransactions.remove(id);
	}

	@Override
	protected void putCacheObject(String id, DatabaseTransaction object)
	{
		loadedTransactions.put(id, object);
	}

	@Override
	public String getID(DatabaseTransaction object) throws DetailException
	{
		return object.getID();
	}
}
