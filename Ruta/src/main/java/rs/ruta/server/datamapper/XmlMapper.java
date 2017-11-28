package rs.ruta.server.datamapper;

import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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
import rs.ruta.common.RutaVersion;
import rs.ruta.common.SearchCriterion;
import rs.ruta.server.DataManipulationException;
import rs.ruta.server.DatabaseException;
import rs.ruta.server.DetailException;
import rs.ruta.server.datamapper.DataMapper;

/**XmlMapper abstract class maps the domain model used in the Ruta System to the eXist database.
 * Each class of the domain model which objects are deposited and fetched from the database
 * have its own data mapper class derived from the XMLMapper.
 */
public abstract class XmlMapper<T> extends ExistConnector implements DataMapper<T, String>
{
	protected final static Logger logger = LoggerFactory.getLogger("rs.ruta.server.datamapper");

	public XmlMapper() throws DatabaseException
	{
		init();
	}

	private void init() throws DatabaseException
	{
		connectDatabase();
		checkCollection(getCollectionPath());
		checkCollection(getDeletedCollectionPath());
		checkCollection(getQueryPath());

		/*try
		{
			@SuppressWarnings("unchecked")
			final Class<Database> dbClass = (Class<Database>) Class.forName("org.exist.xmldb.DatabaseImpl");
			final Database database = dbClass.newInstance();
			database.setProperty("create-database", "true");
			DatabaseManager.registerDatabase(database);
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
		}*/
	}

	@Override
	public T find(String id) throws DetailException
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
//			logger.info("Starting retrival of " + col + "/" + document + ".");
			resource = (XMLResource) collection.getResource(document);
			if(resource != null)
			{
				//saveDocumentAsFile(resource, document);
				final Object result = resource.getContent();
				object = (T) unmarshalFromXML(result.toString());
//				logger.info("Document " + col + "/" + document + " retrieved.");
			}
			else
				logger.error("Document " + col + "/" + document + " not found.");

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

	@Override
	public RutaVersion findClientVersion() throws DetailException
	{
		return MapperRegistry.getMapper(RutaVersion.class).findClientVersion();
	}

	@Override
	public T findByUserId(String userID) throws DetailException
	{
		String id = getIDByUserID(userID);
		T searchResult = find(id);
		return searchResult;


/*		Collection coll = null;
		String searchResult = null;
		try
		{
			coll = getCollection();
			if(coll == null)
				throw new DatabaseException("Collection does not exist.");
			final String uri = getAbsoluteRutaCollectionPath();
			final XQueryService queryService = (XQueryService) coll.getService("XQueryService", "1.0");
			logger.info("Start of the query of the " + uri);
			queryService.setProperty("indent", "yes");

			String query = null; // search query
			//loading the .xq query file from the database
			//prepare query String adding criteria for the search from SearchCriterion object
			query = openDocument(getQueryPath(), getSearchQueryName());
			StringBuilder queryPath = new StringBuilder(getRelativeRutaCollectionPath()).append(getQueryPath()).
					append("/").append(id).append(getDocumentSufix());
			queryService.declareVariable("path", queryPath.toString());

//			final File queryFile = null;
			if(queryFile != null || query != null)
			{
				final StringBuilder queryBuilder = new StringBuilder();
				fileContents(queryFile, queryBuilder);
				CompiledExpression compiled = queryService.compile(queryBuilder.toString());
				CompiledExpression compiled = queryService.compile(query);
				final ResourceSet results = queryService.execute(compiled);
				final ResourceIterator iterator = results.getIterator();
				long resultsCount = results.getSize();
				if (resultsCount > 0) //not possible
					throw new DatabaseException("There has been an error in the process of the query exceution. Too many results.");
				while(iterator.hasMoreResources())
				{
					Resource resource = null;
					try
					{
						resource = iterator.nextResource();
						//System.out.println((String) resource.getContent());
						searchResult = (String) resource.getContent();
						searchResult = load((XMLResource) resource);
						if(searchResult == null) //resource is not whole document rather part of it
							searchResult = (T) unmarshalFromXML((String) resource.getContent());
					}
					finally
					{
						if(resource != null)
							((EXistResource)resource).freeResources();
					}
				}
				logger.info("Finished query of the " + uri);
				return find(searchResult);
			}
			else
				throw new DatabaseException("Could not process the query. Query file does not exist.");
		}
		catch(XMLDBException e)
		{
			logger.error(e.getMessage(), e);
			throw new DatabaseException("Could not process the query. There has been an error in the process of its exceution.", e);
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
		}*/
	}

