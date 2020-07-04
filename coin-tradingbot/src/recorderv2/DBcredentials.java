package recorderv2;

public class DBcredentials 
{
	private String hostname, username, password, schema;
	public String getHostname() {
		return hostname;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getSchema() {
		return schema;
	}

	public int getPort() {
		return port;
	}

	private int port;
	
	public DBcredentials(String hostname, int port, String username, String password, String schema)
	{
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
		this.schema = schema;
	}
	
	
}
