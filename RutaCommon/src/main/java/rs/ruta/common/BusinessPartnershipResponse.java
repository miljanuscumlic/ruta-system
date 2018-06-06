package rs.ruta.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "BusinessPartnershipResponse")
@XmlType(name = "BusinessPartnershipResponse")
public class BusinessPartnershipResponse extends BusinessPartnershipDocument
{
	@XmlElement(name = "Response")
	boolean response;

}