	/**Loads resource in proper object. At the beggining checks if object is already in the memory.
	 * At the end puts the object in the memory.
	 * @param resource resource which contents are to be loaded in the object
	 * @return object or null if the resource is a result of a query
	 * @throws XMLDBException
	 * @throws DataManipulationException if object could not be unmarshalled from the xml
	 */
	protected T load(XMLResource resource) throws XMLDBException, DataManipulationException
	{
		String id = resource.getId();
		if(id == null)// resource is a result of a query and not whole document
			return null;
		else
		{
			T object = getLoadedObject(id);
			if(object == null)
			{
				String result = (String) resource.getContent();
				object = (T) unmarshalFromXML(result);
				putLoadedObject(id, object);
			}
			return object;
		}
	}

	/**Gets the object if it is loaded in the memory.
	 * @param id object's id
	 * @return loaded in-memory object or {@code null} if it is not loaded in the memory or particular subclass
	 * does not have a map of in-memory objects
	 */
	public T getLoadedObject(String id) { return null; }

	/**Puts the object in the memory. If particular subclass of {@link XmlMapper} has no map of in-memory objects
	 * this method does nothing.
	 * @param id object's id
	 * @param object object to be loaded in the memory
	 */
	public void putLoadedObject(String id, T object) { }

	@Override
	public ArrayList<T> findAll() throws DetailException
	{
		ArrayList<T> objects = new ArrayList<>();
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
					objects.add(object);
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
		return objects.size() != 0 ? objects : null;
	}

