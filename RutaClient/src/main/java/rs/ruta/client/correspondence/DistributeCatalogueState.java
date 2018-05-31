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
		final CreateCatalogueProcess process = (CreateCatalogueProcess) correspondence.getState();
		final CatalogueType catalogue = process.getCatalogue();
		final DocumentReference documentReference = correspondence.getLastDocumentReference();
		final Future<?> ret = process.getClient().cdrSendMyCatalogueUpdateRequest(catalogue, documentReference, correspondence);
		process.setFuture(ret);
		changeState(process, ReceiveCatalogueAppResponseState.getInstance());
	}
}