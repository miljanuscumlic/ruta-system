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
			prepareCatalogue();
			produceCatalogue();
			Future<?> future = distributeCatalogue();
			receiveCatalogueAppResp(future);
			if(state instanceof DecideOnActionState)
				decideOnAction();
			else if(state instanceof EndOfProcessState)
				endOfProcess();
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
		setCatalogue(((CreateCatalogueProcessState) state).produceCatalogue(this));
	}

	/**
	 * Sends {@link CatalogueType} {@code UBL document} to the CDR.
	 */
	public Future<?> distributeCatalogue()
	{
		return ((CreateCatalogueProcessState) state).distributeCatalogue(this, getCatalogue());
	}

	/**
	 * Waits to receive the response in the form of {@link ApplicationResponseType} document from the CDR service.
	 * @param future TODO
	 */
	public void receiveCatalogueAppResp(Future<?> future)
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
	 * Ends the process.
	 */
	public void endOfProcess()
	{
		((CreateCatalogueProcessState) state).endOfProcess(this);
	}


}
