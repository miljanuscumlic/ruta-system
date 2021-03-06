package rs.ruta.server;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.activation.DataHandler;
import javax.annotation.*;
import javax.jws.*;
import javax.servlet.ServletContext;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.MTOM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ResponseType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.common.ReportAttachment;
import rs.ruta.common.ReportComment;
import rs.ruta.common.BugReport;
import rs.ruta.common.BugReportSearchCriterion;
import rs.ruta.common.PartnershipRequest;
import rs.ruta.common.PartnershipResolution;
import rs.ruta.common.PartnershipResponse;
import rs.ruta.common.PartnershipSearchCriterion;
import rs.ruta.common.CatalogueSearchCriterion;
import rs.ruta.common.DeregistrationNotice;
import rs.ruta.common.DocBox;
import rs.ruta.common.DocBoxAllIDsSearchCriterion;
import rs.ruta.common.DocBoxDocumentSearchCriterion;
import rs.ruta.common.DocumentDistribution;
import rs.ruta.common.DocumentReceipt;
import rs.ruta.common.Associates;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.PartnershipBreakup;
import rs.ruta.common.PartySearchCriterion;
import rs.ruta.common.RutaVersion;
import rs.ruta.common.SearchCriterion;
import rs.ruta.common.RutaUser;
import rs.ruta.common.datamapper.*;
import rs.ruta.server.datamapper.ServiceMapperRegistry;
import rs.ruta.server.datamapper.ServiceMapperRegistryFactory;

//handlers.xml should be inside ResourceRoot directory /WEB-INF/classes because WildFly is searching for it on that path
//ResourceRoot [root=\"/C:/Program Files/wildfly-10.1.0.Final/bin/content/Ruta-SNAPSHOT-0.0.1.war/WEB-INF/classes\"]

@HandlerChain(file = "/handlers.xml")
/*Complete settings
 * @WebService(endpointInterface = "rs.ruta.server.Server",
	wsdlLocation = "/wsdl/CDR.wsdl",
	portName = "CDRPort",
	targetNamespace = "http://ruta.rs/services",
	serviceName = "CDRService")*/

/*//local wsdl generation
@WebService(endpointInterface = "rs.ruta.server.Server",
 	targetNamespace = "http://ruta.rs/services",
	wsdlLocation = "/wsdl/CDR.wsdl")*/

//wildfly wsdl generation
@WebService(endpointInterface = "rs.ruta.server.Server", targetNamespace = "http://ruta.rs/ns/services")
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_MTOM_BINDING)
//@XmlSeeAlso({OrderResponseType.class, OrderType.class}) //not necessary when typeInclusion method is used
public class CDR implements Server
{
	@Resource
	private WebServiceContext wsCtx;
	private ServletContext sCtx;
	private final static Logger logger = LoggerFactory.getLogger("rs.ruta.server");
	private ExecutorService docBoxPool;
	private MapperRegistry mapperRegistry;

	public CDR()
	{
		mapperRegistry = MapperRegistry.initialize(new ServiceMapperRegistryFactory());
		if(!MapperRegistry.isDatastoreAccessible())
		{
			logger.warn("Cound not connect to the database! The database is not accessible.");
			logger.warn("If database has not been started please start it. Otherwise CDR service will not be operable"
					+ " and all future SOAP requests will be rejected.");
		}
		docBoxPool = Executors.newCachedThreadPool();
	}

	private void init() throws DetailException
	{
		if(wsCtx == null)
			throw new RuntimeException("Dependancy Injection failed on Web Service Context!");
		if(sCtx == null) // ServletContext not yet set?
		{
			MessageContext mCtx = wsCtx.getMessageContext();
			sCtx = (ServletContext) mCtx.get(MessageContext.SERVLET_CONTEXT);
		}
	}

	/**
	 * Releases resources (e.g. docBoxPool pool of thread) before the removal of this {@code CDR} instance from the container.
	 */
	@PreDestroy
	private void shutdown()
	{
		docBoxPool.shutdown();
	}

	/**
	 * Logs the exception and wrapps it in {@link RutaException} that is packed in SOAP response.
	 * @param e exception to process
	 * @param exceptionMsg exception message
	 * @throws RutaException always thrown newly created {@code RutaException}
	 */
	private void processException(Exception e, String exceptionMsg) throws RutaException
	{
		logger.error("Exception is ", e);
		if (e instanceof DetailException)
			throw new RutaException(exceptionMsg, ((DetailException) e).getFaultInfo());
		else
			throw new RutaException(exceptionMsg, e.getMessage());
	}

