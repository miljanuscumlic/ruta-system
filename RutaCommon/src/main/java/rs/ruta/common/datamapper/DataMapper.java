package rs.ruta.common.datamapper;

import java.awt.Image;
import java.io.File;
import java.util.List;

import org.xmldb.api.base.XMLDBException;

import rs.ruta.common.RutaVersion;
import rs.ruta.common.SearchCriterion;
import rs.ruta.common.CatalogueSearchCriterion;

/**DataMapper interface declares methods responsible for mappings between objects from the domain model
 * and persistance interpretation of that data in the data store. To be able to connect to
 * some particular database one should implement DataMapper interface and its methods.
 * @param <T> type of the object
 * @param <ID> type of the object's id
 */
public interface DataMapper<T, ID>
{
	/**Retrieves object with passed id from the data store.
	 * @param id id of the object to be retrieved
	 * @return found object or <code>null</code> if object doesn't exist
	 * @throws DetailException if object could not be retrived
	 */
	public T find(ID id) throws DetailException;

	/**Retrieves object from the data store. Object is identified by the user's ID.
	 * @param id user's ID
	 * @return found object or {@code null} if object doesn't exist
	 * @throws DetailException if there is more than one object with the same ID
	 */
	public T findByUserId(ID id) throws DetailException;

	/**Retrieves all objects from the data store or <code>null</code> if no object exists.
	 * @return list of all the objects, or null if no objects are found
	 * @throws DetailException if object could not be retrieved
	 */
	default public List<T> findAll() throws DetailException { return null; }

	/**Queries the data store based on the search criterion.
	 * Result is a list of objects that are of type {@code<T>} which is a type parameter of the appropriate
	 * subclass instance of {@code DataMapper<T>} interface.
	 * @param criterion search criterion
	 * @return list of objects of type {@code<T>}
	 * @throws DetailException if search request could not be processed
	 */
	default public List<T> findMany(SearchCriterion criterion) throws DetailException {return null; }

	/**Queries the data store based on the search criterion. Uses xQuery that returns sequence of resource's ids
	 * that conform to the queried criterion. Result is a list of objects that are of type {@code<T>}
	 * which is a type parameter of the appropriate subcalss instance of {@code DataMapper<T>} interface.
	 * @param criterion search criterion
	 * @return list of objects of type {@code<T>}
	 * @throws DetailException if search request could not be processed
	 */
	default public List<T> findManyID(CatalogueSearchCriterion criterion) throws DetailException { return null; }

	/**Queries the data store based on the search criterion. Result is a list of objects that are of type {@code<U>}
	 * which is a type parameter of the method.
	 * @param searchResult list of object conforming to the search criterion
	 * @param criterion search criterion
	 * @param <U> type og the object in the result list
	 * @throws DetailException if search request could not be processed
	 */
	default public <U> List<U> findGeneric(CatalogueSearchCriterion criterion) throws DetailException { return null; }

	/** Writes all objects to the data store.
	 * @throws DetailException TODO
	 */
	public void insertAll() throws DetailException;

	/**Inserts object with the passed id to the data store.
	 * @param object object to be stored
	 * @param id id of the object
	 * @param transaction data store transaction which insert is part of
	 * @throws DetailException if object cannot be insert in the store
	 * @param <T> transaction class that is subclass of <code>DSTransaction</code>
	 */
	public void insert(T object, ID id, DSTransaction transaction) throws DetailException;

	//MMM: maybe this method should be excluded from the interface, and use only the variant with the username(=null)
	public void insert(T object) throws DetailException;

	/**Inserts object to the data store. If necessary unique id of the object may be created. This method is supposed
	 * to be used only inside {@code DataMapper} class hierarchy because {@link DSTransaction} object is an
	 * argument, and objects of that class are created only in {@code DataMapper}s subclasses.
	 * @param username username of the user whose object is to be stored in the datastore
	 * @param object object to be stored
	 * @param transaction data store transaction which insert is part of
	 * @return object's id
	 * @throws DetailException if object cannot be insert in the store
	 */
//	@Deprecated // MMM: used as protected method in XmlMapper
//	public ID insert(String username, T object, DSTransaction transaction) throws DetailException;

