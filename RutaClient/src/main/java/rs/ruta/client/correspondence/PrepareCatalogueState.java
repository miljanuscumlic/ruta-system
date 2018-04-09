package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "PrepareCatalogueState", namespace = "urn:rs:ruta:client")
//@XmlType(name = "PrepareCatalogueState", namespace = "urn:rs:ruta:client")
public class PrepareCatalogueState extends CreateCatalogueProcessState
{
	private static CreateCatalogueProcessState INSTANCE = new PrepareCatalogueState();

	public static CreateCatalogueProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void prepareCatalogue(final RutaProcess process)
	{
		//nothing to do
		changeState(process, ProduceCatalogueState.getInstance());
	}
}
