package rs.ruta.common.datamapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.xml.bind.JAXBElement;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.common.Associates;
import rs.ruta.common.PartnershipRequest;
import rs.ruta.common.PartnershipResponse;
import rs.ruta.common.DeregistrationNotice;
import rs.ruta.common.DocBox;
import rs.ruta.common.DocumentDistribution;
import rs.ruta.common.DocumentReceipt;
import rs.ruta.common.ObjectFactory;
import rs.ruta.common.PartnershipBreakup;

public class DocumentDistributionXmlMapper extends XmlMapper<DocumentDistribution>
{
	final private static String collectionPath = "/system/distribution";
//	final private static String docBoxPath = "/doc-box";
	final private static String objectPackageName = "rs.ruta.common.datammaper";
	private volatile static boolean distributionFailure = true;
	final private static int nThreads = 100;
	final private static Semaphore sem = new Semaphore(nThreads);

	public DocumentDistributionXmlMapper(DatastoreConnector connector) throws DetailException
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
		final Associates associates = docDistribution.getAssociates();
		List<String> associateIDs = associates.getAssociateIDs();
		List<String> docCollectionPaths = new ArrayList<>();
		try
		{
			final DocBoxXmlMapper docBoxMapper = ((DocBoxXmlMapper) mapperRegistry.getMapper(DocBox.class));
			final String docBoxPath = docBoxMapper.getCollectionPath();
			for(String aID : associateIDs)
			{
				final String id = getIDByUserID(aID);
				if(id != null)
					docCollectionPaths.add( docBoxPath + "/" + id);
				else
					logger.warn("Party with ID: " + aID + " is not registered in the database.");
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
				else if(documentClazz == OrderType.class)
					((OrderXmlMapper) mapperRegistry.getMapper(OrderType.class)).
					insert(collection, (OrderType) document, docID, null);
				else if(documentClazz == OrderResponseType.class)
					((OrderResponseXmlMapper) mapperRegistry.getMapper(OrderResponseType.class)).
					insert(collection, (OrderResponseType) document, docID, null);
				else if(documentClazz == OrderResponseSimpleType.class)
					((OrderResponseSimpleXmlMapper) mapperRegistry.getMapper(OrderResponseSimpleType.class)).
					insert(collection, (OrderResponseSimpleType) document, docID, null);
				else if(documentClazz == OrderChangeType.class)
					((OrderChangeXmlMapper) mapperRegistry.getMapper(OrderChangeType.class)).
					insert(collection, (OrderChangeType) document, docID, null);
				else if(documentClazz == OrderCancellationType.class)
					((OrderCancellationXmlMapper) mapperRegistry.getMapper(OrderCancellationType.class)).
					insert(collection, (OrderCancellationType) document, docID, null);
				else if(documentClazz == ApplicationResponseType.class)
					((ApplicationResponseXmlMapper) mapperRegistry.getMapper(ApplicationResponseType.class)).
					insert(collection, (ApplicationResponseType) document, docID, null);
				else if(documentClazz == InvoiceType.class)
					((InvoiceXmlMapper) mapperRegistry.getMapper(InvoiceType.class)).
					insert(collection, (InvoiceType) document, docID, null);
				else if(documentClazz == DocumentReceipt.class)
					((DocumentReceiptXmlMapper) mapperRegistry.getMapper(DocumentReceipt.class)).
					insert(collection, (DocumentReceipt) document, docID, null);
				else if(documentClazz == PartnershipRequest.class)
					((PartnershipRequestXmlMapper) mapperRegistry.getMapper(PartnershipRequest.class)).
					insert(collection, (PartnershipRequest) document, docID, null);
				else if(documentClazz == PartnershipResponse.class)
					((PartnershipResponseXmlMapper) mapperRegistry.getMapper(PartnershipResponse.class)).
					insert(collection, (PartnershipResponse) document, docID, null);
				else if(documentClazz == PartnershipBreakup.class)
					((PartnershipBreakupXmlMapper) mapperRegistry.getMapper(PartnershipBreakup.class)).
					insert(collection, (PartnershipBreakup) document, docID, null);
				//MMM other document types

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