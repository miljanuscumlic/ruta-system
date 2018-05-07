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
		//TODO
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		try
		{
			correspondence.block();
		}
		catch(InterruptedException e)
		{
			if(!correspondence.isStopped()) //non-intentional interruption
				throw new StateTransitionException("Correspondence has been interrupted!");
		}
		finally
		{
			((RutaProcess) correspondence.getState()).setActive(false);
		}
	}
}