package testv2;

import config.Logs;

public class Customer implements Comparable<Customer>
{
	String username, ma;
	public String getMA()
	{
		return ma;
	}
	public String getUsername() {
		return username;
	}

	public long getExpiryDateMs() {
		return expiryDateMs;
	}

	long expiryDateMs;
	
	public Customer(String u, long e, String ma)
	{
		username = u;
		expiryDateMs = e;
		this.ma = ma;
	}
	@Override
	public int compareTo(Customer arg0) {
		return (int)((expiryDateMs - arg0.expiryDateMs) / 1000);
	}
	public String toString()
	{
		return username + " " + Logs.getFormattedDate(expiryDateMs / 1000) + " " + ma;
	}
	
}