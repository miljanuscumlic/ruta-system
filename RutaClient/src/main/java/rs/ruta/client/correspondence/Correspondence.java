package rs.ruta.client.correspondence;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyIdentificationType;
import rs.ruta.common.InstanceFactory;

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
	protected volatile Thread thread = null;
	/**
	 * Signals that correspondence thread is close to be finished. Signaling is by {@link Semaphore#release()}
	 * method which is invoked at the end of the correspondence's {@link Runnable#run()} method.
	 */
	protected Semaphore stoppedSemaphore =  new Semaphore(0);
	/**
	 * True when correspondence is stopped by {@link #stop()} method call (invoked usually on closing
	 * of the application).
	 */
	@XmlElement(name = "Stopped")
	protected boolean stopped;
	@XmlElement(name = "CorrespondentIdentification")
	protected PartyIdentificationType correspondentIdentification;
	/**
	 * {@link DocumentReferenceType Document references} of all documents of the {@code Correspondence}.
	 */
	@XmlElement(name = "DocumentReference")
	protected ArrayList<DocumentReferenceType> documentReferences;
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

	public void setCorrespondentIdentification(String correspondentID)
	{
		if(correspondentIdentification == null)
			correspondentIdentification = new PartyIdentificationType();
		correspondentIdentification.setID(correspondentID);
	}

	/**
	 * Gets {@link DocumentReferenceType Document references} of all documents of the {@code Correspondence}.
	 * @return
	 */
	public ArrayList<DocumentReferenceType> getDocumentReferences()
	{
		if(documentReferences == null)
			documentReferences = new ArrayList<>();
		return documentReferences;
	}

	public void setDocumentReferences(
			ArrayList<DocumentReferenceType> documentReferences)
	{
		this.documentReferences = documentReferences;
	}

	/**
	 * Gets the {@link DocumentReferenceType} at passed index.
	 * @param index index of the document reference
	 * @return document reference
	 * @throws IndexOutOfBoundsException if there is no document reference with the passed index
	 */
	public DocumentReferenceType getDocumentReferenceAtIndex(int index)
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
	 * Adds new {@link DocumentReferenceType document reference}.
	 * @param uuid document's UUID
	 * @param id document's ID
	 * @param docType document's type as fully qualified name
	 */
	public void addDocumentReference(String uuid, String id, String docType)
	{
		final DocumentReferenceType docReference = new DocumentReferenceType();
		docReference.setUUID(uuid);
		docReference.setID(id);
		docReference.setDocumentType(docType);
		getDocumentReferences().add(docReference);
	}

	/**
	 * Gets the name of the Party which MyParty is corresponding to.
	 * @return party name or {@code null} if name is not set
	 */
	public String getCorrespondentPartyName()
	{
		// MMM to implement - is it necessary?
		return null;
	}

	/**
	 * Gets the time of last update of the {@link Correspondence}.
	 * @return time as String
	 */
	//MMM this method may be superfluos because getLastActivityTime should be used
	public String getTime()
	{
		DocumentReferenceType lastRef = null;
		final int count = getDocumentReferenceCount();
		String time = null;
		if(count != 0)
		{
			lastRef = getDocumentReferenceAtIndex(count - 1);
			time = InstanceFactory.getLocalDateTimeAsString(lastRef.getIssueTimeValue());
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
	 * Gets {@link Semaphore} that tells whether the {@code Correspondence}'s thread is about to be
	 * stopped.
	 * @return the stoppedSemaphore
	 */
	public Semaphore getStoppedSemaphore()
	{
		return stoppedSemaphore;
	}

	/**
	 * Sets {@link Semaphore} that tells whether the {@code Correspondence}'s thread is about to be
	 * stopped.
	 * @param stoppedSemaphore the stoppedSemaphore to set
	 */
	public void setStoppedSemaphore(Semaphore sem)
	{
		this.stoppedSemaphore = sem;
	}

}