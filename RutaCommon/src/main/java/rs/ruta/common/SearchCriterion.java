package rs.ruta.common;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * Abstraction of the search criterion. JAXB can't handle interfaces so this is a class.
 */
@XmlRootElement(name = "SearchCriterion", namespace = "urn:rs:ruta:common")
@XmlType(name = "SearchCriterion")
@XmlSeeAlso({ PartySearchCriterion.class, CatalogueSearchCriterion.class })
public class SearchCriterion
{
	public SearchCriterion() {}

	/**
	 * If a string field is equal to empty string this method replaces its values with a null.
	 * This enables xml serialization of <code>SearchCriterion</code> object not to have elements that
	 * corresponds to empty strings in the model.
	 * @return this object
	 */
	public SearchCriterion nullEmptyFields()
	{
		return null;
	}

}
