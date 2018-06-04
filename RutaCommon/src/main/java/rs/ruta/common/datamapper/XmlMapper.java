package rs.ruta.common.datamapper;

import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.OutputKeys;

import org.exist.xmldb.CollectionImpl;
import org.exist.xmldb.EXistResource;
import org.exist.xmldb.XQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.BinaryResource;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.common.PartyID;
import rs.ruta.common.RutaVersion;
import rs.ruta.common.SearchCriterion;
import rs.ruta.common.BugReport;
import rs.ruta.common.CatalogueSearchCriterion;
import rs.ruta.common.DocBox;
import rs.ruta.common.DocumentDistribution;
import rs.ruta.common.Associates;
import rs.ruta.common.RutaUser;
import rs.ruta.common.datamapper.DataMapper;

/**
 * XmlMapper abstract class maps the domain model used in the Ruta System to the eXist database.
 * Each class of the domain model which objects are deposited and fetched from the database
 * have its own data mapper class derived from the XMLMapper. {@code XmlMapper} subclasses
 * optionally have a cache map of in-memmory objects. But, this cache map is mandatory for
 * all classes that deal with the concurrent writes to their documents. Objects in those
 * maps are used for the synchronization of the writes to the xml documents.
 */
public abstract class XmlMapper<T> implements DataMapper<T, String>
{
	protected final static Logger logger = LoggerFactory.getLogger("rs.ruta.common");
	private final DSTransactionFactory transactionFactory;
	/** True when database transaction has failed.*/
	private volatile static boolean transactionFailure = true;
	private ExistConnector connector;
	/**Registry of all data mappers translating objects to xml documents.
	 */
	protected MapperRegistry mapperRegistry;

	public XmlMapper(DatastoreConnector connector) throws DetailException
	{
		this.connector = (ExistConnector) connector;
		transactionFactory = MapperRegistry.getTransactionFactory();
		mapperRegistry = MapperRegistry.getInstance();
		init();
	}

	private void init() throws DetailException
	{
		connector.connectToDatabase();
		connector.checkCollection(getCollectionPath());
		connector.checkCollection(getDeletedCollectionPath());
		connector.checkCollection(getQueryPath());
	}

	/**
	 * Gets the {@link ExistConnector} instance responsible for connection to the database.
	 * @return
	 */
	public ExistConnector getConnector()
	{
		return connector;
	}

	/**
	 * Gets the path to the query file in the database.
	 * @return {@code String} representing the path
	 */
	protected String getQueryPath()
	{
		return ExistConnector.getQueryPath();
	}

	/**
	 * Gets absolute path of the main collection of the Ruta application. Path is a String
	 * representing the URL of the collection.
	 * @return {@code String} representing absolute path
	 */
	protected String getAbsoluteRutaCollectionPath()
	{
		return ExistConnector.getAbsoluteRutaCollectionPath();
	}

	/**
	 * Gets relative path of the main collection of the Ruta application. Path is relative to
	 * the {@code /db} which is main collection of the eXist database.
	 * @return {@code String} representing relative path
	 */
	protected String getRelativeRutaCollectionPath()
	{
		return ExistConnector.getRelativeRutaCollectionPath();
	}

	protected String getDocumentSufix()
	{
		return ExistConnector.getDocumentSufix();
	}

	protected Collection getRootCollection() throws XMLDBException
	{
		return ExistConnector.getRootCollection();
	}

	protected Collection getRootCollection(String username, String password) throws XMLDBException
	{
		return ExistConnector.getRootCollection(username, password);
	}

	/**Checks whether there was a transaction failure in previous database opeeration and rolls back
	 * all outstanding transactions if there are any. Creates new transaction for the operation in hand.
	 * @throws DetailException if outstanding transactions could not be rolled back or new transaction could not be opened
	 */
	protected DSTransaction openTransaction() throws DetailException
	{
		if(transactionFailure)
			rollbackTransactions();
		DSTransaction transaction = null;
		try
		{
			transaction = transactionFactory.newTransaction();
		}
		catch(TransactionException e)
		{
			throw new DatabaseException("Could not open new database transaction.", e.getCause());
		}
		return transaction;
	}

	/**Closes transaction by deleting transaction journal and logs the error if transaction could not be closed.
	 * @param transaction transaction to be closed
	 * @throws DetailException if transaction could not be closed
	 */
	protected void closeTransaction(DSTransaction transaction) throws DetailException
	{
		//if transaction has been failed its journal must not be deleted, so rollback can be done
		if (transaction != null && !transaction.isFailed())
		{
			try
			{
				transaction.close();
			}
			catch (TransactionException e)
			{
				transactionFailure = true;
				transaction.setFailed(true);
				logger.error("Exception is ", e);;
				throw (DetailException) e.getCause();
			}
		}
	}

	/**Closes {@link Collection} and logs the error if collection for some reason could not be closed.
	 * @param collection {@code Collection} to be closed
	 */
	protected void closeCollection(Collection collection)
	{
		try
		{
			if(collection != null)
				collection.close();
		}
		catch (XMLDBException e)
		{
			logger.error("Exception is ", e);
		}
	}

