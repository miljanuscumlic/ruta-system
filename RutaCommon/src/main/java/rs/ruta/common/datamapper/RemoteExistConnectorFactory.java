package rs.ruta.common.datamapper;

/**Factory class that creates {@link ExistConnector} object for remote connection to the database.
 */
@Deprecated
public class RemoteExistConnectorFactory implements ExistConnectorFactory
{
	@Override
	public ExistConnector newInstance()
	{
		return new ExistConnector();
	}
}
