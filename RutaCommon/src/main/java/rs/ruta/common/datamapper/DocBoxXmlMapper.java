package rs.ruta.common.datamapper;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.exist.xmldb.XQueryService;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

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
import rs.ruta.common.PartnershipRequest;
import rs.ruta.common.PartnershipResolution;
import rs.ruta.common.PartnershipResponse;
import rs.ruta.common.DeregistrationNotice;
import rs.ruta.common.DocBox;
import rs.ruta.common.DocBoxAllIDsSearchCriterion;
import rs.ruta.common.DocBoxDocumentSearchCriterion;
import rs.ruta.common.DocumentDistribution;
import rs.ruta.common.DocumentException;
import rs.ruta.common.DocumentReceipt;
import rs.ruta.common.PartnershipBreakup;
import rs.ruta.common.SearchCriterion;

public class DocBoxXmlMapper extends XmlMapper<DocBox>
{
	final private static String collectionPath = "/doc-box";
	final private static String objectPackageName = "rs.ruta.common.datammaper";
	final private static String queryNameSearchDocBoxIDs = "search-docbox-id.xq";
	final private static String queryNameFindDocument = "search-docbox-document.xq";

	public DocBoxXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<DocBox> getObjectClass()
	{
		return DocBox.class;
	}

	@Override
	protected String getObjectPackageName()
	{
		return objectPackageName;
	}

	@Override
	public String getCollectionPath()
	{
		return collectionPath;
	}

	@Override
	protected JAXBElement<DocBox> getJAXBElement(DocBox object)
	{
		return null;
	}

	@Override
	public String insert(String username, DocBox docBox, DSTransaction transaction) throws DetailException
	{
		Collection collection = null;
		Object document = docBox.getDocument();
		Class<?> documentClazz = document.getClass();
		List<String> docCollectionPaths = docBox.getDocCollectionPaths();
		String docID = docBox.getDocID();

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
				else if(documentClazz == PartnershipResolution.class)
					((PartnershipResolutionXmlMapper) mapperRegistry.getMapper(PartnershipResolution.class)).
					insert(collection, (PartnershipResolution) document, docID, null);
				else if(documentClazz == PartnershipBreakup.class)
					((PartnershipBreakupXmlMapper) mapperRegistry.getMapper(PartnershipBreakup.class)).
					insert(collection, (PartnershipBreakup) document, docID, null);
				//MMM other document types

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
		return "OK"; // dummy return value
	}

	@Override
	protected DSTransaction openTransaction() throws DetailException
	{
		return ((XmlMapper<DocumentDistribution>) mapperRegistry.getMapper(DocumentDistribution.class)).openTransaction();
	}

	@Override
	protected void closeTransaction(DSTransaction transaction) throws DetailException
	{
		((XmlMapper<DocumentDistribution>) mapperRegistry.getMapper(DocumentDistribution.class)).closeTransaction(transaction);
	}

	//MMM check this method
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

	@Override
	public void deleteDocBoxDocument(String username, String id) throws DetailException
	{
		DSTransaction transaction = openTransaction();
		synchronized(transaction)
		{
			try
			{
				final StringBuilder pathBuilder = new StringBuilder(collectionPath).append("/").append(getID(username));
				final String path = pathBuilder.toString();
				((DistributionTransaction) transaction).addOperation(path, id, "DELETE", null, null, null);
				deleteXmlDocument(path, id + ".xml");
			}
			finally
			{
				closeTransaction(transaction);
			}
		}
	}

