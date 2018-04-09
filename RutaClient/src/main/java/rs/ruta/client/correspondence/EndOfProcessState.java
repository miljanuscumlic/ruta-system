package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "EndOfProcessState", namespace = "urn:rs:ruta:client")
//@XmlType(name = "EndOfProcessState", namespace = "urn:rs:ruta:client")
public class EndOfProcessState extends CreateCatalogueProcessState
{
	private static final EndOfProcessState INSTANCE = new EndOfProcessState();

	public static EndOfProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void endOfProcess(final RutaProcess process) throws StateTransitionException
	{
		changeState(process, PrepareCatalogueState.getInstance());
	}

}