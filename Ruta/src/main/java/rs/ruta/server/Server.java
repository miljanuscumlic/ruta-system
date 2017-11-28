package rs.ruta.server;

import java.awt.Image;
import java.io.File;
import java.util.List;

import javax.activation.DataHandler;
import javax.jws.*;
import javax.xml.bind.annotation.XmlMimeType;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.common.ReportAttachment;
import rs.ruta.common.BugReport;
import rs.ruta.common.RutaVersion;
import rs.ruta.common.SearchCriterion;

@WebService(targetNamespace = "http://ruta.rs/services")
public interface Server
{
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

	/**Searches the database for all parties that conforms to the search criterion.
	 * @param username username of user sending the search request
	 * @param criterion search criterion
	 * @return list of parties conforming the criterion
	 * @throws RutaException if search query could not be processed
	 */
	@WebMethod(operationName = "SearchParty")
	public List<PartyType> searchParty(String username, SearchCriterion criterion) throws RutaException;

	/**Searches the database for all catalogue items that conforms to the search criterion.
	 * @param username username of user sending the search request
	 * @param criterion search criterion
	 * @return list of catalogues with only the catalogue items conforming the criterion
	 * @throws RutaException if search query could not be processed
	 */
	@WebMethod(operationName = "SearchCatalogue")
	public List<CatalogueType> searchCatalogue(String username, SearchCriterion criterion) throws RutaException;

	/**Temporary web method for testing the jaxb.
	 * @return
	 * @throws RutaException
	 */
	@WebMethod(operationName = "FindAllParties")
	public List<PartyType> findAllParties() throws RutaException;

	/**Inserts notification about the new Ruta Client application update
	 * @param version Ruta Client version
	 * @throws RutaException if notification could not be inserted in the data store
	 */
	@WebMethod(operationName = "NotifyUpdate")
	public void notifyUpdate(RutaVersion version) throws RutaException;

	/**Checks the data store if there is a newer version of the Ruta Client application.
	 * @param currentVersion Ruta Client application's version of the user who sends the request
	 * @return new {@link Version} of Ruta Client application or {@code null} if the curent version is the latest one
	 * @throws RutaException if latest {@link Version} could not be retrieved
	 */
	@WebMethod(operationName = "UpdateRutaClient")
	public RutaVersion updateRutaClient(String currentVersion) throws RutaException;

	/**Inserts the {@code BugReport} in the datastore.
	 * @param bugReport bug to be inserted
	 * @throws RutaException if bug could not be inserted in the datastore
	 */
	@WebMethod(operationName = "InsertBugReport")
	public void insertBugReport(BugReport bugReport) throws RutaException;

	/**Retrieves the list of {@code BugReport}s from the datastore.
	 * @param start starting number of the bug
	 * @param count number of bugs to retrieve
	 * @return list of bugs or {@code null} if there are no bugs to retrieve
	 * @throws RutaException if bugs could not be retrieved from the datastore
	 */
	@WebMethod(operationName = "FindBug")
	public List<BugReport> findBugReport(int start, int count) throws RutaException;

	@WebMethod(operationName = "InsertFile")
	public void insertFile(@XmlMimeType("application/octet-stream") DataHandler dataHandler, String filename) throws RutaException;

	@WebMethod(operationName = "InsertImage")
	public void insertImage(@XmlMimeType("application/octet-stream") Image file) throws RutaException;

	@WebMethod(operationName = "InsertAttachment")
	public void insertAttachment(ReportAttachment attachment, String filename) throws RutaException;

}
