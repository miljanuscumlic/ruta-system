package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;

@XmlType(name = "DocumentReference")
@XmlAccessorType(XmlAccessType.NONE)
public class DocumentReference extends DocumentReferenceType
{
	private static final long serialVersionUID = 995064612236910411L;
	public static enum Status
	{
		CDR_RECEIVED, CDR_DOWN, CDR_FAILED,
		CLIENT_FAILED, CLIENT_SENT,
		CORR_RECEIVED, CORR_FAILED, CORR_REJECTED,
		UBL_INVALID, UBL_VALID
	};
	@XmlElement(name = "ReceivedTime")
	XMLGregorianCalendar receivedTime;
	/**
	 * True when the document conforms to the UBL standard.
	 */
	@XmlElement(name = "Valid")
	boolean valid;
	@XmlElement(name = "Status")
	Status status;

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
		valid = docReference.valid;
		status = docReference.status;
	}

	public XMLGregorianCalendar getReceivedTime()
	{
		return receivedTime;
	}

	/**
	 * Sets the time when the documet has been received by the receiver Party.
	 * @param receivedTime
	 */
	public void setReceivedTime(XMLGregorianCalendar receivedTime)
	{
		this.receivedTime = receivedTime;
	}

	/**
	 * Gets the flag denoting whether the document is a valid {@code UBL} document.
	 * @return true when valid
	 */
	public boolean isValid()
	{
		return valid;
	}

	/**
	 * Sets the flag denoting whether the document is a valid {@code UBL} document.
	 * @param valid
	 */
	public void setValid(boolean valid)
	{
		this.valid = valid;
	}

	/**
	 * Gets current {@link Status} of the document.
	 * @return status
	 */
	public Status getStatus()
	{
		return status;
	}

	/**
	 * Sets current {@link Status} of the document.
	 * @param status status to set
	 */
	public void setStatus(Status status)
	{
		this.status = status;
	}

}
