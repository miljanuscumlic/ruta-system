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
	private final static Documents documents = new Documents();
	private final static Logger logger = LoggerFactory.getLogger("rs.ruta.server");
	private DSTransactionFactory transactionFactory;
	//MMM: when transactionFailure==true system should be stoped, database restarted
	//MMM: and rollbackTransactions method should be called after restart???
	private boolean transactionFailure; //database transaction failed

	public CDR()
	{
		transactionFailure = false;
		transactionFactory = MapperRegistry.getTransactionFactory();
		rollbackTransactions();
	}

	@Override
	@WebMethod
	public void putDocument(CatalogueType d)
	{
		init();
		documents.addDocument(d);
	}

	private void init()
	{
		if(wsCtx == null)
			throw new RuntimeException("Dependancy Injection failed on Web Service Context!");
		if(sCtx == null) // ServletContext not yet set?
		{
			MessageContext mCtx = wsCtx.getMessageContext();
			sCtx = (ServletContext) mCtx.get(MessageContext.SERVLET_CONTEXT);
			documents.setServletContext(sCtx);
		}
		if(transactionFailure)
			rollbackTransactions();
	}

	@Override
	@WebMethod
	public CatalogueType getDocument()
	{
		init();
		return documents.removeDocument();
	}

	@Override
	@WebMethod
	public void insertCatalogue(String username, CatalogueType cat) throws RutaException
	{
		init();
		String id = null;
		DSTransaction transaction = null;
		try
		{
			transaction = transactionFactory.openTransaction();
			id = (String) MapperRegistry.getMapper(User.class).getID(username);
			MapperRegistry.getMapper(CatalogueType.class).insert(cat, id, transaction);
		}
		/*		catch (DetailException e)
		{
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
			throw new RutaException("Catalogue could not be deposited to the CDR service!", ((DetailException)e).getFaultInfo());
		}*/
		catch(Exception e)
		{
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
				throw new RutaException("Catalogue could not be deposited to the CDR service!", ((DetailException)e).getFaultInfo());
			else
				throw new RutaException("Catalogue could not be deposited to the CDR service!", e.getMessage());
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
					logger.error("Exception is: ", e);;
					throw new RutaException("Catalogue could not be deposited to the CDR service!",
							((DetailException) e.getCause()).getFaultInfo());
				}
			}
		}
	}

	@Override
	@WebMethod
	public void updateCatalogue(String username, CatalogueType cat) throws RutaException
	{
		init();
		String id = null;
		DSTransaction transaction = null;
		try
		{
			transaction = transactionFactory.openTransaction();
			id = (String) MapperRegistry.getMapper(User.class).getID(username);
			MapperRegistry.getMapper(CatalogueType.class).update(cat, id, transaction);
		}
		catch (DetailException e)
		{
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
			throw new RutaException("Catalogue could not be updated to the CDR service!", ((DetailException)e).getFaultInfo());
		}
		catch(Exception e)
		{
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
			throw new RutaException("Catalogue could not be updated to the CDR service!", e.getMessage());
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
					logger.error("Exception is: ", e);;
					throw new RutaException("Catalogue could not be updated to the CDR service!",
							((DetailException) e.getCause()).getFaultInfo());
				}
			}
		}
	}

	@Override
	@WebMethod
	public CatalogueType findCatalogue(String id) throws RutaException
	{
		init();
		CatalogueType cat = null;
		try
		{
			cat = (CatalogueType) MapperRegistry.getMapper(CatalogueType.class).find(id);
		}
		catch (Exception e)
		{
			throw new RutaException("Catalogue could not be retrieved from the CDR service!", ((DetailException)e).getFaultInfo());
		}
		return cat;
	}

	@Override
	@WebMethod
	public void deleteCatalogue(String username, CatalogueDeletionType catDeletion) throws RutaException
	{
		init();
		DSTransaction transaction = null;
		try
		{
			transaction = transactionFactory.openTransaction();
			String id = (String) MapperRegistry.getMapper(User.class).getID(username);
			MapperRegistry.getMapper(CatalogueDeletionType.class).insert(catDeletion, id, transaction);
			//			MapperRegistry.getMapper(CatalogueType.class).delete(id); //deprecated: when CatalogueDeletion is not used
		}
		/*		catch(DetailException e)
		{
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
			throw new RutaException("Catalogue could not be deleted from the CDR service!", e.getFaultInfo());
		}*/
		catch(Exception e)
		{
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
				throw new RutaException("Catalogue could not be deleted from the CDR service!", ((DetailException)e).getFaultInfo());
			else
				throw new RutaException("Catalogue could not be deleted from the CDR service!", e.getMessage());
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
					logger.error("Exception is: ", e);;
					throw new RutaException("Catalogue could not be deleted from the CDR service!",
							((DetailException) e.getCause()).getFaultInfo());
				}
			}
		}
	}

	@Override
	@WebMethod
	public String registerUser(String username, String password) throws RutaException
	{
		init();
		String secretKey = null;
		DSTransaction transaction = null;
		try
		{
			transaction = transactionFactory.openTransaction();
			secretKey = (String) MapperRegistry.getMapper(User.class).registerUser(username, password, transaction);
		}
		/*		catch (DetailException e)
		{
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
			throw new RutaException("Party could not be registered with the CDR service!", ((DetailException)e).getFaultInfo());
		}*/
		catch(Exception e)
		{
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
				throw new RutaException("Party could not be registered with the CDR service!", ((DetailException)e).getFaultInfo());
			else
				throw new RutaException("Party could not be registered with the CDR service!", e.getMessage());
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
					logger.error("Exception is: ", e);;
					throw new RutaException("Party could not be registered with the CDR service!",
							((DetailException) e.getCause()).getFaultInfo());
				}
			}
		}
		return secretKey;
	}

	@Override
	@WebMethod
	public String insertParty(String username, PartyType party) throws RutaException
	{
		init();
		DSTransaction transaction = null;
		String id = null;
		try
		{
			transaction = transactionFactory.openTransaction();
			id = (String) MapperRegistry.getMapper(User.class).getID(username);
			MapperRegistry.getMapper(PartyType.class).insert(party, id, transaction);
		}
		catch(Exception e)
		{
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
				throw new RutaException("Party could not be registered with the CDR service!", ((DetailException)e).getFaultInfo());
			else
				throw new RutaException("Party could not be registered with the CDR service!", e.getMessage());
		}
		return id;
	}

	@Override
	@WebMethod
	public void updateParty(String username, PartyType party) throws RutaException
	{
		init();
		DSTransaction transaction = null;
		try
		{
			transaction = transactionFactory.openTransaction();
			String id = (String) MapperRegistry.getMapper(User.class).getID(username);
			MapperRegistry.getMapper(PartyType.class).update(party, id, transaction);
		}
		catch(Exception e)
		{
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
				throw new RutaException("Party could not be updated in the CDR service!", ((DetailException)e).getFaultInfo());
			else
				throw new RutaException("Party could not be updated in the CDR service!", e.getMessage());
		}
	}

	@Override
	@WebMethod
	public void deleteUser(String username) throws RutaException
	{
		init();
		DSTransaction transaction = null;
		try
		{
			transaction = transactionFactory.openTransaction();
			String id = (String) MapperRegistry.getMapper(User.class).getID(username);
			MapperRegistry.getMapper(User.class).deleteUser(username, id, transaction);
		}
		/*		catch(DetailException e)
		{
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
			throw new RutaException("Party could not be deleted from the CDR service!", ((DetailException)e).getFaultInfo());
		}*/
		catch(Exception e)
		{
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
				throw new RutaException("Party could not be deleted from the CDR service!", ((DetailException)e).getFaultInfo());
			else
				throw new RutaException("Party could not be deleted from the CDR service!", e.getMessage());
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
					logger.error("Exception is: ", e);;
					throw new RutaException("Party could not be deleted from the CDR service!",
							((DetailException) e.getCause()).getFaultInfo());
				}
			}
		}
	}

	private void rollbackTransactions()
	{
		try
		{
			/*			@SuppressWarnings("unchecked")
			List<DSTransaction> transactions = (List<DSTransaction>) MapperRegistry.getMapper(DSTransaction.class).findAll();
			if(transactions != null)
				for(DSTransaction t: transactions)
					t.rollback();*/
			((ExistTransactionMapper)MapperRegistry.getMapper(DSTransaction.class)).rollbackAllTransactions();
			transactionFailure = false;
		}
		catch (DetailException e)
		{
			logger.error("Exception is: ", e);;
		}
	}

	@Override
	public List<PartyType> queryParty(String username, PartyType criterion) throws RutaException
	{
		try
		{
			List<PartyType> results = new ArrayList<>();
			MapperRegistry.getMapper(PartyType.class).find(criterion);
			return results;
		}
		catch(Exception e)
		{
			if (e instanceof DetailException)
				throw new RutaException("Query could not be processed with the CDR service!", ((DetailException)e).getFaultInfo());
			else
				throw new RutaException("Query could not be processed with the CDR service!", e.getMessage());
		}
	}

	@Override
	public PartyType queryPartyName(String username, String partyName)throws RutaException
	{
		try
		{
			PartyType result = null;
			MapperRegistry.getMapper(PartyType.class).search(partyName);
			return result;
		}
		catch(Exception e)
		{
			if (e instanceof DetailException)
				throw new RutaException("Query could not be processed with the CDR service!", ((DetailException)e).getFaultInfo());
			else
				throw new RutaException("Query could not be processed with the CDR service!", e.getMessage());
		}
	}

	@Override
	public List<PartyType> searchParty(String username, SearchCriterion criterion) throws RutaException
	{
		logger.info(criterion.getPartyName());
		List<PartyType> searchResult = new ArrayList<>();
		try
		{
			MapperRegistry.getMapper(PartyType.class).searchGeneric(searchResult, criterion);
		}
		catch(Exception e)
		{
			if (e instanceof DetailException)
				throw new RutaException("Query could not be processed with the CDR service!", ((DetailException)e).getFaultInfo());
			else
				throw new RutaException("Query could not be processed with the CDR service!", e.getMessage());
		}
		return searchResult.size() != 0 ? searchResult : null;
	}

	@Override
	public List<CatalogueType> searchCatalogue(String username, SearchCriterion criterion) throws RutaException
	{
		logger.info(criterion.getItemName());
		return null;
	}

	@Override
	public void testEpisode(Episode e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public List<PartyType> findAllParties() throws RutaException
	{
		logger.info("Start finding all parties");
		List<PartyType> searchResult = new ArrayList<>();
		try
		{
			MapperRegistry.getMapper(PartyType.class).findAll();
		}
		catch(Exception e)
		{
			if (e instanceof DetailException)
				throw new RutaException("Could not retrieve all parties from the CDR service!", ((DetailException)e).getFaultInfo());
			else
				throw new RutaException("Could not retrieve all parties from the CDR service!", e.getMessage());
		}
		return searchResult.size() != 0 ? searchResult : null;

	}

}
