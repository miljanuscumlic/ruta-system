
package rs.ruta.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="User" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "existOperation", propOrder = {
    "originalCollectionPath",
    "originalDocumentName",
    "operationType",
    "backupCollectionPath",
    "backupDocumentName",
    "user"
})
public abstract class ExistOperation {

    @XmlElement(name = "OriginalCollectionPath")
    protected String originalCollectionPath;
    @XmlElement(name = "OriginalDocumentName")
    protected String originalDocumentName;
    @XmlElement(name = "OperationType")
    protected String operationType;
    @XmlElement(name = "BackupCollectionPath")
    protected String backupCollectionPath;
    @XmlElement(name = "BackupDocumentName")
    protected String backupDocumentName;
    @XmlElement(name = "User")
    protected String user;

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
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

}
