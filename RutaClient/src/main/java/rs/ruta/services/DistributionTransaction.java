
package rs.ruta.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import rs.ruta.common.Followers;


/**
 * <p>Java class for distributionTransaction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="distributionTransaction">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ruta.rs/services}existTransaction">
 *       &lt;sequence>
 *         &lt;element name="Followers" type="{http://ruta.rs/services}Followers" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "distributionTransaction", propOrder = {
    "followers"
})
public class DistributionTransaction
    extends ExistTransaction
{

    @XmlElement(name = "Followers")
    protected Followers followers;

    /**
     * Gets the value of the followers property.
     * 
     * @return
     *     possible object is
     *     {@link Followers }
     *     
     */
    public Followers getFollowers() {
        return followers;
    }

    /**
     * Sets the value of the followers property.
     * 
     * @param value
     *     allowed object is
     *     {@link Followers }
     *     
     */
    public void setFollowers(Followers value) {
        this.followers = value;
    }

}
