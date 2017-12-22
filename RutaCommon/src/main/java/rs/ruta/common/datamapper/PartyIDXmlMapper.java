package rs.ruta.common.datamapper;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

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

	public PartyIDXmlMapper() throws DetailException { super(); }

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
/*		QName _QNAME = new QName("urn:rs:ruta:services", "PartyID");
		JAXBElement<PartyID> jaxbElement = new JAXBElement<PartyID> (_QNAME, PartyID.class,  object);
		return jaxbElement;*/
		 return new rs.ruta.common.ObjectFactory().createPartyID(object);

	}

	@Override
	public String getIDByUserID(String userID) throws DetailException
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
			logger.info("Start of the query of the " + uri);
			queryService.setProperty("indent", "yes");

			String query = null; // search query
			//loading and preparing the .xq query file from the database
			query = openDocument(getQueryPath(), getSearchQueryName());
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
				long resultsCount = results.getSize();
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
				logger.info("Finished query of the " + uri);
				return searchResult;
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
		}
	}
}
