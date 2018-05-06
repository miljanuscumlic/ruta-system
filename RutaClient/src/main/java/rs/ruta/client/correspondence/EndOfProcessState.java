package rs.ruta.client.correspondence;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *	Common state denoting end of the process.
 */
@XmlRootElement(name = "EndOfProcessState")
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
		process.active = false;
	}

	@Override
	public void doActivity(Correspondence correspondence, RutaProcess process)
	{
		process.active = false;
	}

}
