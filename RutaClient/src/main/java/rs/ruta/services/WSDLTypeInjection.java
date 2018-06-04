
package rs.ruta.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;


/**
 * <p>Java class for WSDLTypeInjection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSDLTypeInjection">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arg0" type="{urn:oasis:names:specification:ubl:schema:xsd:Order-2}OrderType" minOccurs="0"/>
 *         &lt;element name="arg1" type="{urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2}OrderResponseType" minOccurs="0"/>
 *         &lt;element name="arg2" type="{urn:oasis:names:specification:ubl:schema:xsd:OrderResponseSimple-2}OrderResponseSimpleType" minOccurs="0"/>
 *         &lt;element name="arg3" type="{urn:oasis:names:specification:ubl:schema:xsd:OrderChange-2}OrderChangeType" minOccurs="0"/>
 *         &lt;element name="arg4" type="{urn:oasis:names:specification:ubl:schema:xsd:OrderCancellation-2}OrderCancellationType" minOccurs="0"/>
 *         &lt;element name="arg5" type="{urn:oasis:names:specification:ubl:schema:xsd:Invoice-2}InvoiceType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSDLTypeInjection", propOrder = {
    "arg0",
    "arg1",
    "arg2",
    "arg3",
    "arg4",
    "arg5"
})
public class WSDLTypeInjection {

    protected OrderType arg0;
    protected OrderResponseType arg1;
    protected OrderResponseSimpleType arg2;
    protected OrderChangeType arg3;
    protected OrderCancellationType arg4;
    protected InvoiceType arg5;

    /**
     * Gets the value of the arg0 property.
     * 
     * @return
     *     possible object is
     *     {@link OrderType }
     *     
     */
    public OrderType getArg0() {
        return arg0;
    }

    /**
     * Sets the value of the arg0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderType }
     *     
     */
    public void setArg0(OrderType value) {
        this.arg0 = value;
    }

    /**
     * Gets the value of the arg1 property.
     * 
     * @return
     *     possible object is
     *     {@link OrderResponseType }
     *     
     */
    public OrderResponseType getArg1() {
        return arg1;
    }

    /**
     * Sets the value of the arg1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderResponseType }
     *     
     */
    public void setArg1(OrderResponseType value) {
        this.arg1 = value;
    }

    /**
     * Gets the value of the arg2 property.
     * 
     * @return
     *     possible object is
     *     {@link OrderResponseSimpleType }
     *     
     */
    public OrderResponseSimpleType getArg2() {
        return arg2;
    }

    /**
     * Sets the value of the arg2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderResponseSimpleType }
     *     
     */
    public void setArg2(OrderResponseSimpleType value) {
        this.arg2 = value;
    }

    /**
     * Gets the value of the arg3 property.
     * 
     * @return
     *     possible object is
     *     {@link OrderChangeType }
     *     
     */
    public OrderChangeType getArg3() {
        return arg3;
    }

    /**
     * Sets the value of the arg3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderChangeType }
     *     
     */
    public void setArg3(OrderChangeType value) {
        this.arg3 = value;
    }

    /**
     * Gets the value of the arg4 property.
     * 
     * @return
     *     possible object is
     *     {@link OrderCancellationType }
     *     
     */
    public OrderCancellationType getArg4() {
        return arg4;
    }

    /**
     * Sets the value of the arg4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderCancellationType }
     *     
     */
    public void setArg4(OrderCancellationType value) {
        this.arg4 = value;
    }

    /**
     * Gets the value of the arg5 property.
     * 
     * @return
     *     possible object is
     *     {@link InvoiceType }
     *     
     */
    public InvoiceType getArg5() {
        return arg5;
    }

    /**
     * Sets the value of the arg5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link InvoiceType }
     *     
     */
    public void setArg5(InvoiceType value) {
        this.arg5 = value;
    }

}