	/**Checks whether transaction is closed and if not then it rolls back that transaction.
	 * @param transaction transaction to be checked
	 */
	protected void rollbackTransaction(DSTransaction transaction)
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
				transaction.setFailed(true);
			}
		}
	}

	/**Rolls back all outstanding transactions saved in the journals if there are any.
	 * @throws DetailException if could not connect to the database or could not roll back the transactions
	 */
	private static synchronized void rollbackTransactions() throws DetailException
	{
		if(transactionFailure) //check again to be sure that some other thread did not finished executing rollbackTransactions
		{
			DatabaseTransaction.rollbackAll();
			transactionFailure = false;
		}
	}

	@Override
	public T find(String id) throws DetailException
	{
		T object = getCachedObject(id);
		if(object == null)
		{
			object = retrieve(id);
			if(object != null)
				putCachedObject(id, object);
		}
		return object;
	}

	/**
	 * Retrieves object with passed id directly from the database not going through cached in-memory objects.
	 * <p>Method does not use transactions.
	 * @param id object's id
	 * @return retrived object or {@code null} if object doesn't exist
	 * @throws DetailException if collection or document could not be retrieved due to database connectivity issues
	 */
	protected T retrieve(String id) throws DetailException
	{
		String document = id + getDocumentSufix();
		String col = getCollectionPath();
		T object = null;
		Collection collection = null;
		XMLResource resource = null;
		try
		{
			collection = getCollection();
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			logger.info("Started retrieval of the document " + col + "/" + document + ".");
			resource = (XMLResource) collection.getResource(document);
			if(resource != null)
			{
				//saveDocumentAsFile(resource, document);
				final Object result = resource.getContent();
				object = (T) unmarshalFromXML(result.toString());
				logger.info("Finished retrieval of the document " + col + "/" + document + ".");
			}
			else
				logger.info("Document " + col + "/" + document + " not found.");

			return object;
		}
		catch (XMLDBException e)
		{
			throw new DatabaseException("Collection or document could not be retrieved from the database.");
		}
		finally
		{
			try
			{
				if(resource != null)
					((EXistResource) resource).freeResources();
				if(collection != null)
					collection.close();
			}
			catch(XMLDBException e)
			{
				logger.error("Exception is ", e);;
			}
		}
	}

	//MMM: check whether this method is necessary
	@Override
	public T findByUsername(String username) throws DetailException
	{
		String id = getID(username);
		T object = find(id);
		return object;
	}

	@Override
	public T findByUserId(String userID) throws DetailException
	{
		String id = getIDByUserID(userID);
		return id != null ? find(id) : null;
	}

	/**Loads resource in proper object if a resource is a complete xml document. At the beggining
	 * it checks whether the object is already in the memory. At the end puts the object in the memory.
	 * @param resource resource which contents are to be loaded in the object
	 * @return object or {@code null} if resource is a result of a query and not a complete document
	 * @throws XMLDBException if contents or id of the resource could not be retrieved
	 * @throws DetailException if object could not be unmarshalled from the xml
	 */
	protected T load(XMLResource resource) throws XMLDBException, DataManipulationException, DetailException
	{
		String id = resource.getDocumentId(); //id of the resource's parent document

		//MMM: this boolean testing is implementation specific, and it might be changed. So this is not
		//MMM: so good way of testing whether the resource is a complete document or not.
		//MMM: The problem is that resource.getId() should return null if the resource is a result of a query,
		//MMM: and not a whole document, but it returns exactly what returns resource.getDocumentId()
		//MMM: and that is ID without ".xml" at the end
		if(id.contains(".xml")) //resource is a whole xml document
		{
			id = id.replace(".xml", "");
			T object = getCachedObject(id);
			if(object == null)
			{
				String result = (String) resource.getContent();
				object = (T) unmarshalFromXML(result);
				putCachedObject(id, object);
			}
			return object;
		}
		else // resource is a result of a query and not the whole document
			return null;
	}

	/**
	 * Puts the object in the memory. If particular subclass of {@link XmlMapper} has no map of in-memory objects
	 * this method does nothing.
	 * @param id object's id
	 * @param object object to be loaded in the memory
	 */
	protected void putCachedObject(String id, T object) { }

	/**
	 * Gets the object if it is loaded in the memory.
	 * @param id object's id
	 * @return loaded in-memory object or {@code null} if it is not loaded in the memory or particular subclass
	 * does not have a map of in-memory objects
	 */
	protected T getCachedObject(String id) { return null; }

	/**
	 * Removes the object if it is loaded in the memory.
	 * @param id object's id
	 * @return loaded in-memory object or {@code null} if it is not loaded in the memory or particular subclass
	 * does not have a map of in-memory objects
	 */
	protected T removeCachedObject(String id) { return null; }

	@Override
	public ArrayList<T> findAll() throws DetailException
	{
		ArrayList<T> results = new ArrayList<>();
		String col = getCollectionPath();
		Collection collection = null;
		XMLResource resource = null;
		try
		{
			collection = getCollection();
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			int count = collection.getResourceCount(); //number of documents in the collection
			String[] resourceIDs = collection.listResources();
			T object = null;
			for(String id : resourceIDs)
			{
				//				logger.info("Starting retrival of the document " + col + "/" + id + ".");
				resource = (XMLResource) collection.getResource(id);
				if(resource != null)
				{
					object = load(resource);
					results.add(object);
					//					logger.info("Document " + col + "/" + id + " retrieved.");
				}
				else
					logger.error("Document: " + "/" + col + id + " not found.");
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
				if(resource != null)
					((EXistResource) resource).freeResources();
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

	@Override
	public ArrayList<String> findAllIDs() throws DetailException
	{
		ArrayList<String> results = new ArrayList<>();
		Collection collection = null;
		try
		{
			collection = getCollection();
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			int count = collection.getResourceCount();
			String[] resourceIDs = collection.listResources();
			for(String id : resourceIDs)
				results.add(id);
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

	@Override
	public ArrayList<T> findMany(SearchCriterion criterion) throws DetailException
	{
		Collection collection = null;
		ArrayList<T> results = new ArrayList<>();
		try
		{
			collection = getCollection();
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			final String uri = getAbsoluteRutaCollectionPath();
			final XQueryService queryService = (XQueryService) collection.getService("XQueryService", "1.0");
			logger.info("Started query of the " + uri);
			queryService.setProperty("indent", "yes");
			String query = null; //search query
			//loading the .xq query file from the database
			//prepare query String adding criteria for the search from SearchCriterion object
			//			query = prepareQuery(criterion);
			query = prepareQuery(criterion, queryService);

			//			final File queryFile = null;
			if(/*queryFile != null ||*/ query != null)
			{
				/*				final StringBuilder queryBuilder = new StringBuilder();
				fileContents(queryFile, queryBuilder);
				CompiledExpression compiled = queryService.compile(queryBuilder.toString());*/
				final CompiledExpression compiled = queryService.compile(query);
				final ResourceSet resourceResults = queryService.execute(compiled);
				final ResourceIterator iterator = resourceResults.getIterator();
				while(iterator.hasMoreResources())
				{
					XMLResource resource = null;
					try
					{
						resource = (XMLResource) iterator.nextResource();
						//System.out.println((String) resource.getContent());
						T object = load(resource);
						if(object == null) //resource is not a whole document rather part of it
							object = (T) unmarshalFromXML((String) resource.getContent());
						if(object != null) //successful unmarshamlling
							results.add(object);
						else
							throw new DatabaseException("Could not umnmarshal the object from xml.");
					}
					finally
					{
						if(resource != null)
							((EXistResource) resource).freeResources();
					}
				}
				logger.info("Finished query of the " + uri);
			}
			else
				throw new DatabaseException("Could not process the query. Query file does not exist.");
		}
		catch(XMLDBException e)
		{
			logger.error(e.getMessage(), e);
			throw new DatabaseException("Could not process the query. There is an error in the process of its execution.", e);
		}
		finally
		{
			if(collection != null)
			{
				try
				{
					collection.close();
				}
				catch(XMLDBException e)
				{
					logger.error(e.getMessage(), e);
				}
			}
		}
		return results.size() != 0 ? results : null;
	}

	@Override
	public ArrayList<String> findManyIDs(SearchCriterion criterion) throws DetailException
	{
		Collection collection = null;
		ArrayList<String> results = new ArrayList<>();
		try
		{
			collection = getCollection();
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			final String uri = getAbsoluteRutaCollectionPath();
			final XQueryService queryService = (XQueryService) collection.getService("XQueryService", "1.0");
			logger.info("Started query of the " + uri);
			queryService.setProperty("indent", "yes");
			String query = null;
			query = prepareQuery(criterion, queryService);
			if(query != null)
			{
				final CompiledExpression compiled = queryService.compile(query);
				final ResourceSet resourceResults = queryService.execute(compiled);
				final ResourceIterator iterator = resourceResults.getIterator();
				while(iterator.hasMoreResources())
				{
					String result = null;
					XMLResource resource = null;
					try
					{
						resource = (XMLResource) iterator.nextResource();
						result = (String) resource.getContent();
						results.add(result);
					}
					finally
					{
						if(resource != null)
							((EXistResource) resource).freeResources();
					}
				}
				logger.info("Finished query of the " + uri);
			}
			else
				throw new DatabaseException("Could not process the query. Query file does not exist.");
		}
		catch(XMLDBException e)
		{
			logger.error(e.getMessage(), e);
			throw new DatabaseException("Could not process the query. There is an error in the process of its execution.", e);
		}
		finally
		{
			if(collection != null)
			{
				try
				{
					collection.close();
				}
				catch(XMLDBException e)
				{
					logger.error(e.getMessage(), e);
				}
			}
		}
		return results.size() != 0 ? results : null;
	}

	@Deprecated
	public List<String> findAllDocumentIDs(String partyID) throws DetailException
	{
		return ((XmlMapper<DocumentDistribution>) mapperRegistry.getMapper(DocumentDistribution.class)).findAllDocumentIDs(partyID);
	}

	@Override
	public ArrayList<T> findManyQueryingByID(CatalogueSearchCriterion criterion) throws DetailException
	{
		Collection coll = null;
		ArrayList<T> results = new ArrayList<T>();
		try
		{
			coll = getCollection();
			if(coll == null)
				throw new DatabaseException("Collection does not exist.");
			final String uri = getAbsoluteRutaCollectionPath();
			final XQueryService queryService = (XQueryService) coll.getService("XQueryService", "1.0");
			logger.info("Started query of the " + uri);
			queryService.setProperty("indent", "yes");
			String query = null; // search query
			//loading the .xq query file from the database
			//prepare query String adding criteria for the search from SearchCriterion object
			query = prepareQuery("search-party-id.xq", criterion);

			//				final File queryFile = null;
			if(/*queryFile != null ||*/ query != null)
			{
				/*				final StringBuilder queryBuilder = new StringBuilder();
					fileContents(queryFile, queryBuilder);
					CompiledExpression compiled = queryService.compile(queryBuilder.toString());*/
				CompiledExpression compiled = queryService.compile(query);
				final ResourceSet resourceResults = queryService.execute(compiled);
				final ResourceIterator iterator = resourceResults.getIterator();
				while(iterator.hasMoreResources())
				{
					Resource resource = null;
					try
					{
						resource = iterator.nextResource();
						T object = find(trimID((String) resource.getContent()));
						results.add(object);
					}
					finally
					{
						if(resource != null)
							((EXistResource)resource).freeResources();
					}
				}
				logger.info("Finished query of the " + uri);
				return results;
			}
			else
				throw new DatabaseException("Could not process the query. Query file does not exist.");
		}
		catch(XMLDBException e)
		{
			logger.error(e.getMessage(), e);
			throw new DatabaseException("Could not process the query. There is an error in the process of its execution.", e);
		}
		finally
		{
			if(coll != null)
			{
				try
				{
					coll.close();
				}
				catch(XMLDBException e)
				{
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	/**Populates query string with search keywords from the {@link CatalogueSearchCriterion} object. Query to be
	 * populated is defined in the subclass of {@code XmlMapper}.
	 * @param criterion defines the search criterion
	 * @return query as {@code String} ready for execution by xQuery processor or {@code null} if
	 * query file does not exist
	 * @throws DatabaseException if collection or query file could not be opened
	 */
	@Deprecated
	protected String prepareQuery(CatalogueSearchCriterion criterion) throws DatabaseException { return null; }

	/**
	 * Binds query with search keywords from the {@link SearchCriterion} object. Query to be
	 * populated is defined in the subclass of {@code XmlMapper}.
	 * @param criterion search criterion
	 * @return query as {@code String} ready for execution by xQuery processor or {@code null} if
	 * query file does not exist
	 * @throws DetailException if query for particular subclass is not defined or collection or
	 * query file could not be opened
	 */
	protected String prepareQuery(SearchCriterion criterion, XQueryService queryService) throws DetailException  { return null; }

	/**Populates query string with search keywords from the {@link CatalogueSearchCriterion} object. Name of the query
	 * to be populated is sent as a argument.
	 * @param queryName query's name
	 * @param criterion defines the search criterion
	 * @return query as {@code String} ready for execution by xQuery processor or {@code null} if
	 * query file does not exist
	 * @throws DatabaseException if collection or query file could not be opened
	 */
	protected String prepareQuery(String queryName, CatalogueSearchCriterion criterion) throws DatabaseException { return null; }

	/**Unmarshall object from XML document represented as string.
	 * @param xml XML as String object to be transformed to the object
	 * @return unmarshalled object
	 * @throws DataManipulationException if object could not be unmarshalled from the xml
	 */
	@SuppressWarnings("unchecked")
	protected T unmarshalFromXML(String xml) throws DataManipulationException
	{
		T result = null;
		try
		{
			JAXBContext jc = getJAXBContext();
			//			JAXBContext jc = JAXBContext.newInstance(getObjectClass());
			//JAXBContext jc = JAXBContext.newInstance(getObjectPackageName());
			Unmarshaller u = jc.createUnmarshaller();
			//System.out.println("*****\n" + xml + "\n*****");
			//logger.info("*****\n" + xml + "\n*****");

/*			@SuppressWarnings("unchecked")
			JAXBElement<T> jaxbResult = (JAXBElement<T>) u.unmarshal(new StringReader(xml));
			result = ((JAXBElement<T>) jaxbResult).getValue();*/

			//MMM: maybe this if clause in superfluous
			Object jaxbResult = u.unmarshal(new StringReader(xml));
			if(jaxbResult instanceof JAXBElement)
				result = ((JAXBElement<T>)jaxbResult).getValue();
			else
				result = (T) jaxbResult; // This is not used anymore ???
		}
		catch(JAXBException e)
		{
			logger.error("Exception is ", e);
			throw new DataManipulationException("The object could not be unmarshalled from the XML document.", e);
		}

		//return jaxbElement.getValue();
		return result;
	}

	protected abstract Class<?> getObjectClass();

	/**
	 * Gets the name of the package that contains object's class. The object and the class in question are ones that
	 * {@code XmlMapper} subclass is mapping to the XML.
	 * @return name of the package
	 */
	protected abstract String getObjectPackageName();

	/**
	 * Hook method called from other methods from {@code XmlMapper} class.
	 * <p>Creates unique ID for an object along doing some subclass specific checks, preparations or modifications.
	 * This method defines default behaviour which is obtaining the ID by retriving its value based on the user's username
	 * or creating a new one if the username is {@code null}. Username must be {@code null} if it is desired to
	 * generate a new ID, and not to use the one associated with the username. This behaviour is overidden by subclass
	 * if it has a need for a specific procedures before or after it creates/retrieves an ID.</p>
	 * @param object object which ID should be generated
	 * @param username user's username which is the object
	 * @param transaction transaction object
	 * @return ID for an object or {@code null} if object is not stored in the database
	 * @throws DetailException if ID could not be retrieved due database connectivity issues
	 */
	protected String doPrepareAndGetID(T object, String username, DSTransaction transaction)
			throws DetailException //MMM: exception not thrown with the UUID implementation
	{
		String id = null;

		/*		//Old implementation
		try
		{
			if(username != null)
				id = getID(username);
			if(id == null)
				id = createCollectionID(collection);
		}
		catch(XMLDBException e)
		{
			throw new DatabaseException("The collection could not be retrieved or unique ID created.", e);
		}*/

		if(username != null)
			id = getID(username);
		if(id == null)
			id = createID();

		return id;
	}

	/*	@Override
	public String insert(String username, T object) throws DetailException
	{
		DSTransaction transaction = openOperation();
		Collection collection = null;
		String id = null;
		try
		{
			collection = getCollection();
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			id = doPrepareAndGetID(collection, object, username, transaction); //subclass's hook operation
			insert(collection, object, id, (DatabaseTransaction) transaction);
			putCacheObject(id, object); //subclass's hook operation
		}
		catch(XMLDBException e)
		{
			throw new DatabaseException("The collection could not be retrieved.", e);
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			checkTransaction(transaction);
			throw e;
		}
		finally
		{
			 closeOperation(collection, transaction);
		}
		return id;
	}*/

	@Override
	public void insertAll(String username, List<T> list) throws DetailException
	{
		DSTransaction transaction = openTransaction();
		//synchronized on transaction because some other thread could potentially invoke rollbackAll
		//method and try to rollback this transaction before it is commited (closed) by this call
		synchronized(transaction)
		{
			try
			{
				for(T object : list)
					insert(username, object, transaction);
			}
			finally
			{
				closeTransaction(transaction);
			}
		}
	}

	@Override
	public String insert(String username, T object) throws DetailException
	{
		DSTransaction transaction = openTransaction();
		//synchronized on transaction because some other thread could potentially invoke rollbackAll
		//method and try to rollback this transaction before it is commited (closed) by this call
		synchronized(transaction)
		{
			try
			{
				return insert(username, object, transaction);
			}
			finally
			{
				closeTransaction(transaction);
			}
		}
	}

	/**
	 * Inserts object into the database.
	 * @param username user's username
	 * @param object object to be inserted
	 * @param transaction transaction object
	 * @return object's id
	 * @throws DetailException if object could not be inserted into the database
	 */
	protected String insert(String username, T object, DSTransaction transaction) throws DetailException
	{
		Collection collection = null;
		String id = null;
		try
		{
			collection = getCollection(); // getCollection(getCollectionPath())
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			id = doPrepareAndGetID(object, username, transaction); //subclass's hook operation
			if(id != null)
			{
				insert(collection, object, id, (DatabaseTransaction) transaction);
				putCachedObject(id, object); //subclass's hook operation
			}
			else
				throw new DatabaseException("Object's ID could not be found.");
		}
		catch(XMLDBException e)
		{
			rollbackTransaction(transaction);
			throw new DatabaseException("The collection could not be retrieved.", e);
		}
		catch(/*DetailException*/Exception e)
		{
			//logger.error("Exception is ", e); //commented because it is logged upper in the call hierarchy
			rollbackTransaction(transaction);
			throw e;
		}
		finally
		{
			closeCollection(collection);
		}
		return id;
	}

	/**Inserts object in the arbritary collection. Path of that collection is passed as {@code String}
	 * argument.
	 * @param collectionPath collection path of the object to be stored
	 * @param object object to be stored
	 * @param transaction transaction object
	 * @return object's id
	 * @throws DetailException if object could not be stored in the database
	 */
	@Deprecated
	public String insertToCollection(String collectionPath, T object, T transaction) throws DetailException
	{
		Collection collection = null;
		String id = null;
		try
		{
			collection = getCollection(collectionPath);
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			id = createCollectionID(collection);
			insert(collection, object, (String)id, (DatabaseTransaction) transaction);
			return id;
		}
		catch(XMLDBException e)
		{
			throw new DatabaseException("The collection could not be retrieved or unique ID created.", e);
		}
		finally
		{
			closeCollection(collection);
		}
	}

	@Override
	@Deprecated
	public void insert(T object) throws DetailException
	{
		DSTransaction transaction = openTransaction();
		Collection collection = null;
		String id = null;
		synchronized(transaction)
		{
			try
			{
				collection = getCollection();
				if(collection == null)
					throw new DatabaseException("Collection does not exist.");
				id = doPrepareAndGetID(object, null, null); //subclass's hook operation
				if(id != null)
				{
					insert(collection, object, id, (DatabaseTransaction) transaction);
					putCachedObject(id, object); //subclass's hook operation
				}
				else
					throw new DatabaseException("Object's ID could not be found");
			}
			catch(XMLDBException e)
			{
				rollbackTransaction(transaction);
				throw new DatabaseException("The collection could not be retrieved.", e);
			}
			catch(/*DetailException*/ Exception e)
			{
				logger.error("Exception is ", e);
				rollbackTransaction(transaction);
				throw e;
			}
			finally
			{
				closeCollection(collection);
				closeTransaction(transaction);
			}
		}
	}

	@Deprecated //MMM: check whether this method should be deleted
	public void insert(T object, String id, DSTransaction transaction) throws DetailException
	{
		Collection collection = null;
		try
		{
			collection = getCollection();
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			insert(collection, object, (String)id, (DatabaseTransaction)transaction);
		}
		catch(XMLDBException e)
		{
			throw new DatabaseException("The collection could not be retrieved.", e);
		}
		finally
		{
			closeCollection(collection);
		}
	}

	@Override
	public void insert(File file, String id, DSTransaction transaction) throws DetailException
	{
		Collection collection = null;
		try
		{
			collection = getCollection();
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			insertBinary(collection, file, (String)id, (DatabaseTransaction)transaction);
		}
		catch(XMLDBException e)
		{
			throw new DatabaseException("The collection could not be retrieved.", e);
		}
		finally
		{
			closeCollection(collection);
		}
	}

	@Override
	public void insert(Image object, String id, DSTransaction transaction) throws DetailException
	{
		Collection collection = null;
		try
		{
			collection = getCollection();
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			insertImage(collection, object, (String)id, (DatabaseTransaction)transaction);
		}
		catch(XMLDBException e)
		{
			throw new DatabaseException("The collection could not be retrieved.", e);
		}
		finally
		{
			closeCollection(collection);
		}
	}

	/**
	 * Inserts object with passed {@code id} in the collection.
	 * @param collection collection in which the object is to be stored
	 * @param object object to be stored
	 * @param transaction transaction object
	 * @param id of the object
	 * @throws DetailException if document could not be saved to the database, could not be made a copy of it
	 * or database transaction journal document could not be updated
	 */
	public void insert(Collection collection, T object, String id, DSTransaction transaction) throws DetailException
	{
		Resource resource = null;
		String resourceType = "XMLResource"; // else "BinaryResource"
		String colPath = getCollectionPath(collection); // getCollectionPath() was OK before I put insertToCollection() method so the path cannot be get from the overriden method getCollectionPath()
		//		colPath = ((CollectionImpl) collection).getPathURI().getCollectionPath();
		String xmlResult = null; //Storing object in the String
		String documentName = "";
		try
		{
			documentName = id + getDocumentSufix();
			xmlResult = marshalToXML(object);
			logger.info("Started storing of the document " + documentName + " to the location " + colPath);
			resource = collection.getResource(documentName);
			if(transaction != null && transaction.isEnabled() && resource != null) // it's update so copy resource to /deleted collection
				copyResourceToDeleted(resource, (DatabaseTransaction) transaction, "UPDATE");
			else //first time insert
			{
				resource = collection.createResource(documentName, resourceType);
				if(transaction != null && transaction.isEnabled())
					((DatabaseTransaction) transaction).addOperation(colPath, documentName, "INSERT", null, null, null);
			}
			resource.setContent(xmlResult);
			collection.storeResource(resource);
			logger.info("Finished storing of the document " + documentName + " to the location " + colPath);
		}
		catch(XMLDBException e)
		{
			logger.error("Could not save the document " + documentName + " to the location " + colPath);
			logger.error("Exception is ", e);
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
				logger.error("Exception is ", e);;
			}
		}
	}

	/**Inserts binary object in the collection.
	 * @param collection collection in which the object is to be stored
	 * @param object file to be stored
	 * @param transaction transaction object
	 * @return id of the file, unique in the scope of the collection
	 * @throws DetailException if file could not be stored
	 */
	private void insertBinary(Collection collection, File object, String id, DatabaseTransaction transaction) throws DetailException
	{
		Resource resource = null;
		String resourceType = "BinaryResource";
		String colPath = getCollectionPath(collection);
		//		colPath = ((CollectionImpl) collection).getPathURI().getCollectionPath();
		String xmlResult = null; //Storing object in the String
		String documentName = "";
		try
		{
			documentName = object.getName();
			logger.info("Started storing of the document " + documentName + " to the location " + colPath);
			resource = collection.getResource(documentName);
			//MMM: commented code in regard with the transaction should be removed and the code prilagodjen to the binary files
			/*			if(transaction != null && transaction.isEnabled() && resource != null) // it's update so copy resource to /deleted collection
				copyResourceToDeleted(resource, transaction, "UPDATE");
			else //first time insert
			 */			{
				 resource = collection.createResource(documentName, resourceType);
				 /*				if(transaction != null && transaction.isEnabled())
					transaction.appendOperation(colPath, documentName, "INSERT", null, null, null);*/
			 }
			 resource.setContent(object);
			 collection.storeResource(resource);
			 logger.info("Finished storing of the document " + documentName + " to the location " + colPath);
		}
		catch(XMLDBException e)
		{
			logger.error("Could not save the document " + documentName + " to the location " + colPath);
			logger.error("Exception is ", e);;
			throw new DatabaseException("The document could not be saved to the database.", e);
		}
		/*		catch(DataManipulationException e)
		{
			logger.error("Could not save the document " + documentName + " to the location " + colPath);
			throw e;
		}*/
		finally
		{
			try
			{
				if (resource != null)
					((EXistResource)resource).freeResources();
			}
			catch(XMLDBException e)
			{
				logger.error("Exception is ", e);;
			}
		}
	}

	/**Inserts image as binary object in the collection.
	 * @param collection collection in which the object is to be stored
	 * @param object image to be stored
	 * @param transaction transaction object
	 * @return id of the file, unique in the scope of the collection
	 * @throws DetailException if file could not be stored
	 */
	private void insertImage(Collection collection, Image object, String id, DatabaseTransaction transaction) throws DetailException
	{
		Resource resource = null;
		String resourceType = "BinaryResource";
		String colPath = getCollectionPath(collection);
		//		colPath = ((CollectionImpl) collection).getPathURI().getCollectionPath();
		String xmlResult = null; //Storing object in the String
		String documentName = "";
		try
		{
			documentName = createCollectionID();
			logger.info("Started storing of the document " + documentName + " to the location " + colPath);
			resource = collection.getResource(documentName);
			//MMM: commented code in regard with the transaction should be removed and the code prilagodjen to the binary files
			/*			if(transaction != null && transaction.isEnabled() && resource != null) // it's update so copy resource to /deleted collection
				copyResourceToDeleted(resource, transaction, "UPDATE");
			else //first time insert
			 */			{
				 resource = collection.createResource(documentName, resourceType);
				 /*				if(transaction != null && transaction.isEnabled())
					transaction.appendOperation(colPath, documentName, "INSERT", null, null, null);*/
			 }
			 resource.setContent(object);
			 collection.storeResource(resource);
			 logger.info("Finished storing of the document " + documentName + " to the location " + colPath);
		}
		catch(XMLDBException e)
		{
			logger.error("Could not save the document " + documentName + " to the location " + colPath);
			logger.error("Exception is ", e);;
			throw new DatabaseException("The document could not be saved to the database.", e);
		}
		/*		catch(DataManipulationException e)
		{
			logger.error("Could not save the document " + documentName + " to the location " + colPath);
			throw e;
		}*/
		finally
		{
			try
			{
				if (resource != null)
					((EXistResource)resource).freeResources();
			}
			catch(XMLDBException e)
			{
				logger.error("Exception is ", e);;
			}
		}
	}

	/**If id has a dot character in itself this method deletes the dot and all characters after it.
	 * @param id id that is the subject of trimming
	 * @return trimmed id
	 */
	protected String trimID(String id)
	{
		String trimmed = id.replaceFirst("^(.+)\\..+", "$1");
		return trimmed;
	}

	/**
	 * Gets the collection from the database as a database admin. Retrieved collection is defined
	 * in the subclass of the {@code XmlMapper}.
	 * @return a {@code Collection} instance for the requested collection or {@code null} if the collection could not be found
	 * @throws XMLDBException if there was an database connectivity issue
	 */
	public Collection getCollection() throws XMLDBException
	{
		return ExistConnector.getCollection(getCollectionPath());
		//		return DatabaseManager.getCollection(getAbsoluteRutaCollectionPath() + getCollectionPath(),
		//				DatabaseAdmin.getInstance().getUsername(), DatabaseAdmin.getInstance().getPassword());
	}

	/**
	 * Gets the collection from the database as a database admin. Reletive path to retrieved collection
	 * is passed as a argument.
	 * @param collectionPath relative path to the collection
	 * @return a {@code Collection} instance for the requested collection or {@code null} if the collection could not be found
	 * @throws XMLDBException if there was an database connectivity issue
	 */
	public Collection getCollection(String collectionPath) throws XMLDBException
	{
		return ExistConnector.getCollection(collectionPath);
		//				DatabaseManager.getCollection(getAbsoluteRutaCollectionPath() + collectionPath,
		//						DatabaseAdmin.getInstance().getUsername(), DatabaseAdmin.getInstance().getPassword());
	}

	/**
	 * Gets the collection from the database as a specified user. Collection that is retrieved
	 * is defined in the subclasses of the {@code XmlMapper}.
	 * @return a {@code Collection} instance for the requested collection or {@code null} if the collection could not be found
	 * @throws XMLDBException if there was an database connectivity issue
	 */
	public Collection getCollection(String username, String password) throws XMLDBException
	{
		return ExistConnector.getCollection(getCollectionPath(), username, password);
		//		return DatabaseManager.getCollection(getAbsoluteRutaCollectionPath() + getCollectionPath(), username, password);
	}

	/**Gets the base collection where are placed deleted documents as a database admin.
	 * Retrieved base collection is defined in the subclasses of the {@code XmlMapper}.
	 * @return a {@code Collection} instance for the base deleted collection or {@code null} if the collection could not be found
	 * @throws XMLDBException if there was an database connectivity issue
	 */
	public Collection getDeletedBaseCollection() throws XMLDBException
	{
		return DatabaseManager.getCollection(getAbsoluteRutaCollectionPath() + getDeletedCollectionPath(),
				DatabaseAdmin.getInstance().getUsername(), DatabaseAdmin.getInstance().getPassword());
	}

	/**Gets the collection from the database as a database admin. If collection does not exist, method creates it.
	 * @param collectionPath relative path to the collection
	 * @return a {@code Collection} instance for the requested collection path
	 * @throws XMLDBException if collection could not be retrieved or created
	 */
	protected Collection getOrCreateCollection(String collectionPath) throws XMLDBException
	{
		Collection collection = getCollection(collectionPath);
		if(collection == null)
			collection = ExistConnector.getOrCreateCollection(collectionPath);
		return collection;
	}

	/**
	 * Deletes {@link Collection}.
	 * @param collection collection to be deleted
	 * @throws XMLDBException if collection could not be deleted or parent collection could not be closed
	 */
	protected static void deleteCollection(final Collection collection) throws XMLDBException
	{
		ExistConnector.deleteCollection(collection);
	}

	@Override
	public void delete(String username, String id) throws DetailException
	{
		DSTransaction transaction = openTransaction();
		synchronized(transaction)
		{
			try
			{
				delete(id, transaction);
			}
			finally
			{
				closeTransaction(transaction);
			}
		}
	}

	/**Deletes object with passed id from the database.
	 * @param id id of the object that should be deleted
	 * @param transaction database transaction which deletion is part of. Might be {@code null}.
	 * @throws DetailException if object cannot be deleted
	 */
	protected void delete(String id, DSTransaction transaction) throws DetailException
	{
		Resource resource = null;
		Collection collection = null;
		String documentName = id + getDocumentSufix();
		try
		{
			collection = getCollection();
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			resource = collection.getResource(documentName);
			if(resource == null)
			{
				logger.info("Document {} does not exist!", getCollectionPath() + "/" + documentName);
				throw new DatabaseException("Document does not exist!");
			}
			else
			{
				logger.info("Started deletion of the document " + documentName + " from the location " + getCollectionPath());
				if(transaction != null && transaction.isEnabled()) // copying only when there is a transaction
					copyResourceToDeleted(resource, (DatabaseTransaction) transaction, "DELETE");
				collection.removeResource(resource);
				logger.info("Finished deletion of the document " + documentName + " from the location " + getCollectionPath());
				removeCachedObject(id);
			}
		}
		catch(XMLDBException e)
		{
			throw new DatabaseException("The object could not be deleted.", e);
		}
		finally
		{
			try
			{
				if (resource != null)
					((EXistResource)resource).freeResources();
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
	public void deleteAll() throws DetailException
	{
		try
		{
			final Collection collection = getCollection();
			if(collection != null) // otherwise data is already deleted which is OK, not an exception
				deleteCollection(collection);
		}
		catch (XMLDBException e)
		{
			logger.error("Database collection " + getCollectionPath() + " could not be deleted", e);
			throw new DatabaseException("Data could not be deleted", e);
		}

	}

	/**Copies resource representing original xml document to the pertinent subcollection of /deleted collection.
	 * @param resource resource representing the original document to be copied
	 * @param transaction transaction inside which this copying is happening
	 * @param operation {@code String} representing the operation beacause of which this copying is happening
	 * @throws DetailException if resource cannot be copied due to database connection issues.
	 */
	private void copyResourceToDeleted(Resource resource, DatabaseTransaction transaction, String operation) throws DetailException
	{
		Collection deletedCollection = null;
		String originalDocumentName = null;
		String deletedDocumentName = null; //document name in /deleted collection
		String deletedPath = null;
		String resourceType = "XMLResource";
		Resource deletedResource = null;
		try
		{
			originalDocumentName = trimID(((XMLResource) resource).getDocumentId());
			deletedPath = getDeletedSubcollectionPath("/" + originalDocumentName); //subcollection has the name same as originalDocumentName

			deletedCollection = ExistConnector.getOrCreateCollection(deletedPath);
			if(deletedCollection == null)
				throw new DatabaseException("Collection does not exist.");
			deletedDocumentName = originalDocumentName + "-" + String.valueOf(deletedCollection.getResourceCount()) + getDocumentSufix();
			logger.info("Start copying the document " + originalDocumentName + ".xml" + " from " + getCollectionPath() +
					" to " + deletedPath + "/" + deletedDocumentName);

			//add operation's information to the transaction
			if(transaction != null && transaction.isEnabled())
				transaction.addOperation(getCollectionPath(), originalDocumentName + ".xml", operation,
						deletedPath, deletedDocumentName, null);

			//copy the resource
			deletedResource = deletedCollection.createResource(deletedDocumentName, resourceType);
			deletedResource.setContent(resource.getContent());
			deletedCollection.storeResource(deletedResource);
			logger.info("Finished copying of the document " + originalDocumentName + ".xml" + " from " + getCollectionPath() +
					" to " + deletedPath + "/" + deletedDocumentName);
		}
		catch(XMLDBException e)
		{
			logger.error("Could not copy the document " + originalDocumentName + ".xml" + " from " + getCollectionPath() +
					" to " + deletedPath + "/" + deletedDocumentName);
			logger.error("Exception is ", e);;
			throw new DatabaseException("Document could not be backed up.", e);
		}
		finally
		{
			try
			{
				if (deletedResource != null)
					((EXistResource) deletedResource).freeResources();
				if(deletedCollection != null)
					deletedCollection.close();
			}
			catch(XMLDBException e)
			{
				logger.error("Exception is ", e);;
			}
		}
	}

	/**
	 * Opens xml document from the database collection which path and name are passed as arguments.
	 * Method does not use transactions.
	 * @param collectionPath path of collection relative to the main collection of the Ruta application in which the document resides
	 * @param documentName document name
	 * @return {@code String} representing the contents of the document or {@code null}
	 * if the document does not exist
	 * @throws DatabaseException if collection or document could not be opened
	 */
	protected String openXmlDocument(String collectionPath, @Nonnull String documentName) throws DatabaseException
	{
		String result = null;
		Collection collection = null;
		Resource resource = null;
		try
		{
			collection = getCollection(collectionPath);
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			resource = collection.getResource(documentName);
			if(resource != null)
			{
				Object content = resource.getContent();
				if("XMLResource".equals(resource.getResourceType()))
					result = content.toString();
				else // binary files like .xq is
				{
					content.getClass();
					result = new String((byte[])content, "UTF-8"); // ASCII
				}
			}
			if(result != null)
				logger.info("The document " + collectionPath + "/" + documentName + " is opened.");
			return result;
		}
		catch(XMLDBException | UnsupportedEncodingException e)
		{
			logger.error("The document " + collectionPath + "/" + documentName + " could not be opened.");
			logger.error("Exception is ", e);
			throw new DatabaseException("The document " + collectionPath + "/" + documentName + " could not be opened.");
		}
		finally
		{
			try
			{
				if(resource != null)
					((EXistResource)resource).freeResources();
				if(collection != null)
					collection.close();
			}
			catch(XMLDBException e)
			{
				logger.error("Exception is ", e);
			}
		}
	}

	/**Saves xml document with specified document name to the specified collection. Document is passed to the method as
	 * a {@code String}. If document does not exist it will be created. If exists its contents will be replaced.
	 * <p>Method does not use transactions.
	 * @param collectionPath path of the collection
	 * @param documentName document name
	 * @param document document as {@code String}
	 * @throws DatabaseException if document could not be saved to the collection
	 */
	protected void saveXmlDocument(String collectionPath, String documentName, String document) throws DatabaseException
	{
		Resource resource = null;
		Collection collection = null;
		try
		{
			logger.info("Start saving of the document " + collectionPath + "/" + documentName + ".");
			collection = getCollection(collectionPath);
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			resource = collection.getResource(documentName);
			if(resource == null)
				resource = collection.createResource(documentName, "XMLResource");
			resource.setContent(document);
			collection.storeResource(resource);
			logger.info("Finished copying of the document " + collectionPath + "/" + documentName  + ".");
		}
		catch(XMLDBException e)
		{
			logger.info("Could not save the document " + collectionPath + "/" + documentName + ".");
			logger.error("Exception is ", e);;
			throw new DatabaseException("Document could not be saved to the collection.", e);
		}
		finally
		{
			try
			{
				if(resource != null)
					((EXistResource)resource).freeResources();
			}
			catch(XMLDBException e)
			{
				logger.error("Exception is ", e);
			}
			closeCollection(collection);
		}
	}

	/**Moves xml document from one collection to the other.
	 * <p>Method does not use transactions.
	 * @param destinationCollectionPath path of the destionation collection relative to the main collection of the Ruta application
	 * @param destinationDocumentName name of the destionation document
	 * @param sourceCollectionPath path of the source collection relative to the main collection of the Ruta application
	 * @param sourceDocumentName name of the source document
	 * @throws DatabaseException if document could not be moved to a new location
	 */
	protected void moveXmlDocument(String destinationCollectionPath, String destinationDocumentName, String sourceCollectionPath,
			String sourceDocumentName) throws DatabaseException
	{
		copyXmlDocument(destinationCollectionPath, destinationDocumentName, sourceCollectionPath, sourceDocumentName);
		deleteXmlDocument(sourceCollectionPath, sourceDocumentName);
	}

	/**Copies xml document from one collection to the other.
	 * <p>Method does not use transactions.
	 * @param destinationCollectionPath path of the destionation collection relative to the main collection of the Ruta application
	 * @param destinationDocumentName name of the destionation document
	 * @param sourceCollectionPath path of the source collection relative to the main collection of the Ruta application
	 * @param sourceDocumentName name of the source document
	 * @throws DatabaseException if source document does not exist or could not be moved to a new location
	 */
	protected void copyXmlDocument(String destinationCollectionPath, String destinationDocumentName, String sourceCollectionPath,
			String sourceDocumentName) throws DatabaseException
	{
		Collection sourceCollection = null;
		Collection destinationCollection = null;
		Resource sourceResource = null;
		Resource destinationResource = null;
		try
		{
			sourceCollection = getCollection(sourceCollectionPath);
			if(sourceCollection == null)
				throw new DatabaseException("Collection does not exist.");
			sourceResource = sourceCollection.getResource(sourceDocumentName);
			if(sourceResource == null)
				throw new DatabaseException("Source document does not exist!");
			logger.info("Start copying of the document " + sourceCollectionPath + "/" + sourceDocumentName + " to " +
					destinationCollectionPath + "/" + destinationDocumentName);
			destinationCollection = getCollection(destinationCollectionPath);
			if(destinationCollection == null)
				throw new DatabaseException("Collection does not exist.");
			destinationResource = destinationCollection.getResource(destinationDocumentName);
			if(destinationResource == null)
				destinationResource = destinationCollection.createResource(destinationDocumentName, "XMLResource");
			destinationResource.setContent(sourceResource.getContent());
			destinationCollection.storeResource(destinationResource);
			logger.info("Finished copying of the document " + sourceCollectionPath + "/" + sourceDocumentName + " to " +
					destinationCollectionPath + "/" + destinationDocumentName);
		}
		catch(XMLDBException e)
		{
			logger.info("Could not move the document " + sourceCollectionPath + "/" + sourceDocumentName + " to " +
					destinationCollectionPath + "/" + destinationDocumentName);
			logger.error("Exception is ", e);;
			throw new DatabaseException("Source document could not be moved to the destination collection.", e);
		}
		finally
		{
			try
			{
				if(sourceResource != null)
					((EXistResource)sourceResource).freeResources();
				if(destinationResource != null)
					((EXistResource)destinationResource).freeResources();
			}
			catch(XMLDBException e)
			{
				logger.error("Exception is ", e);
			}
			try
			{
				if(destinationCollection != null)
					destinationCollection.close();
				if(sourceCollection != null)
					sourceCollection.close();
			} catch (XMLDBException e)
			{
				logger.error("Exception is ", e);
			}
		}
	}

	/**Deletes xml document from the collection which relative path is passed as an argument. Method succeeds
	 * if the document can be deleted, or the document did not exist prior to the method invocation.
	 * <p>Method does not use transactions.
	 * @param collectionPath path of the document's collection relative to the main collection of the Ruta application
	 * @param documentName name of the document to be deleted
	 * @throws DatabaseException if document could not be deleted
	 */
	protected void deleteXmlDocument(String collectionPath, String documentName) throws DatabaseException
	{
		Collection collection = null;
		Resource resource = null;
		try
		{
			logger.info("Start deletion of the document " + collectionPath + "/" + documentName);
			collection = getCollection(collectionPath);
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			resource = collection.getResource(documentName);
			if(resource != null) // document exist, not previously deleted
			{
				collection.removeResource(resource);
				logger.info("Finished deletion of the document " + collectionPath + "/" + documentName);
			}
			else
				logger.warn("Document " + collectionPath + "/" + documentName + " does not exist.");
		}
		catch(XMLDBException e)
		{
			logger.info("Could not delete the document " + collectionPath + "/" + documentName);
			throw new DatabaseException("Document could not be deleted from the collection.", e);
		}
		finally
		{
			try
			{
				if (resource != null)
					((EXistResource)resource).freeResources();
				if(collection != null)
					collection.close();
			}
			catch(XMLDBException e)
			{
				logger.error("Exception is ", e);;
			}
		}
	}


	@Override
	public void deleteDocBoxDocument(String username, String id) throws DetailException
	{
		mapperRegistry.getMapper(DocBox.class).deleteDocBoxDocument(username, id);
	}

	/**Generates unique ID for objects.
	 * @return unique ID
	 */
	@Override
	public String createID()
	{
		return UUID.randomUUID().toString();
	}

	/**Generates unique ID for objects in the scope of the collection that {@code XmlMapper}'s subclass
	 * is manipulating with.
	 * @return unique ID
	 * @throws XMLDBException trown if id cannot be created due to database connectivity issues
	 */
	@Deprecated
	public synchronized String createCollectionID() throws XMLDBException
	{
		String id = null;
		Collection collection = null;
		try
		{
			collection = getCollection();
			id = createCollectionID(collection);
			return id;
		}
		finally
		{
			closeCollection(collection);
		}
	}

	/**Generates unique ID for objects in the scope of the passed collection. Generated ID cannot
	 * be the same as one of previously used but deleted IDs what is checked in this method.
	 * @param collection collection in which scope unique ID is created
	 * @return unique ID
	 * @throws XMLDBException trown if id cannot be created due to database connectivity issues
	 */
	//MMM: should be private after MyPartyExistMapper would had become obsolete
	protected synchronized String createCollectionID(Collection collection) throws XMLDBException
	{
		String id;
		do
			id = trimID(collection.createId());
		while(isIDDeleted(collection, id));
		//		while(isIDPresentInDeleted(id)); // check deleted ids
		return id;
	}

	/**Checks if the id was used before by some of deleted objects. It checks if there is a subcolection in "deleted"
	 * collection. "Deleted" collection is consisting of deleted objects. Deleted objects of the same type and
	 * with the same ID are placed in the subcollection that has a name as the object's ID.
	 * @param id document's name that represents id in check
	 * @return true if id was used earlier
	 * @throws XMLDBException if collection cannot be retrieved from the database
	 */
	private boolean isIDPresentInDeleted(String id) throws XMLDBException
	{
		Collection deleted = null;
		boolean present = true;
		try
		{
			deleted = getDeletedBaseCollection();
			present = !(deleted.getChildCollection(id) == null);
		}
		finally
		{
			closeCollection(deleted);
		}
		return present;
	}

	/**Checks if the ID has been used before by some of deleted objects. It checks if there is a subcollection
	 * in "deleted" collection named the same as the ID. "Deleted" collection is consisting of deleted objects.
	 * Deleted objects of the same type and with the same ID are placed in the subcollection that has the same
	 * name as the object's ID.
	 * @param collection collection in which ID should be unique
	 * @param id id in check
	 * @return true if id has been used earlier
	 * @throws XMLDBException if collection cannot be retrieved from the database
	 */
	private boolean isIDDeleted(Collection collection, String id) throws XMLDBException
	{
		Collection deleted = null;
		String collectionPath = ((CollectionImpl) collection).getPathURI().getCollectionPath();
		String deletedPath = getDeletedCollectionPath(collectionPath); //deleted collection that should be checked
		boolean present = true;
		try
		{
			deleted = getCollection(deletedPath);
			present = !(deleted.getChildCollection(id) == null);
		}
		finally
		{
			closeCollection(deleted);
		}
		return present;
	}

	/**
	 * Serializes object to an XML document respresented as a String.
	 * @param object object to marshall
	 * @return XML as string
	 * @throws JAXBException
	 */
	protected String marshalToXML(T object) throws DataManipulationException
	{
		StringWriter sw = null;
		try
		{
			JAXBContext jc = getJAXBContext();
			//			JAXBContext jc = JAXBContext.newInstance(object.getClass());
			JAXBElement<T> element = getJAXBElement(object);
			Marshaller m = jc.createMarshaller();
//			m.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			//MMM check why eXist is behaving this way
			//property set because without it xml string for DeregistrationNotice would be generated with
			//xml prolog line <?xml version="1.0" encoding="UTF-8" standalone="yes"?> and eXist would not
			//write that string to the database!!!
			m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			//m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			//m.marshal(element, System.out);

			sw = new StringWriter();
			m.marshal(element, sw);
		}
		catch(JAXBException e)
		{
			logger.error("Exception is ", e);
			throw new DataManipulationException("The object could not be marshalled to an XML document.", e);
		}
		return sw.toString();
	}

	/**
	 * Storing document as a file on the filesystem.
	 * @param res resource representing document
	 * @param document filename of the document
	 */
	private void saveDocumentAsFile(Resource res, String document)
	{
		File file = new File(document);
		try
		{
			if(res.getResourceType() == "XMLResource")
			{
				try(PrintWriter writer = new PrintWriter(file, "UTF-8"))
				{
					writer.print(((XMLResource)res).getContent());
					writer.flush();
					logger.info("Document saved at " + file.getAbsolutePath());
				}
				catch(Exception e)
				{
					logger.error("Exception is ", e);;
				}
			}
			else // BinaryResource
			{
				try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file)))
				{
					//MMM: This saves some extra bytes at the begining of the file, presumably belonging to the Object res.getContent()
					os.writeObject(((BinaryResource)res).getContent());
					os.flush();
					logger.info("Document saved at: " + file.getAbsolutePath());
				}
				catch(Exception e)
				{
					logger.error("Exception is ", e);;
				}
			}
		} catch (XMLDBException e)
		{
			logger.error("Exception is ", e);;
		}
	}

	/**
	 * Returns relative path of the collection that stores documents. The path to the collection is defined in
	 * the subclass of the {@code XMLMapper} and is relative to the {@code ExistConnector#rutaCollectionPath} field
	 * which is the relative path of the main collection od the Ruta application.
	 * @return {@code String} that represents relative path of the collection
	 */
	protected abstract String getCollectionPath();

	/**
	 * Returns relative path of the collection. The path is relative to the base application collection.
	 * @param collection {@code Collection} instance
	 * @return {@code String} that represents relative path of the collection
	 */
	protected String getCollectionPath(Collection collection)
	{
		return ((CollectionImpl) collection).getPathURI().getCollectionPath().replaceFirst(getRelativeRutaCollectionPath(), "");
	}

	//MMM: Should be @Deprecated
	/**
	 * Returns relative path of the base collection in which are placed deleted documents. The path to the
	 * {@code /deleted} collection is defined in the subclass of the {@code XmlMapper}. Retrived path
	 * respresents base collection that stores deleted documents for particular subclass.
	 * @return {@code String} that represents relative path of the deleted collection
	 */
	protected String getDeletedCollectionPath(){ return getDeletedCollectionPath(getCollectionPath()); }

	/**Returns relative path (to the root application collection) of the base collection in which are placed
	 * deleted documents from the collection which relative path is passed as argument. The path to the
	 * {@code /deleted} collection is defined the {@code ExistConnector} class.
	 * @return {@code String} that represents relative path of the deleted collection
	 */
	protected String getDeletedCollectionPath(String collectionPath)
	{
		StringBuilder del = new StringBuilder();
		del.append(ExistConnector.getDeletedPath());
		del.append(collectionPath.replaceFirst(getRelativeRutaCollectionPath(), ""));
		return del.toString();
	}

	/**Returns relative path of the subcollection of the base collection in which are placed deleted documents.
	 * @param subPath part of the collection path that defines the subcollection of the base collection
	 * It starts with the <code>"/"</code> character.
	 * @return {@code String} that represent relative path of the deleted collection
	 */
	private String getDeletedSubcollectionPath(String subPath)
	{
		return subPath == null ? getDeletedCollectionPath() : getDeletedCollectionPath() + subPath;
	}

	protected abstract JAXBElement<T> getJAXBElement(T object);

	/**
	 * Gets the instance of {@link JAXBContext} for particular subclass of {@code XMLMapper}.
	 * {@code JAXBContext} is instatiated on the base of the package name that contains the class
	 * of the object to be marshalled. To be able to instantiate {@code JAXBContext} object using
	 * the name of that package as a parameter passed to a {@link JAXBContext#newInstance} method,
	 * it is mandatory to implement two methods in {@link ObjectFactory} class that is contained
	 * in that the same package: a method for creation of the object and a method for instantiation of
	 * the {@link JAXBElement} instance, as is to implement {@link XmlMapper#getObjectPackageName}
	 * in particular {@code XmlMapper}'s subclass.
	 * @return {@code JAXBContext} object
	 * @throws JAXBException if {@code JAXBContext} object could not be instatiated
	 */
	protected JAXBContext getJAXBContext() throws JAXBException
	{
		return JAXBContext.newInstance(getObjectPackageName());
		//return JAXBContext.newInstance(getObjectClass()); this is not working when querying the database
	}

	/**
	 * Gets the ID of the object based on the user's username.
	 * @param username user's username
	 * @return object's ID or {@code null} if object has no ID set in the database
	 * @throws DetailException if user is not registered, ID could not be retrieved due database connectivity issues
	 */
	protected String getID(String username) throws DetailException
	{
		return ((UserXmlMapper) mapperRegistry.getMapper(RutaUser.class)).getID(username);
	}

	/**
	 * Retrieves object's ID from the database. ID is the result of the querying the database
	 * based on the ID user has been given.
	 * @param userID user's ID must not be {@code null}
	 * @return id of the object in the database or {@code null} if object is not stored in the database
	 * @throws DetailException if there is a database connectivity issue during retrieval of the ID
	 */
	public String getIDByUserID(String userID) throws DetailException
	{
		return ((PartyIDXmlMapper) mapperRegistry.getMapper(PartyID.class)).getIDByUserID(userID);
	}

	/**
	 * Retrieves user's ID from the database. ID is the result of the querying the database
	 * based on the object's ID.
	 * @param id object's ID
	 * @return id of the user in the database or {@code null} if user is not registered
	 * @throws DetailException if there is a database connectivity issue during retrieval of the ID
	 */
	public String getUserIDByID(String id) throws DetailException
	{
		return ((PartyIDXmlMapper) mapperRegistry.getMapper(PartyID.class)).getUserIDByID(id);
	}

	@Override
	public String getUserID(String username) throws DetailException
	{
		return mapperRegistry.getMapper(RutaUser.class).getUserID(username);
	}

	@Override
	public boolean checkUser(String partyID) throws DetailException
	{
		if (((PartyXmlMapper) mapperRegistry.getMapper(PartyType.class)).findByUserId(partyID) != null)
			return true;
		else
			return false;
	}

	/**
	 * Updates the object in the data store.
	 * @param username user's username
	 * @param object object to be updated
	 * @param transaction data store transaction which update is part of
	 * @return object's ID
	 * @throws DetailException if object could not be updated
	 */
	protected String update(String username, T object, DSTransaction transaction) throws DetailException
	{
		return insert(username, object, transaction);
	}

	@Override
	public String update(String username, T object) throws DetailException
	{
		return insert(username, object);
	}

	@Override
	public <U> List<U> findGeneric(CatalogueSearchCriterion criterion) throws DetailException
	{
		Collection coll = null;
		ArrayList<U> searchResult = new ArrayList<U>();
		try
		{
			coll = getCollection();
			if(coll == null)
				throw new DatabaseException("Collection does not exist.");
			final String uri = getAbsoluteRutaCollectionPath();
			final XQueryService queryService = (XQueryService) coll.getService("XQueryService", "1.0");
			logger.info("Started query of the " + uri);
			queryService.setProperty("indent", "yes");
			String queryName = getSearchQueryName(); //MMM: based on the type of query proper query name should be put in queryName
			String query = null; // search query
			//loading the .xq query file from the database
			if (queryName != null)
				query = prepareQuery(queryName, criterion);

			//			final File queryFile = null;
			if(/*queryFile != null ||*/ query != null)
			{
				/*				final StringBuilder queryBuilder = new StringBuilder();
				fileContents(queryFile, queryBuilder);
				CompiledExpression compiled = queryService.compile(queryBuilder.toString());*/
				CompiledExpression compiled = queryService.compile(query);
				final ResourceSet results = queryService.execute(compiled);
				final ResourceIterator iterator = results.getIterator();
				while(iterator.hasMoreResources())
				{
					Resource resource = null;
					try
					{
						resource = iterator.nextResource();
						//System.out.println((String) resource.getContent());
						//no lookup in the in-memory objects cause the resource might be any XML element
						@SuppressWarnings("unchecked")
						U res = (U) unmarshalFromXML((String) resource.getContent());
						searchResult.add(res);
					}
					finally
					{
						if(resource != null)
							((EXistResource)resource).freeResources();
					}
				}
				logger.info("Finished query of the " + uri);
				return searchResult;
			}
			else
				throw new DatabaseException("Could not process the query. Query file could not be opened.");
		}
		catch(XMLDBException e)
		{
			logger.error(e.getMessage(), e);
			throw new DatabaseException("Could not process the query. There is an error in the process of its execution.", e);
		}
		finally
		{
			if(coll != null)
			{
				try
				{
					coll.close();
				}
				catch(XMLDBException e)
				{
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	/**Gets the name of the query document with the {@code .xq} extension for particular {@code XMLMapper} subclass.
	 * @return filename of the query document or {@code null} if it is not defined for a subclass
	 */
	protected String getSearchQueryName() { return null; };

	/**Appends the content of a text file to the String Builder.
	 * @param f The file to read the contents of
	 * @param builder The <code>StringBuilder</code> to append the file contents to
	 */
	private static void fileContents(final File f, final StringBuilder builder)
	{//MM: should be implemented with the Scanner
		Reader reader = null;
		try
		{
			reader = new FileReader(f);
			final char buf[] = new char[1024];
			int read = -1;
			while((read = reader.read(buf)) != -1)
				builder.append(buf, 0, read);
		}
		catch(final IOException ioe)
		{
			logger.error(ioe.getMessage(), ioe);
		}
		finally
		{
			if(reader != null)
			{
				try
				{
					reader.close();
				}
				catch(final IOException ioe)
				{
					logger.warn(ioe.getMessage(), ioe);
				}
			}
		}
	}

	/**Gets the list of all document in the subclass's collection.
	 * @return list as array of {@code String}s
	 * @throws DetailException if collection or resources' list could not be retrieved
	 */
	protected String[] listAllDocumentIDs() throws DetailException
	{
		//		List<String> ids = null;
		try
		{
			Collection collection = getCollection();
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			String list[] = collection.listResources();
			//			ids = Arrays.asList(list);
			return list;
		}
		catch (XMLDBException e)
		{
			logger.error("List of all document ids from the collection "+ getCollectionPath() +" could not be retrieved.");
			logger.error("Exception is", e);
			throw new DetailException("List of all document ids from the collection could not be retrieved.", e);
		}
	}

	/*	@Override
	public void update(String username, Object object) throws Exception
	{
		String id = getID(username);
		update(object, id);
	}*/

	@Override
	public void checkDatastoreSetup() throws DetailException
	{
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final String jarDirectoryName = "xquery";
		final InputStream listInputStream = classLoader.getResourceAsStream(jarDirectoryName + "/xquery-list.txt");
		final String tempDirectoryName = System.getProperty("user.dir", ".") + "/temp";
		try
		{
			Files.createDirectories(Paths.get(tempDirectoryName));
			try(final Scanner listScanner = new Scanner(listInputStream))
			{
				connector.connectToDatabase();
				while(listScanner.hasNextLine())
				{
					final String filename = listScanner.nextLine();
					File tempFile = null;
					try(final InputStream fileStream = classLoader.getResourceAsStream(jarDirectoryName + "/" + filename))
					{
						final Path tempPath = Paths.get(tempDirectoryName + "/" + filename);
						Files.copy(fileStream, tempPath, StandardCopyOption.REPLACE_EXISTING);
						tempFile = tempPath.toFile();
						ExistConnector.storeXQueryDocument(tempFile);
					}
					finally
					{
						if(tempFile != null)
							tempFile.delete();
					}
				}
			}
/*			finally
			{
				if(connector != null)
					connector.shutdownDatabase();
			}*/
		}
		catch(Exception e)
		{
			throw new DatabaseException("Database could not be successfully checked!", e.getCause());
		}
	}

}
