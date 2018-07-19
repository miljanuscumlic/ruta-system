package rs.ruta.client.correspondence;

import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import rs.ruta.client.RutaClient;

/**
 * Encapsulating {@code UBL document process} called {@code Create Catalogue Process}
 * for creation, validation and distribution of the {@link CatalogueType} document.
 */
@XmlRootElement(name = "CreateCatalogueProcess")
@XmlType(name = "CreateCatalogueProcess")
@XmlAccessorType(XmlAccessType.NONE)
public class CreateCatalogueProcess extends CatalogueProcess
{
	/**
	 * Constructs new instance of a {@link CreateCatalogueProcess} and sets its state to
	 * default value and uuid to a random value.
	 * @param {@link RutaClient} object
	 * @return {@code CreateCatalogueProcess}
	 */
	public static CreateCatalogueProcess newInstance(RutaClient client)
	{
		CreateCatalogueProcess process = new CreateCatalogueProcess();
		process.setState(PrepareCatalogueState.getInstance());
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
//			if(!correspondence.isStopped())
				correspondence.changeState(ResolveNextCatalogueProcess.newInstance(correspondence.getClient()));
		}
		catch (Exception e)
		{
//			if(e.getCause() != null && e.getCause().getMessage().contains("Read timed out"))
				correspondence.changeState(ResolveNextCatalogueProcess.newInstance(correspondence.getClient()));
			try
			{
				correspondence.stop();
			}
			catch (InterruptedException e1)
			{
				throw new StateActivityException("Unable to stop the correspondence!", e1); 
			}
			throw new StateActivityException("Interrupted execution of Create Catalogue Process!", e); 
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