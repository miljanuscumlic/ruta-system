package rs.ruta.common.datamapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBElement;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

public class DistributionTransactionMapper extends XmlMapper<DistributionTransaction>
{
	final private static String collectionPath = "/system/transaction/distribution";
	final private static String objectPackageName = "rs.ruta.common.datamapper";
	//MMM: This map should be some kind of most recently used collection bounded in size
	private Map<String, DistributionTransaction> loadedTransactions;

	public DistributionTransactionMapper(ExistConnector connector) throws DetailException
	{
		super(connector);
		loadedTransactions = new ConcurrentHashMap<String, DistributionTransaction>();
	}

	@Override
	protected String getCollectionPath() { return collectionPath; }
	@Override
	protected String getObjectPackageName() { return objectPackageName; }

/*	@Override
	public DistributionTransaction find(String id) throws DetailException
	{
		DistributionTransaction txn = loadedTransactions.get(id);
		if(txn == null)
		{
			txn = super.find(id);
			if(txn != null)
				loadedTransactions.put(id, txn);
		}
		return txn;
	}*/

	@Override
	public ArrayList<DistributionTransaction> findAll() throws DetailException
	{
		ArrayList<DistributionTransaction> transactions;
		transactions = (ArrayList<DistributionTransaction>) super.findAll();
		if (transactions != null)
		{
			if(transactions.size() >  1)
				Collections.sort(transactions, new Comparator<DistributionTransaction>()
				{
					@Override
					public int compare(DistributionTransaction o1, DistributionTransaction o2)
					{
						return (int) (o1.getTimestamp() - o2.getTimestamp());
					}
				});
		}
		return transactions;
	}

	@Override
	public String insert(String username, DistributionTransaction object) throws DetailException
	{
		return insert(username, object, null); //not using transaction when writing a transaction journal
	}

	@Override
	public String update(String username, DistributionTransaction object) throws DetailException
	{
		return update(username, object, null); //not using transaction when writing a transaction journal
	}

	@Override
	protected String doPrepareAndGetID(DistributionTransaction tx, String username, DSTransaction transaction)
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
	protected JAXBElement<DistributionTransaction> getJAXBElement(DistributionTransaction object)
	{
		return new ObjectFactory().createDistributionTransaction(object);
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return DistributionTransaction.class;
	}

	@Override
	protected DistributionTransaction getCachedObject(String id)
	{
		return loadedTransactions.get(id);
	}

	@Override
	protected DistributionTransaction removeCachedObject(String id)
	{
		return loadedTransactions.remove(id);
	}

	@Override
	protected void putCacheObject(String id, DistributionTransaction object)
	{
		loadedTransactions.put(id, object);
	}

	@Override
	public String getID(DistributionTransaction object) throws DetailException
	{
		return object.getID();
	}
}

