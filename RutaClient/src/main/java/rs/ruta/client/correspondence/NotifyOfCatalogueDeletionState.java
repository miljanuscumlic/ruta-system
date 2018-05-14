package rs.ruta.client.correspondence;

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import rs.ruta.client.RutaClient;

@XmlRootElement(name = "NotifyOfCatalogueDeletionState")
public class NotifyOfCatalogueDeletionState extends DeleteCatalogueProcessState
{
	private static NotifyOfCatalogueDeletionState INSTANCE = new NotifyOfCatalogueDeletionState();

	public static NotifyOfCatalogueDeletionState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final DeleteCatalogueProcess process = (DeleteCatalogueProcess) correspondence.getState();
		final RutaClient client = process.getClient();
		final CatalogueDeletionType catalogueDeletion = client.getMyParty().produceCatalogueDeletion(client.getCDRParty());
		final Future<?> ret = client.cdrSendMyCatalogueDeletionRequest(catalogueDeletion);
		process.setFuture(ret);
		correspondence.addDocumentReference(catalogueDeletion.getProviderParty(),
				catalogueDeletion.getUUIDValue(), catalogueDeletion.getIDValue(), catalogueDeletion.getIssueDateValue(),
				catalogueDeletion.getIssueTimeValue(), catalogueDeletion.getClass().getName(),
				correspondence.getClient().getMyParty());
		correspondence.setRecentlyUpdated(true);
		changeState(process, ReceiveCatalogueDeletionAppResponseState.getInstance());
	}
}