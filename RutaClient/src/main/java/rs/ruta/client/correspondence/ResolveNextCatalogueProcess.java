package rs.ruta.client.correspondence;

import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import rs.ruta.client.RutaClient;
import rs.ruta.common.datamapper.MapperRegistry;

@XmlRootElement(name = "ResolveNextCatalogueProcess")
public class ResolveNextCatalogueProcess extends CatalogueProcess
{
	/**
	 * Constructs new instance of a {@link ResolveNextCatalogueProcess} and sets its state to
	 * default value and uuid to a random value.
	 * @param {@link RutaClient} object
	 * @return {@code ResolveNextCatalogueProcess}
	 */
	public static ResolveNextCatalogueProcess newInstance(RutaClient client)
	{
		ResolveNextCatalogueProcess process = new ResolveNextCatalogueProcess();
		process.setState(NextCatalogueState.getInstance());
		process.setId(UUID.randomUUID().toString());
		process.setClient(client);
		process.setActive(true);
		return process;
	}

/*	@Deprecated
	@Override
	public void resolveNextCatalogueProcess(final Correspondence correspondence) throws StateTransitionException
	{
		resolveNextProcess();
		boolean create = false;
		if(create)
			correspondence.changeState(CreateCatalogueProcess.newInstance(correspondence.getClient()));
		else
			correspondence.changeState(DeleteCatalogueProcess.newInstance(correspondence.getClient()));
	}*/

	@Override
	public void resolveNextCatalogueProcess(final Correspondence correspondence) throws StateTransitionException
	{
		try
		{
			while(active && !correspondence.isStopped())
			{
				doActivity(correspondence);

/*				JAXBContext jaxbContext = JAXBContext.newInstance(CatalogueCorrespondence.class);
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(correspondence, System.out);*/

				MapperRegistry.getInstance().getMapper(CatalogueCorrespondence.class).
				insert(null, (CatalogueCorrespondence) correspondence);
				int i = 1;
			}
		}
		catch (Exception e)
		{
			throw new StateTransitionException("Interrupted execution of Buyer Ordering Process!", e);
		}
		finally
		{
			if(correspondence.isActive() && !correspondence.isStopped())
			{
				if(((CatalogueCorrespondence) correspondence).isCreateCatalogueProcess())
					correspondence.changeState(CreateCatalogueProcess.newInstance(correspondence.getClient()));
				else
					correspondence.changeState(DeleteCatalogueProcess.newInstance(correspondence.getClient()));
			}
		}
	}

/*	@Override
	public void doActivity(Correspondence correspondence)
	{
		state.doActivity(correspondence);
	}*/

	@Override
	public void doActivity(Correspondence correspondence)
	{
		try
		{
			while(active && !correspondence.isStopped())
			{
				//MapperRegistry.getInstance().getMapper(CatalogueCorrespondence.class).insert(null, (CatalogueCorrespondence) correspondence);
				correspondence.store();
				state.doActivity(correspondence);

/*				JAXBContext jaxbContext = JAXBContext.newInstance(CatalogueCorrespondence.class);
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(correspondence, System.out);*/
			}
		}
		catch (Exception e)
		{
			throw new StateTransitionException("Interrupted execution of Buyer Ordering Process!", e);
		}
		finally
		{
			if(correspondence.isActive() && !correspondence.isStopped())
			{
				if(((CatalogueCorrespondence) correspondence).isCreateCatalogueProcess())
					correspondence.changeState(CreateCatalogueProcess.newInstance(correspondence.getClient()));
				else
					correspondence.changeState(DeleteCatalogueProcess.newInstance(correspondence.getClient()));
			}
		}
	}

	@Override
	@Deprecated
	public void createCatalogue(final Correspondence correspondence) throws StateTransitionException
	{
		correspondence.changeState(CreateCatalogueProcess.newInstance(correspondence.getClient()));
		((CreateCatalogueProcess) correspondence.getState()).createCatalogue(correspondence);
	}

	@Override
	@Deprecated
	public void createCatalogueExecute(final Correspondence correspondence) throws StateTransitionException
	{
		correspondence.changeState(CreateCatalogueProcess.newInstance(correspondence.getClient()));
//		((CreateCatalogueProcess) correspondence.getState()).createCatalogue(correspondence);
//		((CreateCatalogueProcess) correspondence.getState()).createCatalogueExecute(correspondence);
	}

	@Override
	public void deleteCatalogue(final Correspondence correspondence) throws StateTransitionException
	{
		correspondence.changeState(DeleteCatalogueProcess.newInstance(correspondence.getClient()));
		((DeleteCatalogueProcess) correspondence.getState()).deleteCatalogue(correspondence);
	}

	@Override
	public void deleteCatalogueExecute(final Correspondence correspondence) throws StateTransitionException
	{
		correspondence.changeState(DeleteCatalogueProcess.newInstance(correspondence.getClient()));
//		((DeleteCatalogueProcess) correspondence.getState()).deleteCatalogue(correspondence);
	}


	@Deprecated
	public void resolveNextProcess()
	{
		((NextCatalogueState) state).resolveNextProcess(this);
	}

}
