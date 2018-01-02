package rs.ruta.common.datamapper;

//MMM: this factory is not used, but apropriate subclasses of ExistConnector are directly instantiated
@Deprecated
public interface ExistConnectorFactory
{
	/**Creates new instance of concrete {@link ExistConnector} class.
	 * @return
	 */
	public ExistConnector newInstance();
}