	@Override
	protected String prepareQuery(SearchCriterion criterion, XQueryService queryService) throws DetailException
	{
		Class<? extends SearchCriterion> criterionClazz = criterion.getClass();
		String query = null;
		if(criterionClazz == DocBoxAllIDsSearchCriterion.class)
		{
			query = openXmlDocument(getQueryPath(), queryNameSearchDocBoxIDs);
			DocBoxAllIDsSearchCriterion sc = (DocBoxAllIDsSearchCriterion) criterion;
			final String partyID = sc.getPartyID();
			if(query == null)
				return query;
			try
			{
				if(partyID != null)
				{
					StringBuilder queryPath = null;
					final String id = getIDByUserID(partyID);
					if(id != null)
						queryPath = new StringBuilder(getRelativeRutaCollectionPath()).append(collectionPath).append("/").append(id);
					else
						throw(new UserException("Party with ID: " + partyID + " is not registered in the database."));
					queryService.declareVariable("path", queryPath.toString());
				}
				else
					throw new DocumentException("Party ID must not be null when querying DocBox documents.");
			}
			catch(XMLDBException e)
			{
				logger.error(e.getMessage(), e);
				throw new DatabaseException("Could not process the query. There has been an error in the process of its execution.", e);
			}
		}
		else if(criterionClazz == DocBoxDocumentSearchCriterion.class)
		{
			query = openXmlDocument(getQueryPath(), queryNameFindDocument);
			DocBoxDocumentSearchCriterion sc = (DocBoxDocumentSearchCriterion) criterion;
			final String partyID = sc.getPartyID();
			final String documentID = sc.getDocumentID();
			if(query == null)
				return query;
			try
			{
				if(partyID != null && documentID != null)
				{
					StringBuilder queryPath = new StringBuilder(getRelativeRutaCollectionPath()).append(collectionPath).
							append("/").append(getIDByUserID(partyID)).append("/").append(documentID).append(".xml");
					queryService.declareVariable("path", queryPath.toString());
				}
				else
					throw new DocumentException("Party's ID and document's ID both must not be null when querying DocBox documents.");
			}
			catch(XMLDBException e)
			{
				logger.error(e.getMessage(), e);
				throw new DatabaseException("Could not process the query. There has been an error in the process of its execution.", e);
			}
		}
//		MMM: should return null not an exception; check whether exception could be omitted - yes
//		else
//			throw new DocumentException("Not valid search criterion querying DocBox documents.");
		return query;
	}

	@Override
	protected DocBox load(XMLResource resource) throws XMLDBException, DetailException
	{
		DocBox document = new DocBox();
		String id = resource.getDocumentId(); //id of the resource's parent document

		//MMM: this boolean testing is implementation specific, and it might be changed. So this is not
		//MMM: so good way of testing whether the resource is a complete document or not.
		//MMM: The problem is that resource.getId() should return null if the resource is a result of a query,
		//MMM: and not a whole document, but it returns exactly what returns resource.getDocumentId()
		//MMM: and that is ID without ".xml" at the end
		if(id.contains(".xml")) //resource is a whole xml document
		{
			id = id.replace(".xml", "");
			Object object = null;
			final String result = (String) resource.getContent();
			Class<?> objectClazz = getObjectClass(result);
			if(objectClazz == CatalogueType.class)
				object = ((CatalogueXmlMapper) mapperRegistry.getMapper(CatalogueType.class)).unmarshalFromXML(result);
			else if(objectClazz == PartyType.class)
				object = ((PartyXmlMapper) mapperRegistry.getMapper(PartyType.class)).unmarshalFromXML(result);
			else if(objectClazz == CatalogueDeletionType.class)
				object = ((CatalogueDeletionXmlMapper) mapperRegistry.getMapper(CatalogueDeletionType.class)).unmarshalFromXML(result);
			else if(objectClazz == DeregistrationNotice.class)
				object = ((DeregistrationNoticeXmlMapper) mapperRegistry.getMapper(DeregistrationNotice.class)).unmarshalFromXML(result);
			else if(objectClazz == OrderType.class)
				object = ((OrderXmlMapper) mapperRegistry.getMapper(OrderType.class)).unmarshalFromXML(result);
			else if(objectClazz == OrderResponseType.class)
				object = ((OrderResponseXmlMapper) mapperRegistry.getMapper(OrderResponseType.class)).unmarshalFromXML(result);
			else if(objectClazz == OrderResponseSimpleType.class)
				object = ((OrderResponseSimpleXmlMapper) mapperRegistry.getMapper(OrderResponseSimpleType.class)).unmarshalFromXML(result);
			else if(objectClazz == OrderChangeType.class)
				object = ((OrderChangeXmlMapper) mapperRegistry.getMapper(OrderChangeType.class)).unmarshalFromXML(result);
			else if(objectClazz == OrderCancellationType.class)
				object = ((OrderCancellationXmlMapper) mapperRegistry.getMapper(OrderCancellationType.class)).unmarshalFromXML(result);
			else if(objectClazz == ApplicationResponseType.class)
				object = ((ApplicationResponseXmlMapper) mapperRegistry.getMapper(ApplicationResponseType.class)).unmarshalFromXML(result);
			else if(objectClazz == InvoiceType.class)
				object = ((InvoiceXmlMapper) mapperRegistry.getMapper(InvoiceType.class)).unmarshalFromXML(result);
			else if(objectClazz == DocumentReceipt.class)
				object = ((DocumentReceiptXmlMapper) mapperRegistry.getMapper(DocumentReceipt.class)).unmarshalFromXML(result);
			else if(objectClazz == PartnershipRequest.class)
				object = ((PartnershipRequestXmlMapper) mapperRegistry.getMapper(PartnershipRequest.class)).unmarshalFromXML(result);
			else if(objectClazz == PartnershipResponse.class)
				object = ((PartnershipResponseXmlMapper) mapperRegistry.getMapper(PartnershipResponse.class)).unmarshalFromXML(result);
			else if(objectClazz == PartnershipResolution.class)
				object = ((PartnershipResolutionXmlMapper) mapperRegistry.getMapper(PartnershipResolution.class)).unmarshalFromXML(result);
			else if(objectClazz == PartnershipBreakup.class)
				object = ((PartnershipBreakupXmlMapper) mapperRegistry.getMapper(PartnershipBreakup.class)).unmarshalFromXML(result);
			//MMM other document types

			document.setDocument(object);
			return document;
		}
		else // resource is a result of a query and not the whole document
			return null;
	}

