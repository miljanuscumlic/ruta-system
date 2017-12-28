package rs.ruta.common.datamapper;

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
import rs.ruta.common.SearchCriterion;

public class CatalogueXmlMapper extends XmlMapper<CatalogueType>
{
	final private static String collectionPath = "/catalogue";
	final private static String objectPackageName = "oasis.names.specification.ubl.schema.xsd.catalogue_21";
	final private static String queryNameSearchCatalogue = "search-catalogue.xq";
	//MMM: This map should be some kind of most recently used collection
	private Map<String, CatalogueType> loadedCatalogues;

	public CatalogueXmlMapper(ExistConnector connector) throws DetailException
	{
		super(connector);
		loadedCatalogues = new ConcurrentHashMap<String, CatalogueType>();
	}

	@Override
	protected String getCollectionPath() { return collectionPath; }
	@Override
	protected String getObjectPackageName() { return objectPackageName; }

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
		return  new ObjectFactory().createCatalogue(object);
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return CatalogueType.class;
	}

	@Override
	protected CatalogueType getCachedObject(String id)
	{
		return loadedCatalogues.get(id);
	}

	//@Deprecated //MMM: delete it
/*	@Override
	public String update(String username, CatalogueType catalogue, DSTransaction transaction) throws DetailException
	{
//		insert(username, catalogue, transaction);
		String id = (String) super.update(username, catalogue, transaction);
		loadedCatalogues.put(id, catalogue);
		return id;
	}*/

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
	protected String getSearchQueryName()
	{
		return queryNameSearchCatalogue;
	}

	@Override
	protected void doCacheObject(String id, CatalogueType object)
	{
		loadedCatalogues.put(id, object);
	}

	@Override
	protected void clearCachedObjects()
	{
		loadedCatalogues.clear();
	}

	@Override
	protected String prepareQuery(SearchCriterion criterion) throws DatabaseException
	{
		String query = openDocument(getQueryPath(), queryNameSearchCatalogue);
		if(query == null)
			return null;

		//substitutes strings "declare variable $name external := '';" with...
		String partyName = criterion.getPartyName();
		String partyCompanyID = criterion.getPartyCompanyID();
		String partyClassCode = criterion.getPartyClassCode();
		String partyCity = criterion.getPartyCity();
		String partyCountry = criterion.getPartyCountry();
		boolean partyAll = criterion.isPartyAll();

		String itemName = criterion.getItemName();
		String itemDescription = criterion.getItemDescription();
		String itemBarcode = criterion.getItemBarcode();
		String itemCommCode = criterion.getItemCommCode();
		String itemKeyword = criterion.getItemKeyword();
		boolean itemAll = criterion.isItemAll();

/*		String preparedQuery = query.replaceFirst("declare variable party-name external := ''",
				"declare variable party-name external := " + partyName);*/

		String preparedQuery = query;
		if(partyName != null)
			preparedQuery = preparedQuery.replaceFirst("party-name( )+external( )*:=( )*[(][)]",
					(new StringBuilder("party-name := '").append(partyName).append("'")).toString());
		if(partyCompanyID != null)
			preparedQuery = preparedQuery.replaceFirst("party-company-id( )+external( )*:=( )*[(][)]",
					(new StringBuilder("party-company-id := '").append(partyCompanyID).append("'")).toString());
		if(partyClassCode != null)
			preparedQuery = preparedQuery.replaceFirst("party-class-code( )+external( )*:=( )*[(][)]",
					(new StringBuilder("party-class-code := '").append(partyClassCode).append("'")).toString());
		if(partyCity != null)
			preparedQuery = preparedQuery.replaceFirst("party-city( )+external( )*:=( )*[(][)]",
					(new StringBuilder("party-city := '").append(partyCity).append("'")).toString());
		if(partyCountry != null)
			preparedQuery = preparedQuery.replaceFirst("party-country( )+external( )*:=( )*[(][)]",
					(new StringBuilder("party-country := '").append(partyCountry).append("'")).toString());
		if(!partyAll)
			preparedQuery = preparedQuery.replaceFirst("party-all( )+external( )*:=( )*true",
					(new StringBuilder("party-all := false")).toString());

		if(itemName != null)
			preparedQuery = preparedQuery.replaceFirst("item-name( )+external( )*:=( )*[(][)]",
					(new StringBuilder("item-name := '").append(itemName).append("'")).toString());
		if(itemDescription != null)
			preparedQuery = preparedQuery.replaceFirst("item-description( )+external( )*:=( )*[(][)]",
					(new StringBuilder("item-description := '").append(itemDescription).append("'")).toString());
		if(itemBarcode != null)
			preparedQuery = preparedQuery.replaceFirst("item-barcode( )+external( )*:=( )*[(][)]",
					(new StringBuilder("item-barcode := '").append(itemBarcode).append("'")).toString());
		if(itemCommCode != null)
			preparedQuery = preparedQuery.replaceFirst("item-comm-code( )+external( )*:=( )*[(][)]",
					(new StringBuilder("item-comm-code := '").append(itemCommCode).append("'")).toString());
		if(itemKeyword != null)
			preparedQuery = preparedQuery.replaceFirst("item-keyword( )+external( )*:=( )*[(][)]",
					(new StringBuilder("item-keyword := '").append(itemKeyword).append("'")).toString());
		if(!itemAll)
			preparedQuery = preparedQuery.replaceFirst("item-all( )+external( )*:=( )*true",
					(new StringBuilder("item-all := false")).toString());

		return preparedQuery;
	}

}