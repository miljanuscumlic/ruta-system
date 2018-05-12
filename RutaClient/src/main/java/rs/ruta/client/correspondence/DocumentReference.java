package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;
import rs.ruta.common.InstanceFactory;

@XmlType(name = "DocumentReference")
@XmlAccessorType(XmlAccessType.NONE)
public class DocumentReference extends DocumentReferenceType
{
	private static final long serialVersionUID = 995064612236910411L;
	@XmlElement(name = "ReceivedTime")
	XMLGregorianCalendar receivedTime;

	public DocumentReference() { super(); }

	public DocumentReference(DocumentReferenceType docReference)
	{
		super();
		docReference.cloneTo(this);
	}

	@Override
	public DocumentReference clone()
	{
		DocumentReference docReference = new DocumentReference();
		cloneTo(docReference);
		return docReference;
	}

	public void cloneTo(DocumentReference docReference)
	{
		super.cloneTo(docReference);
		if(receivedTime != null)
			docReference.setReceivedTime(InstanceFactory.getDate(receivedTime.toGregorianCalendar()));
	}

	public XMLGregorianCalendar getReceivedTime()
	{
		return receivedTime;
	}

	public void setReceivedTime(XMLGregorianCalendar receivedTime)
	{
		this.receivedTime = receivedTime;
	}

}
