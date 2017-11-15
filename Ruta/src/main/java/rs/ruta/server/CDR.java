package rs.ruta.server;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.*;
import javax.jws.*;
import javax.servlet.ServletContext;
import javax.xml.ws.*;
import javax.xml.ws.handler.MessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.common.RutaVersion;
import rs.ruta.common.SearchCriterion;
import rs.ruta.server.datamapper.*;

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
@WebService(endpointInterface = "rs.ruta.server.Server", targetNamespace = "http://ruta.rs/services")

@BindingType(javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class CDR implements Server
{
	@Resource
	private WebServiceContext wsCtx;
	private ServletContext sCtx;
	private final static Logger logger = LoggerFactory.getLogger("rs.ruta.server");
	private final DSTransactionFactory transactionFactory;
	private volatile boolean transactionFailure; //database transaction failed

	public CDR()
	{
		transactionFailure = true;
		transactionFactory = MapperRegistry.getTransactionFactory();
		try
		{
			rollbackTransactions();
		}
		catch (DetailException e)
		{
			if (e.getCause().getMessage().contains("connect"))
				logger.warn("Cound not connect to the database! The database might not be started. Details: " + e.getMessage() +
						" " + e.getCause().getMessage());
			logger.warn("If database has not been started please start it. Otherwise CDR service will not be operable"
					+ " and all SOAP requests will be rejected.");
		}
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
		if(transactionFailure)
			rollbackTransactions();
	}

	@Override
	@WebMethod
	public void insertCatalogue(String username, CatalogueType cat) throws RutaException
	{
		String exceptionMsg = "Catalogue could not be deposited to the CDR service!";
		DSTransaction transaction = null;
		try
		{
			init();
			transaction = transactionFactory.openTransaction();
			MapperRegistry.getMapper(CatalogueType.class).insert(username, cat, transaction);
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			if (transaction != null)
			{
				try
				{
					transaction.rollback();
				}
				catch(TransactionException ex)
				{
					transactionFailure = true;
				}
			}
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
		finally
		{
			if (transaction != null && !transactionFailure)
			{
				try
				{
					transaction.close();
				}
				catch (TransactionException e)
				{
					transactionFailure = true;
					logger.error("Exception is ", e);;
					throw new RutaException(exceptionMsg, ((DetailException) e.getCause()).getFaultInfo());
				}
			}
		}
	}

	@Override
	@WebMethod
	public void updateCatalogue(String username, CatalogueType cat) throws RutaException
	{
		String exceptionMsg = "Catalogue could not be updated to the CDR service!";
		DSTransaction transaction = null;
		try
		{
			init();
			transaction = transactionFactory.openTransaction();
			MapperRegistry.getMapper(CatalogueType.class).update(username, cat, transaction);
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			if (transaction != null)
			{
				try
				{
					transaction.rollback();
				}
				catch(TransactionException ex)
				{
					transactionFailure = true;
				}
			}
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
		finally
		{
			if (transaction != null && !transactionFailure)
			{
				try
				{
					transaction.close();
				}
				catch (TransactionException e)
				{
					transactionFailure = true;
					logger.error("Exception is ", e);;
					throw new RutaException(exceptionMsg, ((DetailException) e.getCause()).getFaultInfo());
				}
			}
		}
	}

	@Override
	@WebMethod
	public CatalogueType findCatalogue(String id) throws RutaException
	{
		String exceptionMsg = "Catalogue could not be retrieved from the CDR service!";
		CatalogueType cat = null;
		try
		{
			init();
			cat = MapperRegistry.getMapper(CatalogueType.class).findByUserId(id);
			return cat;
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
	@WebMethod
	public void deleteCatalogue(String username, CatalogueDeletionType catDeletion) throws RutaException
	{
		String exceptionMsg = "Catalogue could not be deleted from the CDR service!";
		DSTransaction transaction = null;
		try
		{
			init();
			transaction = transactionFactory.openTransaction();
			MapperRegistry.getMapper(CatalogueDeletionType.class).insert(username, catDeletion, transaction);
			//			MapperRegistry.getMapper(CatalogueType.class).delete(id); //deprecated: when CatalogueDeletion is not used
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			if (transaction != null)
			{
				try
				{
					transaction.rollback();
				}
				catch(TransactionException ex)
				{
					transactionFailure = true;
				}
			}
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
		finally
		{
			if (transaction != null && !transactionFailure)
			{
				try
				{
					transaction.close();
				}
				catch (TransactionException e)
				{
					transactionFailure = true;
					logger.error("Exception is ", e);;
					throw new RutaException(exceptionMsg, ((DetailException) e.getCause()).getFaultInfo());
				}
			}
		}
	}

	@Override
	@WebMethod
	public String registerUser(String username, String password) throws RutaException
	{
		String exceptionMsg = "Party could not be registered with the CDR service!";
		String secretKey = null;
		DSTransaction transaction = null;
		try
		{
			init();
			transaction = transactionFactory.openTransaction();
			secretKey = (String) MapperRegistry.getMapper(User.class).registerUser(username, password, transaction);
			return secretKey;
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			if (transaction != null)
			{
				try
				{
					transaction.rollback();
				}
				catch(TransactionException ex)
				{
					transactionFailure = true;
				}
			}
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
		finally
		{
			if (transaction != null && !transactionFailure)
			{
				try
				{
					transaction.close();
				}
				catch (TransactionException e)
				{
					transactionFailure = true;
					logger.error("Exception is ", e);;
					throw new RutaException(exceptionMsg, ((DetailException) e.getCause()).getFaultInfo());
				}
			}
		}
	}

	@Override
	@WebMethod
	public String insertParty(String username, PartyType party) throws RutaException
	{
		String exceptionMsg = "Party could not be registered with the CDR service!";
		DSTransaction transaction = null;
		String id = null;
		try
		{
			init();
			transaction = transactionFactory.openTransaction();
			MapperRegistry.getMapper(PartyType.class).insert(username, party, transaction);
			id = MapperRegistry.getMapper(User.class).getUserID(username);
			return id;
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			if (transaction != null)
			{
				try
				{
					transaction.rollback();
				}
				catch(TransactionException ex)
				{
					transactionFailure = true;
				}
			}
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
		finally
		{
			if (transaction != null && !transactionFailure)
			{
				try
				{
					transaction.close();
				}
				catch (TransactionException e)
				{
					transactionFailure = true;
					logger.error("Exception is ", e);;
					throw new RutaException(exceptionMsg, ((DetailException) e.getCause()).getFaultInfo());
				}
			}
		}
	}

	@Override
	@WebMethod
	public void updateParty(String username, PartyType party) throws RutaException
	{
		String exceptionMsg = "Party could not be updated in the CDR service!";
		DSTransaction transaction = null;
		try
		{
			init();
			transaction = transactionFactory.openTransaction();
			MapperRegistry.getMapper(PartyType.class).update(username, party, transaction);
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			if (transaction != null)
			{
				try
				{
					transaction.rollback();
				}
				catch(TransactionException ex)
				{
					transactionFailure = true;
				}
			}
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
		finally
		{
			if (transaction != null && !transactionFailure)
			{
				try
				{
					transaction.close();
				}
				catch (TransactionException e)
				{
					transactionFailure = true;
					logger.error("Exception is ", e);;
					throw new RutaException(exceptionMsg, ((DetailException) e.getCause()).getFaultInfo());
				}
			}
		}
	}

	@Override
	@WebMethod
	public void deleteUser(String username) throws RutaException
	{
		String exceptionMsg = "Party could not be deleted from the CDR service!";
		DSTransaction transaction = null;
		try
		{
			init();
			transaction = transactionFactory.openTransaction();
			MapperRegistry.getMapper(User.class).deleteUser(username, transaction);
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			if (transaction != null)
			{
				try
				{
					transaction.rollback();
				}
				catch(TransactionException ex)
				{
					transactionFailure = true;
				}
			}
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
		finally
		{
			if (transaction != null && !transactionFailure)
			{
				try
				{
					transaction.close();
				}
				catch (TransactionException e)
				{
					transactionFailure = true;
					logger.error("Exception is ", e);;
					throw new RutaException(exceptionMsg, ((DetailException) e.getCause()).getFaultInfo());
				}
			}
		}
	}

	/**Roll backs all transactions saved in the journals if there ia any.
	 * @throws DetailException if could not connect to the database or could not to roll back the transactions
	 */
	private synchronized void rollbackTransactions() throws DetailException
	{
		if(transactionFailure) //check again to be sure that some other thread did not finished executing rollbackTransactions
		{
			DSTransaction.rollbackAll();
			transactionFailure = false;
		}
	}

	@Override
	public List<PartyType> searchParty(String username, SearchCriterion criterion) throws RutaException
	{
		String exceptionMsg = "Query could not be processed by CDR service!";
		//		logger.info(criterion.getPartyName());
		List<PartyType> searchResult = new ArrayList<>();
		try
		{
			init();
			searchResult =  MapperRegistry.getMapper(PartyType.class).findMany(criterion);

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
			return searchResult.size() != 0 ? searchResult : null;
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
	public List<CatalogueType> searchCatalogue(String username, SearchCriterion criterion) throws RutaException
	{
		String exceptionMsg = "Query could not be processed by CDR service!";
		logger.info(criterion.getItemName());
		List<CatalogueType> searchResult = new ArrayList<>();
		try
		{
			init();
			searchResult = MapperRegistry.getMapper(CatalogueType.class).findMany(criterion);
			return searchResult.size() != 0 ? searchResult : null;
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
	public List<PartyType> findAllParties() throws RutaException
	{
		String exceptionMsg = "Could not retrieve all parties from the CDR service!";
		logger.info("Start finding all parties");
		List<PartyType> searchResult = new ArrayList<>();
		try
		{
			init();
			searchResult = MapperRegistry.getMapper(PartyType.class).findAll();
			logger.info("Finished finding all parties");
			return searchResult.size() != 0 ? searchResult : null;
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
	public RutaVersion updateRutaClient(String oldVersion) throws RutaException
	{
		String exceptionMsg = "Could not retrieve the current version from the CDR service!";
		try
		{
			init();
			RutaVersion currentVersion = MapperRegistry.getMapper(RutaVersion.class).findClientVersion();
			if(currentVersion.getVersion().compareTo(oldVersion) <= 0) //there is no new version
				currentVersion = null;
			return currentVersion;
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
	public void notifyUpdate(RutaVersion version) throws RutaException
	{
		String exceptionMsg = "Could not notify CDR service about the Ruta Client update!";
		DSTransaction transaction = null;
		try
		{
			init();
			transaction = transactionFactory.openTransaction();
			String id =  ((RutaVersionXmlMapper)MapperRegistry.getMapper(RutaVersion.class)).createID();
			MapperRegistry.getMapper(RutaVersion.class).insert(version, id, transaction);;
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			if (transaction != null)
			{
				try
				{
					transaction.rollback();
				}
				catch(TransactionException ex)
				{
					transactionFailure = true;
				}
			}
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
		finally
		{
			if (transaction != null && !transactionFailure)
			{
				try
				{
					transaction.close();
				}
				catch (TransactionException e)
				{
					transactionFailure = true;
					logger.error("Exception is ", e);;
					throw new RutaException(exceptionMsg, ((DetailException) e.getCause()).getFaultInfo());
				}
			}
		}
	}
}

