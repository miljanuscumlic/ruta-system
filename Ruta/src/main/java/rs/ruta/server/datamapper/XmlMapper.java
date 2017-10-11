package rs.ruta.server.datamapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.exist.xmldb.EXistResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import rs.ruta.server.DataManipulationException;
import rs.ruta.server.DatabaseException;
import rs.ruta.server.DetailException;
import rs.ruta.server.datamapper.DataMapper;

/**XmlMapper abstract class maps the domain model used in the Ruta System to the eXist database.
 * Each class of the domain model which objects are deposited and fetched from the database
 * have its own data mapper class derived from the XMLMapper.
 */
public abstract class XmlMapper extends ExistConnector implements DataMapper
{
	protected final static Logger logger = LoggerFactory.getLogger("rs.ruta.server.datamapper");

	public XmlMapper() throws DatabaseException
	{
		init();
		checkCollection(getCollectionPath());
		checkCollection(getDeletedBaseCollectionPath());
	}

	private void init()
	{
		connectDatabase();
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
			logger.error("Exception is: ", e);
		}*/
	}

	@Override
	public Object find(String id) throws DetailException
	{
		String document = getDocumentPrefix() + id + getDocumentSufix();
		String col = getCollectionPath();
		Object result = null;
		Object object = null;
		Collection collection = null;
		XMLResource resource = null;
		try
		{
			collection = getCollection();
			logger.info("Starting retrival of " + col + "/" + document + ".");
			resource = (XMLResource) collection.getResource(document);
			if(resource != null)
			{
				//				saveDocumentAsFile(resource, document);
				result = resource.getContent();
				object = unmarshallFromXML(/*resource.getContent().toString()*/ result.toString());
				logger.info("Document " + document + " retrieved.");
			}
			else
				logger.error("Document: " + "/" + col + document + " not found.");
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
				logger.error("Exception is: ", e);;
			}
		}
		return object;
	}

	@Override
	public ArrayList<?> findAll() throws DetailException
	{
		ArrayList<Object> objects = new ArrayList<>();
		String col = getCollectionPath();
		Collection collection = null;
		Resource resource = null; //MMM: maybe to replace with XMLResource? - it works with String as contents
		try
		{
			collection = getCollection();
			int count = collection.getResourceCount(); //number of documents in the collection
			String[] resourceIDs = collection.listResources();
			String document = null;
			Object result = null;
			Object object = null;
			for(String id : resourceIDs)
			{
				document = getDocumentPrefix() + id;
				logger.info("Starting retrival of the document " + col + "/" + document + ".");
				resource = collection.getResource(document);
				if(resource != null)
				{
					//saveDocumentAsFile(resource, document);
					result = resource.getContent();
					object = unmarshallFromXML(result.toString());
					objects.add(object);
					logger.info("Document " + col + "/" + document + " retrieved.");
				}
				else
					logger.error("Document: " + "/" + col + document + " not found.");
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
				logger.error("Exception is: ", e);;
			}
		}
		return objects.size() != 0 ? objects : null;
	}

	@Override
	public Object find(Object object)
	{
		return null;
	}

	/**Unmarshall object from XML document represented as string.
	 * @param xml XML as String object to be transformed to the object
	 * @return unmarshalled object
	 * @throws DataManipulationException thrown when object could not be unmarshalled from the xml
	 */
	private Object unmarshallFromXML(String xml) throws DataManipulationException
	{
		JAXBElement<?> jaxbElement = null;
		Object result = null;
		try
		{
			JAXBContext jc = JAXBContext.newInstance(getObjectClass());
			Unmarshaller u = jc.createUnmarshaller();

			//jaxbElement = (JAXBElement<?>) u.unmarshal(new StringReader(xml));
			result = u.unmarshal(new StringReader(xml));
		}
		catch(JAXBException e)
		{
			logger.error("Exception is: ", e);
			logger.error("Exception is: ", e);
			throw new DataManipulationException("The object could not be unmarshalled from the XML document.", e);
		}

		//return jaxbElement.getValue();
		return result;
	}

	abstract public Class<?> getObjectClass();

	@Override
	public <T extends DSTransaction> Object insert(Object object, T transaction) throws DetailException
	{
		Collection collection = null;
		String id = null;
		try
		{
			collection = getCollection();
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
				logger.error("Exception is: ", e);
			}
		}
		return id;
	}

	@Override
	public <T extends DSTransaction> void insert(Object object, Object id, T transaction) throws DetailException
	{
		Collection collection = null;
		try
		{
			collection = getCollection();
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
				logger.error("Exception is: ", e);
			}
		}
	}

	/**Inserts object in the collection.
	 * @param collection collection in which the object is to be stored
	 * @param object object to be stored
	 * @return id of the object, unique in the scope of the collection
	 * @throws DetailException
	 */
	private void insert(Collection collection, Object object, String id, ExistTransaction transaction) throws DetailException
	{
		Resource resource = null;
		String resourceType = "XMLResource"; // else "BinaryResource"
		String colPath = getCollectionPath();
		String xmlResult = null; //Storing object in the String
		String documentName = "";
		try
		{
			documentName = getDocumentPrefix() + id + getDocumentSufix();
			xmlResult = marshallToXML(object);
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

	/**If id has a dot character in itself this method deletes the dot and all characters after it.
	 * @param id id that is the subject of trimming
	 * @return trimmed id
	 */
	protected String trimID(String id)
	{
		String trimmed = id.replaceFirst("^(.+)\\..+", "$1");
		return trimmed;
	}

	/**Gets the collection from the database as a database admin. Path to retrieved collection
	 * is passed as a argument.
	 * @param collectionPath relative path to the collection
	 * @return requested collection object
	 * @throws XMLDBException thrown if the collection cannot be retrieved
	 */
	public Collection getCollection(String collectionPath) throws XMLDBException
	{
		return DatabaseManager.getCollection(getUri() + collectionPath, DatabaseAdmin.getUsername(), DatabaseAdmin.getPassword());
	}

	/**Gets the collection from the database as a database admin. Retrieved collection
	 * is defined in the subclass of the XmlMapper.
	 * @return requested collection object
	 * @throws XMLDBException thrown if the collection cannot be retrieved
	 */
	public Collection getCollection() throws XMLDBException
	{
		return DatabaseManager.getCollection(getUri() + getCollectionPath(), DatabaseAdmin.getUsername(), DatabaseAdmin.getPassword());
	}

	/**Gets the collection from the database as a specified user. Collection that is retrieved
	 * is defined in the subclasses of the XmlMapper.
	 * @return requested collection object
	 * @throws XMLDBException thrown if the collection cannot be retrieved
	 */
	public Collection getCollection(String username, String password) throws XMLDBException
	{
		return DatabaseManager.getCollection(getUri() + getCollectionPath(), username, password);
	}

	/**Gets the base collection where are placed deleted documents from the database as a database admin.
	 * Retrieved base collection is defined in the subclass of the XmlMapper.
	 * @return requested collection object
	 * @throws XMLDBException thrown if the collection cannot be retrieved
	 */
	public Collection getDeletedBaseCollection() throws XMLDBException
	{
		return DatabaseManager.getCollection(getUri() + getDeletedBaseCollectionPath(), DatabaseAdmin.getUsername(), DatabaseAdmin.getPassword());
	}

	/**Gets the collection where are placed deleted documents from the database as a database admin.
	 * Retrieved base collection is defined in the subclass of the XmlMapper. Passed string parameter
	 * defines subcollection of it.
	 * @param subcollection path to be appended on the base deleted collection path
	 * @return requested collection object
	 * @throws XMLDBException thrown if the collection cannot be retrieved
	 */
	public Collection getDeletedCollection(String subPath) throws XMLDBException
	{
		return getOrCreateCollection(getUri(), subPath, DatabaseAdmin.getUsername(), DatabaseAdmin.getPassword());
	}

	@Override
	public <T extends DSTransaction> void delete(Object id, T transaction) throws DetailException
	{
		Resource resource = null;
		Collection collection = null;
		String documentName = (String) id + getDocumentSufix();
		try
		{
			collection = getCollection();
			resource = collection.getResource(documentName);
			if(resource == null)
			{
				logger.error("Document {} does not exist!", getCollectionPath() + "/" + documentName);
				throw new DatabaseException("The object could not be deleted.");
			}
			else
			{
				logger.info("Started deletion of the document " + documentName + " from the location " + getCollectionPath());
				if(transaction != null && transaction.isEnabled()) // no copying when there is not transaction
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
				logger.error("Exception is: ", e);;
			}
		}
	}

	/**Copies resource representing original xml document to the pertinent subcollection of /deleted collection.
	 * @param resource resource representing the original document to be copied
	 * @throws DatabaseException thrown if resource cannot be copied due to database connection issues.
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
			deletedPath = getDeletedCollectionPath("/" + originalDocumentName);

			deletedCollection = getDeletedCollection(deletedPath);
			deletedDocumentName = originalDocumentName + "-" + String.valueOf(deletedCollection.getResourceCount()) + getDocumentSufix();
			logger.info("Start copying the document " + originalDocumentName + ".xml" + "from " + getCollectionPath() +
					" to " + deletedPath + "/" + deletedDocumentName);

			//add operation's information to the transaction
			if(transaction != null && transaction.isEnabled())
				transaction.appendOperation(getCollectionPath(), originalDocumentName + ".xml", operation,
						deletedPath, deletedDocumentName, null);

			//copy the resource
			deletedResource = deletedCollection.createResource(deletedDocumentName, resourceType);
			deletedResource.setContent(resource.getContent());
			deletedCollection.storeResource(deletedResource);
			logger.info("Finished copying of the document " + originalDocumentName + ".xml" + "from " + getCollectionPath() +
					" to " + deletedPath + "/" + deletedDocumentName);
		}
		catch(XMLDBException e)
		{
			logger.error("Could not copy the document " + originalDocumentName + ".xml" + "from " + getCollectionPath() +
					" to " + deletedPath + "/" + deletedDocumentName);
			logger.error("Exception is: ", e);;
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
				logger.error("Exception is: ", e);;
			}
		}
	}

	/**Moves the document from one collection to the other.
	 * @param destinationCollectionPath relative path of the destionation collection
	 * @param destinationDocumentName name of the destionation document
	 * @param sourceCollectionPath relative path of the source collection
	 * @param sourceDocumentName name of the source document
	 * @throws DatabaseException thrown if document could not be moved to a new location
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
	 * @throws DatabaseException thrown if source document does not exist or could not be moved to a new location
	 */
	public void copyDocument(String destinationCollectionPath, String destinationDocumentName, String sourceCollectionPath,
			String sourceDocumentName) throws DatabaseException
	{
		Collection sourceCollection = null;
		Collection destinationCollection = null;
		try
		{
			sourceCollection = getCollection(sourceCollectionPath);
			Resource sourceResource = sourceCollection.getResource(sourceDocumentName);
			if(sourceResource == null)
				throw new DatabaseException("Source document does not exist!");
			logger.info("Start copying of the document " + sourceCollectionPath + "/" + sourceDocumentName + " to " +
					destinationCollectionPath + "/" + destinationDocumentName);
			destinationCollection = getCollection(destinationCollectionPath);
			Resource destinationResource = destinationCollection.getResource(destinationDocumentName);
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
			logger.error("Exception is: ", e);;
			throw new DatabaseException("Source document could not be moved to the destination collection.", e);
		}
		finally
		{
			try
			{
				if(destinationCollection != null)
					destinationCollection.close();
				if(sourceCollection != null)
					sourceCollection.close();
			} catch (XMLDBException e)
			{
				logger.error("Exception is: ", e);
			}
		}
	}

	/**Deletes the document from collection which path is passed as an argument. Method succeeds
	 * if the document can be deleted, or the document did not exist prior to the method invocation.
	 * @param collectionPath relative path of the document's collection
	 * @param documentName name of the document to be deleted
	 * @throws DatabaseException thrown if document could not be deleted
	 */
	public void deleteDocument(String collectionPath, String documentName) throws DatabaseException
	{
		Collection collection = null;

		try
		{
			logger.info("Start deletion of the document " + collectionPath + "/" + documentName);
			collection = getCollection(collectionPath);
			Resource resource = collection.getResource(documentName);
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
				if(collection != null)
					collection.close();
			} catch (XMLDBException e)
			{
				logger.error("Exception is: ", e);
			}
		}
	}

	/**Generates unique ID for objects in the scope of the collection that subclass of XMLMapper is manipulating with.
	 * @return unique ID
	 * @throws XMLDBException trown if id cannot be created due to database connectivity issues
	 */
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
				logger.error("Exception is: ", e);
			}
		}
	}

	/**Generates unique ID for objects in the scope of the passed collection. Generated ID cannot
	 * be the same as one of previously used but deleted IDs.
	 * @param collection collection in which scope unique ID is created
	 * @return unique ID
	 * @throws XMLDBException trown if id cannot be created due to database connectivity issues
	 */
	public String createID(Collection collection) throws XMLDBException
	{
		String id;
		do
			id = trimID(collection.createId());
		while(isIDPresentInDeleted(id)); // check deleted ids
		return id;
	}

	/**Checks if the id was used before by some of deleted objects. It checks if there is a subcolection in "deleted"
	 * collection. "Deleted" collection is consisting of deleted objects. Deleted objects of the same type and
	 * with the same ID are placed in the subcollection that has a name as the object's ID.
	 * @param id collection's name that represents id in check
	 * @return true if id was used earlier, otherwise false
	 * @throws XMLDBException thrown if collection cannot be retrieved from the database
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
				logger.error("Exception is: ", e);
			}
		}
		return present;
	}

	/**Serializes object to an XML document respresented as String.
	 * @param object object to marshall
	 * @return XML as string
	 * @throws JAXBException
	 */
	protected String marshallToXML(Object object) throws DataManipulationException
	{
		StringWriter sw = null;
		try
		{
			JAXBContext jc = JAXBContext.newInstance(object.getClass());
			JAXBElement<?> element = (JAXBElement<?>) getJAXBElement(object);
			Marshaller m = jc.createMarshaller();

			//m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			//m.marshal(element, System.out);

			sw = new StringWriter();
			m.marshal(element, sw);
		}
		catch(JAXBException e)
		{
			logger.error("Exception is: ", e);
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
					writer.print(res.getContent().toString());
					writer.flush();
					logger.info("Document saved at " + file.getAbsolutePath());
				}
				catch(Exception e)
				{
					logger.error("Exception is: ", e);;
				}
			}
			else // BinaryResource
			{
				try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file)))
				{
					//MMM: This saves some extra bytes at the begining of the file, presumably belonging to the Object res.getContent()
					os.writeObject(res.getContent());
					os.flush();
					logger.info("Document saved at: " + file.getAbsolutePath());
				}
				catch(Exception e)
				{
					logger.error("Exception is: ", e);;
				}
			}
		} catch (XMLDBException e)
		{
			// TODO Auto-generated catch block
			logger.error("Exception is: ", e);;
		}
	}

	@Override
	public void insertAll()
	{
		// TODO Auto-generated method stub

	}

	/**Returns relative path of the collection.
	 * @return String that represent relative path of the collection
	 */
	abstract public String getCollectionPath();

	/**Returns relative path of the base collection in which are placed deleted documents.
	 * @return String that represent relative path of the deleted collection
	 */
	abstract public String getDeletedBaseCollectionPath();

	/**Returns relative path of the subcollection of the base collection in which are placed deleted documents.
	 * @param subPath part of the collection path that define the subcollection of the base collection
	 * @return String that represent relative path of the deleted collection
	 */
	public String getDeletedCollectionPath(String subPath)
	{
		return subPath== null ? getDeletedBaseCollectionPath() : getDeletedBaseCollectionPath() + subPath;
	}

	abstract public String getDocumentPrefix();

	protected abstract JAXBElement<?> getJAXBElement(Object object);

	/*Gets the ID of the object i.e. metadata UNIQUE_ID of the user. Object is represented with user's username.
	 */
	@Override
	public String getID(Object object) throws DetailException
	{
		return (String) MapperRegistry.getMapper(User.class).getID(object);
	}

	@Override
	public <T extends DSTransaction> void update(Object object, Object id, T transaction) throws DetailException
	{
		insert(object, id, transaction);
	}

	/*	@Override
	public void update(String username, Object object) throws Exception
	{
		String id = getID(username);
		update(object, id);
	}*/
}
