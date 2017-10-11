package rs.ruta.client.datamapper;

public final class DatabaseAdmin
{
	private final static String username = "admin"; //MMM: maybe it is not needed at all
	private final static String password = "";
	private final static DatabaseAdmin admin = new DatabaseAdmin();

	public static String getUsername() { return username; }

	public static String getPassword() { return password; }

	public DatabaseAdmin getInstance()
	{
		return admin;
	}

}
