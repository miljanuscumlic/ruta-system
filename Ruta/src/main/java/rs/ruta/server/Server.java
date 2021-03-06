package rs.ruta.server;

import java.awt.Image;
import java.io.File;
import java.util.List;

import javax.activation.DataHandler;
import javax.jws.*;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlSeeAlso;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;
import oasis.names.specification.ubl.schema.xsd.ordercancellation_21.OrderCancellationType;
import oasis.names.specification.ubl.schema.xsd.orderchange_21.OrderChangeType;
import oasis.names.specification.ubl.schema.xsd.orderresponse_21.OrderResponseType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_21.OrderResponseSimpleType;
import rs.ruta.common.ReportAttachment;
import rs.ruta.common.ReportComment;
import rs.ruta.common.BugReport;
import rs.ruta.common.BugReportSearchCriterion;
import rs.ruta.common.PartnershipRequest;
import rs.ruta.common.PartnershipResponse;
import rs.ruta.common.CatalogueSearchCriterion;
import rs.ruta.common.DeregistrationNotice;
import rs.ruta.common.DocBoxAllIDsSearchCriterion;
import rs.ruta.common.DocBoxDocumentSearchCriterion;
import rs.ruta.common.DocumentReceipt;
import rs.ruta.common.PartnershipBreakup;
import rs.ruta.common.Associates;
import rs.ruta.common.PartySearchCriterion;
import rs.ruta.common.RutaVersion;
import rs.ruta.common.SearchCriterion;
import rs.ruta.common.datamapper.RutaException;

@WebService(targetNamespace = "http://ruta.rs/ns/services")
public interface Server
{
	/**
	 * Inserts catalogue object in the database.
	 * @param username username of the party which catalogue is stored
	 * @param catalogue catalogue object to be stored in the database
	 * @throws RutaException if the catalogue object could not be inserted in the database
	 */
	@WebMethod(operationName = "InsertCatalogue")
	public void insertCatalogue(String username, CatalogueType catalogue) throws RutaException;

	/**
	 * Updates catalogue object in the database.
	 * @param username username of the party which catalogue is updated
	 * @param catalogue catalogue object to be updated in the database
	 * @throws RutaException if the catalogue object could not be updated in the database
	 */
	@WebMethod(operationName = "UpdateCatalogue")
	@Deprecated
	public void updateCatalogue(String username, CatalogueType catalogue) throws RutaException;

	/**
	 * Updates catalogue object in the database.
	 * @param username username of the party which catalogue is updated
	 * @param catalogue catalogue object to be updated in the database
	 * @throws RutaException if the catalogue object could not be updated in the database
	 * @return {@link ApplicationResponseType} object describing the response on the catalogue update request
	 */
	@WebMethod(operationName = "UpdateCatalogueWithAppResponse")
	public ApplicationResponseType updateCatalogueWithAppResponse(String username, CatalogueType catalogue) throws RutaException;

	/**
	 * Retrives catalogue of the user which id is passed.
	 * @param partyID id of the user which catalogue should be retrieved
	 * @return catalogue user's catalogue
	 * @throws RutaException if Catalogue could not be found
	 */
	@WebMethod(operationName = "FindCatalogue")
	public CatalogueType findCatalogue(String partyID) throws RutaException;

	/**
	 * Deletes catalogue object from the database. Catalogue is referenced with the passed
	 * CatalogueDeletion object.
	 * @param username username of the user with catalogue should be deleted
	 * @param catDeletion CatalogueDeletion object referencing Catalogue
	 * @throws RutaException if the Catalogue object could not be deleted, or CatalogueDeletion
	 * object could not be inserted in the database
	 */
	@WebMethod(operationName = "DeleteCatalogue")
	@Deprecated
	public void deleteCatalogue(String username, CatalogueDeletionType catDeletion) throws RutaException;

	/**
	 * Deletes catalogue object from the database. Catalogue is referenced with the passed
	 * CatalogueDeletion object.
	 * @param username username of the user which catalogue should be deleted
	 * @param catDeletion CatalogueDeletion object referencing Catalogue
	 * @return {@link ApplicationResponseType} object describing the response on the catalogue deletion request
	 * @throws RutaException if the Catalogue object could not be deleted, or CatalogueDeletion
	 * object could not be inserted in the database
	 */
	@WebMethod(operationName = "DeleteCatalogueWithAppResponse")
	public ApplicationResponseType deleteCatalogueWithAppResponse(String username, CatalogueDeletionType catalogueDeletion) throws RutaException;

	/**
	 * Inserts document in appropriate {@link DocBox docBoxes} that way distributing them to all
	 * appropriate recepients.
	 * @param document document to distribute
	 * @throws RutaException if document could not be distributed
	 */
	@WebMethod(operationName = "DistributeDocument")
	public void distributeDocument(Object document) throws RutaException;

