package rs.ruta.client.datamapper;

import javax.xml.bind.JAXBElement;

import rs.ruta.client.correspondence.CreateCatalogueProcess;
import rs.ruta.client.correspondence.ObjectFactory;
import rs.ruta.common.datamapper.DSTransaction;
import rs.ruta.common.datamapper.DatastoreConnector;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.XmlMapper;

public class CreateCatalogueProcessXmlMapper extends XmlMapper<CreateCatalogueProcess>
{

	private static String objectPackageName = "rs.ruta.client.correspondence";
	private static String collectionPath = "/correspondence";
	private static String queryNameSearchCatalogueProcesses = "search-catalogue-processes.xq";

	public CreateCatalogueProcessXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return CreateCatalogueProcess.class;
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
	protected JAXBElement<CreateCatalogueProcess> getJAXBElement(CreateCatalogueProcess object)
	{
		return new ObjectFactory().createCreateCatalogueProcess(object);
	}

	@Override
	protected String doPrepareAndGetID(CreateCatalogueProcess process, String username, DSTransaction transaction)
	{
		String id = process.getIdValue();
		if(id == null) // should not happen
			id = createID();
		return id;
	}

}
