package codewriter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class StatementInfo
{
	private String sqlStatement;
	private long startTimeMs;
	private long endTimeMs;
	private String formattedDateStart;
	private String formattedDateEnd;
	
	public StatementInfo(String query, long minT, long maxT)
	{
		this.sqlStatement = query;
		this.startTimeMs = minT;
		this.endTimeMs = maxT;
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		formattedDateStart = sdf.format(new Date(minT));
		formattedDateEnd = sdf.format(new Date(maxT));
	}
	
	public String getSqlStatement() {
		return sqlStatement;
	}

	public long getStartTimeMs() {
		return startTimeMs;
	}

	public long getEndTimeMs() {
		return endTimeMs;
	}

	public String getFormattedDateStart() {
		return formattedDateStart;
	}

	public String getFormattedDateEnd() {
		return formattedDateEnd;
	}
}
