package rs.ruta.client.correspondence;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.client.MyParty;
import rs.ruta.client.RutaClient;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.MapperRegistry;

/**
 * Class encapsulating {@link Correspondence} between parties during which {@link OrderType},
 * {@link InvoiceType} and alike {@code UBL} business documents are exchanged among parties
 * of the {@code Ruta system}.
 */
@XmlRootElement(name = "BuyingCorrespondence")
public class BuyingCorrespondence extends Correspondence
{
	/**
	 * Constructs new instance of a {@link BuyingCorrespondence} and sets its state to
	 * default value and uuid to a random value.
	 * @param client {@link RutaClient} object
	 * @param correspondentParty correspondent {@link PartyType}
	 * @param correspondentID ID of the correspondent party
	 * @param buyer true if correspondence is on the Buyer's Party side, false if on the Seller's Party side
	 * @return {@code BuyingCorrespondence}
	 */
	public static BuyingCorrespondence newInstance(RutaClient client, PartyType correspondentParty, String correspondentID, boolean buyer)
	{
		BuyingCorrespondence corr = new BuyingCorrespondence();
		corr.setId(UUID.randomUUID().toString());
		if(buyer)
			corr.setState(BuyerOrderingProcess.newInstance(client));
		else
			corr.setState(SellerOrderingProcess.newInstance(client));
		corr.setClient(client);
		corr.setName(corr.uuid.getValue());
//		@Deprecated MMM test and delete
//		corr.setCorrespondentIdentification(correspondentID);
		corr.setCorrespondentParty(correspondentParty);
		final XMLGregorianCalendar currentDateTime = InstanceFactory.getDate();
		corr.setCreationTime(currentDateTime);
		corr.setLastActivityTime(currentDateTime);
		corr.setActive(true);
		corr.setStopped(false);
		corr.setRecentlyUpdated(true);
		return corr;
	}

	@Override
	public void run()
	{
		final Thread myThread = Thread.currentThread();
		while (thread == myThread && active && !stopped)
		{
			state.doActivity(this);
/*			if(state instanceof BuyerOrderingProcess ||
					state instanceof SellerOrderingProcess)
				executeOrderingProcess();
			else if(state instanceof BillingProcess)
				executeBillingProcess();
			else if(state instanceof PaymentNotificationProcess)
				executePaymentNotificationProcess();*/
		}
		if(stopped)
			//stoppedSemaphore.release();
			signalThreadStopped();
	}

	/**
	 * Invokes execution of the ordering process in the {@code Ruta System}.
	 */
	public void executeOrderingProcess()
	{
//		((BuyingProcess) state).ordering(this);
//		((BuyingProcess) state).orderingActivity(this);
		state.doActivity(this);
	}

	/**
	 * Invokes execution of the process of deletion of the {@link CatalogueType} {@code UBL document} from the
	 * {@code Ruta System}.
	 */
	public void executeBillingProcess()
	{
//		((BuyingProcess) state).billing(this);
		state.doActivity(this);
	}

	/**
	 * Invokes execution of the Payment Notification process in the {@code Ruta System}.
	 */
	public void executePaymentNotificationProcess()
	{
//		((BuyingProcess) state).paymentNotification(this);
		state.doActivity(this);
	}

	@Override
	protected void doStore() throws DetailException
	{
		MapperRegistry.getInstance().getMapper(BuyingCorrespondence.class).insert(null, this);
	}

}