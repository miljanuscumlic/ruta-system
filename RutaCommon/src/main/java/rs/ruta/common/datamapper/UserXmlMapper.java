package rs.ruta.common.datamapper;

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
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import rs.ruta.common.DeregistrationNotice;
import rs.ruta.common.DocBox;
import rs.ruta.common.DocumentDistribution;
import rs.ruta.common.Associates;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.PartyID;
import rs.ruta.common.RutaUser;

public class UserXmlMapper extends XmlMapper<RutaUser>
{
	final private static String userGroupName = "users";
	final private static String collectionPath = "/system/key";
	final private static SchemaType SECRET_KEY = AXSchemaType.EMAIL;
	final private static SchemaType DOCUMENT_ID = AXSchemaType.ALIAS_USERNAME;
	final private static SchemaType PARTY_ID = AXSchemaType.FIRSTNAME;
	final private static String objectPackageName = "rs.ruta.common";

	public UserXmlMapper(DatastoreConnector connector) throws DetailException
	{
		super(connector);
	}

	@Override
	public void deleteUser(String username) throws DetailException
	{
		DSTransaction transaction = openTransaction();
		synchronized(transaction)
		{
			try
			{
				String id = getID(username);
				//deleting user's Catalogue, Catalogue Deletion and Associates documents
				try
				{
					((CatalogueXmlMapper) mapperRegistry.getMapper(CatalogueType.class)).delete(id, transaction);
				}
				catch(DetailException e)
				{
					if(! "Document does not exist!".equals(e.getMessage())) //OK if document does not exist, otherwise throw e
						throw e;
				}

				try
				{
					((CatalogueDeletionXmlMapper) mapperRegistry.getMapper(CatalogueDeletionType.class)).delete(id, transaction);
				}
				catch(DetailException e)
				{
					if(! "Document does not exist!".equals(e.getMessage())) //OK if document does not exist, otherwise throw e
						throw e;
				}

				try
				{
					((FollowersXmlMapper) mapperRegistry.getMapper(Associates.class)).delete(id, transaction);
				}
				catch(DetailException e)
				{
					if(! "Document does not exist!".equals(e.getMessage())) //OK if document does not exist, otherwise throw e
						throw e;
				}

				//deleting user's DocBox
				try
				{
					((DocBoxXmlMapper) mapperRegistry.getMapper(DocBox.class)).delete(id, transaction);
				}
				catch(DetailException e)
				{
					if(! "Collection does not exist!".equals(e.getMessage())) //OK if collection does not exist, otherwise throw e
						throw e;
				}

				//deleting Party data
				((PartyXmlMapper) mapperRegistry.getMapper(PartyType.class)).delete(id, transaction);

				//deleting document from the /key collection
				String secretKey = findSecretKey(username);
				super.delete(secretKey, transaction);

				//deleting document from the system/party-id collection
				String partyID = findPartyID(username);
				((PartyIDXmlMapper) mapperRegistry.getMapper(PartyID.class)).delete(partyID, transaction);

				//deleting user from eXist database management system
				deleteExistAccount(username);

			}
			catch(Exception e)
			{
				logger.error("Exception is ", e);
				rollbackTransaction(transaction);
				throw e;
			}
			finally
			{
				closeTransaction(transaction);
			}
		}
	}

	/**Deletes user account from the Exist user management system. No user related documents (e.g. catalogue)
	 * are deleted from the database with this method.
	 * @param username user's account username
	 * @param transaction <code>DSTransaction</code> object responsible for keeping track of transaction operations.
	 * Could be <code>null</code> if no transaction is needed during deletion of the user's account from eXist database.
	 * @throws DetailException if user account could not be deleted
	 */
	public void deleteExistAccount(String username/*, T transaction*/) throws DetailException
	{
		Collection rootCollection = null;
		try
		{
			rootCollection = getRootCollection();
			if(rootCollection == null)
				throw new DatabaseException("Could not retrieve the collection.");
		}
		catch(XMLDBException e)
		{
			throw new DatabaseException("Collection could not be retrieved from the database.");
		}
		try
		{
			logger.info("Start deletion of user \"" + username + "\" account from the database.");
			UserManagementService ums = (UserManagementService) rootCollection.getService("UserManagementService", "1.0");
			Account account = ums.getAccount(username);
			if(account == null)
				throw new UserException("RutaUser account does not exist!");
			ums.removeAccount(account);
			logger.info("Finished deletion of user account \"" + username + "\" from the database.");
/*			There is no need to append anything to the Transaction journal because deregistration has succeeded
 			if(transaction != null)
				((DatabaseTransaction)transaction).appendOperation(null, null, "DEREGISTER", null, null, username);*/
		}
		catch(XMLDBException e)
		{
			logger.info("Could not delete user account \"" + username + "\" from the database.");
			throw new UserException("RutaUser account could not be removed from the database.", e);
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
				logger.error("Exception is ", e);
			}
		}
	}

