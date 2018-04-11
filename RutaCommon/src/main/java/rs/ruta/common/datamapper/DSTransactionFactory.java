package rs.ruta.common.datamapper;

/**
 * Factory class which subclasses generate objects of {@link DSTransaction}. Class
 * {@code DSTransactionFactory} is not used when there is no need for a transaction
 * during the data store operation or data store has its own transaction manager.
 */
public class DSTransactionFactory
{
	public DSTransaction newTransaction() throws TransactionException
	{
		return null;
	}
}