package rs.ruta.client;

import javax.xml.bind.annotation.XmlAccessorType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;

/**
 * Class that is used for the persistence of {@code Search<PartySearch>} class. {@code Search<PartySearch>}
 * class like all other instance classes of the {@code Search<T>} type could not be persisted
 * through {@link rs.ruta.common.datamapper.DataMapper data mapper} calls. Appropriate data mapper is get
 * from the {@link rs.ruta.common.datamapper.MapperRegistry mapper registry} based
 * on the {@link Class} object of the class that is persisted. That would be {@code Search.class}
 * object for all instances of {@code Search<T>} class which means that it is not possible to differentiate among
 * them and choose the proper data mapper for particular class instance based on {@code Search.class} parameter.
 */
@XmlRootElement(namespace = "urn:rs:ruta:client", name = "PartySearch")
@XmlAccessorType(XmlAccessType.FIELD)
public class PartySearch extends Search<PartyType>
{
	public PartySearch()
	{
		super();
	}

/*	public static List<PartySearch> transform(List<Search<PartyType>> oldList)
	{
		List<PartySearch> newList = oldList.stream().map(search -> (PartySearch) search).collect(Collectors.toList());

		List<PartySearch> newList = new ArrayList<>();
		for(Search<PartyType> oldSearch : oldList)
			newList.add((PartySearch) oldSearch);
		return newList;
	}*/


}
