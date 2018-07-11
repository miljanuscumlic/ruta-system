package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;

@XmlRootElement(name = "SupplierReceiveApplicationResponseState")
public class SupplierReceiveApplicationResponseState extends SupplierBillingProcessState
{
	private static SupplierBillingProcessState INSTANCE = new SupplierReceiveApplicationResponseState();

	public static SupplierBillingProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence) throws StateActivityException
	{
		try
		{
			correspondence.block();
			final SupplierBillingProcess process = (SupplierBillingProcess) correspondence.getState();
			final ApplicationResponseType appResponse = process.getApplicationResponse(correspondence);
			correspondence.validateDocument(appResponse);
			changeState(process, SupplierValidateResponseState.getInstance());
		}
		catch(InterruptedException e)
		{
			if(!correspondence.isStopped())
				throw new StateActivityException(Messages.getString("SupplierReceiveApplicationResponseState.0")); 
		}

	}
}