	@Override
	@WebMethod
	@Deprecated //MMM used updateCatalogueWithAppResponse instead
	public void insertCatalogue(String username, CatalogueType catalogue) throws RutaException
	{
		try
		{
			init();
			final String id = mapperRegistry.getMapper(CatalogueType.class).insert(username, catalogue);
			docBoxPool.submit(() ->
			{
				try
				{
					final Associates followers = mapperRegistry.getMapper(Associates.class).find(id).clone();
					final DocumentDistribution catDistribution = new DocumentDistribution(catalogue, followers);
					mapperRegistry.getMapper(DocumentDistribution.class).insert(null, catDistribution);
				}
				catch(DetailException e)
				{
					logger.error("Unable to distribute catalogue for the user: " + username + ".\n Exception is ", e);
				}
			});
		}
		catch(Exception e)
		{
			processException(e, "Catalogue could not be deposited into the CDR service!");
		}
	}

	@Override
	@WebMethod
	@Deprecated
	public void updateCatalogue(String username, CatalogueType catalogue) throws RutaException
	{
		try
		{
			init();
			final String id = mapperRegistry.getMapper(CatalogueType.class).update(username, catalogue);
			docBoxPool.submit(() ->
			{
				try
				{
					final Associates followers = mapperRegistry.getMapper(Associates.class).find(id).clone();
					final DocumentDistribution catDistribution = new DocumentDistribution(catalogue, followers);
					mapperRegistry.getMapper(DocumentDistribution.class).insert(null, catDistribution);
				}
				catch(DetailException e)
				{
					logger.error("Unable to distribute catalogue for the user: " + username + ".\n Exception is ", e);
				}
			});
		}
		catch(Exception e)
		{
			processException(e, "Catalogue could not be updated in the CDR!");
		}
	}

	@Override
	@WebMethod
	public ApplicationResponseType updateCatalogueWithAppResponse(String username, CatalogueType catalogue) throws RutaException
	{
		ApplicationResponseType appResponse = null;
		try
		{
			init();
			final String id = mapperRegistry.getMapper(CatalogueType.class).update(username, catalogue);
			final PartyType senderParty = catalogue.getReceiverParty();
			final PartyType receiverParty = catalogue.getProviderParty();
			final String docUUID = catalogue.getUUIDValue();
			final String docID = catalogue.getIDValue();
			appResponse = InstanceFactory.
					createApplicationResponse(senderParty, receiverParty, docUUID, docID, InstanceFactory.APP_RESPONSE_POSITIVE, null);
			docBoxPool.submit(() ->
			{
				try
				{
					final Associates followers = mapperRegistry.getMapper(Associates.class).find(id).clone();
					final DocumentDistribution catDistribution = new DocumentDistribution(catalogue, followers);
					mapperRegistry.getMapper(DocumentDistribution.class).insert(null, catDistribution);
				}
				catch(DetailException e)
				{
					logger.error("Unable to distribute catalogue for the user: " + username + ".\n Exception is ", e);
				}
			});
		}
		catch(Exception e)
		{
			processException(e, "Catalogue could not be updated in the CDR!");
		}
		return appResponse;
	}

	@Override
	@WebMethod
	public CatalogueType findCatalogue(String partyID) throws RutaException
	{
		CatalogueType cat = null;
		try
		{
			init();
			cat = mapperRegistry.getMapper(CatalogueType.class).findByUserId(partyID);
		}
		catch(Exception e)
		{
			processException(e, "Catalogue could not be retrieved from the CDR service!");
		}
		return cat;
	}

	@Override
	@WebMethod
	@Deprecated
	public void deleteCatalogue(String username, CatalogueDeletionType catDeletion) throws RutaException
	{
		try
		{
			init();
			final String id = mapperRegistry.getMapper(CatalogueDeletionType.class).insert(username, catDeletion);
			docBoxPool.submit(() ->
			{
				try
				{
					final Associates followers = mapperRegistry.getMapper(Associates.class).find(id).clone();
					final DocumentDistribution delDistribution = new DocumentDistribution(catDeletion, followers);
					mapperRegistry.getMapper(DocumentDistribution.class).insert(null, delDistribution);
				}
				catch(DetailException e)
				{
					logger.error("Unable to distribute catalogue deletion for the user: " + username + ".\n Exception is ", e);
				}
			});
		}
		catch(Exception e)
		{
			processException(e, "Catalogue could not be deleted from the CDR service!");
		}
	}

