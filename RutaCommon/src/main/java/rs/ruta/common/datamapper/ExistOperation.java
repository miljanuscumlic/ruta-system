package rs.ruta.common.datamapper;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import rs.ruta.common.User;

import javax.xml.bind.annotation.XmlAccessType;

/**
 *Abstarct class that represents an operation in the eXist database. An operation is
 *update, insert, delete or alike and one or more of these operations could compose an
 *{@link DSTransaction}.
 */
@XmlRootElement(name = "ExistOperation")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ExistOperation
{
	@XmlElement(name = "OriginalCollectionPath")
	private String originalCollectionPath;
	@XmlElement(name = "OriginalDocumentName")
	private String originalDocumentName;
	@XmlElement(name = "OperationType")
	private String operation; //MMM: enum maybe?
	@XmlElement(name = "BackupCollectionPath")
	private String backupCollectionPath;
	@XmlElement(name = "BackupDocumentName")
	private String backupDocumentName;
	@XmlElement(name = "User")
	private String username;

	public ExistOperation()
	{
		originalCollectionPath = originalDocumentName = operation = backupCollectionPath =
				backupDocumentName = username = "";
	}

	public ExistOperation(String collectionPath, String documentName, String operation,
			String backupCollectionPath, String backupDocumentName, String username)
	{
		this.originalCollectionPath = collectionPath;
		this.originalDocumentName = documentName;
		this.operation = operation;
		this.backupCollectionPath = backupCollectionPath;
		this.backupDocumentName = backupDocumentName;
		this.username = username;
	}

	public String getOriginalCollectionPath()
	{
		return originalCollectionPath;
	}

	public void setOriginalCollectionPath(String originalCollectionPath)
	{
		this.originalCollectionPath = originalCollectionPath;
	}

	public String getOriginalDocumentName()
	{
		return originalDocumentName;
	}

	public void setOriginalDocumentName(String originalDocumentName)
	{
		this.originalDocumentName = originalDocumentName;
	}

	public String getOperation()
	{
		return operation;
	}

	public void setOperation(String operation)
	{
		this.operation = operation;
	}

	public String getBackupCollectionPath()
	{
		return backupCollectionPath;
	}

	public void setBackupCollectionPath(String backupCollectionPath)
	{
		this.backupCollectionPath = backupCollectionPath;
	}

	public String getBackupDocumentName()
	{
		return backupDocumentName;
	}

	public void setBackupDocumentName(String backupDocumentName)
	{
		this.backupDocumentName = backupDocumentName;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	/**Rolls back previously executed operation.
	 * @throws DetailException if the operation could not be rolled back
	 */
	public abstract void rollback() throws DetailException;

}