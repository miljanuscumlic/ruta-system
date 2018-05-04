package rs.ruta.client.correspondence;

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlRootElement;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;

@XmlRootElement(name = "DistributeCatalogueState", namespace = "urn:rs:ruta:client:correspondence:catalogue:create")
public class DistributeCatalogueState extends CreateCatalogueProcessState
{
	private static final CreateCatalogueProcessState INSTANCE = new DistributeCatalogueState();

	public static CreateCatalogueProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public Future<?> distributeCatalogue(final RutaProcess process, final CatalogueType catalogue)
	{
		final Future<?> ret = process.getClient().cdrSendMyCatalogueUpdateRequest(catalogue);
		changeState(process, ReceiveCatalogueAppResponseState.getInstance());
		return ret;
	}

	@Override
	public void doActivity(Correspondence correspondence, RutaProcess process)
	{
		final CatalogueType catalogue = ((CreateCatalogueProcess) process).getCatalogue();
		final Future<?> ret = process.getClient().cdrSendMyCatalogueUpdateRequest(catalogue);
		((CreateCatalogueProcess) process).setFuture(ret);
		changeState(process, ReceiveCatalogueAppResponseState.getInstance());
	}



}