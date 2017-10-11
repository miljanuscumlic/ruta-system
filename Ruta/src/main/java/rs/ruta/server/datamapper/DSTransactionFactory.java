package rs.ruta.server.datamapper;

/**Factory class wich subclasses that generate <code>DSTransaction</code>s. Class
 *<code>DSTransactionFactory</code> is used when there is no need for transactions
 *during the database operations or database has its own transaction manager.
 */
public class DSTransactionFactory
{
	public DSTransaction openTransaction() throws TransactionException
	{
		return null;
	}
}
