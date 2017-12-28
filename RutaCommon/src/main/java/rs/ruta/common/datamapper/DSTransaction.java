package rs.ruta.common.datamapper;

import java.util.List;

/**Interface defining basic methods of the data store transactions. Transactions are atomic actions
 * comprised of the data store operations like insert/update/delete.
 */
public interface DSTransaction extends AutoCloseable
{
	/**Opens a transaction for operations withwith the data store.
	 * @throws TransactionException if transaction could not be opened
	 */
	public void open() throws TransactionException;

	/**Closes a transaction.
	 * @throws TransactionException if transaction could not be closed
	 */
	@Override
	public void close() throws TransactionException;

	/**Reverts the state of the documents involved in this transaction to the state before this transaction has been started.
	 * @throws TransactionException if the rollback could not be done
	 */
	public void rollback() throws TransactionException;

	/**Returns boolean value which tells if the <code>DSTransaction</code> is enabled.
	 * Transaction could be disabled if there is no need for it during data store operation.
	 * @return true if enabled, false otherwise
	 */
	public boolean isEnabled();

	/**Roll backs all transactions.
	 * @throws DetailException if could not connect to the database or could not to roll back the transactions
	 */
	public static void rollbackAll() throws DetailException
	{
		List<DSTransaction> transactions  = (List<DSTransaction>) MapperRegistry.getInstance().getMapper(DSTransaction.class).findAll();
		if(transactions != null)
			for(DSTransaction t: transactions)
			{
				t.rollback();
				t.close();
			}
	}

}
