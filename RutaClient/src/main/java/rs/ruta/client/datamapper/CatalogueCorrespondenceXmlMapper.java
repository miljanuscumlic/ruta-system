package rs.ruta.client.datamapper;

import javax.xml.bind.JAXBElement;

import rs.ruta.client.correspondence.CatalogueCorrespondence;
import rs.ruta.client.correspondence.ObjectFactory;
import rs.ruta.common.datamapper.DSTransaction;
import rs.ruta.common.datamapper.DatastoreConnector;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.XmlMapper;

public class CatalogueCorrespondenceXmlMapper extends XmlMapper<CatalogueCorrespondence>
{
	private static String objectPackageName = "rs.ruta.client.correspondence";
	private static String collectionPath = "/correspondence/catalogue";
	private static String queryNameSearchCatalogueCorrespondences = "search-catalogue-correspondences.xq";

	public CatalogueCorrespondenceXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return CatalogueCorrespondence.class;
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
	protected JAXBElement<CatalogueCorrespondence> getJAXBElement(CatalogueCorrespondence object)
	{
		return new ObjectFactory().createCatalogueCorrespondence(object);
	}

	@Override
	protected String doPrepareAndGetID(CatalogueCorrespondence corr, String username, DSTransaction transaction)
	{
		String id = corr.getIdValue();
		if(id == null) // should not happen
			id = createID();
		return id;
	}


}