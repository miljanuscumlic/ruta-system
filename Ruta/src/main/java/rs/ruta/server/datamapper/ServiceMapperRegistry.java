package rs.ruta.server.datamapper;

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
import rs.ruta.common.BugReport;
import rs.ruta.common.PartnershipRequest;
import rs.ruta.common.PartnershipResolution;
import rs.ruta.common.PartnershipResponse;
import rs.ruta.common.DeregistrationNotice;
import rs.ruta.common.DocBox;
import rs.ruta.common.DocumentDistribution;
import rs.ruta.common.DocumentReceipt;
import rs.ruta.common.PartnershipBreakup;
import rs.ruta.common.Associates;
import rs.ruta.common.PartyID;
import rs.ruta.common.RutaVersion;
import rs.ruta.common.RutaUser;
import rs.ruta.common.datamapper.ApplicationResponseXmlMapper;
import rs.ruta.common.datamapper.BugReportXmlMapper;
import rs.ruta.common.datamapper.PartnershipRequestXmlMapper;
import rs.ruta.common.datamapper.PartnershipResolutionXmlMapper;
import rs.ruta.common.datamapper.PartnershipResponseXmlMapper;
import rs.ruta.common.datamapper.CatalogueDeletionXmlMapper;
import rs.ruta.common.datamapper.CatalogueXmlMapper;
import rs.ruta.common.datamapper.DatabaseTransaction;
import rs.ruta.common.datamapper.DataManipulationException;
import rs.ruta.common.datamapper.DataMapper;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.DocumentDistributionXmlMapper;
import rs.ruta.common.datamapper.DocumentReceiptXmlMapper;
import rs.ruta.common.datamapper.DistributionTransaction;
import rs.ruta.common.datamapper.DistributionTransactionMapper;
import rs.ruta.common.datamapper.DocBoxXmlMapper;
import rs.ruta.common.datamapper.ExistConnector;
import rs.ruta.common.datamapper.DatabaseTransactionMapper;
import rs.ruta.common.datamapper.DeregistrationNoticeXmlMapper;
import rs.ruta.common.datamapper.FollowersXmlMapper;
import rs.ruta.common.datamapper.InvoiceXmlMapper;
import rs.ruta.common.datamapper.MapperRegistry;
import rs.ruta.common.datamapper.OrderCancellationXmlMapper;
import rs.ruta.common.datamapper.OrderChangeXmlMapper;
import rs.ruta.common.datamapper.OrderResponseSimpleXmlMapper;
import rs.ruta.common.datamapper.OrderResponseXmlMapper;
import rs.ruta.common.datamapper.OrderXmlMapper;
import rs.ruta.common.datamapper.PartnershipBreakupXmlMapper;
import rs.ruta.common.datamapper.PartyIDXmlMapper;
import rs.ruta.common.datamapper.PartyXmlMapper;
import rs.ruta.common.datamapper.RutaVersionXmlMapper;
import rs.ruta.common.datamapper.UserXmlMapper;

/**
 * Class that holds global variables accountable for the connection to the classes responsible for the
 * database manipulation. One field is the map containing all <code>DataMapper</code>s that maps domain
 * objects to the structures of the particular data store instance in use. Also, there is a
 * <code>DSTransactionFactory</code> field responsible for the instatiation and controlling of the
 * <code>DSTransaction</code> objects.
 */
public class ServiceMapperRegistry extends MapperRegistry
{
	/**
	 * Constructs {@link MapperRegistry} object setting this concrete instace of
	 * {@code ServiceMapperRegistry} as a registry. Also, it sets {@link ExistConnector} object.
	 */
	public ServiceMapperRegistry()
	{
		setRegistry(this);
		setConnector(new RemoteExistConnector());
	}

