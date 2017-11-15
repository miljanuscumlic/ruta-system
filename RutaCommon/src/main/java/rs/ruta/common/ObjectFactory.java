package rs.ruta.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import rs.ruta.common.RutaVersion;

/**{@code ObjectFactory} is a helper class in the process of mapping the objects to {@code XML} elements.
 * {@code ObjectFactory} has two methods for every class which objects it is mapping. One method is for instantiating
 * the object, and the other is for instantiating the {@link JAXBElement} that is an representation of the XML
 * element to which it is mapped.
 */
@XmlRegistry
public class ObjectFactory
{
	public static final QName _RutaVersion_QNAME = new QName("urn:rs:ruta:common", "RutaVersion");

	/**Creates an instance of {@link ExistTransaction}.
     * @return created {@code ExistTransaction} object and never {@code null}
     */

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

}

