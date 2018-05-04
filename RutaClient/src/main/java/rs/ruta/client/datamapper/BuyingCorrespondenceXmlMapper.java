package rs.ruta.client.datamapper;

import javax.xml.bind.JAXBElement;

import rs.ruta.client.correspondence.BuyingCorrespondence;
import rs.ruta.client.correspondence.ObjectFactory;
import rs.ruta.common.datamapper.DSTransaction;
import rs.ruta.common.datamapper.DatastoreConnector;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.XmlMapper;

public class BuyingCorrespondenceXmlMapper extends XmlMapper<BuyingCorrespondence>
{
	private static String objectPackageName = "rs.ruta.client.correspondence";
	private static String collectionPath = "/correspondence/buying";
	private static String queryNameSearchBuyingCorrespondences = "search-buying-correspondences.xq";

	public BuyingCorrespondenceXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return BuyingCorrespondence.class;
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
	protected JAXBElement<BuyingCorrespondence> getJAXBElement(BuyingCorrespondence object)
	{
		return new ObjectFactory().createBuyingCorrespondence(object);
	}

	@Override
	protected String doPrepareAndGetID(BuyingCorrespondence corr, String username, DSTransaction transaction)
	{
		String id = corr.getIdValue();
		if(id == null) // should not happen
			id = createID();
		return id;
	}


}