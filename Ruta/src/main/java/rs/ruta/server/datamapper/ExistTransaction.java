package rs.ruta.server.datamapper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.base.XMLDBException;

import javax.xml.bind.annotation.XmlAccessType;

import rs.ruta.server.DatabaseException;
import rs.ruta.server.DetailException;
import rs.ruta.server.UserException;

/**Represents transaction in the eXist database. At this point only Atomicity of all ACID properties
 * is implemented. Atomicity ensures that all constituent operations inside the transaction are
 * accepted or none of them is.
 */
@XmlRootElement(name = "ExistTransaction", namespace = "urn:rs:ruta:services")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExistTransaction implements DSTransaction
{
	@XmlTransient
	private final static Logger logger = LoggerFactory.getLogger("rs.ruta.server.datamapper");
	@XmlElement(name = "TransactionID", required = true)
	private String transactionID;
	@XmlElement(name = "Timestamp")
	private long timestamp;
	@XmlTransient
	private boolean enabled;
	@XmlElement(name = "Operation")
	private List<Operation> operations;

	public ExistTransaction()
	{
		operations = new ArrayList<Operation>();
		enabled = true;
		timestamp = System.currentTimeMillis();
	}

	/**Reads from the database all transactions that have to be rolled back, and rolls back each of them.
	 */
	public static void rollbackAll()
	{
		try
		{
			@SuppressWarnings("unchecked")
			List<ExistTransaction> transactions  = (List<ExistTransaction>) MapperRegistry.getMapper(DSTransaction.class).findAll();
			if(transactions != null)
				for(ExistTransaction t: transactions)
				{
					t.rollback();
					t.close();
				}
		}
		catch (DetailException e)
		{
			logger.error("Exception is: ", e);
		}
	}

	/**Opens a transaction by creating a journal document that records every interaction with the database.
	 * @throws TransactionException thrown if journal document could not be saved to the database
	 * @see rs.ruta.server.datamapper.DSTransaction#open()
	 */
	@Override
	public void open() throws TransactionException
	{
		try
		{
			transactionID = (String) ((ExistTransactionMapper)(MapperRegistry.getMapper(DSTransaction.class))).createID();
			MapperRegistry.getMapper(DSTransaction.class).insert(this, transactionID, null);
		}
		catch (DetailException | XMLDBException e)
		{
			logger.error("Exception is: ", e);
			throw new TransactionException("Transaction could not be opened.", e);
		}
	}

	/**Closes a transaction by deleting the journal document that records every interaction with the database.
	 * @throws TransactionException thrown if journal document could not be deleted from the database
	 * @see rs.ruta.server.datamapper.DSTransaction#close()
	 */
	@Override
	public void close() throws TransactionException
	{
		try //delete transaction journal document
		{
			MapperRegistry.getMapper(DSTransaction.class).delete(transactionID, null);
		}
		catch (DetailException e)
		{
			logger.error("Could not delete transaction journal " + transactionID + " from the database.");
			logger.error("Exception is: ", e);
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
			logger.info("Started rollback of the transaction "+ transactionID + ".");
			for(Operation op: operations)
				op.rollback();
		}
		catch(DetailException e)
		{
			logger.error("Could not rollback transaction " + transactionID + ".");
			throw new TransactionException("Transaction could not be rolled back.", e);
		}

		logger.info("Finished rollback of the transaction " + transactionID + "."); //successful transaction rollback

/*		try //delete transaction journal document
		{
			MapperRegistry.getMapper(DSTransaction.class).delete(transactionID, null);
		}
		catch (DetailException e)
		{
			logger.error("Could not delete transaction journal " + transactionID + " from the database.");
			logger.error("Exception is: ", e);
		}*/
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

	public void enableTransaction()
	{
		enabled = true;
	}

	public void disableTransaction()
	{
		enabled = false;
	}

	/**Adds parameters that define one database operation to the <code>ExistTransaction</code> object
	 * and stores that object to the database.
	 * @param originalCollectionPath path to the collection that contains inserted/updated/deleted document
	 * @param originalDocumentName name of the inserted/updated/deleted document
	 * @param operation operation that was invoked
	 * @param backupCollectionPath path to the backup collection
	 * @param backupDocumentName name of the backup document
	 * @param username username of the user who is initiating the transaction. This parameter is not used
	 * (i.e. its value should be <code>null</code>) except when creating a new user.
	 * @throws DetailException thrown if transaction object could not be saved to the database
	 */
	public void appendOperation(String originalCollectionPath, String originalDocumentName, String operation,
			String backupCollectionPath, String backupDocumentName, String username) throws DetailException
	{
		operations.add(0, new Operation(originalCollectionPath, originalDocumentName, operation,
				backupCollectionPath, backupDocumentName, username));
		timestamp = System.currentTimeMillis();
		MapperRegistry.getMapper(DSTransaction.class).update(this, transactionID, null);
	}

	/**
	 * @return the transactionID
	 */
	public String getTransactionID()
	{
		return transactionID;
	}

	/**
	 * @param transactionID the transactionID to set
	 */
	public void setTransactionID(String transactionID)
	{
		this.transactionID = transactionID;
	}

	/**Returns all operations within the transaction.
	 * @return the operations
	 */
	public List<Operation> getOperations()
	{
		return operations;
	}

	/**
	 * @param operations the operations to set
	 */
	public void setOperations(List<Operation> operations)
	{
		this.operations = operations;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Operation
	{
		@XmlElement(name = "OriginalCollectionPath")
		private String originalCollectionPath;
		@XmlElement(name = "OriginalDocumentName")
		private String originalDocumentName;
		@XmlElement(name = "Operation")
		private String operation; //MMM: enum maybe?
		@XmlElement(name = "BackupCollectionPath")
		private String backupCollectionPath;
		@XmlElement(name = "BackupDocumentName")
		private String backupDocumentName;
		@XmlElement(name = "User")
		private String username;

		public Operation()
		{
			originalCollectionPath = originalDocumentName = operation = backupCollectionPath =
					backupDocumentName = username = "";
		}

		public Operation(String collectionPath, String documentName, String operation,
				String backupCollectionPath, String backupDocumentName, String username)
		{
			this.originalCollectionPath = collectionPath;
			this.originalDocumentName = documentName;
			this.operation = operation;
			this.backupCollectionPath = backupCollectionPath;
			this.backupDocumentName = backupDocumentName;
			this.username = username;
		}

		/**Rolls back previously executed operation.
		 * @throws DetailException thrown if the operation could not be rolled back
		 */
		public void rollback() throws DetailException
		{
			if(operation.equals("UPDATE") || operation.equals("DELETE")) //move document from /deleted to original collection with original name
			{
				try
				{
					((XmlMapper)MapperRegistry.getMapper(DSTransaction.class)).moveDocument(originalCollectionPath, originalDocumentName,
							backupCollectionPath, backupDocumentName);
				}
				catch(DatabaseException e)
				{// it's OK if the source document does not exist; in that case rollback should not be done
					if(!(e.getMessage().equals("Source document does not exist!")))
						throw e;
				}
			}
			else if(operation.equals("INSERT")) // delete document from original collection
				((XmlMapper)MapperRegistry.getMapper(DSTransaction.class)).deleteDocument(originalCollectionPath, originalDocumentName);
			else if(operation.equals("REGISTER")) //delete user Account from eXist database
			{
				try
				{
					((UserXmlMapper)MapperRegistry.getMapper(User.class)).deleteExistAccount(username);
				}
				catch(UserException e)
				{// it's OK if the user does not exist; in that case rollback should not be done
					if(!(e.getMessage().equals("User account does not exist!")))
						throw e;
				}
			}
			/* 			else if(operation == "UNREGISTER") //MMM: UNREGISTER is not used because it will never be appended to the Transaction
			{} //MMM: return boolean value telling caller to skip all other Operations in the Transaction because the user is
			//seccessfully unregistered. This is a false alarm, that means that transaction was completly done.			*/
			else
				throw new TransactionException("Uknown operation name: " + operation);
		}
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

}
