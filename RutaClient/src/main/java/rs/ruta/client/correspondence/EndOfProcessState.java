package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *	Common state denoting end of the process.
 */
@XmlRootElement(name = "EndOfProcessState", namespace = "urn:rs:ruta:client")
public class EndOfProcessState implements RutaProcessState
{
	private static final EndOfProcessState INSTANCE = new EndOfProcessState();

	public static EndOfProcessState getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void endOfProcess(RutaProcess process)
	{
		//do nothing
	}

}
