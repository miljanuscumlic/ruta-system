
package rs.ruta.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import rs.ruta.common.DocBoxAllIDsSearchCriterion;


/**
 * <p>Java class for FindAllDocBoxDocumentIDs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FindAllDocBoxDocumentIDs">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arg0" type="{http://ruta.rs/services}DocBoxAllIDsSearchCriterion" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FindAllDocBoxDocumentIDs", propOrder = {
    "arg0"
})
public class FindAllDocBoxDocumentIDs {

    protected DocBoxAllIDsSearchCriterion arg0;

    /**
     * Gets the value of the arg0 property.
     * 
     * @return
     *     possible object is
     *     {@link DocBoxAllIDsSearchCriterion }
     *     
     */
    public DocBoxAllIDsSearchCriterion getArg0() {
        return arg0;
    }

    /**
     * Sets the value of the arg0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocBoxAllIDsSearchCriterion }
     *     
     */
    public void setArg0(DocBoxAllIDsSearchCriterion value) {
        this.arg0 = value;
    }

}
