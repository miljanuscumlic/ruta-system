//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.06.13 at 01:37:42 PM CEST 
//


package org.etsi.uri._01903.v1_3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.w3._2000._09.xmldsig_.DigestMethodType;


/**
 * <p>Java class for DigestAlgAndValueType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DigestAlgAndValueType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}DigestMethod"/>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}DigestValue"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DigestAlgAndValueType", propOrder = {
    "digestMethod",
    "digestValue"
})
public class DigestAlgAndValueType {

    @XmlElement(name = "DigestMethod", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected DigestMethodType digestMethod;
    @XmlElement(name = "DigestValue", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected byte[] digestValue;

    /**
     * Gets the value of the digestMethod property.
     * 
     * @return
     *     possible object is
     *     {@link DigestMethodType }
     *     
     */
    public DigestMethodType getDigestMethod() {
        return digestMethod;
    }

    /**
     * Sets the value of the digestMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link DigestMethodType }
     *     
     */
    public void setDigestMethod(DigestMethodType value) {
        this.digestMethod = value;
    }

    /**
     * Gets the value of the digestValue property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getDigestValue() {
        return digestValue;
    }

    /**
     * Sets the value of the digestValue property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setDigestValue(byte[] value) {
        this.digestValue = value;
    }

}
