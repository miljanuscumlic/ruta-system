//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.06.13 at 01:37:42 PM CEST 
//


package org.etsi.uri._01903.v1_3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OCSPRefsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OCSPRefsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OCSPRef" type="{http://uri.etsi.org/01903/v1.3.2#}OCSPRefType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OCSPRefsType", propOrder = {
    "ocspRef"
})
public class OCSPRefsType {

    @XmlElement(name = "OCSPRef", required = true)
    protected List<OCSPRefType> ocspRef;

    /**
     * Gets the value of the ocspRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ocspRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOCSPRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OCSPRefType }
     * 
     * 
     */
    public List<OCSPRefType> getOCSPRef() {
        if (ocspRef == null) {
            ocspRef = new ArrayList<OCSPRefType>();
        }
        return this.ocspRef;
    }

}
