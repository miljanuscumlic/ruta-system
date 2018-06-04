package rs.ruta.client.correspondence;

import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import rs.ruta.client.RutaClient;

@XmlRootElement(name = "SupplierBillingProcess")
@XmlType(name = "SupplierBillingProcess")
@XmlAccessorType(XmlAccessType.NONE)
public class SupplierBillingProcess extends BillingProcess
{
	@XmlElement(name = "InvoiceDiscarded")
	private boolean invoiceDiscarded;

	public SupplierBillingProcess() { }

	/**
	 * Constructs new instance of a {@link SupplierBillingProcess} and sets its state to
	 * default value and uuid to a random value.
	 * @param {@link RutaClient} object
	 * @return {@code SupplierBillingProcess}
	 */
	public static SupplierBillingProcess newInstance(RutaClient client)
	{
		SupplierBillingProcess process = new SupplierBillingProcess();
		process.setState(SupplierRaiseInvoiceState.getInstance());
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

	/**
	 * Tests whether the {@code Invoice} has been discarded by the user.
	 * @return true if discarded; false otherwise
	 */
	public boolean isInvoiceDiscarded()
	{
		return invoiceDiscarded;
	}

	/**
	 * Sets the flag denoting whether the {@code Invoice} has been discarded by the user.
	 * @param true if processed; false otherwise
	 */
	public void setInvoiceDiscarded(boolean invoiceDiscarder)
	{
		this.invoiceDiscarded = invoiceDiscarder;
	}

}