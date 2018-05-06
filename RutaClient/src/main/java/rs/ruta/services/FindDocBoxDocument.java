
package rs.ruta.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import rs.ruta.common.DocBoxDocumentSearchCriterion;


/**
 * <p>Java class for FindDocBoxDocument complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FindDocBoxDocument">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arg0" type="{http://www.ruta.rs/ns/common}DocBoxDocumentSearchCriterion" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FindDocBoxDocument", propOrder = {
    "arg0"
})
public class FindDocBoxDocument {

    protected DocBoxDocumentSearchCriterion arg0;

    /**
     * Gets the value of the arg0 property.
     * 
     * @return
     *     possible object is
     *     {@link DocBoxDocumentSearchCriterion }
     *     
     */
    public DocBoxDocumentSearchCriterion getArg0() {
        return arg0;
    }

    /**
     * Sets the value of the arg0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocBoxDocumentSearchCriterion }
     *     
     */
    public void setArg0(DocBoxDocumentSearchCriterion value) {
        this.arg0 = value;
    }

}
