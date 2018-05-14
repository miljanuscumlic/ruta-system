package rs.ruta.client.correspondence;

import java.util.UUID;
import java.util.concurrent.Future;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import rs.ruta.client.RutaClient;

/**
 * Encapsulating {@code UBL document process} for creation, validation and distribution
 * of the {@link CatalogueType} document, called {@code Create Catalogue Process}.
 */
@XmlRootElement(name = "CreateCatalogueProcess")
@XmlType(name = "CreateCatalogueProcess")
@XmlAccessorType(XmlAccessType.NONE)
public class CreateCatalogueProcess extends CatalogueProcess
{
	private CatalogueType catalogue;
	private Future<?> future;

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

	public CatalogueType getCatalogue()
	{
		return catalogue;
	}

	public void setCatalogue(CatalogueType catalogue)
	{
		this.catalogue = catalogue;
	}

	public Future<?> getFuture()
	{
		return future;
	}

	public void setFuture(Future<?> future)
	{
		this.future = future;
	}

	@Override
	public void doActivity(final Correspondence correspondence) throws StateTransitionException
	{
		try
		{
			while(active)
			{
				state.doActivity(correspondence);
			}
		}
		catch (Exception e)
		{
			throw new StateTransitionException("Interrupted execution of Create Catalogue Process!", e);
		}
		finally
		{
			if(correspondence.isActive() && !correspondence.isStopped())
			{
				correspondence.changeState(ResolveNextCatalogueProcess.newInstance(correspondence.getClient()));
			}
		}
	}

}