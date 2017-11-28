package rs.ruta.server.datamapper;

import javax.xml.bind.JAXBElement;

import org.exist.xmldb.LocalCollection;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

import rs.ruta.common.BugReport;
import rs.ruta.server.DatabaseException;
import rs.ruta.server.DetailException;

public class BugReportXmlMapper extends XmlMapper<BugReport>
{
	final private static String collectionPath = "/bug-report";
	final private static String objectPackageName = "rs.ruta.common";
	final private static String queryBugReport = "search-bug-report.xq"; //MMM: not implemented yet
	final private static String nextIdDocument = "nextId.xml";

	public BugReportXmlMapper() throws DatabaseException
	{
		super();
		if(openDocument(collectionPath, nextIdDocument) == null)
			saveDocument(collectionPath, nextIdDocument, "<nextId>0</nextId>");
	}

	@Override
	public Class<?> getObjectClass() { return BugReport.class; }

	@Override
	public String getObjectPackageName() { return objectPackageName; }

	@Override
	public String getCollectionPath() { return collectionPath; }

	@Override
	protected JAXBElement<BugReport> getJAXBElement(BugReport object)
	{
		return new rs.ruta.common.ObjectFactory().createBugReport(object);
	}

	@Override
	public String getSearchQueryName()
	{
		return queryBugReport;
	}

	/* (non-Javadoc) Creates unique id which is the increment of the last created id.
	 * @see rs.ruta.server.datamapper.XmlMapper#createID()
	 */
	@Override
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
	}
}
