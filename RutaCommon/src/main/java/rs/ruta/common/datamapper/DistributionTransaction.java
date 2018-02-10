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

@XmlRootElement(name = "DistributionTransaction", namespace = "urn:rs:ruta:services")
@XmlAccessorType(XmlAccessType.FIELD)
public class DistributionTransaction extends ExistTransaction
{
	@XmlElement(name = "Followers")
	private Followers followers;

	public DistributionTransaction()
	{
		super();
	}

	@Override
	protected void insertTransaction() throws DetailException
	{
		MapperRegistry.getInstance().getMapper(DistributionTransaction.class).insert(null, this);
	}

	@Override
	protected void deleteTransaction() throws DetailException
	{
		((XmlMapper<DistributionTransaction>) MapperRegistry.getInstance().getMapper(DistributionTransaction.class)).delete(getID(), null);
	}

	@Override
	public void rollback() throws TransactionException
	{
		//TODO
/*		try
		{
			logger.info("Started rollback of the transaction "+ getID() + ".");
			for(Operation op: operations)
				op.rollback();
		}
		catch(DetailException e)
		{
			logger.error("Could not rollback transaction " + getID() + ".");
			throw new TransactionException("Transaction could not be rolled back.", e);
		}
		logger.info("Finished rollback of the transaction " + getID() + "."); //successful transaction rollback
*/	}

	/**
	 * Gets the list of all parties incorporated in the {@link Followers} object
	 * that should get the document.
	 * @return {@code Followers} object or {@code null}
	 */
	public Followers getFollowers()
	{
		return followers;
	}

	public void setFollowers(Followers followers)
	{
		this.followers = followers;
	}

	public synchronized static void distributeAll() throws DetailException
	{
		List<DistributionTransaction> transactions  = MapperRegistry.getInstance().getMapper(DistributionTransaction.class).findAll();
		if(transactions != null)
			for(DSTransaction t: transactions)
			{
				t.rollback(); //MMM: should be called distribute() ?
				t.close();
			}
	}

	@Override
	public void appendOperation(String originalCollectionPath, String originalDocumentName, String operation,
			String backupCollectionPath, String backupDocumentName, String username) throws DetailException
	{
		getOperations().add(0, new DatabaseOperation(originalCollectionPath, originalDocumentName, operation,
				backupCollectionPath, backupDocumentName, username));
		setTimestamp(System.currentTimeMillis());
		MapperRegistry.getInstance().getMapper(DistributionTransaction.class).update(null, this);
	}
}