	/**
	 * Searches the database for all catalogue items that conforms to the search criterion.
	 * @param criterion search criterion
	 * @return list of catalogues containing only catalogue items conforming to the criterion or
	 * {@code null} if no catalogue conforms to the search criterion
	 * @throws RutaException if search query could not be processed
	 */
	@WebMethod(operationName = "SearchCatalogue")
	public List<CatalogueType> searchCatalogue(CatalogueSearchCriterion criterion) throws RutaException;

	/**
	 * Registers user with the CDR service.
	 * @param username user's username
	 * @param password user's password
	 * @param party party data to register
	 * @return user's secret key
	 * @throws RutaException throw if it was unable to register the user
	 */
	@WebMethod(operationName = "RegisterUser")
	public String registerUser(String username, String password, PartyType party) throws RutaException;

	/**
	 * Deregister the user from the CDR service.
	 * @param username user's username to be deleted
	 * @param {{@link DeregistrationNotice} document discribing deregistration request
	 * @throws RutaException if the user could not be deleted
	 */
	@WebMethod(operationName = "DeregisterUser")
	public void deregisterUser(String username, DeregistrationNotice notice) throws RutaException;

	/**
	 * Inserts party object in the database.
	 * @param username party's username
	 * @param party party object representing the user to be inserted into the database
	 * @return party's unique id
	 * @throws RutaException if the party object could not be inserted in the database
	 */
	@WebMethod(operationName = "InsertParty")
	public String insertParty(String username, PartyType party) throws RutaException;

	/**
	 * Updates party object in the database.
	 * @param username party's username
	 * @param party party object representing the user to be updated into the database
	 * @return party's unique id
	 * @throws RutaException if the party object could not be updated in the database
	 */
	@WebMethod(operationName = "UpdateParty")
	public void updateParty(String username, PartyType party) throws RutaException;

	/**
	 * Adds Party as another Party's follower.
	 * @param partyID party's ID
	 * @param followID following party's ID
	 * @return follower's {@link PartyType} object
	 * @throws RutaException if party could not be added as a follower
	 */
	@WebMethod(operationName = "FollowParty")
	public PartyType followParty(String partyID, String followID) throws RutaException;

	/**
	 * Removes Party from another Party's follower list.
	 * @param partyID party's ID
	 * @param followID following party's ID
	 * @throws RutaException if party could not be added as a follower
	 */
	@WebMethod(operationName = "UnfollowParty")
	public void unfollowParty(String partyID, String followID) throws RutaException;

	/**
	 * Searches the database for all parties that conforms to the search criterion.
	 * @param criterion search criterion
	 * @return list of parties conforming to thethe criterion or {@code null} if no party conforms to the search criterion
	 * @throws RutaException if search query could not be processed
	 */
	@WebMethod(operationName = "SearchParty")
	public List<PartyType> searchParty(PartySearchCriterion criterion) throws RutaException;

	/**
	 * Finds all IDs of documents in the DocBox of the party.
	 * @param criterion search criterion containing party's unique ID
	 * @return list of all IDs of DocBox documents or {@code null} if there are none
	 * @throws RutaException if list could not be retrieved
	 */
	@WebMethod(operationName = "FindAllDocBoxDocumentIDs")
	public List<String> findAllDocBoxDocumentIDs(DocBoxAllIDsSearchCriterion criterion) throws RutaException;

	/**
	 * Retrives a DocBox document.
	 * @param criterion search criterion containing party's unique ID and ID of the document that should be retrieved
	 * @return {@code Object} instance representing the retrieved document or {@code null} if document does not exist
	 * @throws RutaException if document could not be retrived
	 */
	@WebMethod(operationName = "FindDocBoxDocument")
	public Object findDocBoxDocument(DocBoxDocumentSearchCriterion criterion) throws RutaException;

	/**
	 * Deletes a DocBox document.
	 * @param username user's username
	 * @param id document's id
	 * @throws RutaException if document could not be deleted
	 */
	@WebMethod(operationName = "DeleteDocBoxDocument")
	@Deprecated
	public void deleteDocBoxDocument(String username, String id) throws RutaException;

	/**
	 * Deletes a DocBox document and sends {@link DocumentReceipt} that is distributed to the sender of the
	 * document
	 * @param username user's username
	 * @param id document's id
	 * @param receipt document receipt
	 * @throws RutaException if document could not be deleted
	 */
	@WebMethod(operationName = "DeleteDocBoxDocumentWithDocumentReceipt")
	public void deleteDocBoxDocumentWithDocumentReceipt(String username, String id, DocumentReceipt receipt) throws RutaException;

