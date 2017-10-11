package rs.ruta.client.datamapper;

import java.util.ArrayList;

public interface OLDDataMapper
{
	/**
	 * Reads objects from the store. All objects already read will be discarded, and read again from the store.
	 * @return list of all the objects, or null if no objects are found
	 */
	public ArrayList<?> findAll();

	/**
	 * Writes all objects to the store
	 */
	public void insertAll();

	public void closeConnection();



}
