package rs.ruta.common;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Apstraction of the search criterion. JAXB can't handle interfaces so this is a class.
 */
@XmlRootElement(name = "SearchCriterion", namespace = "urn:rs:ruta:common")
@XmlType(name = "SearchCriterion")
public class SearchCriterion
{
	public SearchCriterion() {}

}
