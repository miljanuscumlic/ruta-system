
package rs.ruta.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for databaseTransaction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="databaseTransaction">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ruta.rs/services}existTransaction">
 *       &lt;sequence>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "databaseTransaction")
public class DatabaseTransaction
    extends ExistTransaction
{


}
