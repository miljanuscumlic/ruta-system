package rs.ruta.client.datamapper;

import java.util.ArrayList;

public interface OLDDataMapper
{
	/**Reads objects from the data store. All objects already read will be discarded,
	 * and read again from the store.
	 * @return list of all the objects, or null if no objects are found
	 */
	public ArrayList<?> findAll() throws Exception;

	/**Writes all objects to the store
	 * @throws Exception if data could not be stored to teh data store
	 */
	public void insertAll() throws Exception;

	public void closeConnection();

}
