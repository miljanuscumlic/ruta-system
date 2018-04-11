package rs.ruta.client.correspondence;

import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

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
@XmlRootElement(name = "CreateCatalogueProcess", namespace = "urn:rs:ruta:client")
@XmlType(name = "CreateCatalogueProcess")
@XmlAccessorType(XmlAccessType.NONE)
public class CreateCatalogueProcess extends CatalogueProcess
{
	private CatalogueType catalogue;//MMM maybe this should be persisted also?

	/**
	 * Constructs new instance of a {@link CreateCatalogueProcess} and sets its state to
	 * default value and id to a random value.
	 * @return {@code CreateCatalogueProcess}
	 */
	public static CreateCatalogueProcess newInstance(RutaClient client)
	{
		CreateCatalogueProcess process = new CreateCatalogueProcess();
		process.setState(PrepareCatalogueState.getInstance());
		process.setId(UUID.randomUUID().toString());
		process.setClient(client);
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

	@Override
	public void createCatalogue(final Correspondence correspondence) throws StateTransitionException
	{
		try
		{
			boolean loop = true;
			while(loop)
			{
				prepareCatalogue();
				produceCatalogue();
				Future<?> future = distributeCatalogue();
				receiveCatalogueAppResponse(future);
				if(state instanceof DecideOnActionState)
				{
					Semaphore decision = new Semaphore(0);
					decideOnAction(decision);
					decision.acquire();
					if(state instanceof PrepareCatalogueState)
						loop = true;
					else if(state instanceof EndOfProcessState)
					{
						endOfProcess();
						loop = false;
					}
				}
				else if(state instanceof EndOfProcessState)
				{
					endOfProcess();
					loop = false;
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
	 * @param decision {@link Semaphore} that enables calling method to wait for callee to finish its execution in its {@code Thread}
	 */
	public void decideOnAction(Semaphore decision)
	{
		((CreateCatalogueProcessState) state).decideOnAction(this, decision);
	}

	/**
	 * Ends the process.
	 */
	public void endOfProcess()
	{
		state.endOfProcess(this);
	}

}