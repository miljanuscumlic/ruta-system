package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "NextCatalogueState")
public class NextCatalogueState extends ResolveNextCatalogueProcessState
{
	private static NextCatalogueState INSTANCE = new NextCatalogueState();

	public static NextCatalogueState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void resolveNextProcess(final RutaProcess process) throws StateTransitionException
	{
		//TODO nothing?
	}

}
