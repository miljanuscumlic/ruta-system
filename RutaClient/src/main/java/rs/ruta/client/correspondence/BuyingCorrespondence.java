package rs.ruta.client.correspondence;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

import rs.ruta.client.MyParty;
import rs.ruta.client.RutaClient;
import rs.ruta.common.InstanceFactory;

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
	 * @param correspondentID ID of the correspondent party
	 * @param buyer true if correspondence is on the Buyer's Party side, false if on the Seller's Party side
	 * @return {@code BuyingCorrespondence}
	 */
	public static BuyingCorrespondence newInstance(RutaClient client, String correspondentID, boolean buyer)
	{
		BuyingCorrespondence corr = new BuyingCorrespondence();
		corr.setId(UUID.randomUUID().toString());
		if(buyer)
			corr.setState(BuyerOrderingProcess.newInstance(client));
		else
			corr.setState(SellerOrderingProcess.newInstance(client));
		corr.setClient(client);
		corr.setName(corr.uuid.getValue());
		corr.setCorrespondentIdentification(correspondentID);
		final XMLGregorianCalendar currentDateTime = InstanceFactory.getDate();
		corr.setCreationTime(currentDateTime);
		corr.setLastActivityTime(currentDateTime);
		corr.setActive(true);
		corr.setStopped(false);
		return corr;
	}

	@Override
	public void run()
	{
		final Thread myThread = Thread.currentThread();
		while (thread == myThread && active && !stopped)
		{
			if(state instanceof BuyerOrderingProcess ||
					state instanceof SellerOrderingProcess)
				executeOrderingProcess();
			else if(state instanceof BillingProcess)
				executeBillingProcess();
			else if(state instanceof PaymentNotificationProcess)
				executePaymentNotificationProcess();
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
		((BuyingProcess) state).doActivity(this);
	}

	/**
	 * Invokes execution of the process of deletion of the {@link CatalogueType} {@code UBL document} from the
	 * {@code Ruta System}.
	 */
	public void executeBillingProcess()
	{
//		((BuyingProcess) state).billing(this);
		((BuyingProcess) state).doActivity(this);
	}

	/**
	 * Invokes execution of the Payment Notification process in the {@code Ruta System}.
	 */
	public void executePaymentNotificationProcess()
	{
//		((BuyingProcess) state).paymentNotification(this);
		((BuyingProcess) state).doActivity(this);
	}

}
