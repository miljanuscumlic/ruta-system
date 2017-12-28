package rs.ruta.common.datamapper;

public final class DatabaseAdmin
{
	private final static String username = "admin";
	private static String password = "admin";
	private final static DatabaseAdmin admin = new DatabaseAdmin();

	public static String getUsername() { return username; }

	public static String getPassword() { return password; }

	public static void setPassword(String newPassword) { password = newPassword; }

	public DatabaseAdmin getInstance()
	{
		return admin;
	}

}
