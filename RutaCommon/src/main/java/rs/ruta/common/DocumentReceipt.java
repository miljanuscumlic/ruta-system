package rs.ruta.common;

import java.util.UUID;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import rs.ruta.common.DocumentReference.Status;
import rs.ruta.common.datamapper.DetailException;

/**
 * Class describing the receipt of the {@code UBL} document by a {@link #receiverParty} in the Ruta System.
 */
@XmlRootElement(name = "DocumentReceipt")
@XmlType(name = "DocumentReceipt")
@XmlAccessorType(XmlAccessType.NONE)
public class DocumentReceipt
{
	/**
	 * Sender of the Document Receipt.
	 */
	@XmlElement(name = "SenderParty")
	private PartyType senderParty;
	/**
	 * Receiver of the Document Receipt.
	 */
	@XmlElement(name = "ReceiverParty")
	private PartyType receiverParty;
	@XmlElement(name = "ID")
	private IDType id;
	@XmlElement(name = "DocumentReference")
	private DocumentReference documentReference;

	public DocumentReceipt() {}

	/**
	 * Creates new {@link DocumentReceipt}.
	 * @param document
	 * @param status document's status
	 * @return
	 * @throws DetailException if a document of an unexpected type is passed to the method
	 */
	public static <T> DocumentReceipt newInstance(T document, Status status) throws DetailException
	{
		DocumentReceipt documentReceipt = new DocumentReceipt();
		documentReceipt.setID(UUID.randomUUID().toString());
		documentReceipt.setSenderParty(InstanceFactory.getDocumentReceiverParty(document));
		documentReceipt.setReceiverParty(InstanceFactory.getDocumentSenderParty(document));
		final DocumentReference docReference = DocumentReference.newInstance(document, status);
		documentReceipt.setDocumentReference(docReference);
		return documentReceipt;
	}

	/**
	 * Creates new {@link DocumentReceipt} setting the status of the received document to {@code CORR_RECEIVED}.
	 * @param document
	 * @return
	 * @throws DetailException if a document of an unexpected type is passed to the method
	 */
	public static <T> DocumentReceipt newInstance(T document) throws DetailException
	{
		DocumentReceipt documentReceipt = new DocumentReceipt();
		documentReceipt.setID(UUID.randomUUID().toString());
		documentReceipt.setSenderParty(InstanceFactory.getDocumentReceiverParty(document));
		documentReceipt.setReceiverParty(InstanceFactory.getDocumentSenderParty(document));
		final DocumentReference docReference = DocumentReference.newInstance(document, DocumentReference.Status.CORR_RECEIVED);
		documentReceipt.setDocumentReference(docReference);
		return documentReceipt;
	}

	/**
	 * Gets the {@link PartyType sender} of the {@link DocumentReceipt}.
	 * @return
	 */
	public PartyType getSenderParty()
	{
		return senderParty;
	}

	/**
	 * Sets the {@link PartyType sender} of the {@link DocumentReceipt}.
	 */
	public void setSenderParty(PartyType senderParty)
	{
		this.senderParty = senderParty;
	}

	/**
	 * Gets the {@link PartyType receiver} of the {@link DocumentReceipt}.
	 * @return
	 */
	public PartyType getReceiverParty()
	{
		return receiverParty;
	}

	/**
	 * Sets the receiverr of the {@link DocumentReceipt}.
	 */
	public void setReceiverParty(PartyType receiverParty)
	{
		this.receiverParty = receiverParty;
	}

	public IDType getID()
	{
		return id;
	}

	public String getIDValue()
	{
		return id != null ? id.getValue() : null;
	}

	public void setID(IDType id)
	{
		this.id = id;
	}

	/**
	 * Sets a new value for an {@link IDType ID} field. If ID is {@code null} it creates a new
	 * {@link IDType ID} object.
	 * @param value value to set
	 * @return {@link IDType ID} object
	 */
	public IDType setID(@Nullable final String value)
	{
		if(id == null)
			id = new IDType(value);
		id.setValue(value);
		return id;
	}

	/**
	 * Gets the {@link DocumentReference document reference}.
	 * @return document reference
	 */
	public DocumentReference getDocumentReference()
	{
		return documentReference;
	}

	public void setDocumentReference(DocumentReference documentReference)
	{
		this.documentReference = documentReference;
	}

}
