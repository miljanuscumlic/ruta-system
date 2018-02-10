package rs.ruta.common.datamapper;

import rs.ruta.common.User;

public class DatabaseOperation extends ExistOperation
{
	public DatabaseOperation()
	{
		super();
	}

	public DatabaseOperation(String collectionPath, String documentName, String operation,
			String backupCollectionPath, String backupDocumentName, String username)
	{
		super(collectionPath, documentName, operation, backupCollectionPath, backupDocumentName, username);
	}

	@Override
	public void rollback() throws DetailException
	{
		if(getOperation().equals("UPDATE") || getOperation().equals("DELETE")) //move document from /deleted to original collection with original name
		{
			try
			{
				((XmlMapper<DatabaseTransaction>)MapperRegistry.getInstance().getMapper(DatabaseTransaction.class)).
				moveDocument(getOriginalCollectionPath(), getOriginalDocumentName(), getBackupCollectionPath(), getBackupDocumentName());
			}
			catch(DatabaseException e)
			{// it's OK if the source document does not exist; in that case rollback should not be done
				if(!(e.getMessage().equals("Source document does not exist!")))
					throw e;
			}
		}
		else if(getOperation().equals("INSERT")) // delete document from original collection
			((XmlMapper<DatabaseTransaction>)MapperRegistry.getInstance().getMapper(DatabaseTransaction.class)).
			deleteDocument(getOriginalCollectionPath(), getOriginalDocumentName());
		else if(getOperation().equals("REGISTER")) //delete user Account from eXist database
		{
			try
			{
				((UserXmlMapper)MapperRegistry.getInstance().getMapper(User.class)).deleteExistAccount(getUsername());
			}
			catch(UserException e)
			{// it's OK if the user does not exist; in that case rollback should not be done
				if(!(e.getMessage().equals("User account does not exist!")))
					throw e;
			}
		}
		/* 			else if(operation == "UNREGISTER") //MMM: UNREGISTER is not used because it will never be appended to the Transaction
			{} //MMM: return boolean value telling caller to skip all other ExistOperations in the Transaction because the user is
			//seccessfully unregistered. This is a false alarm, that means that transaction was completly done.			*/
		else
			throw new TransactionException("Uknown operation name: " + getOperation());
	}

}
