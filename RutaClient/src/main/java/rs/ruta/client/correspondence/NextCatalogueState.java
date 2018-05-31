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
	public void doActivity(Correspondence correspondence) throws StateActivityException
	{
		try
		{
			correspondence.block();
			((RutaProcess) correspondence.getState()).setActive(false);
		}
		catch(InterruptedException e)
		{
			if(!correspondence.isStopped()) //non-intentional interruption
				throw new StateActivityException("Correspondence has been interrupted!");
		}
	}
}