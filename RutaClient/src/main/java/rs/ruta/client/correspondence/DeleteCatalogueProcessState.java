package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "DeleteCatalogueProcessState", namespace = "urn:rs:ruta:client")
public abstract class DeleteCatalogueProcessState implements RutaProcessState
{
	public void notifyOfCatalogueDeletion(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public void receiveCatalogueDeletionAppResp(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public void reviewDeletionOfCatalogue(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

	public void cancelCatalogue(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}


}