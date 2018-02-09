
package rs.ruta.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import rs.ruta.common.Followers;


/**
 * <p>Java class for documentTransaction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="documentTransaction">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Timestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="Follower" type="{http://ruta.rs/services}Followers" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "documentTransaction", propOrder = {
    "transactionID",
    "timestamp",
    "follower"
})
public class DocumentTransaction {

    @XmlElement(name = "TransactionID", required = true)
    protected String transactionID;
    @XmlElement(name = "Timestamp")
    protected long timestamp;
    @XmlElement(name = "Follower")
    protected Followers follower;

    /**
     * Gets the value of the transactionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionID() {
        return transactionID;
    }

    /**
     * Sets the value of the transactionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionID(String value) {
        this.transactionID = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     */
    public void setTimestamp(long value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the follower property.
     * 
     * @return
     *     possible object is
     *     {@link Followers }
     *     
     */
    public Followers getFollower() {
        return follower;
    }

    /**
     * Sets the value of the follower property.
     * 
     * @param value
     *     allowed object is
     *     {@link Followers }
     *     
     */
    public void setFollower(Followers value) {
        this.follower = value;
    }

}
