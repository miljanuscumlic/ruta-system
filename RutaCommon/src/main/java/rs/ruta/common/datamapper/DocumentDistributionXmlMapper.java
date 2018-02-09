package rs.ruta.common.datamapper;

import java.util.List;
import java.util.concurrent.Semaphore;

import javax.xml.bind.JAXBElement;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import rs.ruta.common.Followers;
import rs.ruta.common.DocumentDistribution;
import rs.ruta.common.ObjectFactory;

public class DocumentDistributionXmlMapper extends XmlMapper<DocumentDistribution>
{
	final private static String collectionPath = "/doc-box";
	final private static String objectPackageName = "rs.ruta.common.datammaper";
	final private static boolean distributionFailure = false;
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

	@Override
	protected synchronized DSTransaction openTransaction() throws DetailException
	{
		if(distributionFailure)
		{
			sem.acquireUninterruptibly(nThreads);
		}
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
			transaction = new DocumentTransaction();
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
		final Followers followers = docDistribution.getFollowers().clone(); //not to mess with the reference to Followers in docDistribution
		List<String> followerIDs = followers.clone().getFollowerIDs(); //not to mess the list of follower IDs within the followers reference
		((DocumentTransaction) transaction).setFollowers(followers);
		mapperRegistry.getMapper(DocumentTransaction.class).insert(null, (DocumentTransaction) transaction);

		for(String followerID : followerIDs)
		{
			Collection collection = null;
			String id = null;
			try
			{
				String docBoxCollectionPath = getCollectionPath() + "/" + getIDByUserID(followerID);
				collection = getOrCreateCollection(docBoxCollectionPath);
				if(collection == null)
					throw new DatabaseException("Collection does not exist.");
				id = doPrepareAndGetID(docDistribution, username, transaction);
				if(id != null)
				{
					if(documentClazz == CatalogueType.class)
						((CatalogueXmlMapper) mapperRegistry.getMapper(CatalogueType.class)).
						insert(collection, (CatalogueType) document, id, null);
					//MMM: TODO PartyType

					followers.remove(followerID);
					mapperRegistry.getMapper(DocumentTransaction.class).update(null, (DocumentTransaction) transaction);
				}
				else
					throw new DatabaseException("Object's ID could not be found.");
			}
			catch(XMLDBException e)
			{
				throw new DatabaseException("The collection could not be retrieved.", e);
			}
			catch(DetailException e)
			{
				throw e;
			}
			finally
			{
				closeCollection(collection);
			}
		}
		return "OK"; //dummy return value
	}

	@Override
	protected void delete(String id, DSTransaction transaction) throws DetailException
	{
		Collection collection = null;
		final String collectionName = id;
		final String collectionPath = getCollectionPath() + "/" + id;
		try
		{
			collection = getCollection(collectionPath);
			if(collection == null)
				throw new DatabaseException("Collection does not exist!");
			logger.info("Started deletion of the collection " + collectionName + " from the location " + getCollectionPath());
			deleteCollection(collection);
			logger.info("Finished deletion of the collection " + collectionName + " from the location " + getCollectionPath());
		}
		catch(XMLDBException e)
		{
			throw new DatabaseException("The DocBox could not be deleted.", e);
		}
		finally
		{
			try
			{
				if(collection != null)
					collection.close();
			}
			catch (XMLDBException e)
			{
				logger.error("Exception is ", e);;
			}
		}
	}

}
