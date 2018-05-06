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
import rs.ruta.common.Associates;

@XmlRootElement(name = "DistributionTransaction")
@XmlAccessorType(XmlAccessType.NONE)
public class DistributionTransaction extends ExistTransaction
{
	@XmlElement(name = "Associates")
	private Associates followers;

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
		((DistributionTransactionMapper) MapperRegistry.getInstance().getMapper(DistributionTransaction.class)).
		delete(getID(), (DSTransaction) null);
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
	 * Gets the list of all parties incorporated in the {@link Associates} object
	 * that should get the document.
	 * @return {@code Associates} object or {@code null}
	 */
	public Associates getFollowers()
	{
		return followers;
	}

	public void setFollowers(Associates followers)
	{
		this.followers = followers;
	}

	private void distribute() throws TransactionException
	{
		try
		{
			logger.info("Started distribution of the document " + getID() + ".");
			for(ExistOperation op: getOperations())
				op.rollback();
			logger.info("Finished distribution of the document " + getID() + "."); //successful transaction distribution
		}
		catch(DetailException e)
		{
			logger.error("Could not distribute document " + getID() + ".");
			throw new TransactionException("Document could not be distributed.", e);
		}
	}

	public synchronized static void distributeAll() throws DetailException
	{
		List<DistributionTransaction> transactions  = MapperRegistry.getInstance().getMapper(DistributionTransaction.class).findAll();
		if(transactions != null)
			for(DistributionTransaction t: transactions)
			{
				t.distribute();
				t.close();
			}
	}

	@Override
	public void addOperation(String originalCollectionPath, String originalDocumentName, String operation,
			String backupCollectionPath, String backupDocumentName, String username) throws DetailException
	{
		getOperations().add(0, new DistributionOperation(originalCollectionPath, originalDocumentName, operation,
				backupCollectionPath, backupDocumentName, username));
		MapperRegistry.getInstance().getMapper(DistributionTransaction.class).update(null, this);
	}

	/**
	 * Sets distribution operations to this transaction and stores the transaction in the database.
	 * @param destinationCollectionPaths list of collection paths to which the document should be distributed
	 * @param sourceCollectionPath collection path of the distributed document
	 * @param docDistributionName name of the distributed document
	 * @throws DetailException if transaction could not be stored to the database
	 */
	public void addDistributionOperations(List<String> destinationCollectionPaths, String sourceCollectionPath, String docDistributionName)
			throws DetailException
	{
		List<ExistOperation> operations = getOperations();
		for(int i = 0; i < destinationCollectionPaths.size(); i++)
			operations.add(0, new DistributionOperation(destinationCollectionPaths.get(i), docDistributionName, "INSERT",
					sourceCollectionPath, docDistributionName, null));
		operations.add(new DistributionOperation(sourceCollectionPath, docDistributionName, "DELETE", null, null, null));
		MapperRegistry.getInstance().getMapper(DistributionTransaction.class).update(null, this);
	}

	/**
	 * Removes first operation from the transaction and updates the transaction in the database.
	 * @throws DetailException if transaction could not be updated in the database
	 */
	public void removeOperation() throws DetailException
	{
		getOperations().remove(0);
		MapperRegistry.getInstance().getMapper(DistributionTransaction.class).update(null, this);
	}

}