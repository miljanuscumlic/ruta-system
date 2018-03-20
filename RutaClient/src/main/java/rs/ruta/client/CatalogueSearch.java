package rs.ruta.client;

import javax.xml.bind.annotation.XmlAccessorType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;

/**
 * Class that is used for the persistence of {@code Search<CatalogueSearch>} class. {@code Search<CatalogueType>}
 * class like all other instance classes of the {@code Search<T>} type could not be persisted
 * through {@link rs.ruta.common.datamapper.DataMapper data mapper} calls. Appropriate data mapper is get
 * from the {@link rs.ruta.common.datamapper.MapperRegistry mapper registry} based
 * on the {@link Class} object of the class that is persisted. That would be {@code Search.class}
 * object for all instances of {@code Search<T>} class which means that it is not possible to differentiate among
 * them and choose the proper data mapper for particular class instance based on {@code Search.class} parameter.
 */
@XmlRootElement(namespace = "urn:rs:ruta:client", name = "CatalogueSearch")
@XmlAccessorType(XmlAccessType.FIELD)
public class CatalogueSearch extends Search<CatalogueType>
{
	public CatalogueSearch()
	{
		super();
	}

/*	public static List<CatalogueSearch> transform(List<Search<CatalogueSearch>> oldList)
	{
		List<CatalogueSearch> newList = oldList.stream().map(search -> (CatalogueSearch) search).collect(Collectors.toList());

		List<CatalogueSearch> newList = new ArrayList<>();
		for(Search<CatalogueSearch> oldSearch : oldList)
			newList.add((CatalogueSearch) oldSearch);
		return newList;
	}*/


}
