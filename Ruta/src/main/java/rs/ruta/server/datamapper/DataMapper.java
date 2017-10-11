package rs.ruta.server.datamapper;

import java.util.ArrayList;

import rs.ruta.server.DetailException;

/**DataMapper interface declares methods for mapping between objects from the domain model
 * and persistance interpretation of that data in the data store. To be able to connect to
 * some particular database one should implement DataMapper interface are its methods.
 */
public interface DataMapper
{
	/**Retrieves object from the data store with passed id or <code>null</code> if object doesn't exist.
	 * @param id id of the object to be retrieved
	 * @return found object or <code>null</code> if object doesn't exist
	 */
	public Object find(String id) throws Exception; // MMM: should be (Object id)

	/**Searches for the object in the data store.
	 * @param object object which is searched for in the data store
	 * @return object wich type depends on the data store against which this method is called
	 */
	public Object find(Object object) throws Exception;

	/**Retrieves all objects from the data store. Objects that are already read will be discarded,
	 * and read again from the data store.
	 * @return list of all the objects, or null if no objects are found
	 * @throws DetailException thrown if object could not be retrieved
	 */
	public ArrayList<?> findAll() throws DetailException;

	/** Writes all objects to the data store.
	 */
	public void insertAll();

	/**Inserts object with the passed id to the data store.
	 * @param object object to be stored
	 * @param id id of the object
	 * @param transaction data store transaction which insert is part of
	 * @param <T> transaction class that is subclass of <code>DSTransaction</code>
	 */
	public <T extends DSTransaction> void insert(Object object, Object id, T transaction) throws DetailException;

	/**Inserts object to the data store. If necessary unique id of the object may be created.
	 * @param object object to be stored
	 * @param transaction data store transaction which insert is part of
	 * @return object's id
	 * @throws Exception exception is thrown if object cannot be insert in the store
	 */
	public <T extends DSTransaction> Object insert(Object object, T transaction) throws DetailException;

	/**Updates user data in the data store.
	 * @param user user object to be updated
	 * @param transaction data store transaction which user update is part of
	 * @result user's id
	 * @throws DetailException TODO
	 */
	default public <T extends DSTransaction> Object updateUser(Object user, T transaction) throws DetailException { return null; }

/*	/**Updates the object in the data store.
	 * @param username user's username
	 * @param object object to be updated
	 * @throws Exception thrown if object cannot be updated
	 *//*
	default public void update(String username, Object object) throws Exception { };*/

	/**Updates object with passed id.
	 * @param object object to be updated
	 * @param id object's id
	 * @param transaction data store transaction which update is part of
	 * @throws Exception thrown if object could not be updated
	 */
	public <T extends DSTransaction>void update(Object object, Object id, T transaction) throws DetailException;

	/**Retrieves object's id from the data store.
	 * @param object object which id is requested
	 * @return object's id
	 * @throws Exception thrown if the object cannot be found in the data store
	 */
	public Object getID(Object object) throws DetailException;

	/**Deletes object with passed id from the data store.
	 * @param id id of the object that should be deleted
	 * @param transaction data store transaction which deletion is part of
	 * @throws Exception thrown if object cannot be deleted
	 */
	public <T extends DSTransaction> void delete(Object id, T transaction) throws DetailException;

	/**Deletes user from the data store.
	 * @param id id of the object that should be deleted
	 * @param transaction data store transaction which user deletion is part of
	 * @param user' username
	 * @throws Exception thrown if user cannot be deleted
	 */
	default public <T extends DSTransaction> void deleteUser(String username, Object id, T transaction) throws DetailException {}

	/**Registers new user with the data store.
	 * @param username user's username
	 * @param password user's password
	 * @param transaction data store transaction which registration of the user is part of
	 * @return user's identification object
	 * @throws DetailException TODO
	 */
	default public <T extends DSTransaction> Object registerUser(String username, String password, T transaction)
			throws DetailException { return null; }

	/**Gets user's secret key from the data store.
	 * @param username user's username
	 * @return secret key or <code>null</code> if secret key is not stored for a given username
	 * @throws Exception thrown if there is a problem with the data store connectivity
	 */
	default public Object findSecretKey(String username) throws DetailException { return null; }

}