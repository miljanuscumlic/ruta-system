package rs.ruta.common.datamapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBElement;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

public class DocumentTransactionMapper extends XmlMapper<DocumentTransaction>
{
	final private static String collectionPath = "/system/transaction/document";
	final private static String objectPackageName = "rs.ruta.common.datamapper";
	//MMM: This map should be some kind of most recently used collection bounded in size
	private Map<String, DocumentTransaction> loadedTransactions;

	public DocumentTransactionMapper(ExistConnector connector) throws DetailException
	{
		super(connector);
		loadedTransactions = new ConcurrentHashMap<String, DocumentTransaction>();
	}

	@Override
	protected String getCollectionPath() { return collectionPath; }
	@Override
	protected String getObjectPackageName() { return objectPackageName; }

/*	@Override
	public DocumentTransaction find(String id) throws DetailException
	{
		DocumentTransaction txn = loadedTransactions.get(id);
		if(txn == null)
		{
			txn = super.find(id);
			if(txn != null)
				loadedTransactions.put(id, txn);
		}
		return txn;
	}*/

	@Override
	public ArrayList<DocumentTransaction> findAll() throws DetailException
	{
		ArrayList<DocumentTransaction> transactions;
		transactions = (ArrayList<DocumentTransaction>) super.findAll();
		if (transactions != null)
		{
			if(transactions.size() >  1)
				Collections.sort(transactions, new Comparator<DocumentTransaction>()
				{
					@Override
					public int compare(DocumentTransaction o1, DocumentTransaction o2)
					{
						return (int) (o1.getTimestamp() - o2.getTimestamp());
					}
				});
		}
		return transactions;
	}

	@Override
	public String insert(String username, DocumentTransaction object) throws DetailException
	{
		return insert(username, object, null); //not using transaction when writing a transaction journal
	}

	@Override
	protected String doPrepareAndGetID(DocumentTransaction tx, String username, DSTransaction transaction)
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
	protected JAXBElement<DocumentTransaction> getJAXBElement(DocumentTransaction object)
	{
		return new ObjectFactory().createDocumentTransaction(object);
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return DocumentTransaction.class;
	}

	@Override
	protected DocumentTransaction getCachedObject(String id)
	{
		return loadedTransactions.get(id);
	}

	@Override
	protected DocumentTransaction removeCachedObject(String id)
	{
		return loadedTransactions.remove(id);
	}

	@Override
	protected void putCacheObject(String id, DocumentTransaction object)
	{
		loadedTransactions.put(id, object);
	}

	@Override
	public String getID(DocumentTransaction object) throws DetailException
	{
		return object.getID();
	}
}

