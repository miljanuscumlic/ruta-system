
package rs.ruta.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import rs.ruta.common.DocumentReference;


/**
 * <p>Java class for partnershipDocument complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="partnershipDocument">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ID" type="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2}IDType" minOccurs="0"/>
 *         &lt;element name="RequesterParty" type="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2}PartyType" minOccurs="0"/>
 *         &lt;element name="RequestedParty" type="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2}PartyType" minOccurs="0"/>
 *         &lt;element name="IssueTime" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" minOccurs="0"/>
 *         &lt;element name="DocumentReference" type="{http://www.ruta.rs/ns/common}DocumentReference" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "partnershipDocument", namespace = "http://www.ruta.rs/ns/common", propOrder = {
    "id",
    "requesterParty",
    "requestedParty",
    "issueTime",
    "documentReference"
})
public abstract class PartnershipDocument {

    @XmlElement(name = "ID", namespace = "http://www.ruta.rs/ns/common")
    protected IDType id;
    @XmlElement(name = "RequesterParty", namespace = "http://www.ruta.rs/ns/common")
    protected PartyType requesterParty;
    @XmlElement(name = "RequestedParty", namespace = "http://www.ruta.rs/ns/common")
    protected PartyType requestedParty;
    @XmlElement(name = "IssueTime", namespace = "http://www.ruta.rs/ns/common")
    @XmlSchemaType(name = "anySimpleType")
    protected Object issueTime;
    @XmlElement(name = "DocumentReference", namespace = "http://www.ruta.rs/ns/common")
    protected DocumentReference documentReference;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link IDType }
     *     
     */
    public IDType getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link IDType }
     *     
     */
    public void setID(IDType value) {
        this.id = value;
    }

    /**
     * Gets the value of the requesterParty property.
     * 
     * @return
     *     possible object is
     *     {@link PartyType }
     *     
     */
    public PartyType getRequesterParty() {
        return requesterParty;
    }

    /**
     * Sets the value of the requesterParty property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyType }
     *     
     */
    public void setRequesterParty(PartyType value) {
        this.requesterParty = value;
    }

    /**
     * Gets the value of the requestedParty property.
     * 
     * @return
     *     possible object is
     *     {@link PartyType }
     *     
     */
    public PartyType getRequestedParty() {
        return requestedParty;
    }

    /**
     * Sets the value of the requestedParty property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyType }
     *     
     */
    public void setRequestedParty(PartyType value) {
        this.requestedParty = value;
    }

    /**
     * Gets the value of the issueTime property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getIssueTime() {
        return issueTime;
    }

    /**
     * Sets the value of the issueTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setIssueTime(Object value) {
        this.issueTime = value;
    }

    /**
     * Gets the value of the documentReference property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentReference }
     *     
     */
    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    /**
     * Sets the value of the documentReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentReference }
     *     
     */
    public void setDocumentReference(DocumentReference value) {
        this.documentReference = value;
    }

}
