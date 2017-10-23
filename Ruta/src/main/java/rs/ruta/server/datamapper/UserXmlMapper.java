package rs.ruta.server.datamapper;

import java.util.ArrayList;

import javax.xml.bind.JAXBElement;

import org.exist.security.AXSchemaType;
import org.exist.security.Account;
import org.exist.security.Group;
import org.exist.security.SchemaType;
import org.exist.security.internal.aider.GroupAider;
import org.exist.security.internal.aider.UserAider;
import org.exist.xmldb.UserManagementService;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ObjectFactory;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.server.DatabaseException;
import rs.ruta.server.DetailException;
import rs.ruta.server.UserException;

public class UserXmlMapper extends XmlMapper
{
	final private static String docPrefix = "";
	final private static String userGroupName = "users";
	final private static String collectionPath = "/ruta/keys";
	final private static String deletedCollectionPath = "/ruta/deleted/keys";
	final private static SchemaType SECRET_KEY = AXSchemaType.EMAIL;
	final private static SchemaType UNIQUE_ID = AXSchemaType.ALIAS_USERNAME;
	final private static String objectPackageName = null;

	public UserXmlMapper() throws DatabaseException
	{
		super();
	}

	@Override
	public Object find(String id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object find(Object object) throws DetailException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertAll()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends DSTransaction> void insert(Object object, Object id, T transaction) throws DetailException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends DSTransaction> String updateUser(Object user, T transaction) throws DetailException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DSTransaction> void update(Object object, Object id, T transaction) throws DetailException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends DSTransaction> void deleteUser(String username, Object id, T transaction) throws DetailException
	{
		//deletes user's catalogue and party data
		try
		{
			MapperRegistry.getMapper(CatalogueType.class).delete(id, transaction);
		}
		catch(Exception e ) { } //this is OK only when the catalogue is previously deleted, otherwise database could end up with leftovers?
		MapperRegistry.getMapper(PartyType.class).delete(id, transaction);

		//deletes document from the /keys collection
		String secretKey = findSecretKey(username);
		super.delete(secretKey, transaction);

		//deletes user from eXist database management system
		deleteExistAccount(username);
	}

	/**Deletes user account from the Exist user management system. No user related documents (e.g. catalogue)
	 * are deleted from the database with this method.
	 * @param username user's account username
	 * @param transaction <code>DSTransaction</code> object responsible for keeping track of transaction operations.
	 * Could be <code>null</code> if no transaction is needed during deletion of the user's account from eXist database.
	 * @throws DetailException if user account could not be deleted
	 */
	public <T extends DSTransaction> void deleteExistAccount(String username/*, T transaction*/) throws DetailException
	{
		Collection rootCollection = null;
		try
		{
			rootCollection = getRootCollection();
		}
		catch(XMLDBException e)
		{
			logger.error("Exception is: ", e);
			throw new DatabaseException("Collection could not be retrieved from the database.");
		}
		try
		{
			logger.info("Start deletion of user " + username + " account from the database.");
			UserManagementService ums = (UserManagementService) rootCollection.getService("UserManagementService", "1.0");
			Account account = ums.getAccount(username);
			if(account == null)
				throw new UserException("User account does not exist!");
			ums.removeAccount(account);
			logger.info("Finished deletion of user " + username + " account from the database.");
/*			There is no need to append anything to the Transaction journal because deregistration has succeeded
 			if(transaction != null)
				((ExistTransaction)transaction).appendOperation(null, null, "DEREGISTER", null, null, username);*/
		}
		catch(XMLDBException e)
		{
			logger.info("Could not delete user " + username + " account from the database.");
			throw new UserException("User account could not be removed from the database.", e);
		}
		finally
		{
			try
			{
				if (rootCollection != null)
					rootCollection.close();
			}
			catch(XMLDBException e)
			{
				logger.error("Exception is: ", e);
			}
		}
	}

	@Override
	public <T extends DSTransaction> Object registerUser(String username, String password, T transaction) throws DetailException
	{
		String secretKey = null;
		try
		{
			logger.info("Start of registering of the user " + username + " with the CDR service.");
			insertUser(username, password, (ExistTransaction) transaction);
			//reservation of unique secretKey in /db/ruta/keys collection
			secretKey = (String) insert(new PartyType(), transaction);
			//insertMetadata(username, MetaSchemaType.SECRET_KEY, secretKey); //doesn't work - bug in eXist
			insertMetadata(username, SECRET_KEY, secretKey);
			//reservation of unique id in /db/ruta/parties
			String id = (String) ((PartyXmlMapper) MapperRegistry.getMapper(PartyType.class)).insert(new PartyType(), transaction);
			insertMetadata(username, UNIQUE_ID, id);
			logger.info("Finished registering of the user " + username + " with the CDR service.");
		}
		catch (XMLDBException e)
		{
			logger.error("Could not register the user " + username + " with the CDR service.");
			logger.error("Exception is: ", e);
			throw new UserException("User has insufficient data in the database, or data could not be retrieved.");
		}
		return secretKey;
	}

	/**Registers new user with the eXist database user management system.
	 * @param username user's username
	 * @param password user's password
	 * @param transaction
	 * @throws DetailException if database collection or some information from it could not be retrieved
	 */
	private void insertUser(String username, String password, ExistTransaction transaction) throws DetailException
	{
		Collection collection = null;
		UserManagementService ums = null;
		try
		{
			collection = getRootCollection();
			ums = (UserManagementService) collection.getService("UserManagementService", "1.0");
		}
		catch(XMLDBException e)
		{
			logger.error("Exception is: ", e);
			throw new DatabaseException("Collection could not be retrieved from the database.", e);
		}
		try
		{
			Group userGroup = ums.getGroup(userGroupName);
			if(userGroupName == null)
			{
				userGroup = new GroupAider(userGroupName);
				ums.addGroup(userGroup);
			}
			transaction.appendOperation(null, null, "REGISTER", null, null, username);
			final UserAider account = new UserAider(username, userGroup);
			account.setPassword(password);
			ums.addAccount(account);
		}
		catch(XMLDBException e)
		{
			logger.error("Exception is: ", e);
			throw new UserException("Database connectivity issues or user already exists.", e);
		}
		finally
		{
			try
			{
				if (collection != null)
					collection.close();
			}
			catch(XMLDBException e)
			{
				logger.error("Exception is: ", e);
			}
		}
	}

	/**Checks if the user has proper credentials to make changes to this user account.
	 * @param username user's username
	 * @param password user's password
	 * @param sekretKey user's secret key
	 * @return user's id
	 * @throws UserException if user has wrong credentials or the probles due database connection
	 */
	public String checkUser(String username, String password, String secretKey) throws UserException
	{
		String partyID = null;
		Account account = null;
		Collection root = null, collection = null;
		UserManagementService ums;
		try
		{
			root = getRootCollection(); // as admin
			ums = (UserManagementService) root.getService("UserManagementService", "1.0");
			account = ums.getAccount(username);
			//check to see if user has right credentials
			collection = getRootCollection(username, password); // user tries to retrieve root collection
			if(!secretKey.equals(account.getMetadataValue(SECRET_KEY)))
				throw new UserException("User is not registered with the database!");
			partyID = account.getMetadataValue(UNIQUE_ID); //null if user is not yet registered
		}
		catch (XMLDBException e)
		{
			logger.error("Exception is: ", e);
			throw new UserException("Collection or the user data could not be retrieved from the database.");
		}
		finally
		{
			try
			{
				if (root != null)
					root.close();
				if(collection != null)
					collection.close();
			}
			catch (XMLDBException e)
			{
				logger.error("Exception is: ", e);
			}
		}
		return partyID;
	}

	/**Inserts or updates pertinent metadata value for a user with passed username.
	 * @param username user's username
	 * @param schemaType type of the metadata
	 * @param metaData metadata value
	 * @throws XMLDBException throws if it is unable to store metadata
	 */
	public void insertMetadata(String username, SchemaType schemaType, String metaData) throws XMLDBException
	{
		Collection collection = null;
		try
		{
			collection = getRootCollection();
			UserManagementService ums = (UserManagementService) collection.getService("UserManagementService", "1.0");
			final UserAider account = (UserAider) ums.getAccount(username);
			account.setMetadataValue(schemaType, metaData);
			ums.updateAccount(account);
		}
		finally
		{
			try
			{
				if(collection != null)
					collection.close();
			}
			catch(XMLDBException e)
			{
				logger.error("Exception is: ", e);
			}
		}
	}

	/**Retrieves metadata value of the given type for a user with passed username.
	 * @param username user which metadata is get
	 * @param schemaType type of the metadata
	 * @return metadata value
	 * @throws UserException if user is not registered or metadata cannot be retrieved
	 */
	public String findMetaData(String username, SchemaType schemaType) throws UserException
	{
		Collection collection = null;
		try
		{
			collection = getRootCollection();
			UserManagementService ums = (UserManagementService) collection.getService("UserManagementService", "1.0");
			Account account = ums.getAccount(username);
			if(account != null)
				return account.getMetadataValue(schemaType);
			else
				throw new UserException("User is not registered with the database!");
		}
		catch(XMLDBException e)
		{
			logger.error("Exception is: ", e);
			throw new UserException("User has insufficient data in the database, or data could not be retrieved.", e);
			//("User has insufficient data in the database, or data could not be retrieved.");
		}
		finally
		{
			try
			{
				if(collection != null)
					collection.close();
			}
			catch(XMLDBException e)
			{
				logger.error("Exception is: ", e);
			}
		}
	}

	@Override
	public Class<?> getObjectClass()
	{
		return null;
	}

	@Override
	public String getCollectionPath() { return collectionPath; }
	@Override
	public String getDeletedBaseCollectionPath() { return deletedCollectionPath; }
	@Override
	public String getDocumentPrefix() { return docPrefix; }

	@Override
	public String getObjectPackageName() { return objectPackageName; }

	@Override
	protected JAXBElement<PartyType> getJAXBElement(Object object)
	{
		JAXBElement<PartyType> partyElement = (new ObjectFactory()).createParty((PartyType) object);
		return partyElement;
	}

	@Override
	public String getID(Object object) throws DetailException
	{
		return findMetaData((String) object, UNIQUE_ID);
	}

	@Override
	public String findSecretKey(String username) throws UserException
	{
		return findMetaData(username, SECRET_KEY);
	}

	public String findID(String username) throws UserException
	{
		return findMetaData(username, UNIQUE_ID);
	}

	/* (non-Javadoc)
	 * @see rs.ruta.server.datamapper.XmlMapper#findAll()
	 */
	@Override
	public ArrayList<?> findAll() throws DetailException
	{
		// TODO
		return null;
	}
}