	/**
	 * Gets the {@link Class} object of the object that should be written in or read from the DocBox.
	 * @param result xml {@code String} representation of the object
	 * @return {@code Class} object or {@code null} if input argument is {@code null} or zero length {@code String}
	 */
	private Class<?> getObjectClass(String result)
	{
		String start = null;
		if(result != null && result.length() != 0)
			start = result.substring(0, 50);
		else
			return null;
		if(start.matches("<((.)+:)?Catalogue (.)+"))
			return CatalogueType.class;
		else if(start.matches("<((.)+:)?Party (.)+"))
			return PartyType.class;
		else if(start.matches("<((.)+:)?CatalogueDeletion (.)+"))
			return CatalogueDeletionType.class;
		else if(start.matches("<((.)+:)?CatalogueDeletion (.)+"))
			return CatalogueDeletionType.class;
		else if(start.matches("<((.)+:)?DeregistrationNotice (.)+"))
			return DeregistrationNotice.class;
		else if(start.matches("<((.)+:)?OrderResponseSimple (.)+"))
			return OrderResponseSimpleType.class;
		else if(start.matches("<((.)+:)?OrderResponse (.)+"))
			return OrderResponseType.class;
		else if(start.matches("<((.)+:)?OrderChange (.)+"))
			return OrderChangeType.class;
		else if(start.matches("<((.)+:)?OrderCancellation (.)+"))
			return OrderCancellationType.class;
		else if(start.matches("<((.)+:)?Order (.)+"))
			return OrderType.class;
		else if(start.matches("<((.)+:)?ApplicationResponse (.)+"))
			return ApplicationResponseType.class;
		else if(start.matches("<((.)+:)?Invoice (.)+"))
			return InvoiceType.class;
		else if(start.matches("<((.)+:)?DocumentReceipt (.)+"))
			return DocumentReceipt.class;
		else if(start.matches("<((.)+:)?PartnershipRequest (.)+"))
			return PartnershipRequest.class;
		else if(start.matches("<((.)+:)?PartnershipResponse (.)+"))
			return PartnershipResponse.class;
		else if(start.matches("<((.)+:)?PartnershipResolution (.)+"))
			return PartnershipResolution.class;
		else if(start.matches("<((.)+:)?PartnershipBreakup (.)+"))
			return PartnershipBreakup.class;
		//MMM other document types
		else
			return null;
	}

}