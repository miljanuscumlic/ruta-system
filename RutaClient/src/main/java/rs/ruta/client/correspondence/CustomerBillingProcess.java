package rs.ruta.client.correspondence;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import rs.ruta.client.RutaClient;

@XmlRootElement(name = "CustomerBillingProcess")
@XmlType(name = "CustomerBillingProcess")
@XmlAccessorType(XmlAccessType.NONE)
public class CustomerBillingProcess extends BillingProcess
{
	public CustomerBillingProcess() {}

	/**
	 * Constructs new instance of a {@link CustomerBillingProcess} and sets its state to
	 * default value and uuid to a random value.
	 * @param {@link RutaClient} object
	 * @return {@code CustomerBillingProcess}
	 */
	public static CustomerBillingProcess newInstance(RutaClient client)
	{
		CustomerBillingProcess process = new CustomerBillingProcess();
		process.setState(CustomerReceiveInvoiceState.getInstance());
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
				state.doActivity(correspondence);
			if(!correspondence.isStopped())
				correspondence.changeState(ClosingProcess.newInstance(client));
		}
		catch(Exception e)
		{
			try
			{
				correspondence.stop();
			}
			catch (InterruptedException e1)
			{
				throw new StateActivityException("Unable to stop the correspondence!", e1);
			}
			throw new StateActivityException("Interrupted execution of Billing Process.", e);
		}
		finally
		{

		}
	}

}