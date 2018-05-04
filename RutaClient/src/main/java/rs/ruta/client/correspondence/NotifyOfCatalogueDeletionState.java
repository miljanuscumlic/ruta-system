package rs.ruta.client.correspondence;

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import rs.ruta.client.RutaClient;

@XmlRootElement(name = "NotifyOfCatalogueDeletionState", namespace = "urn:rs:ruta:client:correspondence:catalogue:delete")
public class NotifyOfCatalogueDeletionState extends DeleteCatalogueProcessState
{
	private static NotifyOfCatalogueDeletionState INSTANCE = new NotifyOfCatalogueDeletionState();

	public static NotifyOfCatalogueDeletionState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public Future<?> notifyOfCatalogueDeletion(final RutaProcess process) throws StateTransitionException
	{
		final RutaClient client = process.getClient();
		final CatalogueDeletionType catalogueDeletion = client.getMyParty().createCatalogueDeletion(client.getCDRParty());
		final Future<?> ret = client.cdrSendMyCatalogueDeletionRequest(catalogueDeletion);
		changeState(process, ReceiveCatalogueDeletionAppResponseState.getInstance());
		return ret;
	}


}
