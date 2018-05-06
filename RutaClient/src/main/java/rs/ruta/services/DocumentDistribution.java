
package rs.ruta.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import rs.ruta.common.Associates;


/**
 * <p>Java class for documentDistribution complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="documentDistribution">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="associates" type="{http://www.ruta.rs/ns/common}Associates" minOccurs="0"/>
 *         &lt;element name="document" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "documentDistribution", namespace = "http://www.ruta.rs/ns/common", propOrder = {
    "associates",
    "document"
})
public class DocumentDistribution {

    @XmlElement(namespace = "http://www.ruta.rs/ns/common")
    protected Associates associates;
    @XmlElement(namespace = "http://www.ruta.rs/ns/common")
    protected Object document;

    /**
     * Gets the value of the associates property.
     * 
     * @return
     *     possible object is
     *     {@link Associates }
     *     
     */
    public Associates getAssociates() {
        return associates;
    }

    /**
     * Sets the value of the associates property.
     * 
     * @param value
     *     allowed object is
     *     {@link Associates }
     *     
     */
    public void setAssociates(Associates value) {
        this.associates = value;
    }

    /**
     * Gets the value of the document property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getDocument() {
        return document;
    }

    /**
     * Sets the value of the document property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setDocument(Object value) {
        this.document = value;
    }

}
