
package rs.ruta.services;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for status.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="status">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CDR_RECEIVED"/>
 *     &lt;enumeration value="CDR_DOWN"/>
 *     &lt;enumeration value="CDR_FAILED"/>
 *     &lt;enumeration value="CLIENT_FAILED"/>
 *     &lt;enumeration value="CLIENT_SENT"/>
 *     &lt;enumeration value="CORR_RECEIVED"/>
 *     &lt;enumeration value="CORR_FAILED"/>
 *     &lt;enumeration value="CORR_REJECTED"/>
 *     &lt;enumeration value="UBL_INVALID"/>
 *     &lt;enumeration value="UBL_VALID"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "status", namespace = "http://www.ruta.rs/ns/common")
@XmlEnum
public enum Status {

    CDR_RECEIVED,
    CDR_DOWN,
    CDR_FAILED,
    CLIENT_FAILED,
    CLIENT_SENT,
    CORR_RECEIVED,
    CORR_FAILED,
    CORR_REJECTED,
    UBL_INVALID,
    UBL_VALID;

    public String value() {
        return name();
    }

    public static Status fromValue(String v) {
        return valueOf(v);
    }

}
