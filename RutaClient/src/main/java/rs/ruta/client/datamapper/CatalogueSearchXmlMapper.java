package rs.ruta.client.datamapper;

import rs.ruta.common.datamapper.DSTransaction;
import rs.ruta.common.datamapper.DatastoreConnector;
import rs.ruta.common.datamapper.DetailException;

import javax.xml.bind.JAXBElement;

import rs.ruta.client.CatalogueSearch;
import rs.ruta.client.ObjectFactory;
import rs.ruta.client.Search;
import rs.ruta.common.datamapper.XmlMapper;

public class CatalogueSearchXmlMapper extends XmlMapper<CatalogueSearch>
{
	private static String objectPackageName = "rs.ruta.client";
	private static String collectionPath = "/search/catalogue";
	public CatalogueSearchXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return Search.class;
	}

	@Override
	protected String getObjectPackageName()
	{
		return objectPackageName;
	}

	@Override
	protected String getCollectionPath()
	{
		return collectionPath;
	}

	@Override
	protected JAXBElement<CatalogueSearch> getJAXBElement(CatalogueSearch object)
	{
		return new ObjectFactory().createCatalogueSearch(object);
	}

	@Override
	protected String doPrepareAndGetID(CatalogueSearch object, String username, DSTransaction transaction)
	{
		return object.getId();
	}

}
