package rs.ruta.server;

import java.util.List;

import javax.jws.*;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.common.SearchCriterion;

@WebService(targetNamespace = "http://ruta.rs/services")
public interface Server
{
	@Deprecated
	@WebMethod(operationName = "PutDocument")
	public void putDocument(CatalogueType d);

	@Deprecated
	@WebMethod(operationName = "GetDocument")
	public CatalogueType getDocument();

	/**Inserts catalogue object in the database.
	 * @param username username of the party which catalogue is stored
	 * @param cat catalogue object to be stored in the database
	 * @throws RutaException if the catalogue object cannot be inserted in the database
	 */
	@WebMethod(operationName = "InsertCatalogue")
	public void insertCatalogue(String username, CatalogueType cat) throws RutaException;

	/**Updates catalogue object in the database.
	 * @param username username of the party which catalogue is updated
	 * @param cat catalogue object to be updated in the database
	 * @throws RutaException if the catalogue object cannot be updated in the database
	 */
	@WebMethod(operationName = "UpdateCatalogue")
	public void updateCatalogue(String username, CatalogueType cat) throws RutaException;

	/**Retrives catalogue with passed id from the database.
	 * @param id catalogue's id
	 * @return catalogue
	 * @throws RutaException if Catalogue could not be found
	 */
	@WebMethod(operationName = "FindCatalogue")
	public CatalogueType findCatalogue(String id) throws RutaException;

	/**Deletes catalogue object from the database. Catalogue is referenced with the passed
	 * CatalogueDeletion object.
	 * @param username username of the user with catalogu should be deleted
	 * @param catDeletion CatalogueDeletion object referencing Catalogue
	 * @throws RutaException if the Catalogue object cannot be deleted, or CatalogueDeletion
	 * object cannot be inserted in the database
	 */
	@WebMethod(operationName = "DeleteCatalogue")
	public void deleteCatalogue(String username, CatalogueDeletionType catDeletion) throws RutaException;

	/**Registers user with the service.
	 * @param username user's username
	 * @param password user's password
	 * @return user's secret key
	 * @throws RutaException throw if it was unable to register the user
	 */
	@WebMethod(operationName = "RegisterUser")
	public String registerUser(String username, String password) throws RutaException;

	/**Deletes user from the database.
	 * @param username user's username to be deleted
	 * @throws RutaException if the user cannot be deleted
	 */
	@WebMethod(operationName = "DeleteUser")
	public void deleteUser(String username) throws RutaException;

	/**Inserts party object in the database.
	 * @param username party's username
	 * @param party party object representing the user to be inserted into the database
	 * @return party's unique id
	 * @throws RutaException if the party object cannot be inserted in the database
	 */
	@WebMethod(operationName = "InsertParty")
	public String insertParty(String username, PartyType party) throws RutaException;

	/**Updates party object in the database.
	 * @param username party's username
	 * @param party party object representing the user to be updated into the database
	 * @return party's unique id
	 * @throws RutaException if the party object cannot be updated in the database
	 */
	@WebMethod(operationName = "UpdateParty")
	public void updateParty(String username, PartyType party) throws RutaException;

	/**Query the parties in database with the search criterion.
	 * @param username username of the party requesting the results
	 * @param criterion search criterion
	 * @return list of <code>PartyType</code>s conforming to the search criterion
	 * @throws RutaException if search could not be conducted
	 */
	@WebMethod(operationName = "QueryParty")
	public List<PartyType> queryParty(String username, PartyType criterion) throws RutaException;

	@WebMethod(operationName = "QueryPartyName")
	public PartyType queryPartyName(String username, String partyName)throws RutaException;

	@WebMethod(operationName = "SearchParty")
	public List<PartyType> searchParty(String username, SearchCriterion criterion) throws RutaException;

	@WebMethod(operationName = "SearchCatalogue")
	public List<CatalogueType> searchCatalogue(String username, SearchCriterion criterion) throws RutaException;

	@WebMethod(operationName = "TestEpisode")
	public void testEpisode(Episode e);

	/**Temporary web method for testing the jaxb.
	 * @return
	 * @throws RutaException
	 */
	@WebMethod(operationName = "FindAllParties")
	public List<PartyType> findAllParties() throws RutaException;
}
