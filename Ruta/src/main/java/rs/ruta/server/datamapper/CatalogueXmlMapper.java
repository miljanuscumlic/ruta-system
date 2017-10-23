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
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import rs.ruta.common.SearchCriterion;
import rs.ruta.server.DatabaseException;
import rs.ruta.server.DetailException;

public class CatalogueXmlMapper extends XmlMapper
{
	final private static String docPrefix = ""; //"catalogue";
	final private static String collectionPath = "/ruta/catalogues";
	final private static String deletedCollectionPath = "/ruta/deleted/catalogues";
	final private static String objectPackageName = "oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21";
	//MMM: This map should be some kind of most recently used collection
	private Map<Object, CatalogueType> loadedCatalogues;

	public CatalogueXmlMapper() throws DatabaseException
	{
		super();
		loadedCatalogues = new ConcurrentHashMap<Object, CatalogueType>();
	}

	@Override
	public String getCollectionPath() { return collectionPath; }
	@Override
	public String getDocumentPrefix() { return docPrefix; }
	@Override
	public String getDeletedBaseCollectionPath() { return deletedCollectionPath; }
	@Override
	public String getObjectPackageName() { return objectPackageName; }

	@Override
	public CatalogueType find(String id) throws DetailException
	{
		CatalogueType catalogue = loadedCatalogues.get(id);
		if(catalogue == null)
		{
			catalogue = (CatalogueType) super.find(id);

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
				logger.error("Exception is: ", e);;
			}*/
			if(catalogue != null)
				loadedCatalogues.put(id, catalogue);
		}
		return catalogue;
	}

	@Override
	public <T extends DSTransaction> void insert(Object catalogue, Object id, T transaction) throws DetailException
	{
		//MMM: should be used this ID instead of id passed a paramater
		//String ID = ((CatalogueType)catalogue).getUUID().getValue();
		super.insert(catalogue, id, transaction);
		loadedCatalogues.put(id, (CatalogueType) catalogue);
	}

	@Override
	public <T extends DSTransaction> void delete(Object id, T transaction) throws DetailException
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
	protected JAXBElement<CatalogueType> getJAXBElement(Object object)
	{
		JAXBElement<CatalogueType> partyElement = (new ObjectFactory()).createCatalogue((CatalogueType) object);
		return partyElement;
	}

	@Override
	public Class<?> getObjectClass()
	{
		return CatalogueType.class;
	}

	@Override
	public <T extends DSTransaction> void update(Object object, Object id, T transaction) throws DetailException
	{
		insert(object, id, transaction);
		loadedCatalogues.put(id, (CatalogueType) object);
	}

	/* (non-Javadoc)
	 * @see rs.ruta.server.datamapper.XmlMapper#findAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<?> findAll() throws DetailException
	{
		ArrayList<CatalogueType> catalogues;
		catalogues = (ArrayList<CatalogueType>) super.findAll();
		if (catalogues != null)
			for(CatalogueType c : catalogues)
				loadedCatalogues.put(getID(c), c);
		return catalogues;
	}

	@Override
	public List<Object> search(SearchCriterion criterion) throws DetailException
	{
		List<Object> searchResult = new ArrayList<>();

		return searchResult;
	}

	@Override
	public String getSearchQueryName()
	{
		return queryNameSearchCatalogue;
	}



}

