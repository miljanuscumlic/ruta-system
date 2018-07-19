package rs.ruta.client.correspondence;

import java.util.UUID;
import java.util.concurrent.Future;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import rs.ruta.client.RutaClient;

/**
 * Encapsulating {@code UBL document process} for deletion
 * of the {@link CatalogueType} document, called {@code Delete Catalogue Process}.
 */
@XmlRootElement(name = "DeleteCatalogueProcess")
@XmlType(name = "DeleteCatalogueProcess")
@XmlAccessorType(XmlAccessType.NONE)
public class DeleteCatalogueProcess extends CatalogueProcess
{
	/**
	 * Constructs new instance of a {@link DeleteCatalogueProcess} and sets its state to
	 * default value and uuid to a random value.
	 * @param {@link RutaClient} object
	 * @return {@link DeleteCatalogueProcess}
	 */
	public static DeleteCatalogueProcess newInstance(RutaClient client)
	{
		DeleteCatalogueProcess process = new DeleteCatalogueProcess();
		process.setState(NotifyOfCatalogueDeletionState.getInstance());
		process.setId(UUID.randomUUID().toString());
		process.setClient(client);
		process.setActive(true);
		return process;
	}

	@Override
	public void doActivity(final Correspondence correspondence) throws StateActivityException
	{
		try
		{
			while(active)
			{
				state.doActivity(correspondence);
			}
			correspondence.changeState(ResolveNextCatalogueProcess.newInstance(correspondence.getClient()));
		}
		catch (Exception e)
		{
			correspondence.changeState(ResolveNextCatalogueProcess.newInstance(correspondence.getClient()));
			try
			{
				correspondence.stop();
			}
			catch (InterruptedException e1)
			{
				throw new StateActivityException("Unable to stop the correspondence!", e1); 
			}
			throw new StateActivityException("Interrupted execution of Delete Catalogue Process!", e); 
		}
		finally
		{
//			if(correspondence.isActive() && !correspondence.isStopped())
//			{
//				correspondence.changeState(ResolveNextCatalogueProcess.newInstance(correspondence.getClient()));
//			}
		}
	}
}