	@WebMethod(operationName = "RequestBusinessPartnership")
	public void requestBusinessPartnership(PartnershipRequest request) throws RutaException;

	@WebMethod(operationName = "ResponseBusinessPartnership")
	public void responseBusinessPartnership(PartnershipResponse response) throws RutaException;

	@WebMethod(operationName = "BreakupBusinessPartnership")
	public void breakupBusinessPartnership(PartnershipBreakup breakup) throws RutaException;

	/**
	 * Searches the database for all {@link BugReport}s that conforms to the {@link SearchCriterion search criterion}.
	 * @param criterion search criterion
	 * @return list of {@code BugReport}s conforming to the search criterion or {@code null}
	 * if no {@code BugReport} conforms to it
	 * @throws RutaException if search query could not be processed
	 */
	@WebMethod(operationName = "SearchBugReport")
	public List<BugReport> searchBugReport(BugReportSearchCriterion criterion) throws RutaException;

	/**
	 * Appends {@link ReportComment} to {@link BugReport}.
	 * @param id {@code BugReport}'s id
	 * @param comment comment to append
	 * @throws RutaException if comment could not be added to a {@code BugReport}
	 */
	@WebMethod(operationName = "AddBugReportComment")
	public void addBugReportComment(String id, ReportComment comment) throws RutaException;

	/**
	 * Temporary web method for testing the jaxb.
	 * @return
	 * @throws RutaException
	 */
	@WebMethod(operationName = "FindAllParties")
	public List<PartyType> findAllParties() throws RutaException;

	/**
	 * Inserts notification about the new Ruta Client application update.
	 * @param version Ruta Client version
	 * @throws RutaException if notification could not be inserted in the data store
	 */
	@WebMethod(operationName = "NotifyUpdate")
	public void insertUpdateNotification(RutaVersion version) throws RutaException;

	/**
	 * Checks the data store if there is a newer version of the Ruta Client application.
	 * @param currentVersion Ruta Client application's version of the user who sends the request
	 * @return new {@link Version} of Ruta Client application or {@code null} if the curent version is the latest one
	 * @throws RutaException if latest {@link Version} could not be retrieved
	 */
	@WebMethod(operationName = "UpdateRutaClient")
	public RutaVersion findClientVersion(String currentVersion) throws RutaException;

	/**
	 * Inserts the {@code BugReport} in the datastore.
	 * @param bugReport bug to be inserted
	 * @throws RutaException if bug could not be inserted in the datastore
	 */
	@WebMethod(operationName = "InsertBugReport")
	public void insertBugReport(BugReport bugReport) throws RutaException;

	/**
	 * Retrieves {@code BugReport} with passed ID from the datastore.
	 * @param ID bug report's ID
	 * @return bug report or {@code null} if there is no bug with that ID
	 * @throws RutaException if bug could not be retrieved from the datastore
	 */
	@WebMethod(operationName = "FindBugReport")
	public BugReport findBugReport(String id) throws RutaException;

	/**
	 * Serves as an dirty injection of types in the wsdl file. All types of arguments are included in wsdl file,
	 * so that other webmethods that are passing arguments which declared type is the {@code Object} class
	 * can have runtime types like {@link OrderType} in the JAXB context for marshalling and unmarshalling.
	 * List of arguments contains variables of all types that are passed as {@code Objects}.
	 */
	@WebMethod(operationName = "WSDLTypeInjection")
	default public void typeInclusion(OrderType o, OrderResponseType or, OrderResponseSimpleType ors,
			OrderChangeType oc, OrderCancellationType oca, InvoiceType i) {}

	/**
	 * Temporary methods for clearing of server side in-memory cache.
	 * @throws RutaException
	 */
	@WebMethod(operationName = "ClearCache")
	public boolean clearCache() throws RutaException;

	/**
	 * Retrieves the list of {@link BugReport}s from the datastore. List could be partial containg some maximum
	 * number of {@code BugReport}s.
	 * @return list of bugs or {@code null} if there are no bugs to retrieve
	 * @throws RutaException if bugs could not be retrieved from the datastore
	 */
	@Deprecated
	@WebMethod(operationName = "FindAllBugReports")
	public List<BugReport> findAllBugReports() throws RutaException;

	@Deprecated
	@WebMethod(operationName = "InsertFile")
	public void insertFile(@XmlMimeType("application/octet-stream") DataHandler dataHandler, String filename) throws RutaException;

	@Deprecated
	@WebMethod(operationName = "InsertImage")
	public void insertImage(@XmlMimeType("application/octet-stream") Image file) throws RutaException;

	@Deprecated
	@WebMethod(operationName = "InsertAttachment")
	public void insertAttachment(ReportAttachment attachment, String filename) throws RutaException;

}