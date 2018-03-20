package rs.ruta.common.datamapper;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import rs.ruta.common.RutaUser;

/**
 *Represents the database operation that are constituents of the {@link DatabaseTransaction transaction}.
 *These operations are ones that are successfully accomplished. Its purpose is to obtain support for
 *the rollback of the transaction in the case the exception is thrown. Only when {@code DatabaseOperation}
 *is rolled back it is removed from the list of outstanding {@code DatabaseOperation}s. Until then it is kept
 *in the {@link DatabaseTransaction transaction journal}.
 */
@XmlRootElement(name = "DatabaseOperation", namespace = "urn:rs:ruta:services")
@XmlType(name = "DatabaseOperation")
@XmlAccessorType(XmlAccessType.FIELD)
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
				moveXmlDocument(getOriginalCollectionPath(), getOriginalDocumentName(), getBackupCollectionPath(), getBackupDocumentName());
			}
			catch(DatabaseException e)
			{// it's OK if the source document does not exist; in that case rollback should not be done
				if(!(e.getMessage().equals("Source document does not exist!")))
					throw e;
			}
		}
		else if(getOperation().equals("INSERT")) // delete document from original collection
			((XmlMapper<DatabaseTransaction>)MapperRegistry.getInstance().getMapper(DatabaseTransaction.class)).
			deleteXmlDocument(getOriginalCollectionPath(), getOriginalDocumentName());
		else if(getOperation().equals("REGISTER")) //delete user Account from eXist database
		{
			try
			{
				((UserXmlMapper)MapperRegistry.getInstance().getMapper(RutaUser.class)).deleteExistAccount(getUsername());
			}
			catch(UserException e)
			{// it's OK if the user does not exist; in that case rollback should not be done
				if(!(e.getMessage().equals("RutaUser account does not exist!")))
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