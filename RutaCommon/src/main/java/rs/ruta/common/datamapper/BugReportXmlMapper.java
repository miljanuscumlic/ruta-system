package rs.ruta.common.datamapper;

import java.util.ArrayList;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import org.exist.xmldb.EXistResource;
import org.exist.xmldb.LocalCollection;
import org.exist.xmldb.XQueryService;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import rs.ruta.common.BugReport;
import rs.ruta.common.BugReportSearchCriterion;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.SearchCriterion;

public class BugReportXmlMapper extends XmlMapper<BugReport>
{
	final private static String collectionPath = "/bug-report";
	final private static String objectPackageName = BugReport.class.getPackage().getName(); //"rs.ruta.common"; //MMM: if OK should be changed everywhere - it is better to retrieve package name through class object than set it as static String, in a case a class change its destinantion
	final private static String queryBugReport = "search-bug-report.xq"; //MMM: not implemented yet
	final private static String nextIdDocument = "nextId.xml";

	public BugReportXmlMapper(ExistConnector connector) throws DetailException
	{
		super(connector);
		if(openDocument(collectionPath, nextIdDocument) == null) //MMM: here should be checked whether there are some BugReports in the collection and if there are retrive last used Id
			saveDocument(collectionPath, nextIdDocument, "<nextId>0</nextId>");
	}

	@Override
	protected Class<?> getObjectClass() { return BugReport.class; }

	@Override
	protected String getObjectPackageName() { return objectPackageName; }

	@Override
	protected String getCollectionPath() { return collectionPath; }

	@Override
	protected JAXBElement<BugReport> getJAXBElement(BugReport object)
	{
		return new rs.ruta.common.ObjectFactory().createBugReport(object);
	}

	@Override
	protected String getSearchQueryName()
	{
		return queryBugReport;
	}

	/* (non-Javadoc) Creates unique id which is the increment of the last created id.
	 * @see rs.ruta.server.datamapper.XmlMapper#createID()
	 */
/*	@Override
	@Deprecated
	public synchronized String createID() throws XMLDBException
	{
		try
		{
			final String doc = openDocument(collectionPath, nextIdDocument);
			String ID = doc.replaceAll("<[/]?nextId>", "");
			String nextID = String.valueOf(Long.parseLong(ID) + 1);
			saveDocument(collectionPath, nextIdDocument, "<nextId>" + nextID + "</nextId>");
			return ID;
		}
		catch (DatabaseException e)
		{
			throw new XMLDBException(0, e.getMessage());
		}
	}*/

	/** Creates unique id which is the increment of the last created id. Passed {@code Collection}
	 * argument is never used because there is a {@code collectionPath} field wich is used instead.
	 * @see rs.ruta.common.datamapper.XmlMapper#createID()
	 */
	@Override
	public synchronized String createID(Collection collection) throws XMLDBException
	{
		try
		{
			final String doc = openDocument(collectionPath, nextIdDocument);
			String ID = doc.replaceAll("<[/]?nextId>", "");
			String nextID = String.valueOf(Long.parseLong(ID) + 1);
			saveDocument(collectionPath, nextIdDocument, "<nextId>" + nextID + "</nextId>");
			return ID;
		}
		catch (DatabaseException e)
		{
			throw new XMLDBException(0, e.getMessage());
		}
	}

	@Override
	protected String doPrepareAndGetID(Collection collection, BugReport bugReport, String username, DSTransaction transaction)
			throws DetailException
	{
		String id = null;
		XMLGregorianCalendar now = InstanceFactory.getDate();
		bugReport.setModified(now);
		id = bugReport.getId();
		if(id == null) // this is creation, not an update of the bug report
		{
			bugReport.setReported(now);
			try
			{
				id = createID(collection);
				bugReport.setId(id);
			}
			catch(XMLDBException e)
			{
				throw new DetailException("Could not create Bug Report ID", e.getCause());
			}
		}
		return id;
	}

	@Override
	protected String prepareQuery(SearchCriterion criterion, XQueryService queryService) throws DatabaseException
	{
		BugReportSearchCriterion sc = (BugReportSearchCriterion) criterion;

		String query = openDocument(getQueryPath(), queryBugReport);
		if(query == null)
			return query;
		try
		{
			StringBuilder queryPath = new StringBuilder(getRelativeRutaCollectionPath()).append(collectionPath);
			queryService.declareVariable("path", queryPath.toString());

			/* here should be put the code for binding the variables like this:
			 * if(partyName != null)
				queryService.declareVariable("party-name", partyName);*/
		}
		catch(XMLDBException e)
		{
			logger.error(e.getMessage(), e);
			throw new DatabaseException("Could not process the query. There has been an error in the process of its exceution.", e);
		}

		return query;
	}

/*	@Override
	@Deprecated
	public void insert(BugReport bugReport, String id, DSTransaction transaction) throws DetailException
	{
		bugReport.setReported(InstanceFactory.getDate());
		try
		{
			id =  createID();
			bugReport.setId(id);
			super.insert(bugReport, id, transaction);
		}
		catch (XMLDBException e)
		{
			throw new DetailException("", e.getCause());
		}
	}*/

	/** Finds all {@link BugReport}s in the database. All retrieved {@code BugReport}s are
	 * modified in a way that there are retained only some fields; all others are set to null. This list has
	 * a purpuose to inform the user of a brief summary of the reported bugs.
	 * @see rs.ruta.common.datamapper.XmlMapper#findAll()
	 */
	@Override
	public ArrayList<BugReport> findAll() throws DetailException //MMM: logic in this method will be replaced wiyh XmlMapper.findMany method
	{
		Collection collection = null;
		ArrayList<BugReport> searchResult = new ArrayList<BugReport>();
		try
		{
			collection = getCollection();
			if(collection == null)
				throw new DatabaseException("Collection does not exist.");
			final String uri = getAbsoluteRutaCollectionPath();
			final XQueryService queryService = (XQueryService) collection.getService("XQueryService", "1.0");
			logger.info("Start of the query of the " + uri);
			queryService.setProperty("indent", "yes");
			StringBuilder queryPath = new StringBuilder(getRelativeRutaCollectionPath()).append(collectionPath);
			queryService.declareVariable("path", queryPath.toString());
			String query = null; // search query
			//loading the .xq query file from the database
			//prepare query String adding criteria for the search from SearchCriterion object
			query = openDocument(getQueryPath(), queryBugReport);

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
/*						BugReport result = load((XMLResource) resource);
						if(result == null) //resource is not whole document rather part of it
							result = (BugReport) unmarshalFromXML((String) resource.getContent());*/

						BugReport result = (BugReport) unmarshalFromXML((String) resource.getContent());

						searchResult.add(result);
					}
					finally
					{
						if(resource != null)
							((EXistResource)resource).freeResources();
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
			throw new DatabaseException("Could not process the query. There is an error in the process of its exceution.", e);
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

		return searchResult.size() != 0 ? searchResult : null;
	}

}
