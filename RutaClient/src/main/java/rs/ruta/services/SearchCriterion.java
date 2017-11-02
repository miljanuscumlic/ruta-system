
package rs.ruta.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SearchCriterion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SearchCriterion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PartyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PartyCompanyID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PartyClassCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PartyCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PartyCountry" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PartyAll" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ItemName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ItemBarcode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ItemCommCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ItemAll" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchCriterion", propOrder = {
    "partyName",
    "partyCompanyID",
    "partyClassCode",
    "partyCity",
    "partyCountry",
    "partyAll",
    "itemName",
    "itemBarcode",
    "itemCommCode",
    "itemAll"
})
public class SearchCriterion {

    @XmlElement(name = "PartyName")
    protected String partyName;
    @XmlElement(name = "PartyCompanyID")
    protected String partyCompanyID;
    @XmlElement(name = "PartyClassCode")
    protected String partyClassCode;
    @XmlElement(name = "PartyCity")
    protected String partyCity;
    @XmlElement(name = "PartyCountry")
    protected String partyCountry;
    @XmlElement(name = "PartyAll")
    protected boolean partyAll;
    @XmlElement(name = "ItemName")
    protected String itemName;
    @XmlElement(name = "ItemBarcode")
    protected String itemBarcode;
    @XmlElement(name = "ItemCommCode")
    protected String itemCommCode;
    @XmlElement(name = "ItemAll")
    protected boolean itemAll;

    /**
     * Gets the value of the partyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartyName() {
        return partyName;
    }

    /**
     * Sets the value of the partyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartyName(String value) {
        this.partyName = value;
    }

    /**
     * Gets the value of the partyCompanyID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartyCompanyID() {
        return partyCompanyID;
    }

    /**
     * Sets the value of the partyCompanyID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartyCompanyID(String value) {
        this.partyCompanyID = value;
    }

    /**
     * Gets the value of the partyClassCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartyClassCode() {
        return partyClassCode;
    }

    /**
     * Sets the value of the partyClassCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartyClassCode(String value) {
        this.partyClassCode = value;
    }

    /**
     * Gets the value of the partyCity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartyCity() {
        return partyCity;
    }

    /**
     * Sets the value of the partyCity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartyCity(String value) {
        this.partyCity = value;
    }

    /**
     * Gets the value of the partyCountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartyCountry() {
        return partyCountry;
    }

    /**
     * Sets the value of the partyCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartyCountry(String value) {
        this.partyCountry = value;
    }

    /**
     * Gets the value of the partyAll property.
     * 
     */
    public boolean isPartyAll() {
        return partyAll;
    }

    /**
     * Sets the value of the partyAll property.
     * 
     */
    public void setPartyAll(boolean value) {
        this.partyAll = value;
    }

    /**
     * Gets the value of the itemName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Sets the value of the itemName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemName(String value) {
        this.itemName = value;
    }

    /**
     * Gets the value of the itemBarcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemBarcode() {
        return itemBarcode;
    }

    /**
     * Sets the value of the itemBarcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemBarcode(String value) {
        this.itemBarcode = value;
    }

    /**
     * Gets the value of the itemCommCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemCommCode() {
        return itemCommCode;
    }

    /**
     * Sets the value of the itemCommCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemCommCode(String value) {
        this.itemCommCode = value;
    }

    /**
     * Gets the value of the itemAll property.
     * 
     */
    public boolean isItemAll() {
        return itemAll;
    }

    /**
     * Sets the value of the itemAll property.
     * 
     */
    public void setItemAll(boolean value) {
        this.itemAll = value;
    }

}
