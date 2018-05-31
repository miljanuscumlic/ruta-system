package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *	Common state denoting end of the process.
 */
@XmlRootElement(name = "ClosingState")
public class ClosingState implements RutaProcessState
{
	private static final ClosingState INSTANCE = new ClosingState();

	public static ClosingState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void doActivity(Correspondence correspondence)
	{
		((RutaProcess) correspondence.getState()).setActive(false);
	}

}