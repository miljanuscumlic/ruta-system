package rs.ruta.common.datamapper;

import java.awt.Image;
import java.io.File;
import java.util.List;

import org.xmldb.api.base.XMLDBException;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
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

	//MMM: check whether this method is necessary
	/**Retrieves object from the data store. Object is identified by the user's username
	 * @param username user's username
	 * @return found object or {@code null} if object doesn't exist
	 * @throws DetailException if user is not registered, ID could not be retrieved
	 */
	public T findByUsername(String username) throws DetailException;

	/**Retrieves all objects from the data store or <code>null</code> if no object exists.
	 * @return list of all the objects, or {@code null} if no objects are found
	 * @throws DetailException if object could not be retrieved
	 */
	default public List<T> findAll() throws DetailException { return null; }

	/**Retrieves all object IDs from the data store or {@code null} if no object exists.
	 * @return list of all the objects or {@code null} if no objects are found
	 * @throws DetailException if object could not be retrieved
	 */
	default public List<ID> findAllIDs() throws DetailException { return null; }

	/**Queries the data store based on the {@link SearchCriterion search criterion}.
	 * Result is a list of objects that are of type {@code<T>} which is a type parameter of the appropriate
	 * subclass instance of {@code DataMapper<T>} interface.
	 * @param criterion search criterion
	 * @return list of objects of type {@code<T>} or {@code null} if no object conforms to the search criterion
	 * @throws DetailException if search request could not be processed
	 */
	default public List<T> findMany(SearchCriterion criterion) throws DetailException { return null; }

	/**Queries the data store based on the {@link SearchCriterion search criterion}.
	 * Result is a list of ids of object that conform to the search criterion.
	 * @param criterion search criterion
	 * @return list of object ids or {@code null} if no object conforms to the search criterion
	 * @throws DetailException if search request could not be processed
	 */
	default public List<ID> findManyIDs(SearchCriterion criterion) throws DetailException { return null; }

	/**Queries the data store based on the search criterion. Uses xQuery that returns sequence of resource's ids
	 * that conform to the queried criterion. Result is a list of objects that are of type {@code<T>}
	 * which is a type parameter of the appropriate subcalss instance of {@code DataMapper<T>} interface.
	 * @param criterion search criterion
	 * @return list of objects of type {@code<T>}
	 * @throws DetailException if search request could not be processed
	 */
	//MMM: this method is not used; it is left just to test querying the database with IDs
	default public List<T> findManyQueryingByID(CatalogueSearchCriterion criterion) throws DetailException { return null; }

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

	/**Inserts object to the data store. If necessary unique id of the object may be created.
	 * @param username username of the user whose object is to be stored in the datastore
	 * @param object object to be stored
	 * @return object's id
	 * @throws DetailException if object cannot be insert in the store
	 */
	public ID insert(String username, T object) throws DetailException;

	/**Updates the object in the data store.
	 * @param username user's username
	 * @param object object to be updated
	 * @return object's ID
	 * @throws DetailException if object could not be updated
	 */
	public ID update(String username, T object) throws DetailException;

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
	 * @param username user's username
	 * @param id id of the object that should be deleted
	 * @throws DetailException if object cannot be deleted
	 */
	public void delete(String username, ID id) throws DetailException;

	/**Deletes user from the data store.
	 * @param user's username
	 * @throws DetailException if user cannot be deleted
	 */
	default public void deleteUser(String username) throws DetailException { }

	/**Deletes DocBoxDocument
	 * @param username username of the user from which DocBox document is to be deleted
	 * @param id document's id
	 * @throws DetailException if document could not be deleted
	 */
	default public void deleteDocBoxDocument(String username, ID id) throws DetailException {}

	/**Registers new user with the data store.
	 * @param username user's username
	 * @param password user's password
	 * @return user's identification object
	 * @throws DetailException if user could not be registered
	 */
/*	@Deprecated
	default public ID registerUser(String username, String password) throws DetailException { return null; }*/

	/**Registers new user with the data store.
	 * @param username user's username
	 * @param password user's password
	 * @param party party data to register
	 * @return user's identification object
	 * @throws DetailException if user could not be registered
	 */
	default public ID registerUser(String username, String password, PartyType party) throws DetailException { return null; }

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
	@Deprecated
	default public void insert(File file, String id, DSTransaction transaction) throws DetailException { }

	/**Inserts image in the datastore.
	 * @param file
	 * @param id
	 * @param transaction
	 */
	@Deprecated
	default public void insert(Image file, String id, DSTransaction transaction) throws DetailException { }

	//MMM: not used except with MyParty and that is only temporary. After end of its usage it should be deleted.
	@Deprecated
	public void insert(T object) throws DetailException;

}