package rs.ruta.common.datamapper;

public interface DatastoreConnector
{
	/**
	 * Connects to the database.
	 * @throws DatabaseException if fails to connect to the database
	 */
	public void connectToDatabase() throws DatabaseException;

	public void shutdownDatabase() throws Exception;

	/**
	 * Checks whether the user is registered with the data store.
	 * @param username
	 * @param password
	 * @return true if user is registered, false otherwise
	 * @throws DatabaseException due to data store connectivity issues
	 */
	public boolean checkUser(String username, String password) throws DatabaseException;

}