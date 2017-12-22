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
 * that is an representation of the XML element to which it is mapped.
 */
@XmlRegistry
public class ObjectFactory
{
	public static final QName _RutaVersion_QNAME = new QName("urn:rs:ruta:common", "RutaVersion");
	public final static QName _BugReport_QNAME = new QName("urn:rs:ruta:common", "BugReport");
	public final static QName _Attachment_QNAME = new QName("urn:rs:ruta:common", "Attachment");
	public final static QName _PartyID_QNAME = new QName("urn:rs:ruta:services", "PartyID");
	public final static QName _User_QNAME = new QName("urn:rs:ruta:services", "User");

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
	@XmlElementDecl(namespace = "urn:rs:ruta:common", name = "RutaVersion")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:common", name = "BugReport")
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
	@XmlElementDecl(namespace = "urn:rs:ruta:common", name = "Attachment")
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
	  @XmlElementDecl(namespace = "urn:rs:ruta:services", name = "PartyID")
	  @Nonnull
	  public JAXBElement<PartyID> createPartyID(@Nullable final PartyID value)
	  {
	      return new JAXBElement<PartyID>(_PartyID_QNAME, PartyID.class, null, value);
	  }

	  /**Creates an instance of {@link User}.
	   * @return created {@code User} object and never {@code null}
	   */
	  @Nonnull
	  public User createUser()
	  {
		  return new User();
	  }

	  /** Creates an instance of {@link JAXBElement }{@code <}{@link User }{@code >}.
	   * @return created JAXBElement and never {@code null}
	   */
	  @XmlElementDecl(namespace = "urn:rs:ruta:services", name = "User")
	  @Nonnull
	  public JAXBElement<User> createUser(@Nullable final User value)
	  {
		  return new JAXBElement<User>(_User_QNAME, User.class, null, value);
	  }


}

