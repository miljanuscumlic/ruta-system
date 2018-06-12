
package rs.ruta.services;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import rs.ruta.common.Associates;
import rs.ruta.common.BugReport;
import rs.ruta.common.DeregistrationNotice;
import rs.ruta.common.DocumentReceipt;
import rs.ruta.common.PartnershipBreakup;
import rs.ruta.common.PartnershipRequest;
import rs.ruta.common.PartnershipResolution;
import rs.ruta.common.PartnershipResponse;
import rs.ruta.common.ReportAttachment;
import rs.ruta.common.RutaVersion;
import rs.ruta.common.SearchCriterion;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the rs.ruta.services package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _UpdateParty_QNAME = new QName("http://ruta.rs/ns/services", "UpdateParty");
    private final static QName _DeleteCatalogue_QNAME = new QName("http://ruta.rs/ns/services", "DeleteCatalogue");
    private final static QName _RequestBusinessPartnership_QNAME = new QName("http://ruta.rs/ns/services", "RequestBusinessPartnership");
    private final static QName _DeleteDocBoxDocument_QNAME = new QName("http://ruta.rs/ns/services", "DeleteDocBoxDocument");
    private final static QName _FollowParty_QNAME = new QName("http://ruta.rs/ns/services", "FollowParty");
    private final static QName _RegisterUser_QNAME = new QName("http://ruta.rs/ns/services", "RegisterUser");
    private final static QName _BugReport_QNAME = new QName("http://www.ruta.rs/ns/common", "BugReport");
    private final static QName _FindAllDocBoxDocumentIDs_QNAME = new QName("http://ruta.rs/ns/services", "FindAllDocBoxDocumentIDs");
    private final static QName _UpdateCatalogue_QNAME = new QName("http://ruta.rs/ns/services", "UpdateCatalogue");
    private final static QName _InsertCatalogueResponse_QNAME = new QName("http://ruta.rs/ns/services", "InsertCatalogueResponse");
    private final static QName _DistributionOperation_QNAME = new QName("http://www.ruta.rs/ns/common", "DistributionOperation");
    private final static QName _DeleteCatalogueWithAppResponse_QNAME = new QName("http://ruta.rs/ns/services", "DeleteCatalogueWithAppResponse");
    private final static QName _InsertCatalogue_QNAME = new QName("http://ruta.rs/ns/services", "InsertCatalogue");
    private final static QName _InsertBugReport_QNAME = new QName("http://ruta.rs/ns/services", "InsertBugReport");
    private final static QName _AddBugReportCommentResponse_QNAME = new QName("http://ruta.rs/ns/services", "AddBugReportCommentResponse");
    private final static QName _BreakupBusinessPartnershipResponse_QNAME = new QName("http://ruta.rs/ns/services", "BreakupBusinessPartnershipResponse");
    private final static QName _NotifyUpdate_QNAME = new QName("http://ruta.rs/ns/services", "NotifyUpdate");
    private final static QName _SearchPartyResponse_QNAME = new QName("http://ruta.rs/ns/services", "SearchPartyResponse");
    private final static QName _UpdateCatalogueWithAppResponse_QNAME = new QName("http://ruta.rs/ns/services", "UpdateCatalogueWithAppResponse");
    private final static QName _PartnershipBreakup_QNAME = new QName("http://www.ruta.rs/ns/common", "PartnershipBreakup");
    private final static QName _WSDLTypeInjection_QNAME = new QName("http://ruta.rs/ns/services", "WSDLTypeInjection");
    private final static QName _DeleteCatalogueResponse_QNAME = new QName("http://ruta.rs/ns/services", "DeleteCatalogueResponse");
    private final static QName _ClearCacheResponse_QNAME = new QName("http://ruta.rs/ns/services", "ClearCacheResponse");
    private final static QName _DeleteDocBoxDocumentWithDocumentReceiptResponse_QNAME = new QName("http://ruta.rs/ns/services", "DeleteDocBoxDocumentWithDocumentReceiptResponse");
    private final static QName _InsertImage_QNAME = new QName("http://ruta.rs/ns/services", "InsertImage");
    private final static QName _InsertAttachmentResponse_QNAME = new QName("http://ruta.rs/ns/services", "InsertAttachmentResponse");
    private final static QName _UnfollowPartyResponse_QNAME = new QName("http://ruta.rs/ns/services", "UnfollowPartyResponse");
    private final static QName _Associates_QNAME = new QName("http://www.ruta.rs/ns/common", "Associates");
    private final static QName _FindAllPartiesResponse_QNAME = new QName("http://ruta.rs/ns/services", "FindAllPartiesResponse");
    private final static QName _UpdateCatalogueResponse_QNAME = new QName("http://ruta.rs/ns/services", "UpdateCatalogueResponse");
    private final static QName _AddBugReportComment_QNAME = new QName("http://ruta.rs/ns/services", "AddBugReportComment");
    private final static QName _DeleteCatalogueWithAppResponseResponse_QNAME = new QName("http://ruta.rs/ns/services", "DeleteCatalogueWithAppResponseResponse");
    private final static QName _InsertFile_QNAME = new QName("http://ruta.rs/ns/services", "InsertFile");
    private final static QName _SearchParty_QNAME = new QName("http://ruta.rs/ns/services", "SearchParty");
    private final static QName _PartnershipResponse_QNAME = new QName("http://www.ruta.rs/ns/common", "PartnershipResponse");
    private final static QName _UpdateRutaClientResponse_QNAME = new QName("http://ruta.rs/ns/services", "UpdateRutaClientResponse");
    private final static QName _InsertBugReportResponse_QNAME = new QName("http://ruta.rs/ns/services", "InsertBugReportResponse");
    private final static QName _RutaVersion_QNAME = new QName("http://www.ruta.rs/ns/common", "RutaVersion");
    private final static QName _DeregisterUserResponse_QNAME = new QName("http://ruta.rs/ns/services", "DeregisterUserResponse");
    private final static QName _FindDocBoxDocument_QNAME = new QName("http://ruta.rs/ns/services", "FindDocBoxDocument");
    private final static QName _UpdatePartyResponse_QNAME = new QName("http://ruta.rs/ns/services", "UpdatePartyResponse");
    private final static QName _InsertAttachment_QNAME = new QName("http://ruta.rs/ns/services", "InsertAttachment");
    private final static QName _FollowPartyResponse_QNAME = new QName("http://ruta.rs/ns/services", "FollowPartyResponse");
    private final static QName _SearchCatalogue_QNAME = new QName("http://ruta.rs/ns/services", "SearchCatalogue");
    private final static QName _DistributeDocumentResponse_QNAME = new QName("http://ruta.rs/ns/services", "DistributeDocumentResponse");
    private final static QName _FindBugReport_QNAME = new QName("http://ruta.rs/ns/services", "FindBugReport");
    private final static QName _InsertFileResponse_QNAME = new QName("http://ruta.rs/ns/services", "InsertFileResponse");
    private final static QName _SearchBugReportResponse_QNAME = new QName("http://ruta.rs/ns/services", "SearchBugReportResponse");
    private final static QName _InsertParty_QNAME = new QName("http://ruta.rs/ns/services", "InsertParty");
    private final static QName _Attachment_QNAME = new QName("http://www.ruta.rs/ns/common", "Attachment");
    private final static QName _DocumentDistribution_QNAME = new QName("http://www.ruta.rs/ns/common", "DocumentDistribution");
    private final static QName _RequestBusinessPartnershipResponse_QNAME = new QName("http://ruta.rs/ns/services", "RequestBusinessPartnershipResponse");
    private final static QName _SearchBugReport_QNAME = new QName("http://ruta.rs/ns/services", "SearchBugReport");
    private final static QName _DatabaseTransaction_QNAME = new QName("http://www.ruta.rs/ns/common", "DatabaseTransaction");
    private final static QName _PartnershipRequest_QNAME = new QName("http://www.ruta.rs/ns/common", "PartnershipRequest");
    private final static QName _InsertPartyResponse_QNAME = new QName("http://ruta.rs/ns/services", "InsertPartyResponse");
    private final static QName _DeleteDocBoxDocumentResponse_QNAME = new QName("http://ruta.rs/ns/services", "DeleteDocBoxDocumentResponse");
    private final static QName _PartyID_QNAME = new QName("http://www.ruta.rs/ns/common", "PartyID");
    private final static QName _DistributionTransaction_QNAME = new QName("http://www.ruta.rs/ns/common", "DistributionTransaction");
    private final static QName _InsertImageResponse_QNAME = new QName("http://ruta.rs/ns/services", "InsertImageResponse");
    private final static QName _RegisterUserResponse_QNAME = new QName("http://ruta.rs/ns/services", "RegisterUserResponse");
    private final static QName _FindCatalogueResponse_QNAME = new QName("http://ruta.rs/ns/services", "FindCatalogueResponse");
    private final static QName _UnfollowParty_QNAME = new QName("http://ruta.rs/ns/services", "UnfollowParty");
    private final static QName _FindAllBugReports_QNAME = new QName("http://ruta.rs/ns/services", "FindAllBugReports");
    private final static QName _FindAllDocBoxDocumentIDsResponse_QNAME = new QName("http://ruta.rs/ns/services", "FindAllDocBoxDocumentIDsResponse");
    private final static QName _DatabaseOperation_QNAME = new QName("http://www.ruta.rs/ns/common", "DatabaseOperation");
    private final static QName _DeregisterUser_QNAME = new QName("http://ruta.rs/ns/services", "DeregisterUser");
    private final static QName _BreakupBusinessPartnership_QNAME = new QName("http://ruta.rs/ns/services", "BreakupBusinessPartnership");
    private final static QName _RutaException_QNAME = new QName("http://ruta.rs/ns/services", "RutaException");
    private final static QName _DeleteDocBoxDocumentWithDocumentReceipt_QNAME = new QName("http://ruta.rs/ns/services", "DeleteDocBoxDocumentWithDocumentReceipt");
    private final static QName _PartnershipResolution_QNAME = new QName("http://www.ruta.rs/ns/common", "PartnershipResolution");
    private final static QName _ClearCache_QNAME = new QName("http://ruta.rs/ns/services", "ClearCache");
    private final static QName _FindDocBoxDocumentResponse_QNAME = new QName("http://ruta.rs/ns/services", "FindDocBoxDocumentResponse");
    private final static QName _DocumentReceipt_QNAME = new QName("http://www.ruta.rs/ns/common", "DocumentReceipt");
    private final static QName _SearchCatalogueResponse_QNAME = new QName("http://ruta.rs/ns/services", "SearchCatalogueResponse");
    private final static QName _DeregistrationNotice_QNAME = new QName("http://www.ruta.rs/ns/common", "DeregistrationNotice");
    private final static QName _FindCatalogue_QNAME = new QName("http://ruta.rs/ns/services", "FindCatalogue");
    private final static QName _WSDLTypeInjectionResponse_QNAME = new QName("http://ruta.rs/ns/services", "WSDLTypeInjectionResponse");
    private final static QName _ReportAttachment_QNAME = new QName("http://www.ruta.rs/ns/common", "ReportAttachment");
    private final static QName _RutaUser_QNAME = new QName("http://www.ruta.rs/ns/common", "RutaUser");
    private final static QName _FindAllBugReportsResponse_QNAME = new QName("http://ruta.rs/ns/services", "FindAllBugReportsResponse");
    private final static QName _NotifyUpdateResponse_QNAME = new QName("http://ruta.rs/ns/services", "NotifyUpdateResponse");
    private final static QName _DistributeDocument_QNAME = new QName("http://ruta.rs/ns/services", "DistributeDocument");
    private final static QName _FindBugReportResponse_QNAME = new QName("http://ruta.rs/ns/services", "FindBugReportResponse");
    private final static QName _ResponseBusinessPartnershipResponse_QNAME = new QName("http://ruta.rs/ns/services", "ResponseBusinessPartnershipResponse");
    private final static QName _FindAllParties_QNAME = new QName("http://ruta.rs/ns/services", "FindAllParties");
    private final static QName _UpdateCatalogueWithAppResponseResponse_QNAME = new QName("http://ruta.rs/ns/services", "UpdateCatalogueWithAppResponseResponse");
    private final static QName _UpdateRutaClient_QNAME = new QName("http://ruta.rs/ns/services", "UpdateRutaClient");
    private final static QName _ResponseBusinessPartnership_QNAME = new QName("http://ruta.rs/ns/services", "ResponseBusinessPartnership");
    private final static QName _SearchCriterion_QNAME = new QName("http://www.ruta.rs/ns/common", "SearchCriterion");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: rs.ruta.services
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DatabaseTransaction }
     * 
     */
    public DatabaseTransaction createDatabaseTransaction() {
        return new DatabaseTransaction();
    }

    /**
     * Create an instance of {@link DocumentDistribution }
     * 
     */
    public DocumentDistribution createDocumentDistribution() {
        return new DocumentDistribution();
    }

    /**
     * Create an instance of {@link DistributionTransaction }
     * 
     */
    public DistributionTransaction createDistributionTransaction() {
        return new DistributionTransaction();
    }

    /**
     * Create an instance of {@link RutaUser }
     * 
     */
    public RutaUser createRutaUser() {
        return new RutaUser();
    }

    /**
     * Create an instance of {@link PartyID }
     * 
     */
    public PartyID createPartyID() {
        return new PartyID();
    }

    /**
     * Create an instance of {@link DatabaseOperation }
     * 
     */
    public DatabaseOperation createDatabaseOperation() {
        return new DatabaseOperation();
    }

    /**
     * Create an instance of {@link DistributionOperation }
     * 
     */
    public DistributionOperation createDistributionOperation() {
        return new DistributionOperation();
    }

    /**
     * Create an instance of {@link FaultInfo }
     * 
     */
    public FaultInfo createFaultInfo() {
        return new FaultInfo();
    }

    /**
     * Create an instance of {@link AddBugReportCommentResponse }
     * 
     */
    public AddBugReportCommentResponse createAddBugReportCommentResponse() {
        return new AddBugReportCommentResponse();
    }

    /**
     * Create an instance of {@link BreakupBusinessPartnershipResponse }
     * 
     */
    public BreakupBusinessPartnershipResponse createBreakupBusinessPartnershipResponse() {
        return new BreakupBusinessPartnershipResponse();
    }

    /**
     * Create an instance of {@link NotifyUpdate }
     * 
     */
    public NotifyUpdate createNotifyUpdate() {
        return new NotifyUpdate();
    }

    /**
     * Create an instance of {@link SearchPartyResponse }
     * 
     */
    public SearchPartyResponse createSearchPartyResponse() {
        return new SearchPartyResponse();
    }

    /**
     * Create an instance of {@link UpdateCatalogueWithAppResponse }
     * 
     */
    public UpdateCatalogueWithAppResponse createUpdateCatalogueWithAppResponse() {
        return new UpdateCatalogueWithAppResponse();
    }

    /**
     * Create an instance of {@link WSDLTypeInjection }
     * 
     */
    public WSDLTypeInjection createWSDLTypeInjection() {
        return new WSDLTypeInjection();
    }

    /**
     * Create an instance of {@link ClearCacheResponse }
     * 
     */
    public ClearCacheResponse createClearCacheResponse() {
        return new ClearCacheResponse();
    }

    /**
     * Create an instance of {@link DeleteDocBoxDocumentWithDocumentReceiptResponse }
     * 
     */
    public DeleteDocBoxDocumentWithDocumentReceiptResponse createDeleteDocBoxDocumentWithDocumentReceiptResponse() {
        return new DeleteDocBoxDocumentWithDocumentReceiptResponse();
    }

    /**
     * Create an instance of {@link InsertImage }
     * 
     */
    public InsertImage createInsertImage() {
        return new InsertImage();
    }

    /**
     * Create an instance of {@link DeleteCatalogueResponse }
     * 
     */
    public DeleteCatalogueResponse createDeleteCatalogueResponse() {
        return new DeleteCatalogueResponse();
    }

    /**
     * Create an instance of {@link InsertAttachmentResponse }
     * 
     */
    public InsertAttachmentResponse createInsertAttachmentResponse() {
        return new InsertAttachmentResponse();
    }

    /**
     * Create an instance of {@link UnfollowPartyResponse }
     * 
     */
    public UnfollowPartyResponse createUnfollowPartyResponse() {
        return new UnfollowPartyResponse();
    }

    /**
     * Create an instance of {@link UpdateCatalogueResponse }
     * 
     */
    public UpdateCatalogueResponse createUpdateCatalogueResponse() {
        return new UpdateCatalogueResponse();
    }

    /**
     * Create an instance of {@link FindAllPartiesResponse }
     * 
     */
    public FindAllPartiesResponse createFindAllPartiesResponse() {
        return new FindAllPartiesResponse();
    }

    /**
     * Create an instance of {@link AddBugReportComment }
     * 
     */
    public AddBugReportComment createAddBugReportComment() {
        return new AddBugReportComment();
    }

    /**
     * Create an instance of {@link DeleteCatalogueWithAppResponseResponse }
     * 
     */
    public DeleteCatalogueWithAppResponseResponse createDeleteCatalogueWithAppResponseResponse() {
        return new DeleteCatalogueWithAppResponseResponse();
    }

    /**
     * Create an instance of {@link InsertFile }
     * 
     */
    public InsertFile createInsertFile() {
        return new InsertFile();
    }

    /**
     * Create an instance of {@link SearchParty }
     * 
     */
    public SearchParty createSearchParty() {
        return new SearchParty();
    }

    /**
     * Create an instance of {@link InsertBugReportResponse }
     * 
     */
    public InsertBugReportResponse createInsertBugReportResponse() {
        return new InsertBugReportResponse();
    }

    /**
     * Create an instance of {@link UpdateRutaClientResponse }
     * 
     */
    public UpdateRutaClientResponse createUpdateRutaClientResponse() {
        return new UpdateRutaClientResponse();
    }

    /**
     * Create an instance of {@link DeregisterUserResponse }
     * 
     */
    public DeregisterUserResponse createDeregisterUserResponse() {
        return new DeregisterUserResponse();
    }

    /**
     * Create an instance of {@link FindDocBoxDocument }
     * 
     */
    public FindDocBoxDocument createFindDocBoxDocument() {
        return new FindDocBoxDocument();
    }

    /**
     * Create an instance of {@link UpdateParty }
     * 
     */
    public UpdateParty createUpdateParty() {
        return new UpdateParty();
    }

    /**
     * Create an instance of {@link DeleteCatalogue }
     * 
     */
    public DeleteCatalogue createDeleteCatalogue() {
        return new DeleteCatalogue();
    }

    /**
     * Create an instance of {@link RequestBusinessPartnership }
     * 
     */
    public RequestBusinessPartnership createRequestBusinessPartnership() {
        return new RequestBusinessPartnership();
    }

    /**
     * Create an instance of {@link DeleteDocBoxDocument }
     * 
     */
    public DeleteDocBoxDocument createDeleteDocBoxDocument() {
        return new DeleteDocBoxDocument();
    }

    /**
     * Create an instance of {@link FollowParty }
     * 
     */
    public FollowParty createFollowParty() {
        return new FollowParty();
    }

    /**
     * Create an instance of {@link RegisterUser }
     * 
     */
    public RegisterUser createRegisterUser() {
        return new RegisterUser();
    }

    /**
     * Create an instance of {@link FindAllDocBoxDocumentIDs }
     * 
     */
    public FindAllDocBoxDocumentIDs createFindAllDocBoxDocumentIDs() {
        return new FindAllDocBoxDocumentIDs();
    }

    /**
     * Create an instance of {@link UpdateCatalogue }
     * 
     */
    public UpdateCatalogue createUpdateCatalogue() {
        return new UpdateCatalogue();
    }

    /**
     * Create an instance of {@link InsertCatalogueResponse }
     * 
     */
    public InsertCatalogueResponse createInsertCatalogueResponse() {
        return new InsertCatalogueResponse();
    }

    /**
     * Create an instance of {@link DeleteCatalogueWithAppResponse }
     * 
     */
    public DeleteCatalogueWithAppResponse createDeleteCatalogueWithAppResponse() {
        return new DeleteCatalogueWithAppResponse();
    }

    /**
     * Create an instance of {@link InsertCatalogue }
     * 
     */
    public InsertCatalogue createInsertCatalogue() {
        return new InsertCatalogue();
    }

    /**
     * Create an instance of {@link InsertBugReport }
     * 
     */
    public InsertBugReport createInsertBugReport() {
        return new InsertBugReport();
    }

    /**
     * Create an instance of {@link BreakupBusinessPartnership }
     * 
     */
    public BreakupBusinessPartnership createBreakupBusinessPartnership() {
        return new BreakupBusinessPartnership();
    }

    /**
     * Create an instance of {@link DeleteDocBoxDocumentWithDocumentReceipt }
     * 
     */
    public DeleteDocBoxDocumentWithDocumentReceipt createDeleteDocBoxDocumentWithDocumentReceipt() {
        return new DeleteDocBoxDocumentWithDocumentReceipt();
    }

    /**
     * Create an instance of {@link ClearCache }
     * 
     */
    public ClearCache createClearCache() {
        return new ClearCache();
    }

    /**
     * Create an instance of {@link FindDocBoxDocumentResponse }
     * 
     */
    public FindDocBoxDocumentResponse createFindDocBoxDocumentResponse() {
        return new FindDocBoxDocumentResponse();
    }

    /**
     * Create an instance of {@link SearchCatalogueResponse }
     * 
     */
    public SearchCatalogueResponse createSearchCatalogueResponse() {
        return new SearchCatalogueResponse();
    }

    /**
     * Create an instance of {@link WSDLTypeInjectionResponse }
     * 
     */
    public WSDLTypeInjectionResponse createWSDLTypeInjectionResponse() {
        return new WSDLTypeInjectionResponse();
    }

    /**
     * Create an instance of {@link FindCatalogue }
     * 
     */
    public FindCatalogue createFindCatalogue() {
        return new FindCatalogue();
    }

    /**
     * Create an instance of {@link FindAllBugReportsResponse }
     * 
     */
    public FindAllBugReportsResponse createFindAllBugReportsResponse() {
        return new FindAllBugReportsResponse();
    }

    /**
     * Create an instance of {@link NotifyUpdateResponse }
     * 
     */
    public NotifyUpdateResponse createNotifyUpdateResponse() {
        return new NotifyUpdateResponse();
    }

    /**
     * Create an instance of {@link DistributeDocument }
     * 
     */
    public DistributeDocument createDistributeDocument() {
        return new DistributeDocument();
    }

    /**
     * Create an instance of {@link FindBugReportResponse }
     * 
     */
    public FindBugReportResponse createFindBugReportResponse() {
        return new FindBugReportResponse();
    }

    /**
     * Create an instance of {@link ResponseBusinessPartnershipResponse }
     * 
     */
    public ResponseBusinessPartnershipResponse createResponseBusinessPartnershipResponse() {
        return new ResponseBusinessPartnershipResponse();
    }

    /**
     * Create an instance of {@link FindAllParties }
     * 
     */
    public FindAllParties createFindAllParties() {
        return new FindAllParties();
    }

    /**
     * Create an instance of {@link UpdateRutaClient }
     * 
     */
    public UpdateRutaClient createUpdateRutaClient() {
        return new UpdateRutaClient();
    }

    /**
     * Create an instance of {@link UpdateCatalogueWithAppResponseResponse }
     * 
     */
    public UpdateCatalogueWithAppResponseResponse createUpdateCatalogueWithAppResponseResponse() {
        return new UpdateCatalogueWithAppResponseResponse();
    }

    /**
     * Create an instance of {@link ResponseBusinessPartnership }
     * 
     */
    public ResponseBusinessPartnership createResponseBusinessPartnership() {
        return new ResponseBusinessPartnership();
    }

    /**
     * Create an instance of {@link UpdatePartyResponse }
     * 
     */
    public UpdatePartyResponse createUpdatePartyResponse() {
        return new UpdatePartyResponse();
    }

    /**
     * Create an instance of {@link InsertAttachment }
     * 
     */
    public InsertAttachment createInsertAttachment() {
        return new InsertAttachment();
    }

    /**
     * Create an instance of {@link DistributeDocumentResponse }
     * 
     */
    public DistributeDocumentResponse createDistributeDocumentResponse() {
        return new DistributeDocumentResponse();
    }

    /**
     * Create an instance of {@link FollowPartyResponse }
     * 
     */
    public FollowPartyResponse createFollowPartyResponse() {
        return new FollowPartyResponse();
    }

    /**
     * Create an instance of {@link SearchCatalogue }
     * 
     */
    public SearchCatalogue createSearchCatalogue() {
        return new SearchCatalogue();
    }

    /**
     * Create an instance of {@link InsertFileResponse }
     * 
     */
    public InsertFileResponse createInsertFileResponse() {
        return new InsertFileResponse();
    }

    /**
     * Create an instance of {@link SearchBugReportResponse }
     * 
     */
    public SearchBugReportResponse createSearchBugReportResponse() {
        return new SearchBugReportResponse();
    }

    /**
     * Create an instance of {@link FindBugReport }
     * 
     */
    public FindBugReport createFindBugReport() {
        return new FindBugReport();
    }

    /**
     * Create an instance of {@link InsertParty }
     * 
     */
    public InsertParty createInsertParty() {
        return new InsertParty();
    }

    /**
     * Create an instance of {@link RequestBusinessPartnershipResponse }
     * 
     */
    public RequestBusinessPartnershipResponse createRequestBusinessPartnershipResponse() {
        return new RequestBusinessPartnershipResponse();
    }

    /**
     * Create an instance of {@link SearchBugReport }
     * 
     */
    public SearchBugReport createSearchBugReport() {
        return new SearchBugReport();
    }

    /**
     * Create an instance of {@link DeleteDocBoxDocumentResponse }
     * 
     */
    public DeleteDocBoxDocumentResponse createDeleteDocBoxDocumentResponse() {
        return new DeleteDocBoxDocumentResponse();
    }

    /**
     * Create an instance of {@link InsertPartyResponse }
     * 
     */
    public InsertPartyResponse createInsertPartyResponse() {
        return new InsertPartyResponse();
    }

    /**
     * Create an instance of {@link FindCatalogueResponse }
     * 
     */
    public FindCatalogueResponse createFindCatalogueResponse() {
        return new FindCatalogueResponse();
    }

    /**
     * Create an instance of {@link UnfollowParty }
     * 
     */
    public UnfollowParty createUnfollowParty() {
        return new UnfollowParty();
    }

    /**
     * Create an instance of {@link InsertImageResponse }
     * 
     */
    public InsertImageResponse createInsertImageResponse() {
        return new InsertImageResponse();
    }

    /**
     * Create an instance of {@link RegisterUserResponse }
     * 
     */
    public RegisterUserResponse createRegisterUserResponse() {
        return new RegisterUserResponse();
    }

    /**
     * Create an instance of {@link FindAllBugReports }
     * 
     */
    public FindAllBugReports createFindAllBugReports() {
        return new FindAllBugReports();
    }

    /**
     * Create an instance of {@link FindAllDocBoxDocumentIDsResponse }
     * 
     */
    public FindAllDocBoxDocumentIDsResponse createFindAllDocBoxDocumentIDsResponse() {
        return new FindAllDocBoxDocumentIDsResponse();
    }

    /**
     * Create an instance of {@link DeregisterUser }
     * 
     */
    public DeregisterUser createDeregisterUser() {
        return new DeregisterUser();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateParty }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "UpdateParty")
    public JAXBElement<UpdateParty> createUpdateParty(UpdateParty value) {
        return new JAXBElement<UpdateParty>(_UpdateParty_QNAME, UpdateParty.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteCatalogue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "DeleteCatalogue")
    public JAXBElement<DeleteCatalogue> createDeleteCatalogue(DeleteCatalogue value) {
        return new JAXBElement<DeleteCatalogue>(_DeleteCatalogue_QNAME, DeleteCatalogue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestBusinessPartnership }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "RequestBusinessPartnership")
    public JAXBElement<RequestBusinessPartnership> createRequestBusinessPartnership(RequestBusinessPartnership value) {
        return new JAXBElement<RequestBusinessPartnership>(_RequestBusinessPartnership_QNAME, RequestBusinessPartnership.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteDocBoxDocument }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "DeleteDocBoxDocument")
    public JAXBElement<DeleteDocBoxDocument> createDeleteDocBoxDocument(DeleteDocBoxDocument value) {
        return new JAXBElement<DeleteDocBoxDocument>(_DeleteDocBoxDocument_QNAME, DeleteDocBoxDocument.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FollowParty }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "FollowParty")
    public JAXBElement<FollowParty> createFollowParty(FollowParty value) {
        return new JAXBElement<FollowParty>(_FollowParty_QNAME, FollowParty.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterUser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "RegisterUser")
    public JAXBElement<RegisterUser> createRegisterUser(RegisterUser value) {
        return new JAXBElement<RegisterUser>(_RegisterUser_QNAME, RegisterUser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BugReport }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "BugReport")
    public JAXBElement<BugReport> createBugReport(BugReport value) {
        return new JAXBElement<BugReport>(_BugReport_QNAME, BugReport.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllDocBoxDocumentIDs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "FindAllDocBoxDocumentIDs")
    public JAXBElement<FindAllDocBoxDocumentIDs> createFindAllDocBoxDocumentIDs(FindAllDocBoxDocumentIDs value) {
        return new JAXBElement<FindAllDocBoxDocumentIDs>(_FindAllDocBoxDocumentIDs_QNAME, FindAllDocBoxDocumentIDs.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateCatalogue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "UpdateCatalogue")
    public JAXBElement<UpdateCatalogue> createUpdateCatalogue(UpdateCatalogue value) {
        return new JAXBElement<UpdateCatalogue>(_UpdateCatalogue_QNAME, UpdateCatalogue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertCatalogueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "InsertCatalogueResponse")
    public JAXBElement<InsertCatalogueResponse> createInsertCatalogueResponse(InsertCatalogueResponse value) {
        return new JAXBElement<InsertCatalogueResponse>(_InsertCatalogueResponse_QNAME, InsertCatalogueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DistributionOperation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "DistributionOperation")
    public JAXBElement<DistributionOperation> createDistributionOperation(DistributionOperation value) {
        return new JAXBElement<DistributionOperation>(_DistributionOperation_QNAME, DistributionOperation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteCatalogueWithAppResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "DeleteCatalogueWithAppResponse")
    public JAXBElement<DeleteCatalogueWithAppResponse> createDeleteCatalogueWithAppResponse(DeleteCatalogueWithAppResponse value) {
        return new JAXBElement<DeleteCatalogueWithAppResponse>(_DeleteCatalogueWithAppResponse_QNAME, DeleteCatalogueWithAppResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertCatalogue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "InsertCatalogue")
    public JAXBElement<InsertCatalogue> createInsertCatalogue(InsertCatalogue value) {
        return new JAXBElement<InsertCatalogue>(_InsertCatalogue_QNAME, InsertCatalogue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertBugReport }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "InsertBugReport")
    public JAXBElement<InsertBugReport> createInsertBugReport(InsertBugReport value) {
        return new JAXBElement<InsertBugReport>(_InsertBugReport_QNAME, InsertBugReport.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddBugReportCommentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "AddBugReportCommentResponse")
    public JAXBElement<AddBugReportCommentResponse> createAddBugReportCommentResponse(AddBugReportCommentResponse value) {
        return new JAXBElement<AddBugReportCommentResponse>(_AddBugReportCommentResponse_QNAME, AddBugReportCommentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BreakupBusinessPartnershipResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "BreakupBusinessPartnershipResponse")
    public JAXBElement<BreakupBusinessPartnershipResponse> createBreakupBusinessPartnershipResponse(BreakupBusinessPartnershipResponse value) {
        return new JAXBElement<BreakupBusinessPartnershipResponse>(_BreakupBusinessPartnershipResponse_QNAME, BreakupBusinessPartnershipResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotifyUpdate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "NotifyUpdate")
    public JAXBElement<NotifyUpdate> createNotifyUpdate(NotifyUpdate value) {
        return new JAXBElement<NotifyUpdate>(_NotifyUpdate_QNAME, NotifyUpdate.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchPartyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "SearchPartyResponse")
    public JAXBElement<SearchPartyResponse> createSearchPartyResponse(SearchPartyResponse value) {
        return new JAXBElement<SearchPartyResponse>(_SearchPartyResponse_QNAME, SearchPartyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateCatalogueWithAppResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "UpdateCatalogueWithAppResponse")
    public JAXBElement<UpdateCatalogueWithAppResponse> createUpdateCatalogueWithAppResponse(UpdateCatalogueWithAppResponse value) {
        return new JAXBElement<UpdateCatalogueWithAppResponse>(_UpdateCatalogueWithAppResponse_QNAME, UpdateCatalogueWithAppResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PartnershipBreakup }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "PartnershipBreakup")
    public JAXBElement<PartnershipBreakup> createPartnershipBreakup(PartnershipBreakup value) {
        return new JAXBElement<PartnershipBreakup>(_PartnershipBreakup_QNAME, PartnershipBreakup.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WSDLTypeInjection }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "WSDLTypeInjection")
    public JAXBElement<WSDLTypeInjection> createWSDLTypeInjection(WSDLTypeInjection value) {
        return new JAXBElement<WSDLTypeInjection>(_WSDLTypeInjection_QNAME, WSDLTypeInjection.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteCatalogueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "DeleteCatalogueResponse")
    public JAXBElement<DeleteCatalogueResponse> createDeleteCatalogueResponse(DeleteCatalogueResponse value) {
        return new JAXBElement<DeleteCatalogueResponse>(_DeleteCatalogueResponse_QNAME, DeleteCatalogueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClearCacheResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "ClearCacheResponse")
    public JAXBElement<ClearCacheResponse> createClearCacheResponse(ClearCacheResponse value) {
        return new JAXBElement<ClearCacheResponse>(_ClearCacheResponse_QNAME, ClearCacheResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteDocBoxDocumentWithDocumentReceiptResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "DeleteDocBoxDocumentWithDocumentReceiptResponse")
    public JAXBElement<DeleteDocBoxDocumentWithDocumentReceiptResponse> createDeleteDocBoxDocumentWithDocumentReceiptResponse(DeleteDocBoxDocumentWithDocumentReceiptResponse value) {
        return new JAXBElement<DeleteDocBoxDocumentWithDocumentReceiptResponse>(_DeleteDocBoxDocumentWithDocumentReceiptResponse_QNAME, DeleteDocBoxDocumentWithDocumentReceiptResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertImage }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "InsertImage")
    public JAXBElement<InsertImage> createInsertImage(InsertImage value) {
        return new JAXBElement<InsertImage>(_InsertImage_QNAME, InsertImage.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertAttachmentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "InsertAttachmentResponse")
    public JAXBElement<InsertAttachmentResponse> createInsertAttachmentResponse(InsertAttachmentResponse value) {
        return new JAXBElement<InsertAttachmentResponse>(_InsertAttachmentResponse_QNAME, InsertAttachmentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnfollowPartyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "UnfollowPartyResponse")
    public JAXBElement<UnfollowPartyResponse> createUnfollowPartyResponse(UnfollowPartyResponse value) {
        return new JAXBElement<UnfollowPartyResponse>(_UnfollowPartyResponse_QNAME, UnfollowPartyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Associates }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "Associates")
    public JAXBElement<Associates> createAssociates(Associates value) {
        return new JAXBElement<Associates>(_Associates_QNAME, Associates.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllPartiesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "FindAllPartiesResponse")
    public JAXBElement<FindAllPartiesResponse> createFindAllPartiesResponse(FindAllPartiesResponse value) {
        return new JAXBElement<FindAllPartiesResponse>(_FindAllPartiesResponse_QNAME, FindAllPartiesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateCatalogueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "UpdateCatalogueResponse")
    public JAXBElement<UpdateCatalogueResponse> createUpdateCatalogueResponse(UpdateCatalogueResponse value) {
        return new JAXBElement<UpdateCatalogueResponse>(_UpdateCatalogueResponse_QNAME, UpdateCatalogueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddBugReportComment }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "AddBugReportComment")
    public JAXBElement<AddBugReportComment> createAddBugReportComment(AddBugReportComment value) {
        return new JAXBElement<AddBugReportComment>(_AddBugReportComment_QNAME, AddBugReportComment.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteCatalogueWithAppResponseResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "DeleteCatalogueWithAppResponseResponse")
    public JAXBElement<DeleteCatalogueWithAppResponseResponse> createDeleteCatalogueWithAppResponseResponse(DeleteCatalogueWithAppResponseResponse value) {
        return new JAXBElement<DeleteCatalogueWithAppResponseResponse>(_DeleteCatalogueWithAppResponseResponse_QNAME, DeleteCatalogueWithAppResponseResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertFile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "InsertFile")
    public JAXBElement<InsertFile> createInsertFile(InsertFile value) {
        return new JAXBElement<InsertFile>(_InsertFile_QNAME, InsertFile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchParty }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "SearchParty")
    public JAXBElement<SearchParty> createSearchParty(SearchParty value) {
        return new JAXBElement<SearchParty>(_SearchParty_QNAME, SearchParty.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PartnershipResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "PartnershipResponse")
    public JAXBElement<PartnershipResponse> createPartnershipResponse(PartnershipResponse value) {
        return new JAXBElement<PartnershipResponse>(_PartnershipResponse_QNAME, PartnershipResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateRutaClientResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "UpdateRutaClientResponse")
    public JAXBElement<UpdateRutaClientResponse> createUpdateRutaClientResponse(UpdateRutaClientResponse value) {
        return new JAXBElement<UpdateRutaClientResponse>(_UpdateRutaClientResponse_QNAME, UpdateRutaClientResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertBugReportResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "InsertBugReportResponse")
    public JAXBElement<InsertBugReportResponse> createInsertBugReportResponse(InsertBugReportResponse value) {
        return new JAXBElement<InsertBugReportResponse>(_InsertBugReportResponse_QNAME, InsertBugReportResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RutaVersion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "RutaVersion")
    public JAXBElement<RutaVersion> createRutaVersion(RutaVersion value) {
        return new JAXBElement<RutaVersion>(_RutaVersion_QNAME, RutaVersion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeregisterUserResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "DeregisterUserResponse")
    public JAXBElement<DeregisterUserResponse> createDeregisterUserResponse(DeregisterUserResponse value) {
        return new JAXBElement<DeregisterUserResponse>(_DeregisterUserResponse_QNAME, DeregisterUserResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindDocBoxDocument }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "FindDocBoxDocument")
    public JAXBElement<FindDocBoxDocument> createFindDocBoxDocument(FindDocBoxDocument value) {
        return new JAXBElement<FindDocBoxDocument>(_FindDocBoxDocument_QNAME, FindDocBoxDocument.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdatePartyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "UpdatePartyResponse")
    public JAXBElement<UpdatePartyResponse> createUpdatePartyResponse(UpdatePartyResponse value) {
        return new JAXBElement<UpdatePartyResponse>(_UpdatePartyResponse_QNAME, UpdatePartyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertAttachment }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "InsertAttachment")
    public JAXBElement<InsertAttachment> createInsertAttachment(InsertAttachment value) {
        return new JAXBElement<InsertAttachment>(_InsertAttachment_QNAME, InsertAttachment.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FollowPartyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "FollowPartyResponse")
    public JAXBElement<FollowPartyResponse> createFollowPartyResponse(FollowPartyResponse value) {
        return new JAXBElement<FollowPartyResponse>(_FollowPartyResponse_QNAME, FollowPartyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchCatalogue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "SearchCatalogue")
    public JAXBElement<SearchCatalogue> createSearchCatalogue(SearchCatalogue value) {
        return new JAXBElement<SearchCatalogue>(_SearchCatalogue_QNAME, SearchCatalogue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DistributeDocumentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "DistributeDocumentResponse")
    public JAXBElement<DistributeDocumentResponse> createDistributeDocumentResponse(DistributeDocumentResponse value) {
        return new JAXBElement<DistributeDocumentResponse>(_DistributeDocumentResponse_QNAME, DistributeDocumentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindBugReport }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "FindBugReport")
    public JAXBElement<FindBugReport> createFindBugReport(FindBugReport value) {
        return new JAXBElement<FindBugReport>(_FindBugReport_QNAME, FindBugReport.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertFileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "InsertFileResponse")
    public JAXBElement<InsertFileResponse> createInsertFileResponse(InsertFileResponse value) {
        return new JAXBElement<InsertFileResponse>(_InsertFileResponse_QNAME, InsertFileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchBugReportResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "SearchBugReportResponse")
    public JAXBElement<SearchBugReportResponse> createSearchBugReportResponse(SearchBugReportResponse value) {
        return new JAXBElement<SearchBugReportResponse>(_SearchBugReportResponse_QNAME, SearchBugReportResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertParty }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "InsertParty")
    public JAXBElement<InsertParty> createInsertParty(InsertParty value) {
        return new JAXBElement<InsertParty>(_InsertParty_QNAME, InsertParty.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReportAttachment }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "Attachment")
    public JAXBElement<ReportAttachment> createAttachment(ReportAttachment value) {
        return new JAXBElement<ReportAttachment>(_Attachment_QNAME, ReportAttachment.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocumentDistribution }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "DocumentDistribution")
    public JAXBElement<DocumentDistribution> createDocumentDistribution(DocumentDistribution value) {
        return new JAXBElement<DocumentDistribution>(_DocumentDistribution_QNAME, DocumentDistribution.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestBusinessPartnershipResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "RequestBusinessPartnershipResponse")
    public JAXBElement<RequestBusinessPartnershipResponse> createRequestBusinessPartnershipResponse(RequestBusinessPartnershipResponse value) {
        return new JAXBElement<RequestBusinessPartnershipResponse>(_RequestBusinessPartnershipResponse_QNAME, RequestBusinessPartnershipResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchBugReport }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "SearchBugReport")
    public JAXBElement<SearchBugReport> createSearchBugReport(SearchBugReport value) {
        return new JAXBElement<SearchBugReport>(_SearchBugReport_QNAME, SearchBugReport.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DatabaseTransaction }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "DatabaseTransaction")
    public JAXBElement<DatabaseTransaction> createDatabaseTransaction(DatabaseTransaction value) {
        return new JAXBElement<DatabaseTransaction>(_DatabaseTransaction_QNAME, DatabaseTransaction.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PartnershipRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "PartnershipRequest")
    public JAXBElement<PartnershipRequest> createPartnershipRequest(PartnershipRequest value) {
        return new JAXBElement<PartnershipRequest>(_PartnershipRequest_QNAME, PartnershipRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertPartyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "InsertPartyResponse")
    public JAXBElement<InsertPartyResponse> createInsertPartyResponse(InsertPartyResponse value) {
        return new JAXBElement<InsertPartyResponse>(_InsertPartyResponse_QNAME, InsertPartyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteDocBoxDocumentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "DeleteDocBoxDocumentResponse")
    public JAXBElement<DeleteDocBoxDocumentResponse> createDeleteDocBoxDocumentResponse(DeleteDocBoxDocumentResponse value) {
        return new JAXBElement<DeleteDocBoxDocumentResponse>(_DeleteDocBoxDocumentResponse_QNAME, DeleteDocBoxDocumentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PartyID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "PartyID")
    public JAXBElement<PartyID> createPartyID(PartyID value) {
        return new JAXBElement<PartyID>(_PartyID_QNAME, PartyID.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DistributionTransaction }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "DistributionTransaction")
    public JAXBElement<DistributionTransaction> createDistributionTransaction(DistributionTransaction value) {
        return new JAXBElement<DistributionTransaction>(_DistributionTransaction_QNAME, DistributionTransaction.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertImageResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "InsertImageResponse")
    public JAXBElement<InsertImageResponse> createInsertImageResponse(InsertImageResponse value) {
        return new JAXBElement<InsertImageResponse>(_InsertImageResponse_QNAME, InsertImageResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterUserResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "RegisterUserResponse")
    public JAXBElement<RegisterUserResponse> createRegisterUserResponse(RegisterUserResponse value) {
        return new JAXBElement<RegisterUserResponse>(_RegisterUserResponse_QNAME, RegisterUserResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindCatalogueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "FindCatalogueResponse")
    public JAXBElement<FindCatalogueResponse> createFindCatalogueResponse(FindCatalogueResponse value) {
        return new JAXBElement<FindCatalogueResponse>(_FindCatalogueResponse_QNAME, FindCatalogueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnfollowParty }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "UnfollowParty")
    public JAXBElement<UnfollowParty> createUnfollowParty(UnfollowParty value) {
        return new JAXBElement<UnfollowParty>(_UnfollowParty_QNAME, UnfollowParty.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllBugReports }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "FindAllBugReports")
    public JAXBElement<FindAllBugReports> createFindAllBugReports(FindAllBugReports value) {
        return new JAXBElement<FindAllBugReports>(_FindAllBugReports_QNAME, FindAllBugReports.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllDocBoxDocumentIDsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "FindAllDocBoxDocumentIDsResponse")
    public JAXBElement<FindAllDocBoxDocumentIDsResponse> createFindAllDocBoxDocumentIDsResponse(FindAllDocBoxDocumentIDsResponse value) {
        return new JAXBElement<FindAllDocBoxDocumentIDsResponse>(_FindAllDocBoxDocumentIDsResponse_QNAME, FindAllDocBoxDocumentIDsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DatabaseOperation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "DatabaseOperation")
    public JAXBElement<DatabaseOperation> createDatabaseOperation(DatabaseOperation value) {
        return new JAXBElement<DatabaseOperation>(_DatabaseOperation_QNAME, DatabaseOperation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeregisterUser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "DeregisterUser")
    public JAXBElement<DeregisterUser> createDeregisterUser(DeregisterUser value) {
        return new JAXBElement<DeregisterUser>(_DeregisterUser_QNAME, DeregisterUser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BreakupBusinessPartnership }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "BreakupBusinessPartnership")
    public JAXBElement<BreakupBusinessPartnership> createBreakupBusinessPartnership(BreakupBusinessPartnership value) {
        return new JAXBElement<BreakupBusinessPartnership>(_BreakupBusinessPartnership_QNAME, BreakupBusinessPartnership.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FaultInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "RutaException")
    public JAXBElement<FaultInfo> createRutaException(FaultInfo value) {
        return new JAXBElement<FaultInfo>(_RutaException_QNAME, FaultInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteDocBoxDocumentWithDocumentReceipt }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "DeleteDocBoxDocumentWithDocumentReceipt")
    public JAXBElement<DeleteDocBoxDocumentWithDocumentReceipt> createDeleteDocBoxDocumentWithDocumentReceipt(DeleteDocBoxDocumentWithDocumentReceipt value) {
        return new JAXBElement<DeleteDocBoxDocumentWithDocumentReceipt>(_DeleteDocBoxDocumentWithDocumentReceipt_QNAME, DeleteDocBoxDocumentWithDocumentReceipt.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PartnershipResolution }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "PartnershipResolution")
    public JAXBElement<PartnershipResolution> createPartnershipResolution(PartnershipResolution value) {
        return new JAXBElement<PartnershipResolution>(_PartnershipResolution_QNAME, PartnershipResolution.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClearCache }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "ClearCache")
    public JAXBElement<ClearCache> createClearCache(ClearCache value) {
        return new JAXBElement<ClearCache>(_ClearCache_QNAME, ClearCache.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindDocBoxDocumentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "FindDocBoxDocumentResponse")
    public JAXBElement<FindDocBoxDocumentResponse> createFindDocBoxDocumentResponse(FindDocBoxDocumentResponse value) {
        return new JAXBElement<FindDocBoxDocumentResponse>(_FindDocBoxDocumentResponse_QNAME, FindDocBoxDocumentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocumentReceipt }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "DocumentReceipt")
    public JAXBElement<DocumentReceipt> createDocumentReceipt(DocumentReceipt value) {
        return new JAXBElement<DocumentReceipt>(_DocumentReceipt_QNAME, DocumentReceipt.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchCatalogueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "SearchCatalogueResponse")
    public JAXBElement<SearchCatalogueResponse> createSearchCatalogueResponse(SearchCatalogueResponse value) {
        return new JAXBElement<SearchCatalogueResponse>(_SearchCatalogueResponse_QNAME, SearchCatalogueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeregistrationNotice }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "DeregistrationNotice")
    public JAXBElement<DeregistrationNotice> createDeregistrationNotice(DeregistrationNotice value) {
        return new JAXBElement<DeregistrationNotice>(_DeregistrationNotice_QNAME, DeregistrationNotice.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindCatalogue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "FindCatalogue")
    public JAXBElement<FindCatalogue> createFindCatalogue(FindCatalogue value) {
        return new JAXBElement<FindCatalogue>(_FindCatalogue_QNAME, FindCatalogue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WSDLTypeInjectionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "WSDLTypeInjectionResponse")
    public JAXBElement<WSDLTypeInjectionResponse> createWSDLTypeInjectionResponse(WSDLTypeInjectionResponse value) {
        return new JAXBElement<WSDLTypeInjectionResponse>(_WSDLTypeInjectionResponse_QNAME, WSDLTypeInjectionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReportAttachment }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "ReportAttachment")
    public JAXBElement<ReportAttachment> createReportAttachment(ReportAttachment value) {
        return new JAXBElement<ReportAttachment>(_ReportAttachment_QNAME, ReportAttachment.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RutaUser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "RutaUser")
    public JAXBElement<RutaUser> createRutaUser(RutaUser value) {
        return new JAXBElement<RutaUser>(_RutaUser_QNAME, RutaUser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllBugReportsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "FindAllBugReportsResponse")
    public JAXBElement<FindAllBugReportsResponse> createFindAllBugReportsResponse(FindAllBugReportsResponse value) {
        return new JAXBElement<FindAllBugReportsResponse>(_FindAllBugReportsResponse_QNAME, FindAllBugReportsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotifyUpdateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "NotifyUpdateResponse")
    public JAXBElement<NotifyUpdateResponse> createNotifyUpdateResponse(NotifyUpdateResponse value) {
        return new JAXBElement<NotifyUpdateResponse>(_NotifyUpdateResponse_QNAME, NotifyUpdateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DistributeDocument }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "DistributeDocument")
    public JAXBElement<DistributeDocument> createDistributeDocument(DistributeDocument value) {
        return new JAXBElement<DistributeDocument>(_DistributeDocument_QNAME, DistributeDocument.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindBugReportResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "FindBugReportResponse")
    public JAXBElement<FindBugReportResponse> createFindBugReportResponse(FindBugReportResponse value) {
        return new JAXBElement<FindBugReportResponse>(_FindBugReportResponse_QNAME, FindBugReportResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponseBusinessPartnershipResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "ResponseBusinessPartnershipResponse")
    public JAXBElement<ResponseBusinessPartnershipResponse> createResponseBusinessPartnershipResponse(ResponseBusinessPartnershipResponse value) {
        return new JAXBElement<ResponseBusinessPartnershipResponse>(_ResponseBusinessPartnershipResponse_QNAME, ResponseBusinessPartnershipResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllParties }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "FindAllParties")
    public JAXBElement<FindAllParties> createFindAllParties(FindAllParties value) {
        return new JAXBElement<FindAllParties>(_FindAllParties_QNAME, FindAllParties.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateCatalogueWithAppResponseResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "UpdateCatalogueWithAppResponseResponse")
    public JAXBElement<UpdateCatalogueWithAppResponseResponse> createUpdateCatalogueWithAppResponseResponse(UpdateCatalogueWithAppResponseResponse value) {
        return new JAXBElement<UpdateCatalogueWithAppResponseResponse>(_UpdateCatalogueWithAppResponseResponse_QNAME, UpdateCatalogueWithAppResponseResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateRutaClient }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "UpdateRutaClient")
    public JAXBElement<UpdateRutaClient> createUpdateRutaClient(UpdateRutaClient value) {
        return new JAXBElement<UpdateRutaClient>(_UpdateRutaClient_QNAME, UpdateRutaClient.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponseBusinessPartnership }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/ns/services", name = "ResponseBusinessPartnership")
    public JAXBElement<ResponseBusinessPartnership> createResponseBusinessPartnership(ResponseBusinessPartnership value) {
        return new JAXBElement<ResponseBusinessPartnership>(_ResponseBusinessPartnership_QNAME, ResponseBusinessPartnership.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchCriterion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "SearchCriterion")
    public JAXBElement<SearchCriterion> createSearchCriterion(SearchCriterion value) {
        return new JAXBElement<SearchCriterion>(_SearchCriterion_QNAME, SearchCriterion.class, null, value);
    }

}
