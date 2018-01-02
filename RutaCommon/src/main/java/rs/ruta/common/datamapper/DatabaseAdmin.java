package rs.ruta.common.datamapper;

/**Class that represents database admin user with the properly initialied username and password.
 * {@code DatabaseAdmin} is a Singleton objet through wich is managed the connection to the database.
 */
public final class DatabaseAdmin
{
	private String username = "admin"; // default value
	private String password = "admin"; // default value
	private static DatabaseAdmin admin = new DatabaseAdmin();;

	/**Creates new {@code DatabaseAdmin} Singleton object with default values for
	 * username and password.
	 */
	protected DatabaseAdmin() { }

	/**Creates new {@code DatabaseAdmin} Singleton object with default values for
	 * username and password.
	 * @param username
	 * @param password
	 */
	protected DatabaseAdmin(String username, String password)
	{
		admin.username = username;
		admin.password = password;
	}

	public String getUsername() { return admin.username; }

	public String getPassword() { return admin.password; }

	/**Gets the Singleton object of the {@code DatabaseAdmin} class. If object has not been created yet,
	 * it creates one with default values of the username and password.
	 * @return {@code DatabaseAdmin} Singleton object
	 */
	public static DatabaseAdmin getInstance()
	{
/*		if(admin == null)
			admin = new DatabaseAdmin();*/
		return admin;
	}

	/**Creates new Singleton instance of {@link DatabaseAdmin} with passed parameters. As {@code DatabaseAdmin}
	 *  class is a Singleton this method creates and replaces the previous object if it exists.
	 * @param username {@code DatabaseAdmin}'s username
	 * @param password {@code DatabaseAdmin}'s password
	 * @return {@code DatabaseAdmin} Singleton object
	 */
	public static DatabaseAdmin newInstance(String username, String password)
	{
		admin.username = username;
		admin.password = password;
		return admin;
	}

}
