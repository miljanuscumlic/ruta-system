
package rs.ruta.server;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the rs.ruta.server package. 
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

    private final static QName _PutDocument_QNAME = new QName("http://server.ruta.rs/", "putDocument");
    private final static QName _GetDocumentResponse_QNAME = new QName("http://server.ruta.rs/", "getDocumentResponse");
    private final static QName _PutDocumentResponse_QNAME = new QName("http://server.ruta.rs/", "putDocumentResponse");
    private final static QName _GetDocument_QNAME = new QName("http://server.ruta.rs/", "getDocument");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: rs.ruta.server
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PutDocument }
     * 
     */
    public PutDocument createPutDocument() {
        return new PutDocument();
    }

    /**
     * Create an instance of {@link PutDocumentResponse }
     * 
     */
    public PutDocumentResponse createPutDocumentResponse() {
        return new PutDocumentResponse();
    }

    /**
     * Create an instance of {@link GetDocument }
     * 
     */
    public GetDocument createGetDocument() {
        return new GetDocument();
    }

    /**
     * Create an instance of {@link GetDocumentResponse }
     * 
     */
    public GetDocumentResponse createGetDocumentResponse() {
        return new GetDocumentResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PutDocument }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ruta.rs/", name = "putDocument")
    public JAXBElement<PutDocument> createPutDocument(PutDocument value) {
        return new JAXBElement<PutDocument>(_PutDocument_QNAME, PutDocument.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDocumentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ruta.rs/", name = "getDocumentResponse")
    public JAXBElement<GetDocumentResponse> createGetDocumentResponse(GetDocumentResponse value) {
        return new JAXBElement<GetDocumentResponse>(_GetDocumentResponse_QNAME, GetDocumentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PutDocumentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ruta.rs/", name = "putDocumentResponse")
    public JAXBElement<PutDocumentResponse> createPutDocumentResponse(PutDocumentResponse value) {
        return new JAXBElement<PutDocumentResponse>(_PutDocumentResponse_QNAME, PutDocumentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDocument }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://server.ruta.rs/", name = "getDocument")
    public JAXBElement<GetDocument> createGetDocument(GetDocument value) {
        return new JAXBElement<GetDocument>(_GetDocument_QNAME, GetDocument.class, null, value);
    }

}
