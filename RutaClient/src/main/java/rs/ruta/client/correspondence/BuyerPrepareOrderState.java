package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import rs.ruta.client.RutaClient;
import rs.ruta.client.gui.RutaClientFrame;

@XmlRootElement(name = "BuyerPrepareOrderState")
public class BuyerPrepareOrderState extends BuyerOrderingProcessState
{
	private static BuyerOrderingProcessState INSTANCE = new BuyerPrepareOrderState();

	public static BuyerOrderingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		final RutaProcess process = (RutaProcess) correspondence.getState();
		if(prepareOrder(process, correspondence.getCorrespondentID()))
		{
			changeState(process, BuyerSendOrderState.getInstance());
		}
		else
		{
			correspondence.setCanceled(true);
			changeState(process, EndOfProcessState.getInstance());
		}

	}

	/**
	 * Prepares blank {@link OrderType order} populating it with the data from correspondent's
	 * {@link CatalogueType catalogue}.
	 * @param process process that this state belongs to
	 * @param correspondentID correspondent's ID
	 * @return true if order is prepared; false if order creation has been canceled by the user
	 */
	private boolean prepareOrder(RutaProcess process, String correspondentID)
	{
		boolean success = false;
		final RutaClient client = process.getClient();
		final OrderType order = client.getMyParty().produceOrder(correspondentID);
		if(order != null)
		{
			((BuyerOrderingProcess) process).setOrder(order);
			success = true;
		}
		return success;
	}

	/**
	 * Prepares blank {@link OrderType order} populating it with the data from correspondent's
	 * {@link CatalogueType catalogue}.
	 * @param process process that this state belongs to
	 * @param correspondentID correspondent's ID
	 * @return true if order is prepared; false if order creation has been canceled by the user
	 */
	private boolean prepareOrderOLD(RutaProcess process, String correspondentID)
	{
		boolean success = false;
		final RutaClient client = process.getClient();
		final RutaClientFrame clientFrame = client.getClientFrame();
		final OrderType order = clientFrame.showOrderDialogOLD("New Order", correspondentID);
		if(order != null)
		{
			((BuyerOrderingProcess) process).setOrder(order);
			success = true;
		}
		return success;
	}
}