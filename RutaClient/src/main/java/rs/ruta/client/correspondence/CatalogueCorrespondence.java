package rs.ruta.client.correspondence;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import rs.ruta.client.RutaClient;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.MapperRegistry;

/**
 * Class encapsulating {@link Correspondence} between parties when exchanging {@link CatalogueType}
 * and {@link CatalogueDeletionType} {@code UBL} business documents. Inside the {@code Ruta system} this
 * correspondence is made between a {@link PartyType party} which is a user of the system and a
 * {@code Central Data Repository} as another party in this type of communication.
 */
@XmlRootElement(name = "CatalogueCorrespondence")
@XmlAccessorType(XmlAccessType.NONE)
public class CatalogueCorrespondence extends Correspondence
{
	/**
	 * True when Create Catalogue Process should be invoked. False when Delete CAtalogue Pprocess
	 * is next to execute.
	 */
	@XmlElement(name = "CreateCatalogueProcess")
	private boolean createCatalogueProcess;

	/**
	 * Constructs new instance of a {@link CatalogueCorrespondence} and sets its state to
	 * {@link ResolveNextCatalogueProcess}.
	 * @param client {@link RutaClient} object
	 * @return {@code CatalogueCorrespondence}
	 */
	public static CatalogueCorrespondence newInstance(RutaClient client)
	{
		CatalogueCorrespondence corr = new CatalogueCorrespondence();
		corr.setId(UUID.randomUUID().toString());
		corr.setState(ResolveNextCatalogueProcess.newInstance(client));
		corr.setClient(client);
		corr.setName(corr.uuid.getValue());
//		@Deprecated MMM test and delete
//		corr.setCorrespondentIdentification(client.getCDRParty().getPartyID());
		corr.setCorrespondentParty(client.getCDRParty());
		final XMLGregorianCalendar currentDateTime = InstanceFactory.getDate();
		corr.setCreationTime(currentDateTime);
		corr.setLastActivityTime(currentDateTime);
		corr.setActive(true);
		corr.setStopped(false);
		corr.createCatalogueProcess = true;
		corr.setRecentlyUpdated(true);
		return corr;
	}

	/**
	 * Tests whether Create Catalogue Process should be invoked.
	 * @return true if Create Catalogue process should be invoked
	 */
	public boolean isCreateCatalogueProcess()
	{
		return createCatalogueProcess;
	}

	/**
	 * Sets boolean field telling whether Create Catalogue Process should be invoked.
	 * @param createCatalogueProcess
	 */
	public void setCreateCatalogueProcess(boolean createCatalogueProcess)
	{
		this.createCatalogueProcess = createCatalogueProcess;
	}

	@Override
	public void run()
	{
		final Thread myThread = Thread.currentThread();
		while (thread == myThread && active && !stopped)
		{
			state.doActivity(this);

/*			if(state instanceof ResolveNextCatalogueProcess)
				executeResolveNextCatalogueProcess();
			if(state instanceof CreateCatalogueProcess)
				executeCreateCatalogueProcess();
			else if(state instanceof DeleteCatalogueProcess)
				executeDeleteCatalogueProcess();*/
		}
/*		if(!active && !((RutaProcess) state).isActive()) //true when correspondence is stopped
		{
			((RutaProcess) state).setActive(true);
			setActive(true);
			signalThreadStopped();
		}*/
		if(stopped)
			signalThreadStopped();
//			stoppedSemaphore.release();
	}

	private void executeResolveNextCatalogueProcess()
	{
		((CatalogueProcess) state).resolveNextCatalogueProcess(this);
	}

	/**
	 * Invokes execution of the process of creation, validation and distribution of the {@link CatalogueType}
	 * in the {@code Ruta System}.
	 */
	public void executeCreateCatalogueProcess()
	{
//		((CatalogueProcess) state).createCatalogue(this);
		((CatalogueProcess) state).createCatalogueExecute(this);
	}

	/**
	 * Invokes execution of the process of deletion of the {@link CatalogueType} {@code UBL document} from the
	 * {@code Ruta System}.
	 */
	public void executeDeleteCatalogueProcess()
	{
		((CatalogueProcess) state).deleteCatalogue(this);
	}

	@Override
	public void signalThreadStopped()
	{
		//do nothing because Semaphore is not used
	}

	@Override
	public void waitThreadStopped() throws InterruptedException
	{
		//do nothing because Semaphore is not used
	}

	@Override
	protected void doStore() throws DetailException
	{
		MapperRegistry.getInstance().getMapper(CatalogueCorrespondence.class).insert(null, this);
	}

}