	@Override
	public ApplicationResponseType deleteCatalogueWithAppResponse(String username, CatalogueDeletionType catalogueDeletion)
			throws RutaException
	{
		ApplicationResponseType appResponse = null;
		try
		{
			init();
			String objID  = null;
			try
			{
				objID = mapperRegistry.getMapper(CatalogueDeletionType.class).insert(username, catalogueDeletion);
			}
			catch (Exception e1)
			{
				if(e1.getMessage() != null && e1.getMessage().contains("Document does not exist!"))
					; // it's OK because the Catalogue is already deleted, so proceed with the method
				else
					throw e1;
			}

			final PartyType senderParty = catalogueDeletion.getReceiverParty();
			final PartyType receiverParty = catalogueDeletion.getProviderParty();
			final String docUUID = catalogueDeletion.getUUIDValue();
			final String docID = catalogueDeletion.getIDValue();
			appResponse = InstanceFactory.
					createApplicationResponse(senderParty, receiverParty, docUUID, docID, InstanceFactory.APP_RESPONSE_POSITIVE, null);
			final String id = objID;
			docBoxPool.submit(() ->
			{
				try
				{
					Associates followers;
					if(id != null)
						followers = mapperRegistry.getMapper(Associates.class).find(id).clone();
					else
						followers = mapperRegistry.getMapper(Associates.class).
						findByUserId(receiverParty.getPartyIdentificationAtIndex(0).getIDValue());
					final DocumentDistribution delDistribution = new DocumentDistribution(catalogueDeletion, followers);
					mapperRegistry.getMapper(DocumentDistribution.class).insert(null, delDistribution);
					logger.info("***Application Response distributed to id: " + id);
				}
				catch(DetailException e)
				{
					logger.error("Unable to distribute catalogue deletion for the user: " + username + ".\n Exception is ", e);
				}
			});
		}
		catch(Exception e)
		{
			processException(e, "Catalogue could not be deleted from the CDR service!");
		}
		return appResponse;

	}

	@Override
	@WebMethod
	public String registerUser(String username, String password, PartyType party) throws RutaException
	{
		String secretKey = null;
		try
		{
			init();
			secretKey = (String) mapperRegistry.getMapper(RutaUser.class).registerUser(username, password, party);
			docBoxPool.submit(() ->
			{
				try
				{
					final Associates followers = mapperRegistry.getMapper(Associates.class).findByUsername(username).clone();
					final DocumentDistribution partyDistribution = new DocumentDistribution(party, followers);
					mapperRegistry.getMapper(DocumentDistribution.class).insert(null, partyDistribution);
				}
				catch(DetailException e)
				{
					logger.error("Unable to send to itself's DocBox its own party for the user: " + username + ".\n Exception is ", e);
				}
			});
		}
		catch(Exception e)
		{
			processException(e, "Party could not be registered with the CDR service!");
		}
		return secretKey;
	}

	@Override
	@WebMethod
	public String insertParty(String username, PartyType party) throws RutaException
	{
		String id = null;
		try
		{
			init();
			mapperRegistry.getMapper(PartyType.class).insert(username, party);
			id = mapperRegistry.getMapper(RutaUser.class).getUserID(username);
		}
		catch(Exception e)
		{
			processException(e, "Party could not be registered with the CDR service!");
		}
		return id;
	}

	@Override
	@WebMethod
	public void updateParty(String username, PartyType party) throws RutaException
	{
		try
		{
			init();
			final String id = mapperRegistry.getMapper(PartyType.class).update(username, party);
			docBoxPool.submit(() ->
			{
				try
				{
					final Associates followers = mapperRegistry.getMapper(Associates.class).find(id).clone();
					final DocumentDistribution partyDistribution = new DocumentDistribution(party, followers);
					mapperRegistry.getMapper(DocumentDistribution.class).insert(null, partyDistribution);
				}
				catch(DetailException e)
				{
					logger.error("Unable to distribute Party document of the user: " + username + ".\n Exception is:", e);
				}
			});
		}
		catch(Exception e)
		{
			processException(e, "Party could not be updated in the CDR service!");
		}
	}

