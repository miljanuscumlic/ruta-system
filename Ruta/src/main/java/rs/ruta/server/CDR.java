package rs.ruta.server;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.annotation.*;
import javax.jws.*;
import javax.servlet.ServletContext;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.MTOM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.common.ReportAttachment;
import rs.ruta.common.BugReport;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.RutaVersion;
import rs.ruta.common.SearchCriterion;
import rs.ruta.server.datamapper.*;

//handlers.xml should be inside ResourceRoot directory /WEB-INF/classes because WildFly is searching for it on that path
//ResourceRoot [root=\"/C:/Program Files/wildfly-10.1.0.Final/bin/content/Ruta-SNAPSHOT-0.0.1.war/WEB-INF/classes\"]

@HandlerChain(file = "/handlers.xml")
/*Complete settings
 * @WebService(endpointInterface = "rs.ruta.server.Server",
	wsdlLocation = "/wsdl/CDR.wsdl",
	portName = "CDRPort",
	targetNamespace = "http://ruta.rs/services",
	serviceName = "CDRService")*/

/*//local wsdl generation
@WebService(endpointInterface = "rs.ruta.server.Server",
 	targetNamespace = "http://ruta.rs/services",
	wsdlLocation = "/wsdl/CDR.wsdl")*/

//wildfly wsdl generation
@WebService(endpointInterface = "rs.ruta.server.Server", targetNamespace = "http://ruta.rs/services")
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_MTOM_BINDING)
public class CDR implements Server
{
	@Resource
	private WebServiceContext wsCtx;
	private ServletContext sCtx;
	private final static Logger logger = LoggerFactory.getLogger("rs.ruta.server");

	public CDR()
	{
		checkDataStore();
	}

	private void checkDataStore()
	{
		if(! MapperRegistry.isDatastoreAccessible())
		{
			//logger.error("Exception is ", e);
			logger.warn("Cound not connect to the database! The database is not accessible.");
			logger.warn("If database has not been started please start it. Otherwise CDR service will not be operable"
					+ " and all future SOAP requests will be rejected.");
		}
	}

	private void init() throws DetailException
	{
		if(wsCtx == null)
			throw new RuntimeException("Dependancy Injection failed on Web Service Context!");
		if(sCtx == null) // ServletContext not yet set?
		{
			MessageContext mCtx = wsCtx.getMessageContext();
			sCtx = (ServletContext) mCtx.get(MessageContext.SERVLET_CONTEXT);
		}
	}

