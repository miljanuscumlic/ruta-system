package rs.ruta.client.correspondence;

import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import rs.ruta.client.RutaClient;

@XmlRootElement(name = "SellerOrderingProcess")
@XmlType(name = "SellerOrderingProcess")
@XmlAccessorType(XmlAccessType.NONE)
public class SellerOrderingProcess extends OrderingProcess
{
	private boolean orderProcessed; //MMM check whether is it necessary
	private boolean orderAccepted;
	private boolean orderRejected;
	private boolean orderModified;
	private boolean orderCancelled;
	private boolean obsoleteCatalogue;

	/**
	 * Constructs new instance of a {@link SellerOrderingProcess} and sets its state to
	 * default value and uuid to a random value.
	 * @param {@link RutaClient} object
	 * @return {@code SellerOrderingProcess}
	 */
	public static SellerOrderingProcess newInstance(RutaClient client)
	{
		SellerOrderingProcess process = new SellerOrderingProcess();
		process.setState(SellerReceiveOrderState.getInstance());
		process.setId(UUID.randomUUID().toString());
		process.setClient(client);
		process.setActive(true);
		process.orderProcessed = false;
		process.orderAccepted = false;
		process.orderRejected = false;
		process.orderModified = false;
		process.obsoleteCatalogue = false;
		return process;
	}

	/**
	 * Tests whether the most recent of the two documents {@link OrderType orderLines} and {@link OrderChangeType orderLines change}
	 * has been processed.
	 * @return true if processed; false otherwise
	 */
	public boolean isOrderProcessed()
	{
		return orderProcessed;
	}

	/**
	 * Sets the flag denoting whether the most recent of the two documents {@link OrderType orderLines} and
	 * {@link OrderChangeType orderLines change} has been processed.
	 * @param true if processed; false otherwise
	 */
	public void setOrderProcessed(boolean orderProcessed)
	{
		this.orderProcessed = orderProcessed;
	}

	/**
	 * Tests whether the most recent of the two documents {@link OrderType orderLines} and {@link OrderChangeType orderLines change}
	 * has been accepted.
	 * @return true if processed; false otherwise
	 */
	public boolean isOrderAccepted()
	{
		return orderAccepted;
	}

	/**
	 * Sets the flag denoting whether the most recent of the two documents {@link OrderType orderLines} and
	 * {@link OrderChangeType orderLines change} has been processed.
	 * @param true if processed; false otherwise
	 */
	public void setOrderAccepted(boolean orderAccepted)
	{
		this.orderAccepted = orderAccepted;
	}
	/**
	 * Tests whether the most recent of the two documents {@link OrderType orderLines} and {@link OrderChangeType orderLines change}
	 * has been rejected.
	 * @return true if processed; false otherwise
	 */
	public boolean isOrderRejected()
	{
		return orderRejected;
	}
	/**
	 * Sets the flag denoting whether the most recent of the two documents {@link OrderType orderLines} and
	 * {@link OrderChangeType orderLines change} has been processed.
	 * @param true if processed; false otherwise
	 */
	public void setOrderRejected(boolean orderRejected)
	{
		this.orderRejected = orderRejected;
	}

	/**
	 * Tests whether the most recent of the two documents {@link OrderType orderLines} and {@link OrderChangeType orderLines change}
	 * has been modified.
	 * @return true if processed; false otherwise
	 */
	public boolean isOrderModified()
	{
		return orderModified;
	}

	/**
	 * Sets the flag denoting whether the most recent of the two documents {@link OrderType orderLines} and
	 * {@link OrderChangeType orderLines change} has been processed.
	 * @param true if processed; false otherwise
	 */
	public void setOrderModified(boolean orderModified)
	{
		this.orderModified = orderModified;
	}

	/**
	 * Tests whether the Order has ben cancelled by receiving {@link OrderCancellationType Order Cancellation}.
	 * @return true if cancelled; false otherwise
	 */
	public boolean isOrderCancelled()
	{
		return orderCancelled;
	}

	/**
	 * Sets the flag denoting whether the Order has ben cancelled by receiving {@link OrderCancellationType Order Cancellation}.
	 * @param true if cancelled; false otherwise
	 */
	public void setOrderCancelled(boolean orderCancelled)
	{
		this.orderCancelled = orderCancelled;
	}

	public boolean isObsoleteCatalogue()
	{
		return obsoleteCatalogue;
	}

	public void setObsoleteCatalogue(boolean obsoleteCatalogue)
	{
		this.obsoleteCatalogue = obsoleteCatalogue;

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
			}
			if(!correspondence.isValid() || orderRejected || orderCancelled)
				correspondence.changeState(ClosingProcess.newInstance(correspondence.getClient()));
			if(!correspondence.isStopped())
				correspondence.changeState(SupplierBillingProcess.newInstance(correspondence.getClient()));
		}
		catch (Exception e)
		{
			try
			{
				correspondence.stop();
			}
			catch (InterruptedException e1)
			{
				throw new StateActivityException("Unable to stop the correspondence!", e1);
			}
			throw new StateActivityException("Interrupted execution of Seller Ordering Process!", e);
		}
		finally
		{
//			if(!correspondence.isValid() || orderRejected || orderCancelled)
//				correspondence.changeState(ClosingProcess.newInstance(correspondence.getClient()));
//			if(!correspondence.isStopped())
//				correspondence.changeState(BillingProcess.newInstance(correspondence.getClient()));
		}
	}

}