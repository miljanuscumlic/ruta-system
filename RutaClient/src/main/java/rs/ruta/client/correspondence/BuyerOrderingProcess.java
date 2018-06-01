package rs.ruta.client.correspondence;

import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import rs.ruta.client.RutaClient;

@XmlRootElement(name = "BuyerOrderingProcess")
@XmlType(name = "BuyerOrderingProcess")
@XmlAccessorType(XmlAccessType.NONE)
public class BuyerOrderingProcess extends OrderingProcess
{
	private boolean orderAccepted;
	private boolean orderRejected;
	private boolean orderChanged;
	private boolean orderCancelled;
	@XmlElement(name = "OrderChangeSequenceNumber")
	private int orderChangeSequenceNumber;

	public BuyerOrderingProcess() { }

	/**
	 * Constructs new instance of a {@link BuyerOrderingProcess} and sets its state to
	 * default value and uuid to a random value.
	 * @param {@link RutaClient} object
	 * @return {@code BuyerOrderingProcess}
	 */
	public static BuyerOrderingProcess newInstance(RutaClient client)
	{
		BuyerOrderingProcess process = new BuyerOrderingProcess();
		process.setState(BuyerPrepareOrderState.getInstance());
		process.setId(UUID.randomUUID().toString());
		process.setClient(client);
		process.setActive(true);
		process.orderChangeSequenceNumber = 0;
		process.orderAccepted = false;
		process.orderRejected = false;
		process.orderChanged = false;
		process.orderCancelled = false;
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

				/*				JAXBContext jaxbContext = JAXBContext.newInstance(BuyingCorrespondence.class);
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(correspondence, System.out);*/
			}
			if(correspondence.isDiscarded() || orderRejected || orderCancelled)
				correspondence.changeState(ClosingProcess.newInstance(correspondence.getClient()));
			else if(!correspondence.isStopped())
				correspondence.changeState(BillingProcess.newInstance(correspondence.getClient()));
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
			throw e;
		}
		finally
		{
//			if(correspondence.isDiscarded() || orderRejected || orderCancelled)
//				correspondence.changeState(ClosingProcess.newInstance(correspondence.getClient()));
//			else if(!correspondence.isStopped())
//				correspondence.changeState(BillingProcess.newInstance(correspondence.getClient()));
		}
	}

	/**
	 * Tests whether the most recent of the two documents {@link OrderType order} and {@link OrderChangeType order change}
	 * has been accepted.
	 * @return true if accepted; false otherwise
	 */
	public boolean isOrderAccepted()
	{
		return orderAccepted;
	}

	/**
	 * Sets the flag noting whether the most recent of the two documents {@link OrderType order} and
	 * {@link OrderChangeType order change} has been accepted.
	 * @param true if accepted; false otherwise
	 */
	public void setOrderAccepted(boolean orderAccepted)
	{
		this.orderAccepted = orderAccepted;
	}

	/**
	 * Tests whether the most recent of the two documents {@link OrderType order} and {@link OrderChangeType order change}
	 * has been rejected.
	 * @return true if rejected; false otherwise
	 */
	public boolean isOrderRejected()
	{
		return orderRejected;
	}

	/**
	 * Sets the flag denoting whether the most recent of the two documents {@link OrderType order} and
	 * {@link OrderChangeType order change} has been rejected.
	 * @param true if rejected; false otherwise
	 */
	public void setOrderRejected(boolean orderRejected)
	{
		this.orderRejected = orderRejected;
	}

	/**
	 * Tests whether the {@link OrderType order} has been changed.
	 * @return true if changed; false otherwise
	 */
	public boolean isOrderChanged()
	{
		return orderChanged;
	}

	/**
	 * Sets the flag denoting whether {@link OrderType order} has been changed.
	 * @param true if changed; false otherwise
	 */
	public void setOrderChanged(boolean orderChanged)
	{
		this.orderChanged = orderChanged;
	}

	/**
	 * Tests whether the {@link OrderType order} has been cancelled.
	 * @return true if cancelled; false otherwise
	 */
	public boolean isOrderCancelled()
	{
		return orderCancelled;
	}

	/**
	 * Sets the flag denoting whether the {@link OrderType order} has been cancelled.
	 * @param true if cancelled; false otherwise
	 */
	public void setOrderCancelled(boolean orderCancelled)
	{
		this.orderCancelled = orderCancelled;
	}

	public int getOrderChangeSequenceNumber()
	{
		return orderChangeSequenceNumber;
	}

	public int getNextOrderChangeSequenceNumber()
	{
		return ++orderChangeSequenceNumber;
	}

	public void decreaseOrderChangeSequenceNumber()
	{
		--orderChangeSequenceNumber;
	}

	public void setOrderChangeSequenceNumber(int orderChangeSequenceNumber)
	{
		this.orderChangeSequenceNumber = orderChangeSequenceNumber;
	}
}