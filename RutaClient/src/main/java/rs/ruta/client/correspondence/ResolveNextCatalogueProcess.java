package rs.ruta.client.correspondence;

import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import rs.ruta.client.RutaClient;

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

	@Override
	public void doActivity(Correspondence correspondence) throws StateActivityException
	{
		try
		{
			while(active && !correspondence.isStopped())
			{
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
			throw new StateActivityException(Messages.getString("ResolveNextCatalogueProcess.0"), e); //$NON-NLS-1$
		}
		finally
		{
			if(correspondence.isActive() && !correspondence.isStopped())
			{
				if(((CatalogueCorrespondence) correspondence).isCreateCatalogue())
					correspondence.changeState(CreateCatalogueProcess.newInstance(correspondence.getClient()));
				else
					correspondence.changeState(DeleteCatalogueProcess.newInstance(correspondence.getClient()));
			}
		}
	}
}