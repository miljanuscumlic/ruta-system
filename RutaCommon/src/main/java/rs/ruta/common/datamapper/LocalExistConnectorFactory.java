package rs.ruta.common.datamapper;

/**Factory class that creates {@link ExistConnector} object for connection to embedded database.
 */
public class LocalExistConnectorFactory implements ExistConnectorFactory
{
	@Override
	public ExistConnector newInstance()
	{
		ExistConnector ec = new ExistConnector();
		ec.setLocalAPI();
		return ec;
	}

}
