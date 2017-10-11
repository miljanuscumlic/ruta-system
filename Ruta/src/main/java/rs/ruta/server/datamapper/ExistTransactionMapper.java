package rs.ruta.server.datamapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.exist.xmldb.EXistResource;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;

import rs.ruta.server.DataManipulationException;
import rs.ruta.server.DatabaseException;
import rs.ruta.server.DetailException;

public class ExistTransactionMapper extends XmlMapper
{
	final private static String docPrefix = ""; // "txn";
	final private static String collectionPath = "/ruta/system/transactions";
	final private static String deletedCollectionPath = "/ruta/deleted/system/transactions";
	//MMM: This map should be some kind of most recently used collection bounded in size
	private Map<Object, ExistTransaction> loadedTransactions;

	public ExistTransactionMapper() throws DatabaseException
	{
		super();
		loadedTransactions = new ConcurrentHashMap<Object, ExistTransaction>();
	}

	/**Roll backs all transactions documented in transaction journal documents.
	 */
	public void rollbackAllTransactions()
	{
		ExistTransaction.rollbackAll();
	}

	@Override
	public String getCollectionPath() { return collectionPath; }
	@Override
	public String getDocumentPrefix() { return docPrefix; }
	@Override
	public String getDeletedBaseCollectionPath() { return deletedCollectionPath; }

	@Override
	public ExistTransaction find(String id) throws DetailException
	{
		ExistTransaction txn = loadedTransactions.get(id);
		if(txn == null)
		{
			txn = (ExistTransaction) super.find(id);
			if(txn != null)
				loadedTransactions.put(id, (ExistTransaction) txn);
		}
		return txn;
	}

	/* (non-Javadoc)
	 * @see rs.ruta.server.datamapper.XmlMapper#findAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<?> findAll() throws DetailException
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
			for(ExistTransaction t : transactions)
				loadedTransactions.put(getID(t), t);
		}
		return transactions;
	}

/*	@Override
	public <T extends DSTransaction> String insert(Object object, T transaction) throws DetailException
	{
		Collection collection = null;
		String id = null;
		try
		{
			collection = getCollection();
			id = createID(collection);
			insert(collection, object, (String)id);
		}
		catch(XMLDBException e)
		{
			throw new DatabaseException("The collection could not be retrieved or unique ID created.", e);
		}
		finally
		{
			try
			{
				if(collection != null)
					collection.close();
			} catch (XMLDBException e)
			{
				logger.error("Exception is: ", e);
			}
		}
		return id;
	}*/

	@Override
	public <T extends DSTransaction> void insert(Object txn, Object id, T transaction) throws DetailException
	{
		super.insert(txn, id, transaction);
		loadedTransactions.put(id, (ExistTransaction) txn);
	}

	/**Inserts object in the collection.
	 * @param collection collection in which the object is to be stored
	 * @param object object to be stored
	 * @return id of the object, unique in the scope of the collection
	 * @throws DetailException
	 */
	private void insert(Collection collection, Object object, String id) throws DetailException
	{
		Resource resource = null;
		String resourceType = "XMLResource";
		String colPath = getCollectionPath();
		String xmlResult = null; //Storing object in the String
		String documentName = "";
		try
		{
			documentName = getDocumentPrefix() + id + getDocumentSufix();
			xmlResult = marshallToXML(object);
			logger.info("Start of storing of the document " + documentName + " to the location " + colPath);
			resource = collection.createResource(documentName, resourceType);
			resource.setContent(xmlResult);
			collection.storeResource(resource);
			logger.info("Finished storing of the document " + documentName + " to the location " + colPath);
		}
		catch(XMLDBException e)
		{
			logger.error("Could not save the document " + documentName + " to the location " + colPath);
			logger.error("Exception is: ", e);;
			throw new DatabaseException("The document could not be saved to the database.", e);
		}
		catch(DataManipulationException e)
		{
			logger.error("Could not save the document " + documentName + " to the location " + colPath);
			throw e;
		}
		finally
		{
			try
			{
				if (resource != null)
					((EXistResource)resource).freeResources();
			}
			catch(XMLDBException e)
			{
				logger.error("Exception is: ", e);;
			}
		}
	}

	/**Generates unique ID for objects in the scope of the passed collection. Generated ID cannot
	 * be the same as one of previously used.
	 * @param collection collection in which scope unique ID is created
	 * @return unique ID
	 * @throws XMLDBException trown if id cannot be created due to database connectivity issues
	 */
	@Override
	public String createID(Collection collection) throws XMLDBException
	{
		String id = trimID(collection.createId());
		return id;
	}

	@Override
	public <T extends DSTransaction> void delete(Object id, T transaction) throws DetailException
	{
		super.delete(id, transaction);
		loadedTransactions.remove(id);
	}

	@Override
	protected JAXBElement<ExistTransaction> getJAXBElement(Object object)
	{
		QName _TRANSACTION_QNAME = new QName("urn:rs:ruta:services", "ExistTransaction");
		JAXBElement<ExistTransaction> element =
				new JAXBElement<ExistTransaction>(_TRANSACTION_QNAME, ExistTransaction.class, (ExistTransaction) object);
		return element;
	}

	@Override
	public Class<?> getObjectClass()
	{
		return ExistTransaction.class;
	}

	@Override
	public String getID(Object object) throws DetailException
	{
		return ((ExistTransaction)object).getTransactionID();
	}
}
