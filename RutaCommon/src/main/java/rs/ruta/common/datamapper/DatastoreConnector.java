package rs.ruta.common.datamapper;

public interface DatastoreConnector
{
	public void connectToDatabase() throws DatabaseException;

	public void shutdownDatabase() throws Exception;

}