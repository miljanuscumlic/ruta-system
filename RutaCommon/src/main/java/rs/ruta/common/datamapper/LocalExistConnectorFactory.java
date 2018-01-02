package rs.ruta.common.datamapper;

/**Factory class that creates {@link ExistConnector} object for connection to embedded database.
 */
@Deprecated
public class LocalExistConnectorFactory implements ExistConnectorFactory
{
	@Override
	public ExistConnector newInstance()
	{
		//setting database administrator with the proper credentials
		DatabaseAdmin.newInstance("admin", null);
		ExistConnector ec = new ExistConnector();
		ec.setLocalAPI();
		return ec;
	}
}
