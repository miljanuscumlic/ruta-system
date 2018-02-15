package rs.ruta.common.datamapper;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

/**
 *Represents the database operation in the process of document distribution. These operations are ones that
 *should be accomplished in the future. Its purpose is to obtain the support in the case exception is thrown
 *during {@link DocumentDistribution document distribution} so the document need to be redistributed.
 *When {@code DistributionOperation} is done it is removed from the list of outstanding {@code DistributionOperation}s.
 *Until then it is kept in the {@link DistributionTransaction transaction journal}.
 */
@XmlRootElement(name = "DistributionOperation", namespace = "urn:rs:ruta:services")
@XmlType(name = "DistributionOperation")
@XmlAccessorType(XmlAccessType.FIELD)
public class DistributionOperation extends ExistOperation
{
	public DistributionOperation()
	{
		super();
	}

	public DistributionOperation(String collectionPath, String documentName, String operation,
			String backupCollectionPath, String backupDocumentName, String username)
	{
		super(collectionPath, documentName, operation, backupCollectionPath, backupDocumentName, username);
	}

	@Override
	public void rollback() throws DetailException
	{
		if(getOperation().equals("INSERT")) // copy document from backup to original collection
			((DistributionTransactionMapper) MapperRegistry.getInstance().getMapper(DistributionTransaction.class)).
			copyXmlDocument(getOriginalCollectionPath(), getOriginalDocumentName(), getBackupCollectionPath(), getBackupDocumentName());
		else if(getOperation().equals("DELETE"))
			((DistributionTransactionMapper) MapperRegistry.getInstance().getMapper(DistributionTransaction.class)).
			deleteXmlDocument(getOriginalCollectionPath(), getOriginalDocumentName());
		else
			throw new TransactionException("Uknown operation name: " + getOperation());
	}

}