/*	@Override
	public String registerUser(String username, String password) throws DetailException
	{
		String secretKey = null;
		DatabaseTransaction transaction = (DatabaseTransaction) openTransaction();
		synchronized(transaction)
		{
			try
			{
				logger.info("Start of registering of the user " + username + " with the CDR service.");
				registerUserWithExist(username, password, transaction);
				//reservation of unique secretKey in /db/ruta/key collection
				//MMM: inserting secret key is not necessary anymore because of the use of the UUID
				secretKey = insert(username, new RutaUser(), transaction);
				//insertMetadata(username, MetaSchemaType.SECRET_KEY, secretKey); //doesn't work - bug in eXist
				insertMetadata(username, SECRET_KEY, secretKey);
				//reservation of unique id for documents in /db/ruta/party
				String id = ((PartyXmlMapper) mapperRegistry.getMapper(PartyType.class)).insert(username, new PartyType(), transaction);
				insertMetadata(username, DOCUMENT_ID, id);
				//reservation of uuid for party in /db/ruta/system/party-id
				String uuid = ((PartyIDXmlMapper) mapperRegistry.getMapper(PartyID.class)).insert(username, new PartyID(null, id), transaction);
				insertMetadata(username, PARTY_ID, uuid);
				//setting party as a follower of himself
				Associates iFollower = new Associates();
				iFollower.setPartyID(uuid);
				iFollower.add(uuid);
				((FollowersXmlMapper) mapperRegistry.getMapper(Associates.class)).insert(username, iFollower, transaction);
				logger.info("Finished registering of the user " + username + " with the CDR service.");
			}
			catch (XMLDBException e)
			{
				logger.error("Could not register the user " + username + " with the CDR service.");
				logger.error("Exception is ", e);
				rollbackTransaction(transaction);
				throw new UserException("RutaUser has insufficient data in the database, or data could not be retrieved.");
			}
			catch(DetailException e)
			{
				logger.error("Exception is ", e);
				rollbackTransaction(transaction);
				throw e;
			}
			finally
			{
				closeTransaction(transaction);
			}
		}
		return secretKey;
	}*/

	@Override
	public String registerUser(String username, String password, PartyType party) throws DetailException
	{
		String secretKey = null;
		DatabaseTransaction transaction = (DatabaseTransaction) openTransaction();
		synchronized(transaction)
		{
			try
			{
				logger.info("Started registration of the user " + username + " with the CDR service.");
				registerUserWithExist(username, password, transaction);
				//reservation of unique secretKey in /db/ruta/key collection
				//MMM: inserting secret key is not necessary anymore because of the use of the UUID ???
				secretKey = insert(username, new RutaUser(), transaction);
				//insertMetadata(username, MetaSchemaType.SECRET_KEY, secretKey); //doesn't work - bug in eXist
				insertMetadata(username, SECRET_KEY, secretKey);
				//inserting party in /db/ruta/party
				final String documentID = ((PartyXmlMapper) mapperRegistry.getMapper(PartyType.class)).insert(username, party, transaction);
				insertMetadata(username, DOCUMENT_ID, documentID);
				//inserting Party ID in /db/ruta/system/party-id
				String partyID = null;
				if(!party.getPartyIdentification().isEmpty())
					partyID = InstanceFactory.getPropertyOrNull(party.getPartyIdentification().get(0).getID(), IDType::getValue);
				if(partyID == null || "".equals(partyID))
					throw new UserException("Party ID has not been set on the client side.");
				insertMetadata(username, PARTY_ID, partyID);
				((PartyIDXmlMapper) mapperRegistry.getMapper(PartyID.class)).insert(username, new PartyID(partyID, documentID), transaction);
				//setting party as a follower of himself
				Associates iFollower = new Associates();
				iFollower.setPartyID(partyID);
				iFollower.addAssociate(partyID);
				((FollowersXmlMapper) mapperRegistry.getMapper(Associates.class)).insert(username, iFollower, transaction);
				logger.info("Finished registration of the user " + username + " with the CDR service.");
			}
			catch (XMLDBException e)
			{
				logger.error("Could not register the user " + username + " with the CDR service.");
				logger.error("Exception is ", e);
				rollbackTransaction(transaction);
				throw new UserException("RutaUser has insufficient data in the database, or data could not be retrieved.");
			}
			catch(/*DetailException*/ Exception e)
			{
//				logger.error("Exception is ", e);

				rollbackTransaction(transaction);
				throw e;
			}
			finally
			{
				closeTransaction(transaction);
			}
		}
		return secretKey;
	}

	/**
	 * Registers new user with the eXist database user management system.
	 * @param username user's username
	 * @param password user's password
	 * @param transaction
	 * @throws DetailException if database collection or some information from it could not be retrieved
	 */
	private void registerUserWithExist(String username, String password, DatabaseTransaction transaction) throws DetailException
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
			logger.error("Exception is ", e);
			throw new DatabaseException("Collection could not be retrieved from the database.", e);
		}
		try
		{
			Group userGroup = ums.getGroup(userGroupName);
			if(userGroup == null)
			{
				userGroup = new GroupAider(userGroupName);
				ums.addGroup(userGroup);
			}
			final UserAider account = new UserAider(username, userGroup);
			account.setPassword(password);
			ums.addAccount(account);
			transaction.addOperation(null, null, "REGISTER", null, null, username);
		}
		catch(XMLDBException e)
		{
			logger.error("Exception is ", e);
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
				logger.error("Exception is ", e);
			}
		}
	}

	/**
	 * Checks whether the user has proper credentials to make changes to this user account.
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
			if(root == null)
				throw new UserException("Could not retrieve the collection.");
			ums = (UserManagementService) root.getService("UserManagementService", "1.0");
			account = ums.getAccount(username);
			//check to see if user has right credentials
			collection = getRootCollection(username, password); // user tries to retrieve root collection
			if(!secretKey.equals(account.getMetadataValue(SECRET_KEY)))
				throw new UserException("RutaUser is not registered with the database!");
			partyID = account.getMetadataValue(DOCUMENT_ID); //null if user is not yet registered
		}
		catch (XMLDBException e)
		{
			logger.error("Exception is ", e);
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
				logger.error("Exception is ", e);
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
/*			final UserAider account = (UserAider) ums.getAccount(username);
			account.setMetadataValue(schemaType, metaData);*/

			final Account account = ums.getAccount(username);
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
				logger.error("Exception is ", e);
			}
		}
	}

	/**Retrieves metadata value of the given type for a user of passed username.
	 * @param username user which metadata is get
	 * @param schemaType type of the metadata
	 * @return metadata value or <code>null</code> if queried metadata is not stored for this user
	 * @throws UserException if user is not registered or metadata cannot be retrieved
	 */
	private String findMetaData(String username, SchemaType schemaType) throws UserException
	{
		Collection collection = null;
		try
		{
			collection = getRootCollection();
			if(collection == null)
				throw new UserException("Could not retrieve the collection.");
			UserManagementService ums = (UserManagementService) collection.getService("UserManagementService", "1.0");
			Account account = ums.getAccount(username);
			if(account != null)
				return account.getMetadataValue(schemaType);
			else
				throw new UserException("RutaUser is not registered with the database!");
		}
		catch(XMLDBException e)
		{
			logger.error("Exception is ", e);
			throw new UserException("RutaUser has insufficient data in the database, or data could not be retrieved.", e);
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
				logger.error("Exception is ", e);
			}
		}
	}

	@Override
	protected Class<?> getObjectClass()
	{
		return null;
	}

	@Override
	protected String getCollectionPath() { return collectionPath; }

	@Override
	protected String getObjectPackageName() { return objectPackageName; }

	@Override
	protected JAXBElement<RutaUser> getJAXBElement(RutaUser object)
	{
		return new rs.ruta.common.ObjectFactory().createUser(object);
	}

	@Override
	protected String getID(String username) throws DetailException
	{
		return findMetaData(username, DOCUMENT_ID);
	}

	@Override
	public String getUserID(String username) throws UserException
	{
		return findMetaData(username, PARTY_ID);
	}

	@Override
	public String findSecretKey(String username) throws UserException
	{
		return findMetaData(username, SECRET_KEY);
	}

	/**Gets the ID designeted for the Party that corresponds to the user.
	 * @param username user'username
	 * @return document ID
	 * @throws UserException if user is not registered or metadata cannot be retrieved
	 */
	public String findPartyID(String username) throws UserException
	{
		return findMetaData(username, PARTY_ID);
	}

	/**Gets the ID designeted for all documents in different collections that belong to the user.
	 * @param username user'username
	 * @return document ID
	 * @throws UserException if user is not registered or metadata cannot be retrieved
	 */
	public String findDocumentID(String username) throws UserException
	{
		return findMetaData(username, DOCUMENT_ID);
	}
}
