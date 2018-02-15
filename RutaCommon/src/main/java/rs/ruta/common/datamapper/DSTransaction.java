package rs.ruta.common.datamapper;

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

	/**Checks whether the transaction should be kept alive, and not closed.
	 * @return true if transaction should be kept alive
	 */
	public boolean isKeepAlive();

	/**Sets the boolean field telling whether the transaction should be kept alive i.e not closed
	 * in the method.
	 * @param keepAlive true if transaction should be kept alive
	 */
	public void setKeepAlive(boolean keepAlive);

	/**Checks whether the transaction has failed.
	 * @return true if transaction has been failed
	 */
	public boolean isFailed();

	/**Sets the boolean field telling whether the transaction has failed.
	 * @param failed true if transaction has been failed
	 */
	public void setFailed(boolean failed);

	/**Gets the timestamp of the {@code DSTransaction}s creation.
	 * @return the timestamp
	 */
	public long getTimestamp();

	/**Sets the timestamp of the {@code DSTransaction}s creation.
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp);

	/**Roll backs all outstanding transactions.
	 * @throws DetailException if could not connect to the database or could not to roll back the transactions
	 */
	public static void rollbackAll() throws DetailException {}

	/**Gets the transaction's ID.
	 * @return transaction's ID
	 */
	public String getID();

	/**Sets the transaction's ID.
	 * @param transactionID ID to set
	 */
	public void setID(String transactionID);

}
