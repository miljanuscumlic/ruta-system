package rs.ruta.common.datamapper;

public class DatabaseTransactionFactory extends DSTransactionFactory
{
	@Override
	public DatabaseTransaction newTransaction() throws TransactionException
	{
		DatabaseTransaction t = new DatabaseTransaction();
		t.open();
		return t;
	}
}
