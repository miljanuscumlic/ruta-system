
package rs.ruta.services;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for existTransaction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="existTransaction">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Timestamp" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="ExistOperation" type="{http://www.ruta.rs/ns/common}existOperation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "existTransaction", namespace = "http://www.ruta.rs/ns/common", propOrder = {
    "transactionID",
    "timestamp",
    "existOperation"
})
@XmlSeeAlso({
    DatabaseTransaction.class,
    DistributionTransaction.class
})
public abstract class ExistTransaction {

    @XmlElement(name = "TransactionID", namespace = "http://www.ruta.rs/ns/common", required = true)
    protected String transactionID;
    @XmlElement(name = "Timestamp", namespace = "http://www.ruta.rs/ns/common")
    protected long timestamp;
    @XmlElement(name = "ExistOperation", namespace = "http://www.ruta.rs/ns/common")
    protected List<ExistOperation> existOperation;

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
     * Gets the value of the existOperation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the existOperation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExistOperation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExistOperation }
     * 
     * 
     */
    public List<ExistOperation> getExistOperation() {
        if (existOperation == null) {
            existOperation = new ArrayList<ExistOperation>();
        }
        return this.existOperation;
    }

}
