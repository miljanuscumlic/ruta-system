package rs.ruta.common.datamapper;

public class ExistTransactionFactory extends DSTransactionFactory
{
	@Override
	public ExistTransaction newTransaction() throws TransactionException
	{
		ExistTransaction t = new ExistTransaction();
		t.open();
		return t;
	}
}
