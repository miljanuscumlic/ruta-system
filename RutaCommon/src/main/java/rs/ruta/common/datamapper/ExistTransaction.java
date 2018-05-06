package rs.ruta.common.datamapper;

import javax.xml.bind.annotation.XmlAccessorType;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@XmlRootElement(name = "ExistTransaction")
@XmlAccessorType(XmlAccessType.NONE)
public abstract class ExistTransaction implements DSTransaction
{
//	@XmlTransient
	protected final static Logger logger = LoggerFactory.getLogger("rs.ruta.common.datamapper");
	@XmlElement(name = "TransactionID", required = true)
	private String transactionID;
	@XmlElement(name = "Timestamp")
	private long timestamp;
//	@XmlTransient
	private boolean enabled;
	/**
	 * True when transaction should be kept alive and not closed i.e. when one transaction has multiple
	 * database operations.
	 */
//	@XmlTransient
	private boolean keepAlive; //MMM: obsolete field
//	@XmlTransient
	private boolean failed;
	@XmlElement(name = "ExistOperation")
	private List<ExistOperation> operations;

	public ExistTransaction()
	{
		operations = new ArrayList<ExistOperation>();
		enabled = true;
		keepAlive = false;
		failed = false;
		timestamp = System.currentTimeMillis();
	}

	/**
	 * Subclass hook method for inserting proper type of a transaction journal in the database.
	 * @throws DetailException if transaction journal could not be inserted
	 */
	protected abstract void insertTransaction() throws DetailException;

	/**
	 * Subclass hook method for deteting a transaction journal from the database.
	 * @throws DetailException if transaction journal could not be deleted
	 */
	protected abstract void deleteTransaction() throws DetailException;

	/**
	 * Opens a transaction by creating a journal document that records every interaction with the database.
	 * @throws TransactionException if journal document could not be saved to the database
	 * @see rs.ruta.common.datamapper.DSTransaction#open()
	 */
	@Override
	public void open() throws TransactionException
	{
		try
		{
			insertTransaction();
		}
		catch (DetailException e)
		{
			logger.error("Exception is ", e);
			throw new TransactionException("Transaction could not be opened.", e);
		}
	}

	/**
	 * Closes a transaction by deleting the journal document that records every interaction
	 * during transaction with the database.
	 * @throws TransactionException if journal document could not be deleted from the database
	 * @see rs.ruta.common.datamapper.DSTransaction#close()
	 */
	@Override
	public void close() throws TransactionException
	{
		try
		{
			deleteTransaction();
		}
		catch (DetailException e)
		{
			logger.error("Could not delete transaction journal " + transactionID + " from the database.");
			logger.error("Exception is ", e);
			throw new TransactionException("Transaction could not be closed.", e);
		}
	}

	@Override
	public void rollback() throws TransactionException
	{
		//MMM: if transaction rollback is unsuccessful then it could be tried again and if again unsuccessful then
		//MMM: system should be stoped, restarted and CDR.rollbackTransactions method should be called after restart?
		try
		{
			logger.info("Started rollback of the transaction " + transactionID + ".");
			for(ExistOperation op: operations)
				op.rollback();
		}
		catch(DetailException e)
		{
			logger.error("Could not rollback transaction " + transactionID + ".");
			throw new TransactionException("Transaction could not be rolled back.", e);
		}
		logger.info("Finished rollback of the transaction " + transactionID + "."); //successful transaction rollback
	}

	public void setEnabled(boolean enable)
	{
		this.enabled = enable;
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	@Override
	@Deprecated
	public boolean isKeepAlive()
	{
		return keepAlive;
	}

	@Override
	@Deprecated
	public void setKeepAlive(boolean keepAlive)
	{
		this.keepAlive = keepAlive;
	}

	/**Adds parameters that define one database operation to the {@code  ExistTransaction} object
	 * and stores that object to the database.
	 * @param originalCollectionPath path to the collection that contains inserted/updated/deleted document
	 * @param originalDocumentName name of the inserted/updated/deleted document
	 * @param operation {@code String} representing operation that was invoked
	 * @param backupCollectionPath path to the backup collection
	 * @param backupDocumentName name of the backup document
	 * @param username username of the user who is initiating the transaction. Except when creating a new user,
	 * this parameter is not used i.e. its value should be {@code null}.
	 * @throws DetailException if transaction object could not be saved to the database
	 */
	public abstract void addOperation(String originalCollectionPath, String originalDocumentName, String operation,
			String backupCollectionPath, String backupDocumentName, String username) throws DetailException;

	/**
	 * @return the transactionID
	 */
	@Override
	public String getID()
	{
		return transactionID;
	}

	/**
	 * @param transactionID the transactionID to set
	 */
	@Override
	public void setID(String transactionID)
	{
		this.transactionID = transactionID;
	}

	/**Returns all operations within the transaction.
	 * @return the operations
	 */
	public List<ExistOperation> getOperations()
	{
		return operations;
	}

	/**
	 * @param operations the operations to set
	 */
	public void setOperations(List<ExistOperation> operations)
	{
		this.operations = operations;
	}

	/**
	 * @return the timestamp
	 */
	@Override
	public long getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	@Override
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	@Override
	public boolean isFailed()
	{
		return failed;
	}

	@Override
	public void setFailed(boolean failed)
	{
		this.failed = failed;
	}

}