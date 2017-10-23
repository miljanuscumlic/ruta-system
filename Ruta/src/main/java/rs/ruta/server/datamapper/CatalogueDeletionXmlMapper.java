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

public class CatalogueDeletionXmlMapper extends XmlMapper
{
	final private static String docPrefix = "";
	final private static String collectionPath = "/ruta/catalogue-deletions";
	final private static String deletedCollectionPath = "/ruta/deleted/catalogue-deletions";
	final private static String objectPackageName = "oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21";
	//MMM: This map should be some kind of most recently used collection
	private Map<Object, CatalogueDeletionType> loadedCatalogueDeletions;

	public CatalogueDeletionXmlMapper() throws DatabaseException
	{
		super();
		loadedCatalogueDeletions = new ConcurrentHashMap<Object, CatalogueDeletionType>();
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
	public CatalogueDeletionType find(String id) throws DetailException
	{
		CatalogueDeletionType catalogue = loadedCatalogueDeletions.get(id);
		if(catalogue == null)
		{
			catalogue = (CatalogueDeletionType) super.find(id);

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
				logger.error("Exception is: ", e);;
			}*/
			if(catalogue != null)
				loadedCatalogueDeletions.put(id, catalogue);
		}
		return catalogue;
	}

	@Override
	public <T extends DSTransaction> void insert(Object catalogueDeletion, Object id, T transaction) throws DetailException
	{
		//MMM: should be used this ID instead of id passed as a paramater
		//String ID = ((CatalogueDeletionType)catalogueDeletion).getUUID().getValue();

		MapperRegistry.getMapper(CatalogueType.class).delete(id, transaction);
		super.insert(catalogueDeletion, id, transaction);
		loadedCatalogueDeletions.put(id, (CatalogueDeletionType) catalogueDeletion);
	}

	@Override
	public <T extends DSTransaction> void delete(Object id, T transaction) throws DetailException
	{
		super.delete(id, transaction);
		loadedCatalogueDeletions.remove(id);
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
	protected JAXBElement<CatalogueDeletionType> getJAXBElement(Object object)
	{
		JAXBElement<CatalogueDeletionType> partyElement = (new ObjectFactory()).createCatalogueDeletion((CatalogueDeletionType) object);
		return partyElement;
	}

	@Override
	public Class<?> getObjectClass()
	{
		return CatalogueDeletionType.class;
	}

	@Override
	public <T extends DSTransaction> void update(Object object, Object id, T transaction) throws DetailException
	{
		insert(object, id, transaction);
	}

	/* (non-Javadoc)
	 * @see rs.ruta.server.datamapper.XmlMapper#findAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<?> findAll() throws DetailException
	{
		ArrayList<CatalogueDeletionType> deletions;
		deletions = (ArrayList<CatalogueDeletionType>) super.findAll();
		if (deletions != null)
			for(CatalogueDeletionType t : deletions)
				loadedCatalogueDeletions.put(getID(t), t);
		return deletions;
	}

}

