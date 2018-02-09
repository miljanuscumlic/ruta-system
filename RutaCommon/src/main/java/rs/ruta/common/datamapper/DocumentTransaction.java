package rs.ruta.common.datamapper;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.base.Collection;

import rs.ruta.common.DocumentDistribution;
import rs.ruta.common.Followers;

@XmlRootElement(name = "DocumentTransaction", namespace = "urn:rs:ruta:services")
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentTransaction implements DSTransaction
{
	@XmlTransient
	private final static Logger logger = LoggerFactory.getLogger("rs.ruta.common.datamapper");
	@XmlElement(name = "TransactionID", required = true)
	private String transactionID;
	@XmlElement(name = "Timestamp")
	private long timestamp;
	@XmlTransient
	private boolean enabled;
	@XmlTransient
	private boolean failed;
	@XmlElement(name = "Follower")
	private Followers followers;

	public DocumentTransaction()
	{
		enabled = true;
		failed = false;
		timestamp = System.currentTimeMillis();
	}

	/**Opens a transaction by creating a journal document that records all outstanding document distribution
	 * operations.
	 * @throws TransactionException if journal document could not be saved to the database
	 * @see rs.ruta.common.datamapper.DSTransaction#open()
	 */
	@Override
	public void open() throws TransactionException
	{
		try
		{
			MapperRegistry.getInstance().getMapper(DocumentTransaction.class).insert(null, this);
		}
		catch (DetailException e)
		{
			logger.error("Exception is ", e);
			throw new TransactionException("Document transaction could not be opened.", e);
		}
	}

	/**Closes a transaction by deleting the journal document that records all outstanding document distribution
	 * operations.
	 * @throws TransactionException if journal document could not be deleted from the database
	 * @see rs.ruta.common.datamapper.DSTransaction#close()
	 */
	@Override
	public void close() throws TransactionException
	{
		try //delete transaction journal document
		{
			((XmlMapper<DocumentTransaction>) MapperRegistry.getInstance().getMapper(DocumentTransaction.class)).delete(transactionID, null);
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
		//TODO
/*		try
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
*/	}

	@Override
	public boolean isEnabled()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isKeepAlive()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setKeepAlive(boolean keepAlive)
	{
		// TODO Auto-generated method stub
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

	@Override
	public long getTimestamp()
	{
		return timestamp;
	}

	@Override
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	@Override
	public String getID()
	{
		return transactionID;
	}

	@Override
	public void setID(String transactionID)
	{
		this.transactionID = transactionID;
	}

	public Followers getFollowers()
	{
		return followers;
	}

	public void setFollowers(Followers followers)
	{
		this.followers = followers;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public synchronized static void distributeAll() throws DetailException
	{
		List<DocumentTransaction> transactions  = MapperRegistry.getInstance().getMapper(DocumentTransaction.class).findAll();
		if(transactions != null)
			for(DSTransaction t: transactions)
			{
				t.rollback();
				t.close();
			}
	}
}
