package rs.ruta.client.correspondence;

import java.awt.Color;
import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import rs.ruta.client.RutaClient;
import rs.ruta.client.gui.RutaClientFrame;
import rs.ruta.common.DocumentReference;
import rs.ruta.common.datamapper.DetailException;

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
		final RutaClientFrame clientFrame = client.getClientFrame();
		clientFrame.appendToConsole(new StringBuilder("Collecting data and producing Catalogue Deletion..."), Color.BLACK);
		final CatalogueDeletionType catalogueDeletion = client.getMyParty().produceCatalogueDeletion(client.getCDRParty());
		if(catalogueDeletion == null)
			throw new StateActivityException("My Catalogue Deletion document is malformed. UBL validation has failed.");
		else
			saveCatalogueDeletion(correspondence, catalogueDeletion);
		final DocumentReference documentReference = correspondence.getLastDocumentReference();
		final Future<?> ret = client.cdrSendMyCatalogueDeletionRequest(catalogueDeletion, documentReference, correspondence);
		process.setFuture(ret);
		changeState(process, ReceiveCatalogueDeletionAppResponseState.getInstance());
	}

	private void saveCatalogueDeletion(Correspondence correspondence, CatalogueDeletionType catalogueDeletion)
			throws StateActivityException
	{
		try
		{
			//		correspondence.addDocumentReference(catalogueDeletion.getProviderParty(),
			//				catalogueDeletion.getUUIDValue(), catalogueDeletion.getIDValue(),
			//				catalogueDeletion.getIssueDateValue(), catalogueDeletion.getIssueTimeValue(),
			//				catalogueDeletion.getClass().getName(), DocumentReference.Status.UBL_VALID);

			correspondence.addDocumentReference(catalogueDeletion, DocumentReference.Status.UBL_VALID);
			correspondence.setRecentlyUpdated(true);
		}
		catch(DetailException e)
		{
			throw new StateActivityException(e.getMessage());
		}
	}
}