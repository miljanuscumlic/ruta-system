package rs.ruta.client.correspondence;

import java.util.concurrent.Future;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;

//@XmlType(name = "CreateCatalogueProcessState", namespace = "urn:rs:ruta:client")
public abstract class CreateCatalogueProcessState implements RutaProcessState
{
	public void prepareCatalogue(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public CatalogueType produceCatalogue(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public Future<?> distributeCatalogue(final RutaProcess process, CatalogueType catalogue) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public void receiveCatalogueAppResponse(final RutaProcess process, Future<?> future) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public void decideOnAction(final RutaProcess process)
	{
		throw new StateTransitionException();
	}

	public void endOfProcess(final RutaProcess process)
	{
		throw new StateTransitionException();
	}

}