	@Override
	@WebMethod
	public void deregisterUser(String username, DeregistrationNotice notice) throws RutaException
	{
		try
		{
			init();
			final Associates followers = mapperRegistry.getMapper(Associates.class).findByUsername(username).clone();
			followers.removeAssociate(followers.getPartyID());
			mapperRegistry.getMapper(RutaUser.class).deleteUser(username);
			docBoxPool.submit(() ->
			{
				try
				{
					final DocumentDistribution noticeDistribution = new DocumentDistribution(notice, followers);
					mapperRegistry.getMapper(DocumentDistribution.class).insert(null, noticeDistribution);
				}
				catch(DetailException e)
				{
					logger.error("Unable to distribute DeregistrationNotice document of the user: " + username + ".\n Exception is:", e);
				}
			});
		}
		catch(Exception e)
		{
			processException(e, "Party could not be deleted from the CDR service!");
		}
	}

	@Override
	public List<PartyType> searchParty(PartySearchCriterion criterion) throws RutaException
	{
		//		logger.info(criterion.getPartyName());
		List<PartyType> searchResult = null;// new ArrayList<>();
		try
		{
			init();
			searchResult =  mapperRegistry.getMapper(PartyType.class).findMany(criterion);

			/*			logger.info("*****************************************************************");
			long fm1avg = 0, fm2avg = 0, fm3avg = 0;
			int num = 5;
			for(int i=0; i<num; i++)
			{
				((PartyXmlMapper) MapperRegistry.getMapper(PartyType.class)).clearInMemoryObjects();
				long fc1 = System.currentTimeMillis();
				searchResult = (List<PartyType>) (MapperRegistry.getMapper(PartyType.class)).findMany(criterion);
				long fc2 = System.currentTimeMillis();
				fm2avg += fc2 - fc1;
			}
			for(int i=0; i<num; i++)
			{
				((PartyXmlMapper) MapperRegistry.getMapper(PartyType.class)).clearInMemoryObjects();
				long fc1 = System.currentTimeMillis();
				searchResult = (List<PartyType>) (MapperRegistry.getMapper(PartyType.class)).findManyID(criterion);
				long fc2 = System.currentTimeMillis();
				fm3avg += fc2 - fc1;
			}
			logger.info("Empty in memory");
			logger.info("findMany: " + fm2avg / num);
			logger.info("findManyID: " + fm3avg / num);
			logger.info("*****************************************************************");
			fm1avg = 0; fm2avg = 0; fm3avg = 0;
			for(int i=0; i<num; i++)
			{
				((PartyXmlMapper) MapperRegistry.getMapper(PartyType.class)).clearInMemoryObjects();
				long fc1 = System.currentTimeMillis();
				searchResult = (List<PartyType>) (MapperRegistry.getMapper(PartyType.class)).findMany(criterion);
				long fc2 = System.currentTimeMillis();
				fm2avg += fc2 - fc1;
			}
			for(int i=0; i<num; i++)
			{
				((PartyXmlMapper) MapperRegistry.getMapper(PartyType.class)).clearInMemoryObjects();
				long fc1 = System.currentTimeMillis();
				searchResult = (List<PartyType>) (MapperRegistry.getMapper(PartyType.class)).findManyID(criterion);
				long fc2 = System.currentTimeMillis();
				fm3avg += fc2 - fc1;
			}
			logger.info("Full in memory");
			logger.info("findMany: " + fm2avg / num);
			logger.info("findManyID: " + fm3avg / num);
			logger.info("*****************************************************************");*/
		}
		catch(Exception e)
		{
			processException(e, "Query could not be processed by CDR service!");
		}
		return searchResult;
	}

	@Override
	public List<CatalogueType> searchCatalogue(CatalogueSearchCriterion criterion) throws RutaException
	{
		List<CatalogueType> searchResult = null;
		try
		{
			init();
			searchResult = mapperRegistry.getMapper(CatalogueType.class).findMany(criterion);
		}
		catch(Exception e)
		{
			processException(e, "Query could not be processed by CDR service!");
		}
		return searchResult;
	}

	@Override
	public List<BugReport> searchBugReport(BugReportSearchCriterion criterion) throws RutaException
	{
		List<BugReport> searchResult = null;
		try
		{
			init();
			searchResult = mapperRegistry.getMapper(BugReport.class).findMany(criterion);
		}
		catch(Exception e)
		{
			processException(e, "Query could not be processed by CDR service!");
		}
		return searchResult;
	}

