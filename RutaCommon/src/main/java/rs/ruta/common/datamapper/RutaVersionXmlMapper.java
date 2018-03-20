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
import org.xmldb.api.modules.XMLResource;

import rs.ruta.common.RutaVersion;

public class RutaVersionXmlMapper extends XmlMapper<RutaVersion>
{
	final private static String collectionPath = "/version";
	final private static String objectPackageName = "rs.ruta.common";
	final private static String queryNameVersion = "search-version.xq";
	private RutaVersion loadedVersion = null;

	public RutaVersionXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<?> getObjectClass() { return RutaVersion.class; }

	@Override
	protected String getObjectPackageName() { return objectPackageName; }

	@Override
	protected String getCollectionPath() { return collectionPath; }

	@Override
	protected JAXBElement<RutaVersion> getJAXBElement(RutaVersion object)
	{
		return new rs.ruta.common.ObjectFactory().createRutaVersion(object);
	}

	@Override
	protected String getSearchQueryName()
	{
		return queryNameVersion;
	}

	@Override
	protected RutaVersion getCachedObject(String id)
	{
		return loadedVersion;
	}

	@Override
	protected void putCachedObject(String id, RutaVersion version)
	{
		loadedVersion = version;
	}

	@Override
	protected RutaVersion retrieve(String id) throws DetailException
	{
		Collection collection = null;
		RutaVersion searchResult = null;
		try
		{
			collection = getCollection();
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			final String uri = getAbsoluteRutaCollectionPath();
			final XQueryService queryService = (XQueryService) collection.getService("XQueryService", "1.0");
			logger.info("Started query of the " + uri);
			queryService.setProperty("indent", "yes");

			String query = null; // search query
			//loading and preparing the .xq query file from the database
			query = openXmlDocument(getQueryPath(), getSearchQueryName());
			StringBuilder queryPath = new StringBuilder(getRelativeRutaCollectionPath()).append(collectionPath);
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
				if(results.getSize() != 0)
				{
					while(iterator.hasMoreResources())
					{
						Resource resource = null;
						try
						{	//MMM: this should be tested whether it is doing the job how it should be done
							resource = iterator.nextResource();
							String result = (String) resource.getContent();
							searchResult = unmarshalFromXML(result);;
						}
						finally
						{
							if(resource != null)
								((EXistResource) resource).freeResources();
						}
					}
					logger.info("Finished query of the " + uri);
					return searchResult;
				}
				else
					throw new DetailException("There is no version object of the Ruta Client application.");
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
	}

}