	/**
	 * Gets the {@link DataMapper} for connection to the data store based on the <code>Class</code> paramater.
	 * If mapper for a particular class is not in the registry, it will be added to it prior to its retrieval.
	 * @param clazz <code>Class</code> object of the class which mapper should be returned
	 * @return data mapper for the input parameter object or <code>null</code>
	 * if datamapper for the intended <code>Class</code> parameter does not exist
	 * @throws DetailException if mapper could not be created and added to the registry
	 * due to database connetivity issues
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <S> DataMapper<S, String> getMapper(Class<S> clazz) throws DetailException
	{
		DataMapper<S, String> dataMapper = (DataMapper<S, String>) getInstance().getMapRegistry().get(clazz);
		if(dataMapper == null)
		{
			if(clazz == CatalogueType.class)
				dataMapper = (DataMapper<S, String>) new CatalogueXmlMapper(getConnector());
			else if(clazz == CatalogueDeletionType.class)
				dataMapper = (DataMapper<S, String>) new CatalogueDeletionXmlMapper(getConnector());
			else if(clazz == PartyType.class)
				dataMapper = (DataMapper<S, String>) new PartyXmlMapper(getConnector());
			else if(clazz == RutaUser.class)
				dataMapper = (DataMapper<S, String>) new UserXmlMapper(getConnector());
			else if(clazz == DatabaseTransaction.class)
				dataMapper = (DataMapper<S, String>) new DatabaseTransactionMapper(getConnector());
			else if(clazz == DistributionTransaction.class)
				dataMapper = (DataMapper<S, String>) new DistributionTransactionMapper(getConnector());
			else if(clazz == PartyID.class)
				dataMapper = (DataMapper<S, String>) new PartyIDXmlMapper(getConnector());
			else if(clazz == RutaVersion.class)
				dataMapper = (DataMapper<S, String>) new RutaVersionXmlMapper(getConnector());
			else if(clazz == BugReport.class)
				dataMapper = (DataMapper<S, String>) new BugReportXmlMapper(getConnector());
			else if(clazz == Associates.class)
				dataMapper = (DataMapper<S, String>) new FollowersXmlMapper(getConnector());
			else if(clazz == DocumentDistribution.class)
				dataMapper = (DataMapper<S, String>) new DocumentDistributionXmlMapper(getConnector());
			else if(clazz == DocBox.class)
				dataMapper = (DataMapper<S, String>) new DocBoxXmlMapper(getConnector());
			else if(clazz == DeregistrationNotice.class)
				dataMapper = (DataMapper<S, String>) new DeregistrationNoticeXmlMapper(getConnector());
			else if(clazz == OrderType.class)
				dataMapper = (DataMapper<S, String>) new OrderXmlMapper(getConnector());
			else if(clazz == OrderResponseType.class)
				dataMapper = (DataMapper<S, String>) new OrderResponseXmlMapper(getConnector());
			else if(clazz == OrderResponseSimpleType.class)
				dataMapper = (DataMapper<S, String>) new OrderResponseSimpleXmlMapper(getConnector());
			else if(clazz == OrderChangeType.class)
				dataMapper = (DataMapper<S, String>) new OrderChangeXmlMapper(getConnector());
			else if(clazz == OrderCancellationType.class)
				dataMapper = (DataMapper<S, String>) new OrderCancellationXmlMapper(getConnector());
			else if(clazz == ApplicationResponseType.class)
				dataMapper = (DataMapper<S, String>) new ApplicationResponseXmlMapper(getConnector());
			else if(clazz == InvoiceType.class)
				dataMapper = (DataMapper<S, String>) new InvoiceXmlMapper(getConnector());
			else if(clazz == DocumentReceipt.class)
				dataMapper = (DataMapper<S, String>) new DocumentReceiptXmlMapper(getConnector());
			else if(clazz == PartnershipRequest.class)
				dataMapper = (DataMapper<S, String>) new PartnershipRequestXmlMapper(getConnector());
			else if(clazz == PartnershipResponse.class)
				dataMapper = (DataMapper<S, String>) new PartnershipResponseXmlMapper(getConnector());
			else if(clazz == PartnershipResolution.class)
				dataMapper = (DataMapper<S, String>) new PartnershipResolutionXmlMapper(getConnector());
			else if(clazz == PartnershipBreakup.class)
				dataMapper = (DataMapper<S, String>) new PartnershipBreakupXmlMapper(getConnector());
			if(dataMapper != null)
				getInstance().getMapRegistry().put(clazz, dataMapper);
		}
		if(dataMapper == null)
			throw new DataManipulationException("There is no mapper for the class " + clazz.toString() + ".");
		return dataMapper;
	}
}
