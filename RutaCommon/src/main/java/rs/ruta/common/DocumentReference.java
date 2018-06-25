package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.common.datamapper.DetailException;

@XmlType(name = "DocumentReference")
@XmlAccessorType(XmlAccessType.NONE)
public class DocumentReference extends DocumentReferenceType
{
	private static final long serialVersionUID = 995064612236910411L;
	public static enum Status
	{
		UBL_INVALID, UBL_VALID,
		CLIENT_FAILED, CLIENT_SENT,
		CDR_DOWN, CDR_FAILED, CDR_RECEIVED,
		CORR_FAILED, CORR_RECEIVED

	};
	@XmlElement(name = "ReceivedTime")
	XMLGregorianCalendar receivedTime;
	/**
	 * True when the document conforms to the UBL standard.
	 */
	@XmlElement(name = "Valid")
	boolean valid;
	@XmlElement(name = "DocumentStatus")
	Status status;

	public DocumentReference() { super(); }

	public DocumentReference(DocumentReferenceType docReference)
	{
		super();
		docReference.cloneTo(this);
	}

	private DocumentReference(CatalogueType document)
	{
		setIssuerParty(document.getProviderParty());
		setUUID(document.getUUIDValue());
		setID(document.getID());
		setIssueDate(document.getIssueDate());
		setIssueTime(document.getIssueTimeValue());
		setDocumentType(document.getClass().getName());
	}

	private DocumentReference(CatalogueDeletionType document)
	{
		setIssuerParty(document.getProviderParty());
		setUUID(document.getUUIDValue());
		setID(document.getID());
		setIssueDate(document.getIssueDate());
		setIssueTime(document.getIssueTimeValue());
		setDocumentType(document.getClass().getName());
	}

	private DocumentReference(OrderType order)
	{
		setIssuerParty(order.getBuyerCustomerParty().getParty());
		setUUID(order.getUUIDValue());
		setID(order.getID());
		setIssueDate(order.getIssueDate());
		setIssueTime(order.getIssueTimeValue());
		setDocumentType(order.getClass().getName());
	}

	private DocumentReference(OrderResponseType document)
	{
		setIssuerParty(document.getSellerSupplierParty().getParty());
		setUUID(document.getUUIDValue());
		setID(document.getID());
		setIssueDate(document.getIssueDate());
		setIssueTime(document.getIssueTimeValue());
		setDocumentType(document.getClass().getName());
	}

	private DocumentReference(OrderResponseSimpleType document)
	{
		setIssuerParty(document.getSellerSupplierParty().getParty());
		setUUID(document.getUUIDValue());
		setID(document.getID());
		setIssueDate(document.getIssueDate());
		setIssueTime(document.getIssueTimeValue());
		setDocumentType(document.getClass().getName());
	}

	private DocumentReference(OrderChangeType document)
	{
		setIssuerParty(document.getBuyerCustomerParty().getParty());
		setUUID(document.getUUIDValue());
		setID(document.getID());
		setIssueDate(document.getIssueDate());
		setIssueTime(document.getIssueTimeValue());
		setDocumentType(document.getClass().getName());
	}

	private DocumentReference(OrderCancellationType document)
	{
		setIssuerParty(document.getBuyerCustomerParty().getParty());
		setUUID(document.getUUIDValue());
		setID(document.getID());
		setIssueDate(document.getIssueDate());
		setIssueTime(document.getIssueTimeValue());
		setDocumentType(document.getClass().getName());
	}

	private DocumentReference(ApplicationResponseType document)
	{
		setIssuerParty(document.getSenderParty());
		setUUID(document.getUUIDValue());
		setID(document.getID());
		setIssueDate(document.getIssueDate());
		setIssueTime(document.getIssueTimeValue());
		setDocumentType(document.getClass().getName());
	}

	private DocumentReference(InvoiceType document)
	{
		setIssuerParty(document.getAccountingSupplierParty().getParty());
		setUUID(document.getUUIDValue());
		setID(document.getID());
		setIssueDate(document.getIssueDate());
		setIssueTime(document.getIssueTimeValue());
		setDocumentType(document.getClass().getName());
	}

	public DocumentReference(PartnershipRequest document)
	{
		setIssuerParty(document.getRequestedParty());
		setUUID(document.getIDValue());
		setID(document.getID());
		setIssueDate(document.getIssueTime());
		setIssueTime(document.getIssueTime());
		setDocumentType(document.getClass().getName());
	}

	/**
	 * Creates ne instance of {@link DocumentReference}
	 * @param document document which reference is to be created
	 * @param status document's status
	 * @return {@link DocumentReference}
	 * @throws DetailException if a document of an unexpected type is passed to the method
	 */
	public static <T> DocumentReference newInstance(T document, Status status) throws DetailException
	{
		DocumentReference docReference = null;
		final Class<? extends Object> documentClazz = document.getClass();
		if(documentClazz == OrderType.class)
			docReference = new DocumentReference((OrderType) document);
		else if(documentClazz == OrderResponseType.class)
			docReference = new DocumentReference((OrderResponseType) document);
		else if(documentClazz == OrderResponseSimpleType.class)
			docReference = new DocumentReference((OrderResponseSimpleType) document);
		else if(documentClazz == OrderChangeType.class)
			docReference = new DocumentReference((OrderChangeType) document);
		else if(documentClazz == OrderCancellationType.class)
			docReference = new DocumentReference((OrderCancellationType) document);
		else if(documentClazz == ApplicationResponseType.class)
			docReference = new DocumentReference((ApplicationResponseType) document);
		else if(documentClazz == InvoiceType.class)
			docReference = new DocumentReference((InvoiceType) document);
		else if(documentClazz == PartnershipRequest.class)
			docReference = new DocumentReference((PartnershipRequest) document);
		else if(documentClazz == CatalogueType.class)
			docReference = new DocumentReference((CatalogueType) document);
		else if(documentClazz == CatalogueDeletionType.class)
			docReference = new DocumentReference((CatalogueDeletionType) document);
		else
			throw new DetailException("Document of an unexpected type has been passed for creation of the Document Reference.");

		final XMLGregorianCalendar now = InstanceFactory.getDate();
		docReference.setReceivedTime(now);
		docReference.setStatus(status);
		return docReference;
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
