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
@XmlRootElement(name = "CreateCatalogueProcess", namespace = "urn:rs:ruta:client:correspondence")
@XmlType(name = "CreateCatalogueProcess")
@XmlAccessorType(XmlAccessType.NONE)
public class CreateCatalogueProcess extends CatalogueProcess
{
	private CatalogueType catalogue;//MMM maybe this should be persisted also?
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
	public void createCatalogue(final Correspondence correspondence) throws StateTransitionException
	{
		try
		{
			while(active)
			{
				prepareCatalogue();
				produceCatalogue();
				Future<?> future = distributeCatalogue();
				receiveCatalogueAppResponse(future);
				if(state instanceof DecideOnActionState)
				{
					decideOnAction();
					if(state instanceof EndOfProcessState)
					{
						endOfProcess();
					}
					//else if(state instanceof PrepareCatalogueState) active = true;
				}
				else if(state instanceof EndOfProcessState)
				{
					endOfProcess();
				}
			}
		}
		catch (Exception e)
		{
			throw new StateTransitionException("Interrupted execution of Create Catalogue Process!", e);
		}
		finally
		{
			correspondence.changeState(ResolveNextCatalogueProcess.newInstance(correspondence.getClient()));
		}
	}

	/**
	 * Prepares {@link CatalogueType} information.
	 */
	public void prepareCatalogue()
	{
		((CreateCatalogueProcessState) state).prepareCatalogue(this);
	}

	/**
	 * Constructs {@link CatalogueType} {@code UBL document}.
	 */
	public void produceCatalogue()
	{
		catalogue = ((CreateCatalogueProcessState) state).produceCatalogue(this);
	}

	/**
	 * Sends {@link CatalogueType} {@code UBL document} to the CDR.
	 * @return {@link Future} object that holds the CDR response
	 */
	public Future<?> distributeCatalogue()
	{
		return ((CreateCatalogueProcessState) state).distributeCatalogue(this, catalogue);
	}

	/**
	 * Waits to receive the response in the form of {@link ApplicationResponseType} document from the CDR service.
	 * @param future {@link Future} object that holds the CDR response
	 */
	public void receiveCatalogueAppResponse(Future<?> future)
	{
		((CreateCatalogueProcessState) state).receiveCatalogueAppResponse(this, future);
	}

	/**
	 * Decides what to do upon receipt of the {@link ApplicationResponseType} document from the CDR service.
	 */
	public void decideOnAction()
	{
		((CreateCatalogueProcessState) state).decideOnAction(this);
	}

	/**
	 * Ends the process. Sets {@code active} boolean field to false.
	 */
	public void endOfProcess()
	{
		active = false;
		state.endOfProcess(this);
	}

	@Override
	public void createCatalogueExecute(final Correspondence correspondence) throws StateTransitionException
	{
		try
		{
			while(active)
			{
				execute(correspondence);
			}
		}
		catch (Exception e)
		{
			throw new StateTransitionException("Interrupted execution of Create Catalogue Process!", e);
		}
		finally
		{
			correspondence.changeState(ResolveNextCatalogueProcess.newInstance(correspondence.getClient()));
		}
	}

	public void execute(Correspondence correspondence)
	{
		state.doActivity(null, this);
	}

}