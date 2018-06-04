package rs.ruta.common;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;

/**
 * Class describing the receipt of the {@code UBL} document by a {@link #receiverParty} in the Ruta System.
 */
public class DocumentReceipt
{
	/**
	 * Sender of the document not of the Document Receipt.
	 */
	private PartyType senderParty;
	/**
	 * Receiver of the document not of the Document Receipt.
	 */
	private PartyType receiverParty;
	private IDType uuid;
	private DocumentReference documentReference;

	/**
	 * Gets the {@link PartyType sender} of the document not of the {@link DocumentReceipt}.
	 * @return
	 */
	public PartyType getSenderParty()
	{
		return senderParty;
	}

	/**
	 * Sets the {@link PartyType sender} of the document not of the {@link DocumentReceipt}.
	 */
	public void setSenderParty(PartyType senderParty)
	{
		this.senderParty = senderParty;
	}

	/**
	 * Gets the {@link PartyType receiver} of the document not of the {@link DocumentReceipt}.
	 * @return
	 */
	public PartyType getReceiverParty()
	{
		return receiverParty;
	}

	/**
	 * Sets the receiverr of the document not of the {@link DocumentReceipt}.
	 */
	public void setReceiverParty(PartyType receiverParty)
	{
		this.receiverParty = receiverParty;
	}

	public IDType getUUUID()
	{
		return uuid;
	}

	public void setUUUID(IDType uuid)
	{
		this.uuid = uuid;
	}

	public void setUuid(String value)
	{
		this.uuid.setValue(value);
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
