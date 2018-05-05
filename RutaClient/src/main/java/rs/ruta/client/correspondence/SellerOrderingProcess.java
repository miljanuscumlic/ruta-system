package rs.ruta.client.correspondence;

import java.util.UUID;
import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import rs.ruta.client.RutaClient;
import rs.ruta.common.datamapper.MapperRegistry;

@XmlRootElement(name = "SellerOrderingProcess")
@XmlType(name = "SellerOrderingProcess")
@XmlAccessorType(XmlAccessType.NONE)
public class SellerOrderingProcess extends BuyingProcess
{
	private OrderType order;
	private Future<?> future;

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
		return process;
	}

	public OrderType getOrder()
	{
		return order;
	}

	public void setOrder(OrderType order)
	{
		this.order = order;
	}

	public Future<?> getFuture()
	{
		return future;
	}

	public void setFuture(Future<?> future)
	{
		this.future = future;
	}

	@Override
	public void orderingActivity(Correspondence correspondence) throws StateTransitionException
	{
		try
		{
			while(active && !correspondence.isStopped())
			{
				doActivity(correspondence, this);
				MapperRegistry.getInstance().getMapper(BuyingCorrespondence.class).insert(null, (BuyingCorrespondence) correspondence);
			}
		}
		catch (Exception e)
		{
			throw new StateTransitionException("Interrupted execution of Seller Ordering Process!", e);
		}
		finally
		{
			if(correspondence.isActive() && !correspondence.isStopped())
				correspondence.changeState(BillingProcess.newInstance(correspondence.getClient()));
			else
				correspondence.changeState(BuyingClosingProcess.newInstance(correspondence.getClient()));
		}
	}

	@Override
	public void doActivity(Correspondence correspondence, RutaProcess process)
	{
		((SellerOrderingProcessState) state).doActivity(correspondence, process);
	}

}