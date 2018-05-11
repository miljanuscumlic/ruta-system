package rs.ruta.client.correspondence;

import java.util.UUID;
import java.util.concurrent.Future;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.client.RutaClient;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.MapperRegistry;

@XmlRootElement(name = "BuyerOrderingProcess")
@XmlType(name = "BuyerOrderingProcess")
@XmlAccessorType(XmlAccessType.NONE)
public class BuyerOrderingProcess extends BuyingProcess
{
	private OrderType order;
	private OrderResponseSimpleType orderResponseSimple;
	private Future<?> future;

	public BuyerOrderingProcess() { }

	/**
	 * Constructs new instance of a {@link BuyerOrderingProcess} and sets its state to
	 * default value and uuid to a random value.
	 * @param {@link RutaClient} object
	 * @return {@code BuyerOrderingProcess}
	 */
	//MMM to solve the issue when Correspondence is retrieved from the database one parameter should be
	//MMM Correspondence; should be a field of DocumentProcess
	public static BuyerOrderingProcess newInstance(RutaClient client)
	{
		BuyerOrderingProcess process = new BuyerOrderingProcess();
		process.setState(BuyerPlaceOrderState.getInstance());
		process.setId(UUID.randomUUID().toString());
		process.setClient(client);
		process.setActive(true);
		return process;
	}

	//MMM to solve the issue when Correspondence is retrieved from the database when order equals to null
	//MMM try to find it in Correspondence's document references
	public OrderType getOrder()
	{
		return order;
	}

	public void setOrder(OrderType order)
	{
		this.order = order;
	}

	public OrderResponseSimpleType getOrderResponseSimple()
	{
		return orderResponseSimple;
	}

	public void setOrderResponseSimple(OrderResponseSimpleType orderResponseSimple)
	{
		this.orderResponseSimple = orderResponseSimple;

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
	public void ordering(Correspondence correspondence) throws StateTransitionException
	{
		try
		{
			placeOrder();
			while(active)
			{
				receiveOrderResponse();
				processResponse();
				if(state instanceof BuyerOrderRejectedState)
				{
					correspondence.active = false;
					orderRejected();
					endOfProcess();
				}
				if(state instanceof BuyerChangeOrderState)
				{
					changeOrder();
				}
				else if(state instanceof BuyerCancelOrderState)
				{
					correspondence.active = false;
					cancelOrder();
					endOfProcess();
				}
				else if(state instanceof BuyerAcceptOrderState)
				{
					acceptOrder();
					orderAccepted();
					endOfProcess();
				}
			}
		}
		catch (Exception e)
		{
			throw new StateTransitionException("Interrupted execution of Buyer Ordering Process!", e);
		}
		finally
		{
			if(correspondence.active)
				correspondence.changeState(BillingProcess.newInstance(correspondence.getClient()));
			else
				correspondence.changeState(BuyingClosingProcess.newInstance(correspondence.getClient()));
		}
	}

	@Override
	@Deprecated
	public void orderingActivity(Correspondence correspondence) throws StateTransitionException
	{
		try
		{
			while(active && !correspondence.isStopped())
			{
				doActivity(correspondence);

/*				JAXBContext jaxbContext = JAXBContext.newInstance(BuyingCorrespondence.class);
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(correspondence, System.out);*/

				MapperRegistry.getInstance().getMapper(BuyingCorrespondence.class).insert(null, (BuyingCorrespondence) correspondence);
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
				if(active)
					correspondence.changeState(BillingProcess.newInstance(correspondence.getClient()));
				else //MMM check whether this is the right test for transition to BuyingClosingProcess
					correspondence.changeState(BuyingClosingProcess.newInstance(correspondence.getClient()));
			}
		}
	}

	/**
	 * Prepares and sends {@link OrderType} document.
	 */
	public void placeOrder()
	{
		((BuyerOrderingProcessState) state).placeOrder(this);
	}

	/**
	 *
	 */
	public void receiveOrderResponse()
	{
		((BuyerOrderingProcessState) state).receiveOrderResponse(this);
	}

	/**
	 *
	 */
	public void orderRejected()
	{
		((BuyerOrderingProcessState) state).orderRejected(this);
	}

	/**
	 *
	 */
	public void processResponse()
	{
		((BuyerOrderingProcessState) state).processResponse(this);
	}

	/**
	 *
	 */
	public void changeOrder()
	{
		((BuyerOrderingProcessState) state).changeOrder(this);
	}

	/**
	 *
	 */
	public void cancelOrder()
	{
		((BuyerOrderingProcessState) state).cancelOrder(this);
	}

	/**
	 *
	 */
	public void acceptOrder()
	{
		((BuyerOrderingProcessState) state).acceptOrder(this);
	}

	/**
	 *
	 */
	//MMM: maybe not necessary at all. Use endOfProcess() instead?
	public void orderAccepted()
	{
		((BuyerOrderingProcessState) state).orderAccepted(this);
	}

	/**
	 * Ends the process. Sets {@code active} boolean field to false.
	 */
	public void endOfProcess()
	{
		active = false;
		state.endOfProcess(this);
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		try
		{
			while(active && !correspondence.isStopped())
			{
				//MMM refactor database store in a new method of Correspondence
		/*		new Thread( () ->
				{
					try
					{
						MapperRegistry.getInstance().
						getMapper(BuyingCorrespondence.class).insert(null, (BuyingCorrespondence) correspondence);
					}
					catch (DetailException e)
					{
						//MMM log error;
					}
				}
				).start();*/

				correspondence.store();

				state.doActivity(correspondence);

/*				JAXBContext jaxbContext = JAXBContext.newInstance(BuyingCorrespondence.class);
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
				if(active)
					correspondence.changeState(BillingProcess.newInstance(correspondence.getClient()));
				else //MMM check whether this is the right test for transition to BuyingClosingProcess
					correspondence.changeState(BuyingClosingProcess.newInstance(correspondence.getClient()));
			}
		}
	}


}