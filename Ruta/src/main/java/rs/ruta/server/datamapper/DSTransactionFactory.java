package rs.ruta.server.datamapper;

/**Factory class which subclasses generates objects of <code>DSTransaction</code>. Class
 *<code>DSTransactionFactory</code> is not used when there is no need for transactions
 *during the database operations or database has its own transaction manager.
 */
public class DSTransactionFactory
{
	public DSTransaction openTransaction() throws TransactionException
	{
		return null;
	}
}
