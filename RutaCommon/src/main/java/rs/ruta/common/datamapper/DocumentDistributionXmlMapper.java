package rs.ruta.common.datamapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.xml.bind.JAXBElement;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.common.Followers;
import rs.ruta.common.DeregistrationNotice;
import rs.ruta.common.DocBox;
import rs.ruta.common.DocumentDistribution;
import rs.ruta.common.ObjectFactory;

public class DocumentDistributionXmlMapper extends XmlMapper<DocumentDistribution>
{
	final private static String collectionPath = "/system/distribution";
//	final private static String docBoxPath = "/doc-box";
	final private static String objectPackageName = "rs.ruta.common.datammaper";
	private volatile static boolean distributionFailure = true;
	final private static int nThreads = 100;
	final private static Semaphore sem = new Semaphore(nThreads);

	public DocumentDistributionXmlMapper(ExistConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected String getCollectionPath() { return collectionPath; }

	@Override
	protected String getObjectPackageName() { return objectPackageName; }

	@Override
	protected JAXBElement<DocumentDistribution> getJAXBElement(DocumentDistribution object)
	{
		return new ObjectFactory().createDocumentDistribution(object);
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return DocumentDistribution.class;
	}

	@Override
	protected String doPrepareAndGetID(DocumentDistribution object, String username, DSTransaction transaction)
			throws DetailException
	{
		return createID();
	}

	/**Process all outstanding {@link DistributionTransaction transactions} saved in the journals if there are any.
	 * Also, method acquires one permit on semaphore.
	 * @throws DetailException if could not connect to the database or could not process the transactions
	 */
	private static synchronized void processTransactions() throws DetailException
	{
		if(distributionFailure) //check again to be shure that none other thread managed to process distributionFailure
		{
			sem.acquireUninterruptibly(nThreads);
			try
			{
				DistributionTransaction.distributeAll();
				distributionFailure = false;
				sem.release(nThreads-1);
			}
			catch(DetailException e)
			{
				sem.release(nThreads);
				throw e;
			}
		}
		else //permit should be acquired in any case
		{
			try
			{
				sem.acquire();
			}
			catch (InterruptedException e)
			{
				throw new DatabaseException("Could not open new document transaction.", e.getCause());
			}
		}
	}

	@Override
	protected DSTransaction openTransaction() throws DetailException
	{
		if(distributionFailure)
			processTransactions();
		else
		{
			try
			{
				sem.acquire();
			}
			catch (InterruptedException e)
			{
				throw new DatabaseException("Could not open new document transaction.", e.getCause());
			}
		}

		DSTransaction transaction = null;
		try
		{
			//transaction = transactionFactory.newTransaction();
			transaction = new DistributionTransaction();
			transaction.open();
		}
		catch(TransactionException e)
		{
			throw new DatabaseException("Could not open new document transaction.", e.getCause());
		}
		return transaction;
	}

	@Override
	protected void closeTransaction(DSTransaction transaction) throws DetailException
	{
		sem.release();
		super.closeTransaction(transaction);
	}

	@Override
	protected String insert(String username, DocumentDistribution docDistribution, DSTransaction transaction) throws DetailException
	{
		final Object document = docDistribution.getDocument();
		final Class<?> documentClazz = document.getClass();
		final Followers followers = docDistribution.getFollowers();
		List<String> followerIDs = followers.getFollowerIDs();
		List<String> docCollectionPaths = new ArrayList<>();
		try
		{
			DocBoxXmlMapper docBoxMapper = ((DocBoxXmlMapper) mapperRegistry.getMapper(DocBox.class));
			String docBoxPath = docBoxMapper.getCollectionPath();
			for(String followerID : followerIDs)
			{
				final String id = getIDByUserID(followerID);
				if(id != null)
					docCollectionPaths.add( docBoxPath + "/" + id);
				else
					logger.warn("Party with ID: " + followerID + " is not registered in the database.");
			}

			String docID = createID();
			Collection collection = null;
			try
			{
				collection = getCollection();
				if(documentClazz == CatalogueType.class)
					((CatalogueXmlMapper) mapperRegistry.getMapper(CatalogueType.class)).
					insert(collection, (CatalogueType) document, docID, null);
				else if(documentClazz == PartyType.class)
					((PartyXmlMapper) mapperRegistry.getMapper(PartyType.class)).
					insert(collection, (PartyType) document, docID, null);
				else if(documentClazz == CatalogueDeletionType.class)
					((CatalogueDeletionXmlMapper) mapperRegistry.getMapper(CatalogueDeletionType.class)).
					insert(collection, (CatalogueDeletionType) document, docID, null);
				else if(documentClazz == DeregistrationNotice.class)
					((DeregistrationNoticeXmlMapper) mapperRegistry.getMapper(DeregistrationNotice.class)).
					insert(collection, (DeregistrationNotice) document, docID, null);
				//TODO other document types

			}
			catch(XMLDBException e)
			{
				throw new DatabaseException("The collection " + getCollectionPath() + " could not be retrieved.", e);
			}
			finally
			{
				closeCollection(collection);
			}

			((DistributionTransaction) transaction).addDistributionOperations(docCollectionPaths, collectionPath, docID + ".xml");
			DocBox docBox = new DocBox(document, docCollectionPaths, docID);
			docBoxMapper.insert(username, docBox, transaction);
			delete(docID, (DSTransaction) null);
		}
		catch(Exception e)
		{
			distributionFailure = true;
			transaction.setFailed(true);
			throw e;
		}



/*		//Implementation without DocBoxMapper
		final Object document = docDistribution.getDocument();
		final Class<?> documentClazz = document.getClass();
		final Followers followers = docDistribution.getFollowers();
		List<String> followerIDs = followers.getFollowerIDs();
		List<String> docCollectionPaths = new ArrayList<>();
		try
		{
			for(String followerID : followerIDs)
				docCollectionPaths.add(docBoxPath + "/" + getIDByUserID(followerID));

			String docID = createID();
			Collection collection = null;
			try
			{
				collection = getCollection();
				if(documentClazz == CatalogueType.class)
					((CatalogueXmlMapper) mapperRegistry.getMapper(CatalogueType.class)).
					insert(collection, (CatalogueType) document, docID, null);
				else if(documentClazz == PartyType.class)
					((PartyXmlMapper) mapperRegistry.getMapper(PartyType.class)).
					insert(collection, (PartyType) document, docID, null);
				//TODO other document types

			}
			catch(XMLDBException e)
			{
				throw new DatabaseException("The collection " + getCollectionPath() + " could not be retrieved.", e);
			}
			finally
			{
				closeCollection(collection);
			}

			((DistributionTransaction) transaction).setOperations(docCollectionPaths, collectionPath, docID + ".xml");

			for(int i = 0; i < docCollectionPaths.size(); i++)
			{
				String docBoxCollectionPath = null;
				try
				{
					docBoxCollectionPath = docCollectionPaths.get(i);
					collection = getOrCreateCollection(docBoxCollectionPath);
					if(collection == null)
						throw new DatabaseException("Collection does not exist.");
					if(documentClazz == CatalogueType.class)
						((CatalogueXmlMapper) mapperRegistry.getMapper(CatalogueType.class)).
						insert(collection, (CatalogueType) document, docID, null);
					else if(documentClazz == PartyType.class)
						((PartyXmlMapper) mapperRegistry.getMapper(PartyType.class)).
						insert(collection, (PartyType) document, docID, null);
					//TODO other document types

					((DistributionTransaction) transaction).removeOperation();
				}
				catch(XMLDBException e)
				{
					throw new DatabaseException("The collection " + docBoxCollectionPath + " could not be retrieved.", e);
				}
				finally
				{
					closeCollection(collection);
				}
			}

			if(documentClazz == CatalogueType.class)
				((CatalogueXmlMapper) mapperRegistry.getMapper(CatalogueType.class)).deleteDocument(collectionPath, docID + ".xml");
			else if(documentClazz == PartyType.class)
				((PartyXmlMapper) mapperRegistry.getMapper(PartyType.class)).deleteDocument(collectionPath, docID + ".xml");
			//TODO other document types
		}
		catch(Exception e)
		{
			distributionFailure = true;
			transaction.setFailed(true);
			throw e;
		}*/

		return "OK"; //dummy return value
	}

	@Override
	protected void delete(String id, DSTransaction transaction) throws DetailException
	{
		//deletes document no regard with of which type it is; deleteDocument method is part of the abstract XmlMapper class
		((CatalogueXmlMapper) mapperRegistry.getMapper(CatalogueType.class)).deleteXmlDocument(collectionPath, id + ".xml");
	}

	@Override
	@Deprecated
	public List<String> findAllDocumentIDs(String partyID) throws DetailException
	{
		ArrayList<String> results = new ArrayList<>();
		String docBoxCollectionPath = ((DocBoxXmlMapper) mapperRegistry.getMapper(DocBox.class)).getCollectionPath() +
				"/" + getIDByUserID(partyID);
		Collection collection = null;
		try
		{
			collection = getCollection(docBoxCollectionPath);
			if(collection != null)
			{
				int count = collection.getResourceCount();
				String[] resourceIDs = collection.listResources();
				for(String id : resourceIDs)
					results.add(id);
			}
		}
		catch (XMLDBException e)
		{
			throw new DatabaseException("Collection or document could not be retrieved from the database.");
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
				logger.error("Exception is ", e);;
			}
		}
		return results.size() != 0 ? results : null;
	}

}