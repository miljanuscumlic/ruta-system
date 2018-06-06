package rs.ruta.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "BusinessPartnershipResolution")
@XmlType(name = "BusinessPartnershipResolution")
public class BusinessPartnershipResolution extends BusinessPartnershipDocument
{
	@XmlElement(name = "Resolution")
	boolean resolution;

}
