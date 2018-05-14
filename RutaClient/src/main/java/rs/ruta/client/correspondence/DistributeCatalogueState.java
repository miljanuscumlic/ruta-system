package rs.ruta.client.correspondence;

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlRootElement;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;

@XmlRootElement(name = "DistributeCatalogueState")
public class DistributeCatalogueState extends CreateCatalogueProcessState
{
	private static final CreateCatalogueProcessState INSTANCE = new DistributeCatalogueState();

	public static CreateCatalogueProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final RutaProcess process = (RutaProcess) correspondence.getState();
		final CatalogueType catalogue = ((CreateCatalogueProcess) process).getCatalogue();
		final Future<?> ret = process.getClient().cdrSendMyCatalogueUpdateRequest(catalogue);
		((CreateCatalogueProcess) process).setFuture(ret);
		correspondence.addDocumentReference(catalogue.getProviderParty(),
				catalogue.getUUIDValue(), catalogue.getIDValue(), catalogue.getIssueDateValue(),
				catalogue.getIssueTimeValue(), catalogue.getClass().getName(),
				correspondence.getClient().getMyParty());
		correspondence.setRecentlyUpdated(true);
		changeState(process, ReceiveCatalogueAppResponseState.getInstance());
	}
}