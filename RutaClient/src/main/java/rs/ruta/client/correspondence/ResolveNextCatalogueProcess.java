package rs.ruta.client.correspondence;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import rs.ruta.client.RutaClient;

@XmlRootElement(name = "ResolveNextCatalogueProcess", namespace = "urn:rs:ruta:client")
public class ResolveNextCatalogueProcess extends CatalogueProcess
{
/*	public ResolveNextCatalogueProcess()
	{
		state = NextCatalogueState.getInstance();
	}*/

	/**
	 * Constructs new instance of a {@link ResolveNextCatalogueProcess} and sets its state to
	 * default value and id to a random value.
	 * @return {@code ResolveNextCatalogueProcess}
	 */
	public static ResolveNextCatalogueProcess newInstance(RutaClient client)
	{
		ResolveNextCatalogueProcess process = new ResolveNextCatalogueProcess();
		process.setState(NextCatalogueState.getInstance());
		process.setId(UUID.randomUUID().toString());
		process.setClient(client);
		return process;
	}

	@Deprecated
	@Override
	public void resolveNextCatalogueProcess(final Correspondence correspondence) throws StateTransitionException
	{
		resolveNextProcess();
		boolean create = false;
		if(create)
			correspondence.changeState(CreateCatalogueProcess.newInstance(correspondence.getClient()));
		else
			correspondence.changeState(DeleteCatalogueProcess.newInstance(correspondence.getClient()));
	}

	@Override
	public void createCatalogue(final Correspondence correspondence) throws StateTransitionException
	{
		correspondence.changeState(CreateCatalogueProcess.newInstance(correspondence.getClient()));
		((CreateCatalogueProcess) correspondence.getState()).createCatalogue(correspondence);
	}

	/**
	 * Deletes {@link CatalogueType} document from the {@code Ruta System}
	 * @param correspondence correspondence to which process belongs
	 * @throws StateTransitionException
	 */
	@Override
	public void deleteCatalogue(final Correspondence correspondence) throws StateTransitionException
	{
		correspondence.changeState(DeleteCatalogueProcess.newInstance(correspondence.getClient()));
		((DeleteCatalogueProcess) correspondence.getState()).deleteCatalogue(correspondence);
	}

	@Deprecated
	public void resolveNextProcess()
	{
		((NextCatalogueState) state).resolveNextProcess(this);
	}
}
