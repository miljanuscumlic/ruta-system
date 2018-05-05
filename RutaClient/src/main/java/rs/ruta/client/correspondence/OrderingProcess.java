package rs.ruta.client.correspondence;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import rs.ruta.client.RutaClient;

@Deprecated //MMM now there are two separate classes BuyerOrderingProcess and SellerOrderingProcess
@XmlRootElement(name = "OrderingProcess")
@XmlType(name = "OrderingProcess")
@XmlAccessorType(XmlAccessType.NONE)
public class OrderingProcess extends BuyingProcess
{
	/**
	 * Constructs new instance of a {@link OrderingProcess} and sets its state to
	 * default value and uuid to a random value.
	 * @param {@link RutaClient} object
	 * @return {@code OrderingProcess}
	 */
	public static OrderingProcess newInstance(RutaClient client)
	{
		OrderingProcess process = new OrderingProcess();
		process.setState(PrepareCatalogueState.getInstance());//MMM: state should be changed
		process.setId(UUID.randomUUID().toString());
		process.setClient(client);
		return process;
	}

	@Override
	public void ordering(Correspondence correspondence) throws StateTransitionException
	{


	}




}
