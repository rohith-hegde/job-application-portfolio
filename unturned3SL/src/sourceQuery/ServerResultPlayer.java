package sourceQuery;

/**
 * Represents a VSC player's information
 * @author Hammereditor
 */
public class ServerResultPlayer 
{
	private String username;
	private float onlineTimeS;
	private int score;
	
	/**
	 * 
	 * @param username The Steam player name
	 * @param onlineTimeMs The time, in milliseconds, they have been playing
	 * @param score The score
	 */
	public ServerResultPlayer(String username, float onlineTimeS, int score)
	{
		this.username = username;
		this.onlineTimeS = onlineTimeS;
		this.score = score;
	}
	
	/**
	 * Gets the username
	 * @return The username
	 */
	public String getUsername()
	{
		return username;
	}
	
	/**
	 * Gets the time they have been on the server
	 * @return The online time, in seconds
	 */
	public float getOnlineTimeS()
	{
		return onlineTimeS;
	}
	
	/**
	 * Gets the score
	 * @return The player's score
	 */
	public int getScore()
	{
		return score;
	}
	
	public String toString()
	{
		return username + " " + onlineTimeS + " " + score;
	}
}
