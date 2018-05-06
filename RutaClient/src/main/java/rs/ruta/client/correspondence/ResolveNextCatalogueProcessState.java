package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ResolveNextCatalogueProcessState")
public abstract class ResolveNextCatalogueProcessState implements RutaProcessState
{
	public void resolveNextProcess(final RutaProcess process) throws StateTransitionException
	{
		throw new StateTransitionException();
	}

}