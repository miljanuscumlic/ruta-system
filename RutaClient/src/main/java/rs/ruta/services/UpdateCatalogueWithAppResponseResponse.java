
package rs.ruta.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;


/**
 * <p>Java class for UpdateCatalogueWithAppResponseResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateCatalogueWithAppResponseResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2}ApplicationResponseType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateCatalogueWithAppResponseResponse", propOrder = {
    "_return"
})
public class UpdateCatalogueWithAppResponseResponse {

    @XmlElement(name = "return")
    protected ApplicationResponseType _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link ApplicationResponseType }
     *     
     */
    public ApplicationResponseType getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link ApplicationResponseType }
     *     
     */
    public void setReturn(ApplicationResponseType value) {
        this._return = value;
    }

}
