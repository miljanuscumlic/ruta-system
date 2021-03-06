package rs.ruta.client.correspondence;

/**
 * Class encapsulating a state machine that represents one abstract {@code UBL document process}.
 * It serves as a marker for all sublacess that are realized {@code UBL document processes} like
 * process for{@link CreateCatalogueProcess creation and distribution of the catalogue}, for
 * {@link BillingProcess making and sending invoce} etc.
 */
public abstract class DocumentProcess extends RutaProcess implements RutaProcessState
{

}