	@Override
	public List<PartyType> findAllParties() throws RutaException
	{
		logger.info("Start finding all parties");
		List<PartyType> searchResult = new ArrayList<>();
		try
		{
			init();
			searchResult = mapperRegistry.getMapper(PartyType.class).findAll();
			logger.info("Finished finding all parties");
		}
		catch(Exception e)
		{
			processException(e, "Could not retrieve all parties from the CDR service!");
		}
		return searchResult.size() != 0 ? searchResult : null;
	}

	@Override
	public RutaVersion findClientVersion(String currentVersion) throws RutaException
	{
		RutaVersion latestVersion = null;
		try
		{
			init();
			latestVersion = mapperRegistry.getMapper(RutaVersion.class).find(null);
			if(latestVersion.getVersion().compareTo(currentVersion) <= 0) //there is no new version
				latestVersion = null;
		}
		catch(Exception e)
		{
			processException(e, "Could not retrieve the current version from the CDR service!");
		}
		return latestVersion;
	}

	@Override
	public void insertUpdateNotification(RutaVersion version) throws RutaException
	{
		try
		{
			init();
			//			String id =  MapperRegistry.getMapper(RutaVersion.class).createID();
			mapperRegistry.getMapper(RutaVersion.class).insert(null, version);
		}
		catch(Exception e)
		{
			processException(e, "Could not notify CDR service about the Ruta Client update!");
		}
	}

	@Override
	@WebMethod
	public void insertBugReport(BugReport bugReport) throws RutaException
	{
		try
		{
			init();
			mapperRegistry.getMapper(BugReport.class).insert(null, bugReport);
		}
		catch(Exception e)
		{
			processException(e, "Bug could not be inserted in the database!");
		}
	}

	@Override
	public BugReport findBugReport(String id) throws RutaException
	{
		BugReport bugReport = null;
		try
		{
			init();
			bugReport = mapperRegistry.getMapper(BugReport.class).find(id);
		}
		catch(Exception e)
		{
			processException(e, "Bug report could not be retrieved from the database!");
		}
		return bugReport;
	}

	@Override
	public void addBugReportComment(String id, ReportComment comment) throws RutaException
	{
		try
		{
			init();
			BugReport bugReport = mapperRegistry.getMapper(BugReport.class).find(id);
			synchronized(bugReport)
			{
				bugReport.addComment(comment);
				mapperRegistry.getMapper(BugReport.class).update(null, bugReport);
			}
		}
		catch(Exception e)
		{
			processException(e, "Comment could not be added to the Bug report!");
		}
	}

	@Override
	public PartyType followParty(String partyID, String followID) throws RutaException
	{
		PartyType party = null;
		try
		{
			init();
			final Associates followers = mapperRegistry.getMapper(Associates.class).findByUserId(followID);
			if(followers == null)
			{
				String msg = null;
				if(mapperRegistry.getMapper(RutaUser.class).findByUserId(followID) == null)
					msg = "Party to follow does not exist in CDR.";
				else
					msg = "Associates document is missing for the party to follow.";
				logger.error(msg);
				throw new DatabaseException(msg);
			}
			synchronized(followers)
			{
				followers.addAssociate(partyID);
				mapperRegistry.getMapper(Associates.class).update(null, followers);
			}
			docBoxPool.submit(() ->
			{
				final Associates iFollower = new Associates();
				iFollower.setPartyID(followID);
				iFollower.addAssociate(partyID);
				try
				{
					final CatalogueType cat = mapperRegistry.getMapper(CatalogueType.class).findByUserId(followID);
					if(cat != null)
					{
						final DocumentDistribution catDistribution = new DocumentDistribution(cat, iFollower);
						mapperRegistry.getMapper(DocumentDistribution.class).insert(null, catDistribution);
					}
				}
				catch (DetailException e)
				{
					logger.error("Unable to distribute catalogue for the user with ID: " + followID + ". Exception is ", e);
				}
			});

			party = mapperRegistry.getMapper(PartyType.class).findByUserId(followID);
		}
		catch(Exception e)
		{
			processException(e, "Party " + partyID + " could not be added as a follower of the requested party " + followID + "!");
		}
		return party;
	}

