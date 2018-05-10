package rs.ruta.client.correspondence;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueDateType;
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
	private Semaphore threadStopped =  new Semaphore(0);
	/**
	 * True when correspondence is stopped by {@link #stop()} method call (invoked usually on closing
	 * of the application).
	 */
	@XmlElement(name = "Stopped")
	protected boolean stopped;
	@XmlElement(name = "CorrespondentIdentification")
	protected PartyIdentificationType correspondentIdentification;
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
	 * Starts correspondence thread or continues its execution.
	 */
	public void start()
	{
		if(thread == null)
		{
			thread = new Thread(this);
			thread.start();
			stopped = false;
		}
	}

	/**
	 * Stops correspondence and initiates stoppage of its thread.
	 * @throws InterruptedException
	 */
	public void stop() throws InterruptedException
	{
		if(thread != null)
		{
			stopped = true;
			final Thread stopThread = thread;
			thread = null;
			stopThread.interrupt();
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
				thread.notify();
			}
		}
	}

	/**
	 * Blocks correspondence thread.
	 * @throws InterruptedException if any thread interrupted the current thread before or while
	 * the current thread was waiting for a notification
	 */
	public void block() throws InterruptedException
	{
		if(thread != null)
		{
			synchronized(thread)
			{
				thread.wait();
			}
		}
	}

	/**
	 * Blocks correspondence thread for specified amount of time.
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
				thread.wait(timeout);
			}
		}
	}

	/**
	 * Tests whether correspondence thread is alive.
	 * @return true if thread is alive
	 */
	public boolean isAlive()
	{
		return thread != null ? thread.isAlive() : false;
	}

	/**
	 * Test whether the correspondence is stopped by invoked {@link #stop()} method. The thread of it might be still alive.
	 * @return true if correspondence is stopped
	 */
	public boolean isStopped()
	{
		return stopped;
	}

	public void setStopped(boolean stopped)
	{
		this.stopped = stopped;
	}

	public PartyIdentificationType getCorrespondentIdentification()
	{
		return correspondentIdentification;
	}

	public void setCorrespondentIdentification(PartyIdentificationType partyIdentification)
	{
		this.correspondentIdentification = partyIdentification;
	}

	public PartyType getCorrespondentParty()
	{
		return correspondentParty;
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

	public void setCorrespondentParty(PartyType correspondentParty)
	{
		this.correspondentParty = correspondentParty;
	}

	public void setCorrespondentIdentification(String correspondentID)
	{
		if(correspondentIdentification == null)
			correspondentIdentification = new PartyIdentificationType();
		correspondentIdentification.setID(correspondentID);
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

	public void setDocumentReferences(
			ArrayList<DocumentReference> documentReferences)
	{
		this.documentReferences = documentReferences;
	}

	/**
	 * Gets the {@link DocumentReference} at passed index.
	 * @param index index of the document reference
	 * @return document reference
	 * @throws IndexOutOfBoundsException if there is no document reference with the passed index
	 */
	public DocumentReference getDocumentReferenceAtIndex(int index)
	{
		if(documentReferences == null)
			documentReferences = new ArrayList<>();
		return documentReferences.get(index);
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
	 * @param uuid document's UUID
	 * @param id document's ID
	 * @param issueDate issue date of referenced document
	 * @param issueTime issue time of referenced document
	 * @param docType document's type as fully qualified name
	 */
	public void addDocumentReference(String uuid, String id, XMLGregorianCalendar issueDate,
			XMLGregorianCalendar issueTime, String docType)
	{
		final DocumentReference docReference = new DocumentReference();
		docReference.setDocumentType(docType);
		docReference.setUUID(uuid);
		docReference.setID(id);
		docReference.setIssueDate(issueDate);
		docReference.setIssueTime(issueTime);
		final XMLGregorianCalendar now = InstanceFactory.getDate();
		docReference.setReceivedTime(now);
		getDocumentReferences().add(docReference);
//		System.out.println(InstanceFactory.getLocalDateTimeAsString(lastActivityTime));
//		System.out.println(InstanceFactory.getLocalDateTimeAsString(issueDate));
//		System.out.println(InstanceFactory.getLocalDateTimeAsString(issueTime));
/*		final XMLGregorianCalendar issueDateTime = issueDate;
		issueDateTime.setTime(issueTime.getHour(), issueTime.getMinute(), issueTime.getSecond());
		System.out.println(InstanceFactory.getLocalDateTimeAsString(issueDateTime));*/
		setLastActivityTime(now);
//		System.out.println(InstanceFactory.getLocalDateTimeAsString(lastActivityTime));
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
/*		final XMLGregorianCalendar issueDateTime = docReference.getIssueDateValue();
		final XMLGregorianCalendar issueTime = docReference.getIssueTimeValue();
		issueDateTime.setTime(issueTime.getHour(), issueTime.getMinute(), issueTime.getSecond());*/
		setLastActivityTime(now);
	}

	/**
	 * Gets the time of last update of the {@link Correspondence}.
	 * @return time as String
	 */
	//MMM this method may be superfluos because getLastActivityTime is to be used
	public String getTime()
	{
		DocumentReference lastRef = null;
		final int count = getDocumentReferenceCount();
		String time = null;
		if(count != 0)
		{
			lastRef = getDocumentReferenceAtIndex(count - 1);
			time = InstanceFactory.getLocalDateTimeAsString(lastRef.getReceivedTime());
		}
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
	 * @return time of the last document in the correspondence
	 */
	public XMLGregorianCalendar getLastDocumentIssueTime()
	{
		XMLGregorianCalendar issueDateTime = null;
		final int referenceCount = getDocumentReferenceCount();
		if(referenceCount != 0)
		{
			final DocumentReference lastDocRef = getDocumentReferenceAtIndex(referenceCount - 1);
			issueDateTime = InstanceFactory.mergeDateTime(lastDocRef.getIssueDateValue(), lastDocRef.getIssueTimeValue());
		}
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
	 * Signals that the thread is about to be stopped.
	 */
	public void signalThreadStopped()
	{
		threadStopped.release();
	}

	/**
	 * Waits for the signal that the thread is about to be stopped.
	 * @throws InterruptedException
	 */
	public void waitThreadStopped() throws InterruptedException
	{
		threadStopped.acquire();
	}

	/**
	 * Stores {@link Correspondence} to the database.
	 */
	public void store()
	{
		new Thread( () ->
		{
			try
			{
				doStore();
			}
			catch (DetailException e)
			{
				logger.error("Correspondence could not be stored to the database. Exception is: ", e);
			}
		}).start();
	}

	/**
	 * Subclass specific method for storing object of {@link Correspondence}'s subclasses.
	 * @throws DetailException if object could not be stored to the database
	 */
	protected abstract void doStore() throws DetailException;

}