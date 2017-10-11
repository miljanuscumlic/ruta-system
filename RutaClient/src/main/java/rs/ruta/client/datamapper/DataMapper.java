package rs.ruta.client.datamapper;

import java.util.ArrayList;

public interface DataMapper
{

	/**Reads the object from the data store wich have apropriate id
	 * @param id id of the object in search
	 * @return found object
	 */
	public Object find(String id);

	/**Searches for the object in the data store
	 * @param object object which is searched for in the data store
	 * @return object wich depends of the data store against which this method is called
	 */
	public Object find(Object object);

	/**
	 * Reads objects from the store. All objects already read will be discarded, and read again from the store.
	 * @return list of all the objects, or null if no objects are found
	 */
	public ArrayList<?> findAll(Object o);

	/**
	 * Writes all objects to the store
	 */
	public void insertAll();

	public void insert(Object object, String id);




}