package rs.ruta.common.datamapper;

/**Factory class which subclasses generates objects of <code>DSTransaction</code>. Class
 *<code>DSTransactionFactory</code> is not used when there is no need for transactions
 *during the database operations or database has its own transaction manager.
 */
public class DSTransactionFactory
{
	public DSTransaction newTransaction() throws TransactionException
	{
		return null;
	}
}
