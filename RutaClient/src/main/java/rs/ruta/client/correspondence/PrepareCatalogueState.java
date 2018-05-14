package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PrepareCatalogueState")
public class PrepareCatalogueState extends CreateCatalogueProcessState
{
	private static CreateCatalogueProcessState INSTANCE = new PrepareCatalogueState();

	public static CreateCatalogueProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		changeState((RutaProcess) correspondence.getState(), ProduceCatalogueState.getInstance());
	}
}