
package rs.ruta.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import rs.ruta.common.Followers;


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
 *         &lt;element name="document" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="followers" type="{http://ruta.rs/services}Followers" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "documentDistribution", propOrder = {
    "document",
    "followers"
})
public class DocumentDistribution {

    protected Object document;
    protected Followers followers;

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

    /**
     * Gets the value of the followers property.
     * 
     * @return
     *     possible object is
     *     {@link Followers }
     *     
     */
    public Followers getFollowers() {
        return followers;
    }

    /**
     * Sets the value of the followers property.
     * 
     * @param value
     *     allowed object is
     *     {@link Followers }
     *     
     */
    public void setFollowers(Followers value) {
        this.followers = value;
    }

}