	@Override
	public void unfollowParty(String partyID, String followID) throws RutaException
	{
		try
		{
			init();
			if(!mapperRegistry.getMapper(RutaUser.class).checkUser(followID))
				throw new DatabaseException("Party with ID " + followID + " is not registered with the CDR service!");
			final Associates followers = mapperRegistry.getMapper(Associates.class).findByUserId(followID);
			if(followers == null)
				throw new DatabaseException("Associates document is missing for the Party with ID: " + followID);


//			{
//			Associates followers = mapperRegistry.getMapper(Associates.class).findByUserId(followID);
//			if(followers == null)
//			{
//				String msg = null;
//				if(mapperRegistry.getMapper(RutaUser.class).findByUserId(followID) == null)
//					msg = "Party with ID " + followID + " is not registered with the CDR service!";
//				else
//					msg = "Associates document is missing for the party to follow.";
//				logger.error(msg);
//				throw new DatabaseException(msg);
//			}

			synchronized(followers)
			{
				followers.removeAssociate(partyID);
				mapperRegistry.getMapper(Associates.class).update(null, followers);
			}
		}
		catch(Exception e)
		{
			processException(e, "My Party could not be removed as a follower!");
		}
	}

	@Override
	public List<String> findAllDocBoxDocumentIDs(DocBoxAllIDsSearchCriterion criterion) throws RutaException
	{
		List<String> ids = null;
		try
		{
			init();
			ids = mapperRegistry.getMapper(DocBox.class).findManyIDs(criterion);
		}
		catch (Exception e)
		{
			processException(e, "DocBox document's id list could not be retrieved!");
		}
		return ids;
	}

	@Override
	public Object findDocBoxDocument(DocBoxDocumentSearchCriterion criterion) throws RutaException
	{
		Object document = null;
		try
		{
			init();
			List<DocBox> db = mapperRegistry.getMapper(DocBox.class).findMany(criterion);
			if(db != null)
				document = db.get(0).getDocument();
		}
		catch(Exception e)
		{
			processException(e, "DocBox document could not be retrieved!");
		}
		return document;
	}

	@Override
	public void deleteDocBoxDocument(String username, String id) throws RutaException
	{
		try
		{
			init();
			mapperRegistry.getMapper(DocBox.class).deleteDocBoxDocument(username, id);
		}
		catch(Exception e)
		{
			processException(e, "DocBox document could not be deleted!");
		}
	}

	@Override
	public void deleteDocBoxDocumentWithDocumentReceipt(String username, String id, DocumentReceipt documentReceipt) throws RutaException
	{
		try
		{
			init();
			if(documentReceipt != null)
				distributeDocument(documentReceipt);
			mapperRegistry.getMapper(DocBox.class).deleteDocBoxDocument(username, id);
		}
		catch(Exception e)
		{
			processException(e, "DocBox document could not be deleted!");
		}
	}

