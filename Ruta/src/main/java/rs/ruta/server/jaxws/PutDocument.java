
package rs.ruta.server.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class was generated by Apache CXF 3.1.6
 * Wed Jun 14 12:13:38 CEST 2017
 * Generated source version: 3.1.6
 */

@XmlRootElement(name = "putDocument", namespace = "http://server.ruta.rs/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "putDocument", namespace = "http://server.ruta.rs/")

public class PutDocument {

    @XmlElement(name = "arg0")
    private oasis.names.specification.ubl.schema.xsd.catalogue_2.CatalogueType arg0;

    public oasis.names.specification.ubl.schema.xsd.catalogue_2.CatalogueType getArg0() {
        return this.arg0;
    }

    public void setArg0(oasis.names.specification.ubl.schema.xsd.catalogue_2.CatalogueType newArg0)  {
        this.arg0 = newArg0;
    }

}

