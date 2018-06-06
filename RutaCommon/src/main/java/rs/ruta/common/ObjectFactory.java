package rs.ruta.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import rs.ruta.common.RutaVersion;

/**{@code ObjectFactory} is a helper class in the process of mapping objects to {@code XML} elements.
 * {@code ObjectFactory} has two methods {@code createXxx} for every class which objects it is mapping.
 * One method is for instantiating the object, and the other is for instantiating the {@link JAXBElement}
 * that is an representation of the XML element to which it is mapped. These methods are used internally
 * by JAXB.
 */
@XmlRegistry
public class ObjectFactory
{
	public final static QName _Attachment_QNAME = new QName("http://www.ruta.rs/ns/common", "Attachment");
	public final static QName _BugReport_QNAME = new QName("http://www.ruta.rs/ns/common", "BugReport");
	public final static QName _DocumentDistribution_QNAME = new QName("http://www.ruta.rs/ns/common", "DocumentDistribution");
	public final static QName _DocumentReceipt_QNAME = new QName("http://www.ruta.rs/ns/common", "DocumentReceipt");
	public final static QName _DeregistrationNotice_QNAME = new QName("http://www.ruta.rs/ns/common", "DeregistrationNotice");
	public final static QName _Followers_QNAME = new QName("http://www.ruta.rs/ns/common", "Associates");
	public final static QName _PartyID_QNAME = new QName("http://www.ruta.rs/ns/common", "PartyID");
	public final static QName _RutaUser_QNAME = new QName("http://www.ruta.rs/ns/common", "RutaUser");
	public static final QName _RutaVersion_QNAME = new QName("http://www.ruta.rs/ns/common", "RutaVersion");

	/**Creates an instance of {@link RutaVersion}.
	 * @return created {@code RutaVersion} object and never {@code null}
	 */
	@Nonnull
	public RutaVersion createRutaVerion()
	{
		return new RutaVersion();
	}

	/** Creates an instance of {@link JAXBElement }{@code <}{@link RutaVersion }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "RutaVersion")
	@Nonnull
	public JAXBElement<RutaVersion> createRutaVersion(@Nullable final RutaVersion value)
	{
		return new JAXBElement<RutaVersion>(_RutaVersion_QNAME, RutaVersion.class, null, value);
	}

	/**Creates an instance of {@link BugReport}.
	 * @return created {@code BugReport} object and never {@code null}
	 */
	@Nonnull
	public BugReport createBugReport()
	{
		return new BugReport();
	}

	/** Creates an instance of {@link JAXBElement }{@code <}{@link BugReport }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "BugReport")
	@Nonnull
	public JAXBElement<BugReport> createBugReport(@Nullable final BugReport value)
	{
		return new JAXBElement<BugReport>(_BugReport_QNAME, BugReport.class, null, value);
	}

	/**Creates an instance of {@link ReportAttachment}.
	 * @return created {@code Attachment} object and never {@code null}
	 */
	@Nonnull
	public ReportAttachment createAttachment()
	{
		return new ReportAttachment();
	}

	/** Creates an instance of {@link JAXBElement }{@code <}{@link ReportAttachment }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "Attachment")
	@Nonnull
	public JAXBElement<ReportAttachment> createAttachment(@Nullable final ReportAttachment value)
	{
		return new JAXBElement<ReportAttachment>(_Attachment_QNAME, ReportAttachment.class, null, value);
	}

	/**Creates an instance of {@link PartyID}.
	 * @return created {@code PartyID} object and never {@code null}
	 */
	@Nonnull
	public PartyID createPartyID()
	{
		return new PartyID();
	}

	/** Creates an instance of {@link JAXBElement }{@code <}{@link PartyID }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "PartyID")
	@Nonnull
	public JAXBElement<PartyID> createPartyID(@Nullable final PartyID value)
	{
		return new JAXBElement<PartyID>(_PartyID_QNAME, PartyID.class, null, value);
	}

	/**
	 * Creates an instance of {@link RutaUser}.
	 * @return created {@code RutaUser} object and never {@code null}
	 */
	@Nonnull
	public RutaUser createUser()
	{
		return new RutaUser();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link RutaUser }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "RutaUser")
	@Nonnull
	public JAXBElement<RutaUser> createUser(@Nullable final RutaUser value)
	{
		return new JAXBElement<RutaUser>(_RutaUser_QNAME, RutaUser.class, null, value);
	}

	/**
	 * Creates an instance of {@link Associates}.
	 * @return created {@code Associates} object and never {@code null}
	 */
	@Nonnull
	public Associates createFollowers()
	{
		return new Associates();
	}

	/** Creates an instance of {@link JAXBElement }{@code <}{@link Associates }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "Associates")
	@Nonnull
	public JAXBElement<Associates> createFollowers(@Nullable final Associates value)
	{
		return new JAXBElement<Associates>(_Followers_QNAME, Associates.class, null, value);
	}

	/**Creates an instance of {@link DocumentDistribution}.
	 * @return created {@code DocumentDistribution} object and never {@code null}
	 */
	@Nonnull
	public DocumentDistribution createDocumentDistribution()
	{
		return new DocumentDistribution();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link DocumentDistribution }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "DocumentDistribution")
	@Nonnull
	public JAXBElement<DocumentDistribution> createDocumentDistribution(@Nullable final DocumentDistribution value)
	{
		return new JAXBElement<DocumentDistribution>(_DocumentDistribution_QNAME, DocumentDistribution.class, null, value);
	}

	/**Creates an instance of {@link DocumentReceipt}.
	 * @return created {@code DocumentReceipt} object and never {@code null}
	 */
	@Nonnull
	public DocumentReceipt createDocumentReceipt()
	{
		return new DocumentReceipt();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link DocumentReceipt }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "DocumentReceipt")
	@Nonnull
	public JAXBElement<DocumentReceipt> createDocumentReceipt(@Nullable final DocumentReceipt value)
	{
		return new JAXBElement<DocumentReceipt>(_DocumentReceipt_QNAME, DocumentReceipt.class, null, value);
	}

	/**
	 * Creates an instance of {@link DeregistrationNotice}.
	 * @return created {@code DeregistrationNotice} object and never {@code null}
	 */
	@Nonnull
	public DeregistrationNotice createDeregistrationNotice()
	{
		return new DeregistrationNotice();
	}

	/**
	 * Creates an instance of {@link JAXBElement }{@code <}{@link DeregistrationNotice }{@code >}.
	 * @return created JAXBElement and never {@code null}
	 */
	@XmlElementDecl(namespace = "http://www.ruta.rs/ns/common", name = "DeregistrationNotice")
	@Nonnull
	public JAXBElement<DeregistrationNotice> createDeregistrationNotice(@Nullable final DeregistrationNotice value)
	{
		return new JAXBElement<DeregistrationNotice>(_DeregistrationNotice_QNAME, DeregistrationNotice.class, null, value);
	}

}