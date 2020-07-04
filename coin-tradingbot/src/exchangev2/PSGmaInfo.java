package exchangev2;

public class PSGmaInfo 
{
	private int period_simpleMa, period_expMa1, period_expMa2;
	
	public int getPeriod_simpleMa() {
		return period_simpleMa;
	}

	public int getPeriod_expMa1() {
		return period_expMa1;
	}

	public int getPeriod_expMa2() {
		return period_expMa2;
	}

	public PSGmaInfo(int period_simpleMa, int period_expMa1, int period_expMa2)
	{
		this.period_expMa1 = period_expMa1;
		this.period_expMa2 = period_expMa2;
		this.period_simpleMa = period_simpleMa;
	}
	
	
}