	@Override
	public ArrayList<T> findMany(SearchCriterion criterion) throws DetailException
	{
		Collection coll = null;
		ArrayList<T> searchResult = new ArrayList<T>();
		try
		{
			coll = getCollection();
			if(coll == null)
				throw new DatabaseException("Collection does not exist.");
			final String uri = getAbsoluteRutaCollectionPath();
			final XQueryService queryService = (XQueryService) coll.getService("XQueryService", "1.0");
			logger.info("Start of the query of the " + uri);
			queryService.setProperty("indent", "yes");
			String query = null; // search query
			//loading the .xq query file from the database
			//prepare query String adding criteria for the search from SearchCriterion object
			query = prepareQuery(criterion);

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
						T result = load((XMLResource) resource);
						if(result == null) //resource is not whole document rather part of it
							result = (T) unmarshalFromXML((String) resource.getContent());
						searchResult.add(result);
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
				throw new DatabaseException("Could not process the query. Query file does not exist.");
		}
		catch(XMLDBException e)
		{
			logger.error(e.getMessage(), e);
			throw new DatabaseException("Could not process the query. There is an error in the process of its exceution.", e);
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

	@Override
	public ArrayList<T> findManyID(SearchCriterion criterion) throws DetailException
		{
			Collection coll = null;
			ArrayList<T> searchResult = new ArrayList<T>();
			try
			{
				coll = getCollection();
				if(coll == null)
					throw new DatabaseException("Collection does not exist.");
				final String uri = getAbsoluteRutaCollectionPath();
				final XQueryService queryService = (XQueryService) coll.getService("XQueryService", "1.0");
				logger.info("Start of the query of the " + uri);
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
					final ResourceSet results = queryService.execute(compiled);
					final ResourceIterator iterator = results.getIterator();
					while(iterator.hasMoreResources())
					{
						Resource resource = null;
						try
						{
							resource = iterator.nextResource();
							T res = find(trimID((String) resource.getContent()));
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
					throw new DatabaseException("Could not process the query. Query file does not exist.");
			}
			catch(XMLDBException e)
			{
				logger.error(e.getMessage(), e);
				throw new DatabaseException("Could not process the query. There is an error in the process of its exceution.", e);
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

	/**Populates query string with search keywords from the {@link SearchCriterion} object. Query to be
	 * populated is defined in the subclass of {@code XmlMapper}.
	 * @param criterion defines the search criterion
	 * @return query as {@code String} ready for execution by xQuery processor or {@code null} if
	 * query file does not exist
	 * @throws DatabaseException if collection or query file could not be opened
	 */
	public String prepareQuery(SearchCriterion criterion) throws DatabaseException { return null; }

	/**Populates query string with search keywords from the {@link SearchCriterion} object. Name of the query
	 * to be populated is sent as a argument.
	 * @param queryName query's name
	 * @param criterion defines the search criterion
	 * @return query as {@code String} ready for execution by xQuery processor or {@code null} if
	 * query file does not exist
	 * @throws DatabaseException if collection or query file could not be opened
	 */
	public String prepareQuery(String queryName, SearchCriterion criterion) throws DatabaseException { return null; }

	/**Unmarshall object from XML document represented as string.
	 * @param xml XML as String object to be transformed to the object
	 * @return unmarshalled object
	 * @throws DataManipulationException if object could not be unmarshalled from the xml
	 */
	private T unmarshalFromXML(String xml) throws DataManipulationException
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

			//JAXBElement<?> jaxbElement = (JAXBElement<?>) u.unmarshal(new StringReader(xml));
			@SuppressWarnings("unchecked")
			JAXBElement<T> jaxbResult = (JAXBElement<T>) u.unmarshal(new StringReader(xml));
			result = ((JAXBElement<T>) jaxbResult).getValue();
			//MMM: maybe this if clause in superfluous
/*			if(jaxbResult instanceof JAXBElement)
				result = ((JAXBElement<T>)jaxbResult).getValue();
			else
				result = (T) jaxbResult;*/ // This is not used anymore ???
		}
		catch(JAXBException e)
		{
			logger.error("Exception is ", e);
			throw new DataManipulationException("The object could not be unmarshalled from the XML document.", e);
		}

		//return jaxbElement.getValue();
		return result;
	}

	abstract public Class<?> getObjectClass();

	/**Gets the name of the package which object's class belongs. The object and class in question are ones that
	 * <code>XmlMapper</code> subclass is mapping to the XML.
	 * @return name of the package
	 */
	abstract public String getObjectPackageName();

	@Override
	public String insert(String username, T object, DSTransaction transaction) throws DetailException
	{
		Collection collection = null;
		String id = null;
		try
		{
			collection = getCollection();
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			id = createID(collection);
			insert(collection, object, (String)id, (ExistTransaction) transaction);
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
				logger.error("Exception is ", e);
			}
		}
		return id;
	}

	/**Inserts object in the arbritary collection. Path of that collection is passed as <code>String</code>
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
			id = createID(collection);
			insert(collection, object, (String)id, (ExistTransaction) transaction);
			return id;
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
				logger.error("Exception is ", e);
			}
		}
	}

	@Override
	public void insert(T object, String id, DSTransaction transaction) throws DetailException
	{
		Collection collection = null;
		try
		{
			collection = getCollection();
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			insert(collection, object, (String)id, (ExistTransaction)transaction);
		}
		catch(XMLDBException e)
		{
			throw new DatabaseException("The collection could not be retrieved.", e);
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
				logger.error("Exception is ", e);
			}
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
			insertBinary(collection, file, (String)id, (ExistTransaction)transaction);
		}
		catch(XMLDBException e)
		{
			throw new DatabaseException("The collection could not be retrieved.", e);
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
				logger.error("Exception is ", e);
			}
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
			insertImage(collection, object, (String)id, (ExistTransaction)transaction);
		}
		catch(XMLDBException e)
		{
			throw new DatabaseException("The collection could not be retrieved.", e);
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
				logger.error("Exception is ", e);
			}
		}
	}


	/**Inserts object in the collection.
	 * @param collection collection in which the object is to be stored
	 * @param object object to be stored
	 * @param transaction transaction object
	 * @return id of the object, unique in the scope of the collection
	 * @throws DetailException
	 */
	private void insert(Collection collection, T object, String id, ExistTransaction transaction) throws DetailException
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
			logger.info("Start of storing of the document " + documentName + " to the location " + colPath);
			resource = collection.getResource(documentName);
			if(transaction != null && transaction.isEnabled() && resource != null) // it's update so copy resource to /deleted collection
				copyResourceToDeleted(resource, transaction, "UPDATE");
			else //first time insert
			{
				resource = collection.createResource(documentName, resourceType);
				if(transaction != null && transaction.isEnabled())
					transaction.appendOperation(colPath, documentName, "INSERT", null, null, null);
			}
			resource.setContent(xmlResult);
			collection.storeResource(resource);
			logger.info("Finished storing of the document " + documentName + " to the location " + colPath);
		}
		catch(XMLDBException e)
		{
			logger.error("Could not save the document " + documentName + " to the location " + colPath);
			logger.error("Exception is ", e);;
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
	private void insertBinary(Collection collection, File object, String id, ExistTransaction transaction) throws DetailException
	{
		Resource resource = null;
		String resourceType = "BinaryResource";
		String colPath = getCollectionPath(collection); // getCollectionPath() was OK before I put insertToCollection() method so the path cannot be get from the overriden method getCollectionPath()
//		colPath = ((CollectionImpl) collection).getPathURI().getCollectionPath();
		String xmlResult = null; //Storing object in the String
		String documentName = "";
		try
		{
			documentName = object.getName();
			logger.info("Start of storing of the document " + documentName + " to the location " + colPath);
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
	private void insertImage(Collection collection, Image object, String id, ExistTransaction transaction) throws DetailException
	{
		Resource resource = null;
		String resourceType = "BinaryResource";
		String colPath = getCollectionPath(collection); // getCollectionPath() was OK before I put insertToCollection() method so the path cannot be get from the overriden method getCollectionPath()
//		colPath = ((CollectionImpl) collection).getPathURI().getCollectionPath();
		String xmlResult = null; //Storing object in the String
		String documentName = "";
		try
		{
			documentName = createID();
			logger.info("Start of storing of the document " + documentName + " to the location " + colPath);
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

	/**Gets the collection from the database as a database admin. Retrieved collection is defined
	 * in the subclass of the <code>XmlMapper</code>.
	 * @return a <code>Collection</code> instance for the requested collection or <code>null</code> if the collection could not be found
	 * @throws XMLDBException if there was an database connectivity issue
	 */
	public Collection getCollection() throws XMLDBException
	{
		return DatabaseManager.getCollection(getAbsoluteRutaCollectionPath() + getCollectionPath(),
				DatabaseAdmin.getUsername(), DatabaseAdmin.getPassword());
	}

	/**Gets the collection from the database as a database admin. Path to retrieved collection
	 * is passed as a argument.
	 * @param collectionPath relative path to the collection
	 * @return a <code>Collection</code> instance for the requested collection or <code>null</code> if the collection could not be found
	 * @throws XMLDBException if there was an database connectivity issue
	 */
	public Collection getCollection(String collectionPath) throws XMLDBException
	{
		return DatabaseManager.getCollection(getAbsoluteRutaCollectionPath() + collectionPath, DatabaseAdmin.getUsername(), DatabaseAdmin.getPassword());
	}

	/**Gets the collection from the database as a specified user. Collection that is retrieved
	 * is defined in the subclasses of the <code>XmlMapper</code>.
	 * @return a <code>Collection</code> instance for the requested collection or <code>null</code> if the collection could not be found
	 * @throws XMLDBException if there was an database connectivity issue
	 */
	public Collection getCollection(String username, String password) throws XMLDBException
	{
		return DatabaseManager.getCollection(getAbsoluteRutaCollectionPath() + getCollectionPath(), username, password);
	}

	/**Gets the base collection where are placed deleted documents as a database admin.
	 * Retrieved base collection is defined in the subclasses of the <code>XmlMapper</code>.
	 * @return a <code>Collection</code> instance for the base deleted collection or <code>null</code> if the collection could not be found
	 * @throws XMLDBException if there was an database connectivity issue
	 */
	public Collection getDeletedBaseCollection() throws XMLDBException
	{
		return DatabaseManager.getCollection(getAbsoluteRutaCollectionPath() + getDeletedCollectionPath(),
				DatabaseAdmin.getUsername(), DatabaseAdmin.getPassword());
	}

	@Override
	public void delete(String id, DSTransaction transaction) throws DetailException
	{
		Resource resource = null;
		Collection collection = null;
		String documentName = (String) id + getDocumentSufix();
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
					copyResourceToDeleted(resource, (ExistTransaction) transaction, "DELETE");
				collection.removeResource(resource);
				logger.info("Finished deletion of the document " + documentName + " from the location " + getCollectionPath());
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

	/**Copies resource representing original xml document to the pertinent subcollection of /deleted collection.
	 * @param resource resource representing the original document to be copied
	 * @throws DetailException if resource cannot be copied due to database connection issues.
	 */
	private void copyResourceToDeleted(Resource resource, ExistTransaction transaction, String operation) throws DetailException
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

			deletedCollection = getOrCreateCollection(deletedPath);
			if(deletedCollection == null)
				throw new DatabaseException("Collection does not exist.");
			deletedDocumentName = originalDocumentName + "-" + String.valueOf(deletedCollection.getResourceCount()) + getDocumentSufix();
			logger.info("Start copying the document " + originalDocumentName + ".xml" + " from " + getCollectionPath() +
					" to " + deletedPath + "/" + deletedDocumentName);

			//add operation's information to the transaction
			if(transaction != null && transaction.isEnabled())
				transaction.appendOperation(getCollectionPath(), originalDocumentName + ".xml", operation,
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

	/**Opens document from the database collection which path and name are passed as the arguments.
	 * @param collectionPath path of the collection in which the document resides
	 * @param documentName document name
	 * @return <code>String</code> representing the contents of the document or <code>null</code>
	 * if the document does not exist
	 * @throws DatabaseException if collection or document could not ber opened
	 */
	public String openDocument(String collectionPath, String documentName) throws DatabaseException
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

	/**Saves the document with specified document name to the specified collection. Document is passed to the method as
	 * a {@code String}. If document does not exist it will be created. If exists its contents will be replaced.
	 * @param collectionPath path of the collection
	 * @param documentName document name
	 * @param document document as {@code String}
	 * @throws DatabaseException if document could not be saved to the collection
	 */
	public void saveDocument(String collectionPath, String documentName, String document) throws DatabaseException
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
			try
			{
				if(collection != null)
					collection.close();
			} catch (XMLDBException e)
			{
				logger.error("Exception is ", e);
			}
		}
	}

	/**Moves the document from one collection to the other.
	 * @param destinationCollectionPath relative path of the destionation collection
	 * @param destinationDocumentName name of the destionation document
	 * @param sourceCollectionPath relative path of the source collection
	 * @param sourceDocumentName name of the source document
	 * @throws DatabaseException if document could not be moved to a new location
	 */
	public void moveDocument(String destinationCollectionPath, String destinationDocumentName, String sourceCollectionPath,
			String sourceDocumentName) throws DatabaseException
	{
		copyDocument(destinationCollectionPath, destinationDocumentName, sourceCollectionPath, sourceDocumentName);
		deleteDocument(sourceCollectionPath, sourceDocumentName);
	}

	/**Copies the document from one collection to the other.
	 * @param destinationCollectionPath relative path of the destionation collection
	 * @param destinationDocumentName name of the destionation document
	 * @param sourceCollectionPath relative path of the source collection
	 * @param sourceDocumentName name of the source document
	 * @throws DatabaseException if source document does not exist or could not be moved to a new location
	 */
	public void copyDocument(String destinationCollectionPath, String destinationDocumentName, String sourceCollectionPath,
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

	/**Deletes the document from collection which path is passed as an argument. Method succeeds
	 * if the document can be deleted, or the document did not exist prior to the method invocation.
	 * @param collectionPath relative path of the document's collection
	 * @param documentName name of the document to be deleted
	 * @throws DatabaseException if document could not be deleted
	 */
	public void deleteDocument(String collectionPath, String documentName) throws DatabaseException
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
				logger.info("Document " + collectionPath + "/" + documentName + " does not exist.");
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

	/**Generates unique ID for objects in the scope of the collection that subclass of XMLMapper is manipulating with.
	 * @return unique ID
	 * @throws XMLDBException trown if id cannot be created due to database connectivity issues
	 */
	@Override
	public String createID() throws XMLDBException
	{
		String id = null;
		Collection collection = null;
		try
		{
			collection = getCollection();
			id = createID(collection);
			return id;
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
				logger.error("Exception is ", e);
			}
		}
	}

	/**Generates unique ID for objects in the scope of the passed collection. Generated ID cannot
	 * be the same as one of previously used but deleted IDs.
	 * @param collection collection in which scope unique ID is created
	 * @return unique ID
	 * @throws XMLDBException trown if id cannot be created due to database connectivity issues
	 */
	protected String createID(Collection collection) throws XMLDBException
	{
		String id;
		do
			id = trimID(collection.createId());
		while(wasIDDeleted(collection, id));
//		while(isIDPresentInDeleted(id)); // check deleted ids
		return id;
	}

	/**Checks if the id was used before by some of deleted objects. It checks if there is a subcolection in "deleted"
	 * collection. "Deleted" collection is consisting of deleted objects. Deleted objects of the same type and
	 * with the same ID are placed in the subcollection that has a name as the object's ID.
	 * @param id document's name that represents id in check
	 * @return true if id was used earlier, otherwise false
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
			try
			{
				if(deleted != null)
					deleted.close();
			}
			catch(XMLDBException e)
			{
				logger.error("Exception is ", e);
			}
		}
		return present;
	}

	/**Checks if the ID has been used before by some of deleted objects. It checks if there is a subcollection
	 * in "deleted" collection named the same as the ID. "Deleted" collection is consisting of deleted objects.
	 * Deleted objects of the same type and with the same ID are placed in the subcollection that has the same
	 * name as the object's ID.
	 * @param collection collection in which ID should be unique
	 * @param id id in check
	 * @return true if id has been used earlier, otherwise false
	 * @throws XMLDBException if collection cannot be retrieved from the database
	 */
	protected boolean wasIDDeleted(Collection collection, String id) throws XMLDBException
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
			try
			{
				if(deleted != null)
					deleted.close();
			}
			catch(XMLDBException e)
			{
				logger.error("Exception is ", e);
			}
		}
		return present;
	}

	/**Serializes object to an XML document respresented as String.
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

	/**Storing document as a file on the filesystem.
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

	@Override
	public void insertAll()
	{
		// TODO Auto-generated method stub

	}

	/**Returns relative path of the collection that stores documents. The path to the collection is defined in
	 * the subclass of the <code>XMLMapper</code>.
	 * @return <code>String</code> that represent relative path of the collection
	 */
	abstract public String getCollectionPath();

	/**Returns relative path of the collection. The path is relative to the base application collection.
	 * @param collection <code>Collection</code> instance
	 * @return <code>String</code> that represent relative path of the collection
	 */
	private String getCollectionPath(Collection collection)
	{
		return ((CollectionImpl) collection).getPathURI().getCollectionPath().replaceFirst(getRelativeRutaCollectionPath(), "");
	}

	//MMM: Should be @Deprecated
	/**Returns relative path of the base collection in which are placed deleted documents. The path to the
	 * <code>/deleted</code> collection is defined in the subclass of the <code>XMLMapper</code>. Retrived path
	 * respresents base collection that stores deleted documents for particular subclass.
	 * @return <code>String</code> that represent relative path of the deleted collection
	 */
	public String getDeletedCollectionPath(){ return getDeletedCollectionPath(getCollectionPath()); }

	/**Returns relative path (to the root application collection) of the base collection in which are placed
	 * deleted documents from the collection which relative path is passed as argument. The path to the
	 * <code>/deleted</code> collection is defined the <code>ExistConnector</code> class.
	 * @return String that represent relative path of the deleted collection
	 */
	public String getDeletedCollectionPath(String collectionPath)
	{
		StringBuilder del = new StringBuilder();
		del.append(getDeletedPath());
		del.append(collectionPath.replaceFirst(getRelativeRutaCollectionPath(), ""));
		return del.toString();
	}

	/**Returns relative path of the subcollection of the base collection in which are placed deleted documents.
	 * @param subPath part of the collection path that defines the subcollection of the base collection
	 * It starts with the <code>"/"</code> character.
	 * @return <code>String</code> that represent relative path of the deleted collection
	 */
	private String getDeletedSubcollectionPath(String subPath)
	{
		return subPath == null ? getDeletedCollectionPath() : getDeletedCollectionPath() + subPath;
	}

	protected abstract JAXBElement<T> getJAXBElement(T object);

	/**Gets instance of {@link JAXBContext} for particular subclass of {@code XMLMapper}. <code>JAXBContext</code>
	 * is instatiated on the base of the class's package name. To be able to instantiate {@code JAXBContext} object
	 * with the name of the object's package passed as an argument, it is mandatory to implement method for creation of
	 * the object and method for instantiation of the {@link JAXBElement} instance in {@link ObjectFactory} class,
	 * as is to implement {@link XmlMapper#getObjectPackageName}.
	 * @return <code>JAXBContext</code> object
	 * @throws JAXBException if <code>JAXBContext</code> object could not be instatiated
	 */
	protected JAXBContext getJAXBContext() throws JAXBException
	{
		return JAXBContext.newInstance(getObjectPackageName());
		//return JAXBContext.newInstance(getObjectClass()); this is not working when querying the database
	}

	/**Gets the ID of the object  determined by the user's username.
	 * @param username user'username
	 * @return object's ID
	 * @throws DetailException if user is not registered, ID could not be retrieved or database connectivity issues
	 */
	public String getID(String username) throws DetailException
	{
		return ((UserXmlMapper) MapperRegistry.getMapper(User.class)).getID(username);
	}

	@Override
	public String getIDByUserID(String userID) throws DetailException
	{
		return MapperRegistry.getMapper(PartyID.class).getIDByUserID(userID);
	}

	@Override
	public String getUserID(String username) throws DetailException
	{
		return MapperRegistry.getMapper(User.class).getUserID(username);
	}

	@Override
	public String update(String username, T object, DSTransaction transaction) throws DetailException
	{
		String id = getID(username);
		insert(object, id, transaction);
		return id;
	}

	@Override
	public void update(T object, String id, DSTransaction transaction) throws DetailException
	{
		insert(object, id, transaction);
	}

	@Override
	public <U> List<U> findGeneric(SearchCriterion criterion) throws DetailException
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
			logger.info("Start of the query of the " + uri);
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
			throw new DatabaseException("Could not process the query. There is an error in the process of its exceution.", e);
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
	public String getSearchQueryName() { return null; };

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

	public String[] listAllDocumentIDs() throws DetailException
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

	/**Clears in memory object in {@link XmlMapper} subclass.
	 */
	protected void clearLoadedObjects() { }

	/**Clears all in memory objects in all {@link XmlMapper} subclasses.
	 * @throws DetailException due to database connetivity issues
	 */
	public void clearInMemoryObjects() throws DetailException
	{
		((PartyXmlMapper) MapperRegistry.getMapper(PartyType.class)).clearLoadedObjects();
		((CatalogueXmlMapper) MapperRegistry.getMapper(CatalogueType.class)).clearLoadedObjects();
		((CatalogueDeletionXmlMapper) MapperRegistry.getMapper(CatalogueDeletionType.class)).clearLoadedObjects();
	}

}
