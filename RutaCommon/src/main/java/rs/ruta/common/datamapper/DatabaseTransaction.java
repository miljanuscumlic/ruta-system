package rs.ruta.common.datamapper;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

/**Represents database operation transaction in the eXist database. At this point only Atomicity
 * of all ACID properties is implemented. Atomicity ensures that all constituent operations inside
 * the transaction are accepted or none of them is.
 */
@XmlRootElement(name = "DatabaseTransaction")
@XmlAccessorType(XmlAccessType.NONE)
public class DatabaseTransaction extends ExistTransaction
{
	public DatabaseTransaction()
	{
		super();
	}

	@Override
	protected void insertTransaction() throws DetailException
	{
		MapperRegistry.getInstance().getMapper(DatabaseTransaction.class).insert(null, this);
	}

	@Override
	protected void deleteTransaction() throws DetailException
	{
		((DatabaseTransactionMapper) MapperRegistry.getInstance().getMapper(DatabaseTransaction.class)).
		delete(getID(), (DSTransaction) null);
	}

	/**
	 * Rolls back all outstanding transactions.
	 * @throws DetailException if transactions could not be rolled back
	 */
	public synchronized static void rollbackAll() throws DetailException
	{
		List<DatabaseTransaction> transactions  = (List<DatabaseTransaction>) MapperRegistry.getInstance().
				getMapper(DatabaseTransaction.class).findAll();
		if(transactions != null)
			for(DatabaseTransaction t: transactions)
			{
				//synchronized on transaction because in some other thread that transaction could be in the
				//middle of its execution
				synchronized(t)
				{
					t.rollback();
					t.close();
				}
			}
	}

	@Override
	public void addOperation(String originalCollectionPath, String originalDocumentName, String operation,
			String backupCollectionPath, String backupDocumentName, String username) throws DetailException
	{
		getOperations().add(0, new DatabaseOperation(originalCollectionPath, originalDocumentName, operation,
				backupCollectionPath, backupDocumentName, username));
		setTimestamp(System.currentTimeMillis());
		MapperRegistry.getInstance().getMapper(DatabaseTransaction.class).update(null, this);
	}
}
