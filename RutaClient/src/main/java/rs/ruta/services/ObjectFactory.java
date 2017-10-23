
package rs.ruta.services;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import rs.ruta.client.Episode;
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

    private final static QName _SearchParty_QNAME = new QName("http://ruta.rs/services", "SearchParty");
    private final static QName _QueryPartyResponse_QNAME = new QName("http://ruta.rs/services", "QueryPartyResponse");
    private final static QName _UpdateCatalogueResponse_QNAME = new QName("http://ruta.rs/services", "UpdateCatalogueResponse");
    private final static QName _FindAllPartiesResponse_QNAME = new QName("http://ruta.rs/services", "FindAllPartiesResponse");
    private final static QName _GetDocument_QNAME = new QName("http://ruta.rs/services", "GetDocument");
    private final static QName _TestEpisode_QNAME = new QName("http://ruta.rs/services", "TestEpisode");
    private final static QName _FindAllParties_QNAME = new QName("http://ruta.rs/services", "FindAllParties");
    private final static QName _DeleteUserResponse_QNAME = new QName("http://ruta.rs/services", "DeleteUserResponse");
    private final static QName _GetDocumentResponse_QNAME = new QName("http://ruta.rs/services", "GetDocumentResponse");
    private final static QName _SearchPartyResponse_QNAME = new QName("http://ruta.rs/services", "SearchPartyResponse");
    private final static QName _RutaException_QNAME = new QName("http://ruta.rs/services", "RutaException");
    private final static QName _FindCatalogue_QNAME = new QName("http://ruta.rs/services", "FindCatalogue");
    private final static QName _SearchCatalogueResponse_QNAME = new QName("http://ruta.rs/services", "SearchCatalogueResponse");
    private final static QName _DeleteCatalogueResponse_QNAME = new QName("http://ruta.rs/services", "DeleteCatalogueResponse");
    private final static QName _DeleteUser_QNAME = new QName("http://ruta.rs/services", "DeleteUser");
    private final static QName _InsertCatalogueResponse_QNAME = new QName("http://ruta.rs/services", "InsertCatalogueResponse");
    private final static QName _TestEpisodeResponse_QNAME = new QName("http://ruta.rs/services", "TestEpisodeResponse");
    private final static QName _UpdateCatalogue_QNAME = new QName("http://ruta.rs/services", "UpdateCatalogue");
    private final static QName _Episode_QNAME = new QName("http://ruta.rs/services", "Episode");
    private final static QName _InsertPartyResponse_QNAME = new QName("http://ruta.rs/services", "InsertPartyResponse");
    private final static QName _FindCatalogueResponse_QNAME = new QName("http://ruta.rs/services", "FindCatalogueResponse");
    private final static QName _InsertCatalogue_QNAME = new QName("http://ruta.rs/services", "InsertCatalogue");
    private final static QName _RegisterUserResponse_QNAME = new QName("http://ruta.rs/services", "RegisterUserResponse");
    private final static QName _SearchCriterion_QNAME = new QName("urn:rs:ruta:client", "SearchCriterion");
    private final static QName _PutDocumentResponse_QNAME = new QName("http://ruta.rs/services", "PutDocumentResponse");
    private final static QName _QueryPartyName_QNAME = new QName("http://ruta.rs/services", "QueryPartyName");
    private final static QName _SearchCatalogue_QNAME = new QName("http://ruta.rs/services", "SearchCatalogue");
    private final static QName _DeleteCatalogue_QNAME = new QName("http://ruta.rs/services", "DeleteCatalogue");
    private final static QName _UpdateParty_QNAME = new QName("http://ruta.rs/services", "UpdateParty");
    private final static QName _UpdatePartyResponse_QNAME = new QName("http://ruta.rs/services", "UpdatePartyResponse");
    private final static QName _QueryParty_QNAME = new QName("http://ruta.rs/services", "QueryParty");
    private final static QName _RegisterUser_QNAME = new QName("http://ruta.rs/services", "RegisterUser");
    private final static QName _InsertParty_QNAME = new QName("http://ruta.rs/services", "InsertParty");
    private final static QName _QueryPartyNameResponse_QNAME = new QName("http://ruta.rs/services", "QueryPartyNameResponse");
    private final static QName _PutDocument_QNAME = new QName("http://ruta.rs/services", "PutDocument");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: rs.ruta.services
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FaultInfo }
     * 
     */
    public FaultInfo createFaultInfo() {
        return new FaultInfo();
    }

    /**
     * Create an instance of {@link GetDocumentResponse }
     * 
     */
    public GetDocumentResponse createGetDocumentResponse() {
        return new GetDocumentResponse();
    }

    /**
     * Create an instance of {@link SearchPartyResponse }
     * 
     */
    public SearchPartyResponse createSearchPartyResponse() {
        return new SearchPartyResponse();
    }

    /**
     * Create an instance of {@link DeleteCatalogueResponse }
     * 
     */
    public DeleteCatalogueResponse createDeleteCatalogueResponse() {
        return new DeleteCatalogueResponse();
    }

    /**
     * Create an instance of {@link DeleteUser }
     * 
     */
    public DeleteUser createDeleteUser() {
        return new DeleteUser();
    }

    /**
     * Create an instance of {@link SearchCatalogueResponse }
     * 
     */
    public SearchCatalogueResponse createSearchCatalogueResponse() {
        return new SearchCatalogueResponse();
    }

    /**
     * Create an instance of {@link FindCatalogue }
     * 
     */
    public FindCatalogue createFindCatalogue() {
        return new FindCatalogue();
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
     * Create an instance of {@link GetDocument }
     * 
     */
    public GetDocument createGetDocument() {
        return new GetDocument();
    }

    /**
     * Create an instance of {@link QueryPartyResponse }
     * 
     */
    public QueryPartyResponse createQueryPartyResponse() {
        return new QueryPartyResponse();
    }

    /**
     * Create an instance of {@link SearchParty }
     * 
     */
    public SearchParty createSearchParty() {
        return new SearchParty();
    }

    /**
     * Create an instance of {@link FindAllParties }
     * 
     */
    public FindAllParties createFindAllParties() {
        return new FindAllParties();
    }

    /**
     * Create an instance of {@link DeleteUserResponse }
     * 
     */
    public DeleteUserResponse createDeleteUserResponse() {
        return new DeleteUserResponse();
    }

    /**
     * Create an instance of {@link TestEpisode }
     * 
     */
    public TestEpisode createTestEpisode() {
        return new TestEpisode();
    }

    /**
     * Create an instance of {@link UpdateParty }
     * 
     */
    public UpdateParty createUpdateParty() {
        return new UpdateParty();
    }

    /**
     * Create an instance of {@link UpdatePartyResponse }
     * 
     */
    public UpdatePartyResponse createUpdatePartyResponse() {
        return new UpdatePartyResponse();
    }

    /**
     * Create an instance of {@link DeleteCatalogue }
     * 
     */
    public DeleteCatalogue createDeleteCatalogue() {
        return new DeleteCatalogue();
    }

    /**
     * Create an instance of {@link PutDocumentResponse }
     * 
     */
    public PutDocumentResponse createPutDocumentResponse() {
        return new PutDocumentResponse();
    }

    /**
     * Create an instance of {@link QueryPartyName }
     * 
     */
    public QueryPartyName createQueryPartyName() {
        return new QueryPartyName();
    }

    /**
     * Create an instance of {@link SearchCatalogue }
     * 
     */
    public SearchCatalogue createSearchCatalogue() {
        return new SearchCatalogue();
    }

    /**
     * Create an instance of {@link PutDocument }
     * 
     */
    public PutDocument createPutDocument() {
        return new PutDocument();
    }

    /**
     * Create an instance of {@link InsertParty }
     * 
     */
    public InsertParty createInsertParty() {
        return new InsertParty();
    }

    /**
     * Create an instance of {@link QueryPartyNameResponse }
     * 
     */
    public QueryPartyNameResponse createQueryPartyNameResponse() {
        return new QueryPartyNameResponse();
    }

    /**
     * Create an instance of {@link QueryParty }
     * 
     */
    public QueryParty createQueryParty() {
        return new QueryParty();
    }

    /**
     * Create an instance of {@link RegisterUser }
     * 
     */
    public RegisterUser createRegisterUser() {
        return new RegisterUser();
    }

    /**
     * Create an instance of {@link InsertPartyResponse }
     * 
     */
    public InsertPartyResponse createInsertPartyResponse() {
        return new InsertPartyResponse();
    }

    /**
     * Create an instance of {@link TestEpisodeResponse }
     * 
     */
    public TestEpisodeResponse createTestEpisodeResponse() {
        return new TestEpisodeResponse();
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
     * Create an instance of {@link FindCatalogueResponse }
     * 
     */
    public FindCatalogueResponse createFindCatalogueResponse() {
        return new FindCatalogueResponse();
    }

    /**
     * Create an instance of {@link InsertCatalogue }
     * 
     */
    public InsertCatalogue createInsertCatalogue() {
        return new InsertCatalogue();
    }

    /**
     * Create an instance of {@link RegisterUserResponse }
     * 
     */
    public RegisterUserResponse createRegisterUserResponse() {
        return new RegisterUserResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchParty }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "SearchParty")
    public JAXBElement<SearchParty> createSearchParty(SearchParty value) {
        return new JAXBElement<SearchParty>(_SearchParty_QNAME, SearchParty.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryPartyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "QueryPartyResponse")
    public JAXBElement<QueryPartyResponse> createQueryPartyResponse(QueryPartyResponse value) {
        return new JAXBElement<QueryPartyResponse>(_QueryPartyResponse_QNAME, QueryPartyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateCatalogueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "UpdateCatalogueResponse")
    public JAXBElement<UpdateCatalogueResponse> createUpdateCatalogueResponse(UpdateCatalogueResponse value) {
        return new JAXBElement<UpdateCatalogueResponse>(_UpdateCatalogueResponse_QNAME, UpdateCatalogueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllPartiesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "FindAllPartiesResponse")
    public JAXBElement<FindAllPartiesResponse> createFindAllPartiesResponse(FindAllPartiesResponse value) {
        return new JAXBElement<FindAllPartiesResponse>(_FindAllPartiesResponse_QNAME, FindAllPartiesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDocument }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "GetDocument")
    public JAXBElement<GetDocument> createGetDocument(GetDocument value) {
        return new JAXBElement<GetDocument>(_GetDocument_QNAME, GetDocument.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestEpisode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "TestEpisode")
    public JAXBElement<TestEpisode> createTestEpisode(TestEpisode value) {
        return new JAXBElement<TestEpisode>(_TestEpisode_QNAME, TestEpisode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindAllParties }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "FindAllParties")
    public JAXBElement<FindAllParties> createFindAllParties(FindAllParties value) {
        return new JAXBElement<FindAllParties>(_FindAllParties_QNAME, FindAllParties.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteUserResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "DeleteUserResponse")
    public JAXBElement<DeleteUserResponse> createDeleteUserResponse(DeleteUserResponse value) {
        return new JAXBElement<DeleteUserResponse>(_DeleteUserResponse_QNAME, DeleteUserResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDocumentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "GetDocumentResponse")
    public JAXBElement<GetDocumentResponse> createGetDocumentResponse(GetDocumentResponse value) {
        return new JAXBElement<GetDocumentResponse>(_GetDocumentResponse_QNAME, GetDocumentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchPartyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "SearchPartyResponse")
    public JAXBElement<SearchPartyResponse> createSearchPartyResponse(SearchPartyResponse value) {
        return new JAXBElement<SearchPartyResponse>(_SearchPartyResponse_QNAME, SearchPartyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FaultInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "RutaException")
    public JAXBElement<FaultInfo> createRutaException(FaultInfo value) {
        return new JAXBElement<FaultInfo>(_RutaException_QNAME, FaultInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindCatalogue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "FindCatalogue")
    public JAXBElement<FindCatalogue> createFindCatalogue(FindCatalogue value) {
        return new JAXBElement<FindCatalogue>(_FindCatalogue_QNAME, FindCatalogue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchCatalogueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "SearchCatalogueResponse")
    public JAXBElement<SearchCatalogueResponse> createSearchCatalogueResponse(SearchCatalogueResponse value) {
        return new JAXBElement<SearchCatalogueResponse>(_SearchCatalogueResponse_QNAME, SearchCatalogueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteCatalogueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "DeleteCatalogueResponse")
    public JAXBElement<DeleteCatalogueResponse> createDeleteCatalogueResponse(DeleteCatalogueResponse value) {
        return new JAXBElement<DeleteCatalogueResponse>(_DeleteCatalogueResponse_QNAME, DeleteCatalogueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteUser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "DeleteUser")
    public JAXBElement<DeleteUser> createDeleteUser(DeleteUser value) {
        return new JAXBElement<DeleteUser>(_DeleteUser_QNAME, DeleteUser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertCatalogueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "InsertCatalogueResponse")
    public JAXBElement<InsertCatalogueResponse> createInsertCatalogueResponse(InsertCatalogueResponse value) {
        return new JAXBElement<InsertCatalogueResponse>(_InsertCatalogueResponse_QNAME, InsertCatalogueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TestEpisodeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "TestEpisodeResponse")
    public JAXBElement<TestEpisodeResponse> createTestEpisodeResponse(TestEpisodeResponse value) {
        return new JAXBElement<TestEpisodeResponse>(_TestEpisodeResponse_QNAME, TestEpisodeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateCatalogue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "UpdateCatalogue")
    public JAXBElement<UpdateCatalogue> createUpdateCatalogue(UpdateCatalogue value) {
        return new JAXBElement<UpdateCatalogue>(_UpdateCatalogue_QNAME, UpdateCatalogue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Episode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "Episode")
    public JAXBElement<Episode> createEpisode(Episode value) {
        return new JAXBElement<Episode>(_Episode_QNAME, Episode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertPartyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "InsertPartyResponse")
    public JAXBElement<InsertPartyResponse> createInsertPartyResponse(InsertPartyResponse value) {
        return new JAXBElement<InsertPartyResponse>(_InsertPartyResponse_QNAME, InsertPartyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindCatalogueResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "FindCatalogueResponse")
    public JAXBElement<FindCatalogueResponse> createFindCatalogueResponse(FindCatalogueResponse value) {
        return new JAXBElement<FindCatalogueResponse>(_FindCatalogueResponse_QNAME, FindCatalogueResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertCatalogue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "InsertCatalogue")
    public JAXBElement<InsertCatalogue> createInsertCatalogue(InsertCatalogue value) {
        return new JAXBElement<InsertCatalogue>(_InsertCatalogue_QNAME, InsertCatalogue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterUserResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "RegisterUserResponse")
    public JAXBElement<RegisterUserResponse> createRegisterUserResponse(RegisterUserResponse value) {
        return new JAXBElement<RegisterUserResponse>(_RegisterUserResponse_QNAME, RegisterUserResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchCriterion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:rs:ruta:client", name = "SearchCriterion")
    public JAXBElement<SearchCriterion> createSearchCriterion(SearchCriterion value) {
        return new JAXBElement<SearchCriterion>(_SearchCriterion_QNAME, SearchCriterion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PutDocumentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "PutDocumentResponse")
    public JAXBElement<PutDocumentResponse> createPutDocumentResponse(PutDocumentResponse value) {
        return new JAXBElement<PutDocumentResponse>(_PutDocumentResponse_QNAME, PutDocumentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryPartyName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "QueryPartyName")
    public JAXBElement<QueryPartyName> createQueryPartyName(QueryPartyName value) {
        return new JAXBElement<QueryPartyName>(_QueryPartyName_QNAME, QueryPartyName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchCatalogue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "SearchCatalogue")
    public JAXBElement<SearchCatalogue> createSearchCatalogue(SearchCatalogue value) {
        return new JAXBElement<SearchCatalogue>(_SearchCatalogue_QNAME, SearchCatalogue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteCatalogue }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "DeleteCatalogue")
    public JAXBElement<DeleteCatalogue> createDeleteCatalogue(DeleteCatalogue value) {
        return new JAXBElement<DeleteCatalogue>(_DeleteCatalogue_QNAME, DeleteCatalogue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateParty }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "UpdateParty")
    public JAXBElement<UpdateParty> createUpdateParty(UpdateParty value) {
        return new JAXBElement<UpdateParty>(_UpdateParty_QNAME, UpdateParty.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdatePartyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "UpdatePartyResponse")
    public JAXBElement<UpdatePartyResponse> createUpdatePartyResponse(UpdatePartyResponse value) {
        return new JAXBElement<UpdatePartyResponse>(_UpdatePartyResponse_QNAME, UpdatePartyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryParty }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "QueryParty")
    public JAXBElement<QueryParty> createQueryParty(QueryParty value) {
        return new JAXBElement<QueryParty>(_QueryParty_QNAME, QueryParty.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterUser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "RegisterUser")
    public JAXBElement<RegisterUser> createRegisterUser(RegisterUser value) {
        return new JAXBElement<RegisterUser>(_RegisterUser_QNAME, RegisterUser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertParty }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "InsertParty")
    public JAXBElement<InsertParty> createInsertParty(InsertParty value) {
        return new JAXBElement<InsertParty>(_InsertParty_QNAME, InsertParty.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryPartyNameResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "QueryPartyNameResponse")
    public JAXBElement<QueryPartyNameResponse> createQueryPartyNameResponse(QueryPartyNameResponse value) {
        return new JAXBElement<QueryPartyNameResponse>(_QueryPartyNameResponse_QNAME, QueryPartyNameResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PutDocument }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ruta.rs/services", name = "PutDocument")
    public JAXBElement<PutDocument> createPutDocument(PutDocument value) {
        return new JAXBElement<PutDocument>(_PutDocument_QNAME, PutDocument.class, null, value);
    }

}
