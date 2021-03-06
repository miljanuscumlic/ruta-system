package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.jetty.server.Authentication.User;
import org.exist.xmldb.EXistResource;
import org.exist.xmldb.XQueryService;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;

import rs.ruta.common.PartyID;

public class PartyIDXmlMapper extends XmlMapper<PartyID>
{
	final private static String collectionPath = "/system/party-id";
	final private static String objectPackageName = "rs.ruta.common";
	final private static String queryNameSearchID = "search-id.xq";
	final private static String queryNameSearchPartyID = "search-party-id.xq";

	public PartyIDXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<?> getObjectClass() { return PartyID.class; }

	@Override
	protected String getObjectPackageName() { return objectPackageName; }

	@Override
	protected String getCollectionPath() { return collectionPath; }

	@Override
	protected PartyID getCachedObject(String id) { return null; }

	@Override
	protected String getSearchQueryName()
	{
		return queryNameSearchID;
	}

	@Override
	protected JAXBElement<PartyID> getJAXBElement(PartyID object)
	{
/*		QName _QNAME = new QName("http://www.ruta.rs/ns/services", "PartyID");
		JAXBElement<PartyID> jaxbElement = new JAXBElement<PartyID> (_QNAME, PartyID.class,  object);
		return jaxbElement;*/
		 return new rs.ruta.common.ObjectFactory().createPartyID(object);
	}

	@Override
	protected String doPrepareAndGetID(PartyID object, String username, DSTransaction transaction)
			throws DetailException //MMM: exception not thrown with the UUID implementation
	{
		String id = object.getPartyID();
		if(id == null && username != null)
			id = ((UserXmlMapper) mapperRegistry.getMapper(rs.ruta.common.RutaUser.class)).findPartyID(username);

/*		if(username != null)
			id = ((UserXmlMapper) mapperRegistry.getMapper(rs.ruta.common.RutaUser.class)).findPartyID(username);
		if(id == null)
			id = createID();*/
		return id;
	}

	@Override
	public String getIDByUserID(String userID) throws DatabaseException
	{
		Collection coll = null;
		String searchResult = null;
		try
		{
			coll = getCollection();
			if(coll == null)
				throw new DatabaseException("Collection does not exist.");
			final String uri = getAbsoluteRutaCollectionPath();
			final XQueryService queryService = (XQueryService) coll.getService("XQueryService", "1.0");
			logger.info("Started query of the " + uri + getCollectionPath());
			queryService.setProperty("indent", "no");

			String query = null; // search query
			//loading and preparing the .xq query file from the database
			query = openXmlDocument(getQueryPath(), getSearchQueryName());
			StringBuilder queryPath = new StringBuilder(getRelativeRutaCollectionPath()).append(collectionPath).
					append("/").append(userID).append(getDocumentSufix());
			queryService.declareVariable("path", queryPath.toString());

//			final File queryFile = null;
			if(/*queryFile != null ||*/ query != null)
			{
/*				final StringBuilder queryBuilder = new StringBuilder();
				fileContents(queryFile, queryBuilder);
				CompiledExpression compiled = queryService.compile(queryBuilder.toString());*/
				CompiledExpression compiled = queryService.compile(query);
				final ResourceSet results = queryService.execute(compiled);
				final ResourceIterator iterator = results.getIterator();
				long resultCount = results.getSize();
				while(iterator.hasMoreResources())
				{
					Resource resource = null;
					try
					{
						resource = iterator.nextResource();
						searchResult = (String) resource.getContent();
					}
					finally
					{
						if(resource != null)
							((EXistResource)resource).freeResources();
					}
				}
				logger.info("Finished query of the " + uri + getCollectionPath());
				return searchResult;
			}
			else
				throw new DatabaseException("Could not process the query. Query file does not exist.");
		}
		catch(XMLDBException e)
		{
			logger.error(e.getMessage(), e);
			throw new DatabaseException("Could not process the query. There has been an error in the process of its execution.", e);
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
	public String getUserIDByID(String id) throws DetailException
	{
		Collection coll = null;
		String searchResult = null;
		try
		{
			coll = getCollection();
			if(coll == null)
				throw new DatabaseException("Collection does not exist.");
			final String uri = getAbsoluteRutaCollectionPath();
			final XQueryService queryService = (XQueryService) coll.getService("XQueryService", "1.0");
			logger.info("Started query for the user by its ID.");
			queryService.setProperty("indent", "no");

			String query = null; // search query
			//loading and preparing the .xq query file from the database
			query = openXmlDocument(getQueryPath(), queryNameSearchPartyID);
			StringBuilder queryPath = new StringBuilder(getRelativeRutaCollectionPath()).append(collectionPath);
			queryService.declareVariable("path", queryPath.toString());
			queryService.declareVariable("id", id);

//			final File queryFile = null;
			if(/*queryFile != null ||*/ query != null)
			{
/*				final StringBuilder queryBuilder = new StringBuilder();
				fileContents(queryFile, queryBuilder);
				CompiledExpression compiled = queryService.compile(queryBuilder.toString());*/
				CompiledExpression compiled = queryService.compile(query);
				final ResourceSet results = queryService.execute(compiled);
				long resultCount = results.getSize();
				if(resultCount == 1)
				{
					final ResourceIterator iterator = results.getIterator();
					Resource resource = null;
					try
					{
						resource = iterator.nextResource();
						searchResult = (String) resource.getContent();
					}
					finally
					{
						if(resource != null)
							((EXistResource)resource).freeResources();
					}
				}
				else if(resultCount > 1)
					throw new DatabaseException("Critical error! There are more than one party that corespond to the same object ID.");
				logger.info("Finished query for the user by its ID.");
				return searchResult; // null for no results
			}
			else
				throw new DatabaseException("Could not process the query. Query file does not exist.");
		}
		catch(XMLDBException e)
		{
			logger.error(e.getMessage(), e);
			throw new DatabaseException("Could not process the query. There has been an error in the process of its execution.", e);
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
}
