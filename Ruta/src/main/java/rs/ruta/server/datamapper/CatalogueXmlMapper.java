package rs.ruta.server.datamapper;

import java.io.StringReader;
import java.util.*;
import java.util.concurrent.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.ObjectFactory;
import rs.ruta.server.DatabaseException;
import rs.ruta.server.DetailException;

public class CatalogueXmlMapper extends XmlMapper<CatalogueType>
{
	final private static String docPrefix = ""; //"catalogue";
	final private static String collectionPath = "/catalogue";
	final private static String objectPackageName = "oasis.names.specification.ubl.schema.xsd.catalogue_21";
	//MMM: This map should be some kind of most recently used collection
	private Map<String, CatalogueType> loadedCatalogues;

	public CatalogueXmlMapper() throws DatabaseException
	{
		super();
		loadedCatalogues = new ConcurrentHashMap<String, CatalogueType>();
	}

	@Override
	public String getCollectionPath() { return collectionPath; }
	@Override
	public String getDocumentPrefix() { return docPrefix; }
	@Override
	public String getObjectPackageName() { return objectPackageName; }

	@Override
	public CatalogueType find(String id) throws DetailException
	{
		CatalogueType catalogue = loadedCatalogues.get(id);
		if(catalogue == null)
		{
			catalogue =  super.find(id);

/*			try
			{
				JAXBContext jc = JAXBContext.newInstance(CatalogueType.class);
				Unmarshaller u = jc.createUnmarshaller();

				// unmarshal instance document into a tree of Java content
				@SuppressWarnings("unchecked")
				JAXBElement<?> jaxbElement = (JAXBElement<?>) u.unmarshal(new StringReader(result.toString()));
				catalogue = jaxbElement.getValue();
			}
			catch (JAXBException e)
			{
				logger.error("Exception is ", e);;
			}*/
			if(catalogue != null)
				loadedCatalogues.put(id, catalogue);
		}
		return catalogue;
	}

	@Override
	public void insert(CatalogueType catalogue, String id, DSTransaction transaction) throws DetailException
	{
		super.insert(catalogue, id, transaction);
		loadedCatalogues.put(id, catalogue);
	}

	@Override
	public String insert(String username, CatalogueType object, DSTransaction transaction) throws DetailException
	{
		String id = getID(username);
		insert(object, id, transaction);
		return id;
	}

	@Override
	public void delete(String id, DSTransaction transaction) throws DetailException
	{
		super.delete(id, transaction);
		loadedCatalogues.remove(id);
	}

/*	@Override
	public void delete(Object ref, String id) throws DetailException
	{
		try
		{
			MapperRegistry.getMapper(CatalogueDeletionType.class).insert(ref);
		}
		catch (Exception e)
		{
			throw new DatabaseException("Database connectivity issue. Catalogue Deletion object could not be saved.");
		}
		delete(id);
	}*/

	@Override
	protected JAXBElement<CatalogueType> getJAXBElement(CatalogueType object)
	{
		JAXBElement<CatalogueType> jaxbElement = new ObjectFactory().createCatalogue(object);
		return jaxbElement;
	}

	@Override
	public Class<?> getObjectClass()
	{
		return CatalogueType.class;
	}

	@Override
	public CatalogueType getLoadedObject(String id)
	{
		return loadedCatalogues.get(id);
	}

	@Override
	public String update(String username, CatalogueType catalogue, DSTransaction transaction) throws DetailException
	{
//		insert(username, catalogue, transaction);
		String id = (String) super.update(username, catalogue, transaction);
		loadedCatalogues.put(id, catalogue);
		return id;
	}

	@Override
	public ArrayList<CatalogueType> findAll() throws DetailException
	{

		ArrayList<CatalogueType> catalogues = new ArrayList<>();
		String ids[] = listAllDocumentIDs();
		for(String id : ids)
		{
			final CatalogueType catalogue = find(trimID(id));
			if(catalogue != null)
				catalogues.add(catalogue);
		}
		return catalogues.size() > 0 ? catalogues : null;


/*		ArrayList<CatalogueType> catalogues;
		catalogues = (ArrayList<CatalogueType>) super.findAll();
		if (catalogues != null)
			for(CatalogueType c : catalogues)
				loadedCatalogues.put(getID(c), c);
		return catalogues;*/
	}

	@Override
	public String getSearchQueryName()
	{
		return queryNameSearchCatalogue;
	}

	@Override
	protected void clearLoadedObjects()
	{
		loadedCatalogues.clear();
	}

}