	@Deprecated
	@Override
	public List<BugReport> findAllBugReports() throws RutaException
	{
		List<BugReport> bugReports = null;
		try
		{
			init();
			bugReports = mapperRegistry.getMapper(BugReport.class).findAll();
			return bugReports;
		}
		catch(Exception e)
		{
			String exceptionMsg = "List of bug reports could not be retrieved from the database!";
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Deprecated //MMM: and does not work: transaction is null
	@Override
	public void insertImage(@XmlMimeType("application/octet-stream") Image image) throws RutaException
	{
		String exceptionMsg = "File could not be deposited into the CDR service!";
		DSTransaction transaction = null;
		try
		{
			init();
			String id =  mapperRegistry.getMapper(BugReport.class).createID();
			mapperRegistry.getMapper(BugReport.class).insert(image, id, transaction);
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Deprecated //MMM: and does not work: transaction is null
	@Override
	public void insertFile(@XmlMimeType("application/octet-stream") DataHandler dataHandler, String filename) throws RutaException
	{
		String exceptionMsg = "File could not be deposited into the CDR service!";
		DSTransaction transaction = null;
		try
		{
			init();

			File file = new File(filename);
			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
			BufferedInputStream bin = new BufferedInputStream(dataHandler.getInputStream());
			byte buffer[] = new byte[1024];
			int n = 0;
			while((n = bin.read(buffer)) != -1)
				bout.write(buffer, 0, n);
			bin.close();
			bout.close();

			String id =  mapperRegistry.getMapper(BugReport.class).createID();
			mapperRegistry.getMapper(BugReport.class).insert(file, id, transaction);
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Deprecated //MMM: and does not work: transaction is null
	@Override
	public void insertAttachment(ReportAttachment attachment, String filename) throws RutaException
	{
		String exceptionMsg = "Attachment could not be deposited into the CDR service!";
		DSTransaction transaction = null;
		try
		{
			init();
			File file = attachment.createFile("");
			String id =  mapperRegistry.getMapper(BugReport.class).createID();
			mapperRegistry.getMapper(BugReport.class).insert(file, id, transaction);
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Override
	public boolean clearCache() throws RutaException
	{
		boolean success = false;
		String exceptionMsg = "CDR service cache could not be cleared!";
		try
		{
			init();
//			Thread.sleep(10000);
			mapperRegistry.clearCachedObjects();
			success = true;
			logger.info("Service thread has finished in entirety");
			return success;
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Override
	public void distributeDocument(Object document) throws RutaException
	{
		try
		{
			init();
			final Associates recepient = getAssociates(document);
			final String senderID = recepient.getPartyID();
			final String receiverID = recepient.getAssociateAtIndex(0);
			if(!mapperRegistry.getMapper(RutaUser.class).checkUser(receiverID))
				throw new DatabaseException("User with ID " + receiverID + " is not registered with the CDR service!");
			final Associates followers = mapperRegistry.getMapper(Associates.class).findByUserId(senderID);
			if(!followers.isAssociate(receiverID))
				throw new DatabaseException("User with ID " + receiverID + " is not a business partner or follower!");
			final DocumentDistribution documentDistribution = new DocumentDistribution(document, recepient);
			mapperRegistry.getMapper(DocumentDistribution.class).insert(null, documentDistribution);
		}
		catch(Exception e)
		{
			processException(e, "Document could not be distributed to the Receiver Party!");
		}
	}

	@Override
	public void requestBusinessPartnership(PartnershipRequest request) throws RutaException
	{
		try
		{
			init();
			final Associates recepient = getAssociates(request);
			final String senderID = recepient.getPartyID();
			final String receiverID = recepient.getAssociateAtIndex(0);
			if(!mapperRegistry.getMapper(RutaUser.class).checkUser(receiverID))
				throw new DatabaseException("User with ID " + receiverID + " is not registered with the CDR service!");
			final PartnershipSearchCriterion sc = new PartnershipSearchCriterion();
			sc.setRequestedPartyID(receiverID);
			sc.setRequesterPartyID(senderID);
			final DataMapper<PartnershipRequest, String> requestMapper = mapperRegistry.getMapper(PartnershipRequest.class);

			synchronized(requestMapper)
			{
				if(requestMapper.findMany(sc) != null)
					throw new DetailException("There is already an unresolved Partnership Request involving two Parties of interest.");
				requestMapper.insert(null, request);
			}
			final DocumentDistribution documentDistribution = new DocumentDistribution(request, recepient);
			mapperRegistry.getMapper(DocumentDistribution.class).insert(null, documentDistribution);
		}
		catch(Exception e)
		{
			processException(e, "Partnership Request could not be distributed to the Receiver Party!");
		}
	}

	@Override
	public void responseBusinessPartnership(PartnershipResponse response) throws RutaException
	{
		try
		{
			init();
			final Associates recepient = getAssociates(response);
			final String senderID = recepient.getPartyID();
			final String receiverID = recepient.getAssociateAtIndex(0);
			if(!mapperRegistry.getMapper(RutaUser.class).checkUser(receiverID))
				throw new DatabaseException("User with ID " + receiverID + " is not registered with the CDR service!");
			final PartnershipSearchCriterion sc = new PartnershipSearchCriterion();
			sc.setRequestedPartyID(receiverID);
			sc.setRequesterPartyID(senderID);
			final List<PartnershipRequest> requests = mapperRegistry.getMapper(PartnershipRequest.class).findMany(sc);
			if(requests == null)
				throw new DetailException("There is no matching unresolved Partnership Request involving two Parties of interest.");
			final PartnershipResolution resolution = new PartnershipResolution();
			resolution.setID(UUID.randomUUID().toString());
			resolution.setIssueTime(InstanceFactory.getDate());
			resolution.setResponsedTime(response.getIssueTime());
			resolution.setRequesterParty(response.getRequesterParty());
			resolution.setRequestedParty(response.getRequestedParty());
			resolution.setAccepted(response.isAccepted());

			DocumentDistribution resolutionDistribution = new DocumentDistribution(resolution, recepient);
			mapperRegistry.getMapper(DocumentDistribution.class).insert(null, resolutionDistribution);
			final Associates recepient2 = new Associates();
			recepient2.setPartyID(receiverID);
			recepient2.addAssociate(senderID);
			resolutionDistribution = new DocumentDistribution(resolution, recepient2);
			mapperRegistry.getMapper(DocumentDistribution.class).insert(null, resolutionDistribution);

			for(PartnershipRequest request : requests)
				mapperRegistry.getMapper(PartnershipRequest.class).delete(null, request.getIDValue());

			if(response.isAccepted())
			{
				followParty(senderID, receiverID);
				followParty(receiverID, senderID);

//				docBoxPool.submit(() ->
//				{
//					try
//					{
//						final CatalogueType cat = mapperRegistry.getMapper(CatalogueType.class).findByUserId(senderID);
//						if(cat != null)
//						{
//							final DocumentDistribution catDistribution = new DocumentDistribution(cat, recepient);
//							mapperRegistry.getMapper(DocumentDistribution.class).insert(null, catDistribution);
//						}
//					}
//					catch (DetailException e)
//					{
//						logger.error("Unable to distribute catalogue for the user with ID: " + senderID + ". Exception is ", e);
//					}
//				});
//
//				docBoxPool.submit(() ->
//				{
//					try
//					{
//						final CatalogueType cat = mapperRegistry.getMapper(CatalogueType.class).findByUserId(receiverID);
//						if(cat != null)
//						{
//							final DocumentDistribution catDistribution = new DocumentDistribution(cat, recepient2);
//							mapperRegistry.getMapper(DocumentDistribution.class).insert(null, catDistribution);
//						}
//					}
//					catch (DetailException e)
//					{
//						logger.error("Unable to distribute catalogue for the user with ID: " + receiverID + ". Exception is ", e);
//					}
//				});

			}
		}
		catch(Exception e)
		{
			processException(e, "Partnership Request could not be distributed to the Receiver Party!");
		}
	}

	@Override
	public void breakupBusinessPartnership(PartnershipBreakup breakup) throws RutaException
	{
		try
		{
			init();
			final Associates recepient = getAssociates(breakup);
			final String senderID = recepient.getPartyID();
			final String receiverID = recepient.getAssociateAtIndex(0);

			if(!mapperRegistry.getMapper(RutaUser.class).checkUser(senderID))
				throw new DatabaseException("Party with ID " + senderID + " is not registered with the CDR service!");
			if(!mapperRegistry.getMapper(RutaUser.class).checkUser(receiverID))
				throw new DatabaseException("Party with ID " + receiverID + " is not registered with the CDR service!");
			unfollowParty(senderID, receiverID);
			unfollowParty(receiverID, senderID);
			DocumentDistribution documentDistribution = new DocumentDistribution(breakup, recepient);
			mapperRegistry.getMapper(DocumentDistribution.class).insert(null, documentDistribution);

			final Associates recepient2 = new Associates();
			recepient2.setPartyID(receiverID);
			recepient2.addAssociate(senderID);
			documentDistribution = new DocumentDistribution(breakup, recepient2);
			mapperRegistry.getMapper(DocumentDistribution.class).insert(null, documentDistribution);
		}
		catch(Exception e)
		{
			processException(e, "Partnership Breakup could not be processed!");
		}
	}

	/**
	 * Creates {@link Associates} object from the data of passed document. {@code Associates} object is
	 * used for that document distribution.
	 * @param document {@code UBL document} which sender and receiver party's IDs are used
	 * @return {@link Associates} object
	 * @throws DetailException if ID of the associates could not be retrieved
	 */
	private Associates getAssociates(Object document) throws DetailException
	{
		final Associates recepient = new Associates();
		String senderID = null, receiverID = null;
		senderID = InstanceFactory.getDocumentSenderID(document);
		receiverID = InstanceFactory.getDocumentReceiverID(document);

		if(senderID == null)
			throw new DetailException("Sender's ID coud not be retrived.");
		if(receiverID == null)
			throw new DetailException("Receiver's ID could not be retrieved.");
		recepient.setPartyID(senderID);
		recepient.addAssociate(receiverID);

		return recepient;

	}
}