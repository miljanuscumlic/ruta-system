package rs.ruta.client.correspondence;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.ubl21.UBL21Validator;
import com.helger.ubl21.UBL21ValidatorBuilder;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.client.CorrespondenceEvent;
import rs.ruta.client.RutaClientFrameEvent;
import rs.ruta.common.DocumentReference;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.MapperRegistry;

/**
 * Abstract class that serves as a marker class for all correspondences in {@code Ruta System}.
 * Correspondence is a process of exchanging {@code UBL business documents} among parties
 * in other to do the bussiness. One type of a correspondences is a compound process of ordering,
 * invocing and paying ordered goods and services.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public abstract class Correspondence extends RutaProcess implements Runnable
{
	private static Logger logger = LoggerFactory.getLogger("rs.ruta.client");
	protected volatile Thread thread = null;
	/**
	 * Signals that correspondence thread is close to be finished. Signaling is by {@link Semaphore#release()}
	 * method which is invoked at the end of the correspondence's {@link Runnable#run()} method.
	 */
	private volatile Semaphore threadStopped = new Semaphore(0);
	/**
	 * Used for synchronization with the calling thread in sutuations when correspondence
	 * shoud be started in a state in which the correspondence is blocked and waiting for a notification.
	 */
	private volatile Semaphore threadBlocked = new Semaphore(0);
	/**
	 *	Used for sequential database access for a single correspondence.
	 */
	private ExecutorService sequentialAccess = Executors.newSingleThreadExecutor();
	/**
	 * True when correspondence is stopped by {@link #stop()} method call (invoked usually on closing
	 * of the application).
	 */