	/**Inserts object to the data store. If necessary unique id of the object may be created.
	 * @param username username of the user whose object is to be stored in the datastore
	 * @param object object to be stored
	 * @return object's id
	 * @throws DetailException if object cannot be insert in the store
	 */
	public ID insert(String username, T object) throws DetailException;

	/**Updates object with passed id.
	 * @param object object to be updated
	 * @param id id of the object
	 * @param transaction data store transaction which insert is part of
	 * @throws DetailException if object cannot be updated
	 * @param <T> transaction class that is subclass of <code>DSTransaction</code>
	 */
	default public void update(T object, ID id, DSTransaction transaction) throws DetailException { }

	/**Updates the object in the data store.
	 * @param username user's username
	 * @param object object to be updated
	 * @param transaction data store transaction which update is part of
	 * @return object's ID
	 * @throws DetailException if object could not be updated
	 */
	@Deprecated
	public ID update(String username, T object, DSTransaction transaction) throws DetailException;

	/**Updates the object in the data store.
	 * @param username user's username
	 * @param object object to be updated
	 * @return object's ID
	 * @throws DetailException if object could not be updated
	 */
	public ID update(String username, T object) throws DetailException;

	//MMM: maybe unnecessary method ???
	/**Retrieves object's id from the data store. Id is the result of the querying the data store
	 * based on the passed argument.
	 * @param userID user's ID
	 * @return id of the object in the datastore
	 * @throws DetailException if the object cannot be found in the data store
	 */
	default public ID getIDByUserID(ID userID) throws DetailException { return null; }

	//MMM: maybe this method should always return String, because insert WebMethod returns String
	/**Retrieves unique ID of the user of the CDR service.
	 * @param username user's username
	 * @return user's unique ID
	 * @throws DetailException if user ID could not be retrieved
	 */
	public ID getUserID(String username) throws DetailException;

	/**Retrieves object's id from the data store. ID is the result of the querying the data store
	 * based on the contents of the passed argument.
	 * @param object object which id is requested
	 * @return object's id
	 * @throws DetailException if the object cannot be found in the data store
	 */
	default public ID getID(T object) throws DetailException { return null; }

	/**Deletes object with passed id from the data store.
	 * @param id id of the object that should be deleted
	 * @param transaction data store transaction which deletion is part of
	 * @throws DetailException if object cannot be deleted
	 */
	public void delete(ID id, DSTransaction transaction) throws DetailException;

	/**Deletes user from the data store.
	 * @param user' username
	 * @throws DetailException if user cannot be deleted
	 */
	default public void deleteUser(String username) throws DetailException {}

	/**Registers new user with the data store.
	 * @param username user's username
	 * @param password user's password
	 * @return user's identification object
	 * @throws DetailException if user could not be registered
	 */
	default public ID registerUser(String username, String password) throws DetailException { return null; }

	/**Gets user's secret key from the data store.
	 * @param username user's username
	 * @return secret key or <code>null</code> if secret key is not stored for a given username
	 * @throws DetailException if there is a problem with the data store connectivity or user is not registered
	 */
	default public Object findSecretKey(String username) throws DetailException { return null; }

	//MMM: should be checked if this method could be put in some subclass
	/**Creates unique ID for an object.
	 * @param <S> type of the id object
	 * @return ID
	 */
	public ID createID() throws XMLDBException;

	/**Inserts file in the datastore.
	 * @param file
	 * @param id
	 * @param transaction
	 */
	default public void insert(File file, String id, DSTransaction transaction) throws DetailException { }

	/**Inserts image in the datastore.
	 * @param file
	 * @param id
	 * @param transaction
	 */
	default public void insert(Image file, String id, DSTransaction transaction) throws DetailException { }

}