
package rs.ruta.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import rs.ruta.common.SearchCriterion;


/**
 * <p>Java class for DocBoxAllIDsSearchCriterion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocBoxAllIDsSearchCriterion">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ruta.rs/services}SearchCriterion">
 *       &lt;sequence>
 *         &lt;element name="PartyID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocBoxAllIDsSearchCriterion", propOrder = {
    "partyID"
})
public class DocBoxSearchCriterion
    extends SearchCriterion
{

    @XmlElement(name = "PartyID")
    protected String partyID;

    /**
     * Gets the value of the partyID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartyID() {
        return partyID;
    }

    /**
     * Sets the value of the partyID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartyID(String value) {
        this.partyID = value;
    }

}