//	@XmlElement(name = "Stopped")
	protected boolean stopped;
	/**
	 * True when correspondence is blocked by {@link #block()} method call.
	 */
	protected boolean blocked;
	/**
	 * True when there is a recent update for this correspondence from the CDR service.
	 */
	@XmlElement(name = "RecentlyUpdated")
	private boolean recentlyUpdated;
	/**
	 * True when the user has given up of started correspondence so it can be closed.
	 */
	protected boolean discarded;
	/**
	 * False when last received document does not conform to the UBL standard.
	 */
	protected boolean valid;
	@XmlElement(name = "CorrespondentParty")
	protected PartyType correspondentParty;
	/**
	 * {@link DocumentReference Document references} of all documents of the {@code Correspondence}.
	 */
	@XmlElement(name = "DocumentReference")
	protected ArrayList<DocumentReference> documentReferences;
	/**
	 * Correspondence's name.
	 */
	@XmlElement(name = "CorrespondenceName")
	protected String name;
	@XmlElement(name = "CreationTime")
	private XMLGregorianCalendar creationTime;
	@XmlElement(name = "LastActivityTime")
	private XMLGregorianCalendar lastActivityTime;

	/**
	 * Starts correspondence thread.
	 */
	public void start()
	{
		if(thread == null)
		{
			stopped = blocked = discarded = false;
			valid = true;
			thread = new Thread(this);
			thread.start();
		}
	}

	/**
	 * Stops correspondence initiating stoppage of its thread by throwing an {@link InterruptedException}.
	 * @throws InterruptedException if thread of the correspondence has a non-{@code null} value
	 */
	public void stop() throws InterruptedException
	{
		if(thread != null)
		{
			stopped = true;
			blocked = false;
			final Thread stopThread = thread;
			thread = null;
			stopThread.interrupt();
			signalThreadStopped();
		}
	}

	/**
	 * Notifies correspondence thread so it can proceed with its execution.
	 */
	public void proceed()
	{
		if(thread != null)
		{
			synchronized(thread)
			{
				blocked = false;
				thread.notify();
			}
		}
	}

	/**
	 * Blocks correspondence thread and signals pertaining {@link Semaphore} that it is blocked.
	 * @throws InterruptedException if any thread interrupted the current thread before or while
	 * the current thread was waiting for a notification
	 * @throw {@link StateActivityException} if thread is not alive
	 */
	public void block() throws InterruptedException
	{
		if(thread != null)
		{
			synchronized(thread)
			{
				blocked = true;
				signalThreadBlocked();
				thread.wait();
			}
		}
		else // MMM is this necessary?
			throw new StateActivityException("Correspondence thread could not be blocked. It is not alive!");
	}

	/**
	 * Blocks correspondence thread for specified amount of time and signals pertaining {@link Semaphore}
	 * that it is blocked.
	 * @param timeout maximum time to wait in milliseconds
	 * @throws InterruptedException if any thread interrupted the current thread before or while
	 * the current thread was waiting for a notification
	 */
	public void block(long timeout) throws InterruptedException
	{
		if(thread != null)
		{
			synchronized(thread)
			{
				blocked = true;
				signalThreadBlocked();
				thread.wait(timeout);
			}
		}
	}

	/**
	 * Tests whether correspondence thread is alive.
	 * @return true if thread is alive; false otherwise
	 */
	public boolean isAlive()
	{
		return thread != null ? thread.isAlive() : false;
	}

	/**
	 * Tests whether correspondence thread is blocked and waiting for a notification.
	 * @return true if thread is blocked; false otherwise
	 */
	public boolean isBlocked()
	{
		return blocked;
	}

	public void setBlocked(boolean blocked)
	{
		this.blocked = blocked;
	}

	/**
	 * Tests whether the correspondence is stopped by invoked {@link #stop()} method. The thread of it might be still alive.
	 * @return true if correspondence is stopped; false otherwise
	 */
	public boolean isStopped()
	{
		return stopped;
	}

	public void setStopped(boolean stopped)
	{
		this.stopped = stopped;
	}

	/**
	 * Tests whether the correspondence is recently updated by adding new document to it.
	 * @return true if correspondence is recently updated; false otherwise
	 */
	public boolean isRecentlyUpdated()
	{
		return recentlyUpdated;
	}

	public void setRecentlyUpdated(boolean recentlyUpdated)
	{
		this.recentlyUpdated = recentlyUpdated;
	}

	/**
	 * Tests whether the correspondence is discarded by the user.
	 * @return true if correspondence is discarded; false otherwise
	 */
	public boolean isDiscarded()
	{
		return discarded;
	}

	/**
	 * Sets the flag that denotes that the user has given up of started correspondence.
	 * @param discarded
	 */
	public void setDiscarded(boolean discarded)
	{
		this.discarded = discarded;
	}

	/**
	 * Tests whether the last received document is conforming to the UBL standard.
	 * @return false if received document does not conform to the UBL; true otherwise
	 */
	public boolean isValid()
	{
		return valid;
	}

	/**
	 * Sets the flag that denotes the last received document is conforming to the UBL standard.
	 * @param valid
	 */
	public void setValid(boolean valid)
	{
		this.valid = valid;
	}

	public PartyType getCorrespondentParty()
	{
		return correspondentParty;
	}

	public void setCorrespondentParty(PartyType correspondentParty)
	{
		this.correspondentParty = correspondentParty;
	}

	/**
	 * Gets the name of the Party which MyParty is corresponding to.
	 * @return party name or {@code null} if name is not set
	 */
	public String getCorrespondentPartyName()
	{
		String partyName = null;
		if(correspondentParty != null)
		{
			try
			{
				partyName = correspondentParty.getPartyNameAtIndex(0).getNameValue();
			}
			catch(Exception e)
			{}
		}
		return partyName;
	}

	/**
	 * Gets correspondence's ID or {@code null} if it is not set.
	 * @return id or {@code null}
	 */
	public String getCorrespondentID()
	{
		String id = null;
		if(correspondentParty != null)
		{
			try
			{
				id = correspondentParty.getPartyIdentificationAtIndex(0).getIDValue();
			}
			catch(Exception e)
			{
				id = null;
			}
		}
		return id;
	}

	/**
	 * Gets {@link DocumentReference Document references} of all documents of the {@code Correspondence}.
	 * @return
	 */
	public ArrayList<DocumentReference> getDocumentReferences()
	{
		if(documentReferences == null)
			documentReferences = new ArrayList<>();
		return documentReferences;
	}

	public void setDocumentReferences(ArrayList<DocumentReference> documentReferences)
	{
		this.documentReferences = documentReferences;
	}

	/**
	 * Gets the {@link DocumentReference} of the document which has specific {@code UUID}.
	 * @param documentUUID UUID of the document
	 * @return document reference or {@code null} if there is no matching document reference
	 */
	public DocumentReference getDocumentReference(String documentUUID)
	{
		DocumentReference docReference = null;
		if(documentReferences != null && documentUUID != null)
		{
			for(DocumentReference ref : documentReferences)
				if(documentUUID.equals(ref.getUUIDValue()))
				{
					docReference = ref;
					break;
				}
		}
		return docReference;
	}

	/**
	 * Gets the {@link DocumentReference} at passed index.
	 * @param index index of the document reference
	 * @return document reference or {@code null} if there is no document reference with the passed index
	 */
	public DocumentReference getDocumentReferenceAtIndex(int index)
	{
		DocumentReference docRef = null;
		if(documentReferences != null)
		{
			try
			{
				docRef = documentReferences.get(index);
			}
			catch(IndexOutOfBoundsException e) { }
		}
		return docRef;
	}

	/**
	 * Gets the {@link DocumentReference} at last index.
	 * @return document reference or {@code null} if there is no document references
	 */
	public DocumentReference getLastDocumentReference()
	{
		return getDocumentReferenceAtIndex(getDocumentReferenceCount() - 1);
	}

	/**
	 * Gets current number of documents in the {@link Correspondence}.
	 * @return number of documents
	 */
	public int getDocumentReferenceCount()
	{
		return getDocumentReferences().size();
	}

	/**
	 * Adds new {@link DocumentReference document reference}.
	 * <p>Notifies listeners registered for this type of the {@link CorrespondenceEvent event}.</p>
	 * @param issuerParty Party that issued referenced document
	 * @param uuid referenced document's UUID
	 * @param id referenced document's ID
	 * @param issueDate issue date of referenced document
	 * @param issueTime issue time of referenced document
	 * @param docType document's type as fully qualified name
	 * @param status {@link DocumentReference.Status transport status} of the document
	 */
	public void addDocumentReference(PartyType issuerParty, String uuid, String id,
			XMLGregorianCalendar issueDate, XMLGregorianCalendar issueTime, String docType,
			DocumentReference.Status status)
	{
		final DocumentReference docReference = new DocumentReference();
		docReference.setIssuerParty(issuerParty);
		docReference.setDocumentType(docType);
		docReference.setUUID(uuid);
		docReference.setID(id);
		docReference.setIssueDate(issueDate);
		docReference.setIssueTime(issueTime);
		final XMLGregorianCalendar now = InstanceFactory.getDate();
		docReference.setReceivedTime(now);
		docReference.setStatus(status);
		getDocumentReferences().add(docReference);
		setLastActivityTime(now);
		client.getMyParty().notifyListeners(new CorrespondenceEvent(this, CorrespondenceEvent.CORRESPONDENCE_UPDATED));
	}

	/**
	 * Adds new {@link DocumentReferenceType document reference}. This is just convinient method.
	 * @param docReference document reference to add
	 */
	public void addDocumentReference(DocumentReferenceType docReference)
	{
		addDocumentReference(new DocumentReference(docReference));
	}

	/**
	 * Adds new {@link DocumentReference document reference}.
	 * @param docReference document reference to add
	 */
	public void addDocumentReference(DocumentReference docReference)
	{
		final XMLGregorianCalendar now = InstanceFactory.getDate();
		docReference.setReceivedTime(now);
		getDocumentReferences().add(docReference);
		setLastActivityTime(now);
	}

	/**
	 * Updates {@link DocumentReference.Status transport status} of the document of the correspondence.
	 * <p>Notifies listeners registered for this type of the {@link CorrespondenceEvent event}.</p>
	 * @param docReference {@link DocumentReference document reference}
	 * @param status status to be set
	 */
	public void updateDocumentStatus(DocumentReference docReference, DocumentReference.Status status)
	{
		docReference.setStatus(status);
		client.getMyParty().notifyListeners(new CorrespondenceEvent(this, CorrespondenceEvent.CORRESPONDENCE_UPDATED));
	}

	/**
	 * Gets the time of last received document of the {@link Correspondence}.
	 * @return time as String or {@code null} if there is no document in the correspondence
	 */
	public String getLastReceivedTime() // same as lastActivityTime
	{
		DocumentReference lastRef = getLastDocumentReference();
		String time = null;
		if(lastRef != null)
			time = InstanceFactory.getLocalDateTimeAsString(lastRef.getReceivedTime());
		return time;
	}

	/**
	 * Gets the name of the {@link Correspondence}.
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the {@link Correspondence}.
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the time of the creation of the {@link Correspondence}.
	 * @return
	 */
	public XMLGregorianCalendar getCreationTime()
	{
		return creationTime;
	}

	public void setCreationTime(XMLGregorianCalendar creationTime)
	{
		this.creationTime = creationTime;
	}

	/**
	 * Gets the time of last activity of the {@link Correspondence}.
	 * @return
	 */
	public XMLGregorianCalendar getLastActivityTime()
	{
		return lastActivityTime;
	}

	public void setLastActivityTime(XMLGregorianCalendar lastActivityTime)
	{
		this.lastActivityTime = lastActivityTime;
	}

	/**
	 * Gets the issue date and time of the last document in the correspondence.
	 * @return issue date and time of the last document in the correspondence or {@code null} if there are
	 * no documents in it
	 */
	public XMLGregorianCalendar getLastDocumentIssueTime()
	{
		XMLGregorianCalendar issueDateTime = null;
		final DocumentReference lastDocRef = getLastDocumentReference();
		if(lastDocRef != null)
			issueDateTime = InstanceFactory.mergeDateTime(lastDocRef.getIssueDateValue(), lastDocRef.getIssueTimeValue());
		return issueDateTime;
	}

	/**
	 * Gets {@link Semaphore} that tells whether the {@code Correspondence}'s thread is about to be
	 * stopped.
	 * @return the threadStopped
	 */
	public Semaphore getStoppedSemaphore()
	{
		return threadStopped;
	}

	/**
	 * Sets {@link Semaphore} that tells whether the {@code Correspondence}'s thread is about to be
	 * stopped.
	 * @param threadStopped the threadStopped to set
	 */
	public void setStoppedSemaphore(Semaphore sem)
	{
		this.threadStopped = sem;
	}

	/**
	 * Signals that the correspondence thread is about to be stopped.
	 */
	public void signalThreadStopped()
	{
		threadStopped.release();
	}

	/**
	 * Waits for the signal that the correspondence thread is about to be stopped.
	 * @throws InterruptedException
	 */
	public void waitThreadStopped() throws InterruptedException
	{
		threadStopped.acquire();
	}

	/**
	 * Signals that the correspondence thread is about to be blocked.
	 */
	public void signalThreadBlocked()
	{
		threadBlocked.release();
	}

	/**
	 * Waits for the signal that the thread is about to be blocked.
	 * @throws InterruptedException
	 */
	public void waitThreadBlocked() throws InterruptedException
	{
		threadBlocked.acquire();
	}

	/**
	 * Stores {@link Correspondence} to the database in a new Thread.
	 */
	public void store()
	{
		sequentialAccess.submit(() ->
		{
			try
			{
				doStore();
			}
			catch (DetailException e)
			{
				logger.error("Correspondence could not be stored to the database. Exception is: ", e);
			}
		});
	}

	/**
	 * Deletes {@link Correspondence} from the database in a new Thread.
	 */
	public void delete()
	{
		sequentialAccess.submit(() -> {
			try
			{
				doDelete();
			}
			catch (DetailException e)
			{
				logger.error("Correspondence could not be deleted from the database. Exception is: ", e);
			}
		});
	}

	/**
	 * Returns String representing object of {@link Correspondence} class. Used as a node name in the tree model.
	 * @return the name of the correspondence or {@code null} if name is not set
	 */
	@Override
	public String toString()
	{
		return name;
	}

	/**
	 * Subclass hook method for storing object of {@link Correspondence}'s subclasses in the database.
	 * @throws DetailException if object could not be stored to the database
	 */
	protected abstract void doStore() throws DetailException;

	/**
	 * Subclass hook method for deleting object of {@link Correspondence}'s subclasses from the database.
	 * @throws DetailException if object could not be stored to the database
	 */
	protected abstract void doDelete() throws DetailException;

	/**
	 * Gets the most recently received document of specific type.
	 * @param <T>
	 * @param documentClazz class of the document
	 * @return document or {@code null} if document could not be found in correspondence
	 * @throws StateActivityException if document could not be retrieved from the database
	 */
	public <T> T getLastDocument(Class<T> documentClazz)
	{
		T document = null;
		String id = null;
		/*final ArrayList<DocumentReference> allReferences = getDocumentReferences();
		for(DocumentReference documentReference : allReferences)
			if(documentClazz.getName().equals(documentReference.getDocumentTypeValue()))
				id = documentReference.getUUIDValue();*/

		final DocumentReference documentReference = getLastDocumentReference(documentClazz);
		if (documentReference != null)
			id = documentReference.getUUIDValue();
		if(id != null)
		{
			try
			{
				document = MapperRegistry.getInstance().getMapper(documentClazz).find(id);
			}
			catch (DetailException e)
			{
				throw new StateActivityException("Document could not be retrieved from the database!", e);
			}
		}
		return document;
	}

	/**
	 * Gets the document which {@link DocumentReference} is passed as an argument.
	 * @param documentReference document reference
	 * @return document or {@code null} if document could not be found in correspondence
	 * @throws StateActivityException if document could not be retrieved from the database
	 */
	public Object getDocument(DocumentReference documentReference)
	{
		Object document = null;
		String id = null;
		if (documentReference != null)
			id = documentReference.getUUIDValue();
		if(id != null)
		{
			try
			{
				final Class<?> documentClazz = Class.forName(documentReference.getDocumentTypeValue());
				document = MapperRegistry.getInstance().getMapper(documentClazz).find(id);
			}
			catch (DetailException | ClassNotFoundException e)
			{
				throw new StateActivityException("Document could not be retrieved from the database!", e);
			}
		}
		return document;
	}

	/**
	 * Gets the most recently received document's reference of the document of specific type.
	 * @param documentClazz class of the document
	 * @return document or {@code null} if document could not be found in correspondence
	 * @throws StateActivityException if document could not be retrieved from the database
	 */
	public DocumentReference getLastDocumentReference(Class<?> documentClazz)
	{
		DocumentReference documentReference = null;
		final ArrayList<DocumentReference> allReferences = getDocumentReferences();
		final int referenceCount = allReferences.size();
		for(int i = referenceCount - 1; i >= 0; i--)
			if(documentClazz.getName().equals(allReferences.get(i).getDocumentTypeValue()))
			{
				documentReference = allReferences.get(i);
				break;
			}

		return documentReference;
	}

	/**
	 * Gets the document at last index.
	 * @return document or {@code null} if there is no documents
	 */
	public Object getLastDocument()
	{
		return getDocumentAtIndex(getDocumentReferenceCount() - 1);
	}

	/**
	 * Gets the document of the coorespondence with passed index.
	 * @param index index of document references
	 * @return document document or {@code null} if document could not be found in correspondence
	 * @throws StateActivityException if document could not be retrieved from the database
	 */
	public Object getDocumentAtIndex(int index)
	{
		Object document = null;
		final DocumentReference documentReference = getDocumentReferenceAtIndex(index);
		if(documentReference != null)
		{
			final String id = documentReference.getUUIDValue();
			if(id != null)
			{
				final String documentType = documentReference.getDocumentTypeValue();
				try
				{
					final Class<?> documentClazz = Class.forName(documentType);
					document = MapperRegistry.getInstance().getMapper(documentClazz).find(id);
				}
				catch (DetailException | ClassNotFoundException e)
				{
					throw new StateActivityException("Document could not be retrieved from the database!", e);
				}
			}
		}
		return document;
	}

	/**
	 * Validates document of the most recently added {@link DocumentReference document reference}
	 * to the correspondence, to the UBL standard conformance.
	 * @throws StateActivityException if document validation fails
	 */
	void validateLastDocument() throws StateActivityException
	{
		validateDocument(getLastDocument());
	}

	/**
	 * Validates document of the correspondence to the UBL standard conformance.
	 * @param document to validate
	 */
	void validateDocument(@Nullable Object document) throws StateActivityException
	{
		if(document != null)
		{
			DocumentReference docReference = null;
			if(document.getClass() == OrderType.class)
			{
				docReference = getDocumentReference(((OrderType) document).getUUIDValue());
				if(!InstanceFactory.validateUBLDocument(document,
						doc -> UBL21Validator.order().validate((OrderType) doc)))
				{
					valid = false;
					if(docReference != null)
						updateDocumentStatus(docReference, DocumentReference.Status.UBL_INVALID);
					throw new StateActivityException("Order " + ((OrderType) document).getIDValue() +
							" does not conform to UBL standard!");
				}
			}
			else if(document.getClass() == OrderResponseType.class)
			{
				docReference = getDocumentReference(((OrderResponseType) document).getUUIDValue());
				if(!InstanceFactory.validateUBLDocument(document,
						doc -> UBL21Validator.orderResponse().validate((OrderResponseType) doc)))
				{
					valid = false;
					if(docReference != null)
						updateDocumentStatus(docReference, DocumentReference.Status.UBL_INVALID);
					throw new StateActivityException("Order Response Simple " + ((OrderResponseType) document).getIDValue() +
							" does not conform to UBL standard!");
				}
			}
			else if(document.getClass() == OrderResponseSimpleType.class)
			{
				docReference = getDocumentReference(((OrderResponseSimpleType) document).getUUIDValue());
				if(!InstanceFactory.validateUBLDocument(document,
						doc -> UBL21Validator.orderResponseSimple().validate((OrderResponseSimpleType) doc)))
				{
					valid = false;
					if(docReference != null)
						updateDocumentStatus(docReference, DocumentReference.Status.UBL_INVALID);
					throw new StateActivityException("Order Response Simple " + ((OrderResponseSimpleType) document).getIDValue() +
							" does not conform to UBL standard!");
				}
			}
			else if(document.getClass() == OrderChangeType.class)
			{
				docReference = getDocumentReference(((OrderChangeType) document).getUUIDValue());
				if(!InstanceFactory.validateUBLDocument(document,
						doc -> UBL21Validator.orderChange().validate((OrderChangeType) doc)))
				{
					valid = false;
					if(docReference != null)
						updateDocumentStatus(docReference, DocumentReference.Status.UBL_INVALID);
					throw new StateActivityException("Order Change " + ((OrderChangeType) document).getIDValue() +
							" does not conform to UBL standard!");
				}
			}
			else if(document.getClass() == OrderCancellationType.class)
			{
				docReference = getDocumentReference(((OrderCancellationType) document).getUUIDValue());
				if(!InstanceFactory.validateUBLDocument(document,
						doc -> UBL21Validator.orderCancellation().validate((OrderCancellationType) doc)))
				{
					valid = false;
					if(docReference != null)
						updateDocumentStatus(docReference, DocumentReference.Status.UBL_INVALID);
					throw new StateActivityException("Order Cancellation " + ((OrderCancellationType) document).getIDValue() +
							" does not conform to UBL standard!");
				}
			}
			else if(document.getClass() == ApplicationResponseType.class)
			{
				docReference = getDocumentReference(((ApplicationResponseType) document).getUUIDValue());
				if(!InstanceFactory.validateUBLDocument(document,
						doc -> UBL21Validator.applicationResponse().validate((ApplicationResponseType) doc)))
				{
					valid = false;
					if(docReference != null)
						updateDocumentStatus(docReference, DocumentReference.Status.UBL_INVALID);
					throw new StateActivityException("Application Response " + ((ApplicationResponseType) document).getIDValue() +
							" does not conform to UBL standard!");
				}
			}
			else if(document.getClass() == InvoiceType.class)
			{
				docReference = getDocumentReference(((InvoiceType) document).getUUIDValue());
				if(!InstanceFactory.validateUBLDocument(document,
						doc -> UBL21Validator.invoice().validate((InvoiceType) doc)))
				{
					valid = false;
					if(docReference != null)
						updateDocumentStatus(docReference, DocumentReference.Status.UBL_INVALID);
					throw new StateActivityException("Invoice " + ((InvoiceType) document).getIDValue() +
							" does not conform to UBL standard!");
				}
			}
			else
				throw new StateActivityException("Document of unexpected type!");
			if(valid && docReference != null)
				updateDocumentStatus(docReference, DocumentReference.Status.UBL_VALID);
		}
		else
			throw new StateActivityException("UBL Validation has failed. Document must not have a null value.");
	}

	/**
	 * Stores most recently added document of the correspondence to the database.
	 * @throws StateActivityException if document could not be stored to the database or found in the correspondence
	 */
	void storeLastDocument() throws StateActivityException
	{
		final DocumentReference docReference = getLastDocumentReference();
		if(docReference != null)
		{
			final String docType = docReference.getDocumentTypeValue();
			final BuyerOrderingProcess process = (BuyerOrderingProcess) getState();
			if(docType.contains("OrderResponseSimpleType"))
				storeOrderResponseSimple(process.getOrderResponseSimple(this));
			else if(docType.contains("OrderResponseType"))
				storeOrderResponse(process.getOrderResponse(this));
			else if(docType.contains("OrderType"))
				storeOrder(process.getOrder(this));
		}
		else
			throw new StateActivityException("Document could not be found in the correspondence.");
	}

	/**
	 * Stores document of the correspondence to the database.
	 * @throws StateActivityException if document is of unexpected type
	 */
	//MMM this should be a method that is overridden in subclasses or hook methods should be used
	//MMM every subclass should deal with documents pertaining its processes
	public void storeDocument(@Nullable Object document) throws StateActivityException
	{
		if(document != null)
		{
			try
			{
				final Class<? extends Object> documentClazz = document.getClass();
				if(documentClazz == OrderType.class)
					MapperRegistry.getInstance().getMapper(OrderType.class).insert(null, (OrderType) document);
				else if(documentClazz == OrderResponseType.class)
					MapperRegistry.getInstance().getMapper(OrderResponseType.class).insert(null, (OrderResponseType) document);
				else if(documentClazz == OrderResponseSimpleType.class)
					MapperRegistry.getInstance().getMapper(OrderResponseSimpleType.class).insert(null, (OrderResponseSimpleType) document);
				else if(documentClazz == OrderChangeType.class)
					MapperRegistry.getInstance().getMapper(OrderChangeType.class).insert(null, (OrderChangeType) document);
				else if(documentClazz == OrderCancellationType.class)
					MapperRegistry.getInstance().getMapper(OrderCancellationType.class).insert(null, (OrderCancellationType) document);
				else if(documentClazz == ApplicationResponseType.class)
					MapperRegistry.getInstance().getMapper(ApplicationResponseType.class).insert(null, (ApplicationResponseType) document);
				else if(documentClazz == InvoiceType.class)
					MapperRegistry.getInstance().getMapper(InvoiceType.class).insert(null, (InvoiceType) document);
				//MMM other document types
				else
					throw new StateActivityException("Document could not be stored to the database because of its unexpected type.");
			}
			catch (DetailException e)
			{
				throw new StateActivityException("Document could not be stored to the database!", e);
			}
		}
		else
			throw new StateActivityException("Document has a null value. As such it cannot be stored to the database!");
	}

	/**
	 * Stores {@link OrderType Order} document to the database.
	 * @param {@link OrderType Order} to store
	 * @throws StateActivityException if document has a {@code null} value or could not be stored to the database
	 */
	public void storeOrder(@Nullable OrderType order) throws StateActivityException
	{
		if(order != null)
		{
			try
			{
				MapperRegistry.getInstance().getMapper(OrderType.class).insert(null, order);
			}
			catch (DetailException e)
			{
				throw new StateActivityException("Order could not be saved to the database!", e);
			}
		}
		else
			throw new StateActivityException("Order has a null value. Order could not be stored to the database!");
	}

	/**
	 * Stores {@link OrderResponseSimpleType Order Response Simple} document to the database.
	 * @param orderResponse {@link OrderResponseSimpleType Order Response Simple} to store
	 * @throws StateActivityException if document has a {@code null} value or could not be stored to the database
	 */
	public void storeOrderResponseSimple(OrderResponseSimpleType orderResponse) throws StateActivityException
	{
		if(orderResponse != null)
		{
			try
			{
				MapperRegistry.getInstance().getMapper(OrderResponseSimpleType.class).insert(null, orderResponse);
			}
			catch (DetailException e)
			{
				throw new StateActivityException("Order could not be saved to the database!", e);
			}
		}
		else
			throw new StateActivityException("Order has a null value. Order could not be saved to the database!");
	}

	/**
	 * Stores {@link OrderResponseType Order Response} document to the database.
	 * @param orderResponse {@link OrderResponseType Order Response Simple} to store
	 * @throws StateActivityException if document has a {@code null} value or could not be stored to the database
	 */
	public void storeOrderResponse(OrderResponseType orderResponse) throws StateActivityException
	{
		if(orderResponse != null)
		{
			try
			{
				MapperRegistry.getInstance().getMapper(OrderResponseType.class).insert(null, orderResponse);
			}
			catch (DetailException e)
			{
				throw new StateActivityException("Order could not be saved to the database!", e);
			}
		}
		else
			throw new StateActivityException("Order has a null value. Order could not be saved to the database!");
	}

}