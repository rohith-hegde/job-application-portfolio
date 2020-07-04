package recorderv2;


public class PoloniexChartDescription 
{
	private PCDtimespan pcdt;
	private PCDcandlePeriod pcdcp;
	
	public PoloniexChartDescription(PCDtimespan pcdt, PCDcandlePeriod pcdp)
	{
		this.pcdcp = pcdp;
		this.pcdt = pcdt;
	}
	
	public PCDtimespan getPcdt() {
		return pcdt;
	}

	public PCDcandlePeriod getPcdcp() {
		return pcdcp;
	}

	public enum PCDtimespan {
		MONTH1, WEEK2, WEEK1, DAY4, DAY2, HOUR24, HOUR6
	}
	
	public enum PCDcandlePeriod {
		DAY1, HOUR4, HOUR2, MINUTE30, MINUTE15, MINUTE5
	}
}
