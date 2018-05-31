package rs.ruta.client.correspondence;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import rs.ruta.client.RutaClient;

@XmlRootElement(name = "BillingProcess")
@XmlType(name = "BillingProcess")
@XmlAccessorType(XmlAccessType.NONE)
public class BillingProcess extends DocumentProcess
{
	/**
	 * Constructs new instance of a {@link BillingProcess} and sets its state to
	 * default value and uuid to a random value.
	 * @param {@link RutaClient} object
	 * @return {@code BillingProcess}
	 */
	public static BillingProcess newInstance(RutaClient client)
	{
		BillingProcess process = new BillingProcess();
		process.setState(ClosingState.getInstance()); //MMM: default state should be changed
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
			while(active)
				state.doActivity(correspondence);
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
			correspondence.changeState(ClosingProcess.newInstance(client));
		}

	}

}