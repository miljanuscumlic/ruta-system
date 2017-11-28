package rs.ruta.server.datamapper;

import java.io.StringReader;
import java.util.*;
import java.util.concurrent.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.ObjectFactory;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import rs.ruta.server.DatabaseException;
import rs.ruta.server.DetailException;

public class CatalogueDeletionXmlMapper extends XmlMapper<CatalogueDeletionType>
{
	final private static String collectionPath = "/catalogue-deletion";
	final private static String objectPackageName = "oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21";
	//MMM: This map should be some kind of most recently used collection
	private Map<String, CatalogueDeletionType> loadedCatalogueDeletions;

	public CatalogueDeletionXmlMapper() throws DatabaseException
	{
		super();
		loadedCatalogueDeletions = new ConcurrentHashMap<String, CatalogueDeletionType>();
	}

	@Override
	public String getCollectionPath() { return collectionPath; }
	@Override
	public String getObjectPackageName() { return objectPackageName; }

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
	public void insert(CatalogueDeletionType catalogueDeletion, String id,  DSTransaction transaction) throws DetailException
	{
		MapperRegistry.getMapper(CatalogueType.class).delete(id, transaction);
		super.insert(catalogueDeletion, id, transaction);
		loadedCatalogueDeletions.put(id, catalogueDeletion);
	}

	@Override
	public String insert(String username, CatalogueDeletionType object, DSTransaction transaction) throws DetailException
	{
		String id = getID(username);
		insert(object, id, transaction);
		return id;
	}

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
	public Class<?> getObjectClass()
	{
		return CatalogueDeletionType.class;
	}

	@Override
	public CatalogueDeletionType getLoadedObject(String id)
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
	protected void clearLoadedObjects()
	{
		loadedCatalogueDeletions.clear();
	}

}