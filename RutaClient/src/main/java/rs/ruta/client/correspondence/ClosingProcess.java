package rs.ruta.client.correspondence;

import java.awt.Color;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import rs.ruta.client.RutaClient;

@XmlRootElement(name = "ClosingProcess")
@XmlType(name = "ClosingProcess")
@XmlAccessorType(XmlAccessType.NONE)
public class ClosingProcess extends DocumentProcess
{
	/**
	 * Constructs new instance of a {@link ClosingProcess} and sets its state to
	 * default value and uuid to a random value.
	 * @param {@link RutaClient} object
	 * @return {@code ClosingProcess}
	 */
	public static ClosingProcess newInstance(RutaClient client)
	{
		ClosingProcess process = new ClosingProcess();
		process.setState(ClosingState.getInstance());
		process.setId(UUID.randomUUID().toString());
		process.setClient(client);
		return process;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		while(active)
			state.doActivity(correspondence);
		correspondence.setActive(false);
		client.getClientFrame().appendToConsole(new StringBuilder(Messages.getString("ClosingProcess.0")). 
				append(correspondence.getName()).append(Messages.getString("ClosingProcess.1")), Color.BLACK); 
	}



}