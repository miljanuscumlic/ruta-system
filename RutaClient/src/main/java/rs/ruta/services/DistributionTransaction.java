
package rs.ruta.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import rs.ruta.common.Associates;


/**
 * <p>Java class for distributionTransaction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="distributionTransaction">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.ruta.rs/ns/common}existTransaction">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.ruta.rs/ns/common}Associates" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "distributionTransaction", namespace = "http://www.ruta.rs/ns/common", propOrder = {
    "associates"
})
public class DistributionTransaction
    extends ExistTransaction
{

    @XmlElement(name = "Associates", namespace = "http://www.ruta.rs/ns/common")
    protected Associates associates;

    /**
     * Gets the value of the associates property.
     * 
     * @return
     *     possible object is
     *     {@link Associates }
     *     
     */
    public Associates getAssociates() {
        return associates;
    }

    /**
     * Sets the value of the associates property.
     * 
     * @param value
     *     allowed object is
     *     {@link Associates }
     *     
     */
    public void setAssociates(Associates value) {
        this.associates = value;
    }

}
