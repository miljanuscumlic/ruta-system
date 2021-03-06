
package rs.ruta.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for existOperation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="existOperation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OriginalCollectionPath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OriginalDocumentName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OperationType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BackupCollectionPath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BackupDocumentName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RutaUser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "existOperation", namespace = "http://www.ruta.rs/ns/common", propOrder = {
    "originalCollectionPath",
    "originalDocumentName",
    "operationType",
    "backupCollectionPath",
    "backupDocumentName",
    "rutaUser"
})
@XmlSeeAlso({
    DatabaseOperation.class,
    DistributionOperation.class
})
public abstract class ExistOperation {

    @XmlElement(name = "OriginalCollectionPath", namespace = "http://www.ruta.rs/ns/common")
    protected String originalCollectionPath;
    @XmlElement(name = "OriginalDocumentName", namespace = "http://www.ruta.rs/ns/common")
    protected String originalDocumentName;
    @XmlElement(name = "OperationType", namespace = "http://www.ruta.rs/ns/common")
    protected String operationType;
    @XmlElement(name = "BackupCollectionPath", namespace = "http://www.ruta.rs/ns/common")
    protected String backupCollectionPath;
    @XmlElement(name = "BackupDocumentName", namespace = "http://www.ruta.rs/ns/common")
    protected String backupDocumentName;
    @XmlElement(name = "RutaUser", namespace = "http://www.ruta.rs/ns/common")
    protected String rutaUser;

    /**
     * Gets the value of the originalCollectionPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginalCollectionPath() {
        return originalCollectionPath;
    }

    /**
     * Sets the value of the originalCollectionPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginalCollectionPath(String value) {
        this.originalCollectionPath = value;
    }

    /**
     * Gets the value of the originalDocumentName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginalDocumentName() {
        return originalDocumentName;
    }

    /**
     * Sets the value of the originalDocumentName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginalDocumentName(String value) {
        this.originalDocumentName = value;
    }

    /**
     * Gets the value of the operationType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * Sets the value of the operationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationType(String value) {
        this.operationType = value;
    }

    /**
     * Gets the value of the backupCollectionPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackupCollectionPath() {
        return backupCollectionPath;
    }

    /**
     * Sets the value of the backupCollectionPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackupCollectionPath(String value) {
        this.backupCollectionPath = value;
    }

    /**
     * Gets the value of the backupDocumentName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackupDocumentName() {
        return backupDocumentName;
    }

    /**
     * Sets the value of the backupDocumentName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackupDocumentName(String value) {
        this.backupDocumentName = value;
    }

    /**
     * Gets the value of the rutaUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRutaUser() {
        return rutaUser;
    }

    /**
     * Sets the value of the rutaUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRutaUser(String value) {
        this.rutaUser = value;
    }

}