	@Override
	@WebMethod
	public void insertCatalogue(String username, CatalogueType cat) throws RutaException
	{
		try
		{
			init();
			MapperRegistry.getMapper(CatalogueType.class).insert(username, cat);
		}
		catch(Exception e)
		{
			String exceptionMsg = "Catalogue could not be deposited to the CDR service!";
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Override
	@WebMethod
	public void updateCatalogue(String username, CatalogueType cat) throws RutaException
	{
		try
		{
			init();
			MapperRegistry.getMapper(CatalogueType.class).update(username, cat);
		}
		catch(Exception e)
		{
			String exceptionMsg = "Catalogue could not be updated to the CDR service!";
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Override
	@WebMethod
	public CatalogueType findCatalogue(String id) throws RutaException
	{
		CatalogueType cat = null;
		try
		{
			init();
			cat = MapperRegistry.getMapper(CatalogueType.class).findByUserId(id);
			return cat;
		}
		catch(Exception e)
		{
			String exceptionMsg = "Catalogue could not be retrieved from the CDR service!";
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Override
	@WebMethod
	public void deleteCatalogue(String username, CatalogueDeletionType catDeletion) throws RutaException
	{
		try
		{
			init();
			MapperRegistry.getMapper(CatalogueDeletionType.class).insert(username, catDeletion);
			//			MapperRegistry.getMapper(CatalogueType.class).delete(id); //deprecated: when CatalogueDeletion is not used
		}
		catch(Exception e)
		{
			String exceptionMsg = "Catalogue could not be deleted from the CDR service!";
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Override
	@WebMethod
	public String registerUser(String username, String password) throws RutaException
	{
		String secretKey = null;
		try
		{
			init();
			secretKey = (String) MapperRegistry.getMapper(User.class).registerUser(username, password);
			return secretKey;
		}
		catch(Exception e)
		{
			String exceptionMsg = "Party could not be registered with the CDR service!";
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}


	@Override
	@WebMethod
	public String insertParty(String username, PartyType party) throws RutaException
	{
		String id = null;
		try
		{
			init();
			MapperRegistry.getMapper(PartyType.class).insert(username, party);
			id = MapperRegistry.getMapper(User.class).getUserID(username);
			return id;
		}
		catch(Exception e)
		{
			String exceptionMsg = "Party could not be registered with the CDR service!";
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Override
	@WebMethod
	public void updateParty(String username, PartyType party) throws RutaException
	{
		String exceptionMsg = "Party could not be updated in the CDR service!";
		try
		{
			init();
			MapperRegistry.getMapper(PartyType.class).update(username, party);
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}


	@Override
	@WebMethod
	public void deleteUser(String username) throws RutaException
	{
		try
		{
			init();
			MapperRegistry.getMapper(User.class).deleteUser(username);
		}
		catch(Exception e)
		{
			String exceptionMsg = "Party could not be deleted from the CDR service!";
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Override
	public List<PartyType> searchParty(String username, SearchCriterion criterion) throws RutaException
	{
		//		logger.info(criterion.getPartyName());
		List<PartyType> searchResult = new ArrayList<>();
		try
		{
			init();
			searchResult =  MapperRegistry.getMapper(PartyType.class).findMany(criterion);

			/*			logger.info("*****************************************************************");
			long fm1avg = 0, fm2avg = 0, fm3avg = 0;
			int num = 5;
			for(int i=0; i<num; i++)
			{
				((PartyXmlMapper) MapperRegistry.getMapper(PartyType.class)).clearInMemoryObjects();
				long fc1 = System.currentTimeMillis();
				searchResult = (List<PartyType>) (MapperRegistry.getMapper(PartyType.class)).findMany(criterion);
				long fc2 = System.currentTimeMillis();
				fm2avg += fc2 - fc1;
			}
			for(int i=0; i<num; i++)
			{
				((PartyXmlMapper) MapperRegistry.getMapper(PartyType.class)).clearInMemoryObjects();
				long fc1 = System.currentTimeMillis();
				searchResult = (List<PartyType>) (MapperRegistry.getMapper(PartyType.class)).findManyID(criterion);
				long fc2 = System.currentTimeMillis();
				fm3avg += fc2 - fc1;
			}
			logger.info("Empty in memory");
			logger.info("findMany: " + fm2avg / num);
			logger.info("findManyID: " + fm3avg / num);
			logger.info("*****************************************************************");
			fm1avg = 0; fm2avg = 0; fm3avg = 0;
			for(int i=0; i<num; i++)
			{
				((PartyXmlMapper) MapperRegistry.getMapper(PartyType.class)).clearInMemoryObjects();
				long fc1 = System.currentTimeMillis();
				searchResult = (List<PartyType>) (MapperRegistry.getMapper(PartyType.class)).findMany(criterion);
				long fc2 = System.currentTimeMillis();
				fm2avg += fc2 - fc1;
			}
			for(int i=0; i<num; i++)
			{
				((PartyXmlMapper) MapperRegistry.getMapper(PartyType.class)).clearInMemoryObjects();
				long fc1 = System.currentTimeMillis();
				searchResult = (List<PartyType>) (MapperRegistry.getMapper(PartyType.class)).findManyID(criterion);
				long fc2 = System.currentTimeMillis();
				fm3avg += fc2 - fc1;
			}
			logger.info("Full in memory");
			logger.info("findMany: " + fm2avg / num);
			logger.info("findManyID: " + fm3avg / num);
			logger.info("*****************************************************************");*/
			return searchResult.size() != 0 ? searchResult : null;
		}
		catch(Exception e)
		{
			String exceptionMsg = "Query could not be processed by CDR service!";
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Override
	public List<CatalogueType> searchCatalogue(String username, SearchCriterion criterion) throws RutaException
	{
		logger.info(criterion.getItemName());
		List<CatalogueType> searchResult = new ArrayList<>();
		try
		{
			init();
			searchResult = MapperRegistry.getMapper(CatalogueType.class).findMany(criterion);
			return searchResult.size() != 0 ? searchResult : null;
		}
		catch(Exception e)
		{
			String exceptionMsg = "Query could not be processed by CDR service!";
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Override
	public List<PartyType> findAllParties() throws RutaException
	{
		logger.info("Start finding all parties");
		List<PartyType> searchResult = new ArrayList<>();
		try
		{
			init();
			searchResult = MapperRegistry.getMapper(PartyType.class).findAll();
			logger.info("Finished finding all parties");
			return searchResult.size() != 0 ? searchResult : null;
		}
		catch(Exception e)
		{
			String exceptionMsg = "Could not retrieve all parties from the CDR service!";
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Override
	public RutaVersion findClientVersion(String currentVersion) throws RutaException
	{
		try
		{
			init();
			RutaVersion latestVersion = MapperRegistry.getMapper(RutaVersion.class).findClientVersion();
			if(latestVersion.getVersion().compareTo(currentVersion) <= 0) //there is no new version
				latestVersion = null;
			return latestVersion;
		}
		catch(Exception e)
		{
			String exceptionMsg = "Could not retrieve the current version from the CDR service!";
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Override
	public void insertUpdateNotification(RutaVersion version) throws RutaException
	{
		try
		{
			init();
			//			String id =  MapperRegistry.getMapper(RutaVersion.class).createID();
			MapperRegistry.getMapper(RutaVersion.class).insert(null, version);
		}
		catch(Exception e)
		{
			String exceptionMsg = "Could not notify CDR service about the Ruta Client update!";
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Override
	@WebMethod
	public void insertBugReport(BugReport bugReport) throws RutaException
	{
		try
		{
			init();
			MapperRegistry.getMapper(BugReport.class).insert(null, bugReport);
		}
		catch(Exception e)
		{
			String exceptionMsg = "Bug could not be inserted in the database!";
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Override
	public List<BugReport> findBugReport(int start, int count) throws RutaException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Deprecated //MMM: and does not work: transaction is null
	@Override
	public void insertImage(@XmlMimeType("application/octet-stream") Image image) throws RutaException
	{
		String exceptionMsg = "File could not be deposited to the CDR service!";
		DSTransaction transaction = null;
		try
		{
			init();
			String id =  MapperRegistry.getMapper(BugReport.class).createID();
			MapperRegistry.getMapper(BugReport.class).insert(image, id, transaction);
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Deprecated //MMM: and does not work: transaction is null
	@Override
	public void insertFile(@XmlMimeType("application/octet-stream") DataHandler dataHandler, String filename) throws RutaException
	{
		String exceptionMsg = "File could not be deposited to the CDR service!";
		DSTransaction transaction = null;
		try
		{
			init();

			File file = new File(filename);
			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
			BufferedInputStream bin = new BufferedInputStream(dataHandler.getInputStream());
			byte buffer[] = new byte[1024];
			int n = 0;
			while((n = bin.read(buffer)) != -1)
				bout.write(buffer, 0, n);
			bin.close();
			bout.close();

			String id =  MapperRegistry.getMapper(BugReport.class).createID();
			MapperRegistry.getMapper(BugReport.class).insert(file, id, transaction);
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}

	@Deprecated //MMM: and does not work: transaction is null
	@Override
	public void insertAttachment(ReportAttachment attachment, String filename) throws RutaException
	{
		String exceptionMsg = "Attachment could not be deposited to the CDR service!";
		DSTransaction transaction = null;
		try
		{
			init();
			File file = attachment.getFile();
			String id =  MapperRegistry.getMapper(BugReport.class).createID();
			MapperRegistry.getMapper(BugReport.class).insert(file, id, transaction);
		}
		catch(Exception e)
		{
			logger.error("Exception is ", e);
			if (e instanceof DetailException)
				throw new RutaException(exceptionMsg, ((DetailException)e).getFaultInfo());
			else
				throw new RutaException(exceptionMsg, e.getMessage());
		}
	}
}