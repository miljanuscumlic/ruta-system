package rs.ruta.common.datamapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBElement;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

public class ExistTransactionMapper extends XmlMapper<ExistTransaction>
{
	final private static String collectionPath = "/system/transaction/operation";
	final private static String objectPackageName = "rs.ruta.common.datamapper";
	//MMM: This map should be some kind of most recently used collection bounded in size
	private Map<String, ExistTransaction> loadedTransactions;

	public ExistTransactionMapper(ExistConnector connector) throws DetailException
	{
		super(connector);
		loadedTransactions = new ConcurrentHashMap<String, ExistTransaction>();
	}

	@Override
	protected String getCollectionPath() { return collectionPath; }
	@Override
	protected String getObjectPackageName() { return objectPackageName; }

/*	@Override
	public ExistTransaction find(String id) throws DetailException
	{
		ExistTransaction txn = loadedTransactions.get(id);
		if(txn == null)
		{
			txn = super.find(id);
			if(txn != null)
				loadedTransactions.put(id, txn);
		}
		return txn;
	}*/

	@Override
	public ArrayList<ExistTransaction> findAll() throws DetailException
	{
		ArrayList<ExistTransaction> transactions;
		transactions = (ArrayList<ExistTransaction>) super.findAll();
		if (transactions != null)
		{
			if(transactions.size() >  1)
				Collections.sort(transactions, new Comparator<ExistTransaction>()
				{
					@Override
					public int compare(ExistTransaction o1, ExistTransaction o2)
					{
						return (int) (o1.getTimestamp() - o2.getTimestamp());
					}
				});
		}
		return transactions;
	}

	@Override
	public String insert(String username, ExistTransaction object) throws DetailException
	{
		return insert(username, object, null); //not using transaction when writing a transaction journal
	}

	@Deprecated
	@Override
	public void insert(ExistTransaction txn, String id, DSTransaction transaction) throws DetailException
	{
		super.insert(txn, id, transaction);
		loadedTransactions.put(id, txn);
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
	protected String doPrepareAndGetID(ExistTransaction tx, String username, DSTransaction transaction)
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
	protected JAXBElement<ExistTransaction> getJAXBElement(ExistTransaction object)
	{
		return new ObjectFactory().createExistTransaction(object);

	}

	@Override
	protected Class<?> getObjectClass()
	{
		return ExistTransaction.class;
	}

	@Override
	protected ExistTransaction getCachedObject(String id)
	{
		return loadedTransactions.get(id);
	}

	@Override
	protected ExistTransaction removeCachedObject(String id)
	{
		return loadedTransactions.remove(id);
	}

	@Override
	protected void putCacheObject(String id, ExistTransaction object)
	{
		loadedTransactions.put(id, object);
	}

	@Override
	public String getID(ExistTransaction object) throws DetailException
	{
		return object.getID();
	}
}
