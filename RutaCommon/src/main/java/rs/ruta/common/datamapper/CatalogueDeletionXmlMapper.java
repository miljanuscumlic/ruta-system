package rs.ruta.common.datamapper;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.ObjectFactory;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;

public class CatalogueDeletionXmlMapper extends XmlMapper<CatalogueDeletionType>
{
	final private static String collectionPath = "/catalogue-deletion";
	final private static String objectPackageName = "oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21";
	//MMM: This map should be some kind of most recently used collection
	private Map<String, CatalogueDeletionType> loadedCatalogueDeletions;

	public CatalogueDeletionXmlMapper(ExistConnector connector) throws DetailException
	{
		super(connector);
		loadedCatalogueDeletions = new ConcurrentHashMap<String, CatalogueDeletionType>();
	}

	@Override
	protected String getCollectionPath() { return collectionPath; }
	@Override
	protected String getObjectPackageName() { return objectPackageName; }

	@Override
	public CatalogueDeletionType find(String id) throws DetailException
	{
		CatalogueDeletionType catalogue = loadedCatalogueDeletions.get(id);
		if(catalogue == null)
		{
			catalogue = super.find(id);

/*			try
			{
				JAXBContext jc = JAXBContext.newInstance(CatalogueDeletionType.class);
				Unmarshaller u = jc.createUnmarshaller();

				// unmarshal instance document into a tree of Java content
				@SuppressWarnings("unchecked")
				JAXBElement<?> jaxbElement = (JAXBElement<?>) u.unmarshal(new StringReader(result.toString()));
				catalogue = jaxbElement.getValue();
			}
			catch (JAXBException e)
			{
				logger.error(Exception is ", e);;
			}*/
			if(catalogue != null)
				loadedCatalogueDeletions.put(id, catalogue);
		}
		return catalogue;
	}

	@Override
	protected String doGetOrCreateID(Collection collection, CatalogueDeletionType catalogueDeletion, String username, DSTransaction transaction)
			throws DetailException
	{
		String id = null;
		if(username != null)
			id = getID(username);
		MapperRegistry.getInstance().getMapper(CatalogueType.class).delete(id, transaction);
		return id;
	}

/*	@Deprecated
	@Override
	public void insert(CatalogueDeletionType catalogueDeletion, String id,  DSTransaction transaction) throws DetailException
	{
		MapperRegistry.getMapper(CatalogueType.class).delete(id, transaction);
		super.insert(catalogueDeletion, id, transaction);
		loadedCatalogueDeletions.put(id, catalogueDeletion);
	}

	@Deprecated
	@Override
	public String insert(String username, CatalogueDeletionType object, DSTransaction transaction) throws DetailException
	{
		String id = getID(username);
		insert(object, id, transaction);
		return id;
	}*/

	@Override
	public void delete(String id, DSTransaction transaction) throws DetailException
	{
		super.delete(id, transaction);
		loadedCatalogueDeletions.remove(id);
	}

	@Override
	protected JAXBElement<CatalogueDeletionType> getJAXBElement(CatalogueDeletionType object)
	{
		return new ObjectFactory().createCatalogueDeletion(object);
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return CatalogueDeletionType.class;
	}

	@Override
	protected CatalogueDeletionType getCachedObject(String id)
	{
		return loadedCatalogueDeletions.get(id);
	}

	@Override
	public ArrayList<CatalogueDeletionType> findAll() throws DetailException
	{

		ArrayList<CatalogueDeletionType> deletions = new ArrayList<>();
		String ids[] = listAllDocumentIDs();
		for(String id : ids)
		{
			final CatalogueDeletionType deletion = find(trimID(id));
			if (deletion != null)
				deletions.add(deletion);
		}
		return deletions.size() > 0 ? deletions : null;

/*		ArrayList<CatalogueDeletionType> deletions;
		deletions = (ArrayList<CatalogueDeletionType>) super.findAll();
		if (deletions != null)
			for(CatalogueDeletionType t : deletions)
				loadedCatalogueDeletions.put(getID(t), t);
		return deletions;*/
	}

	@Override
	protected void clearCachedObjects()
	{
		loadedCatalogueDeletions.clear();
	}

}