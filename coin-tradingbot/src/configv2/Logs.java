package configv2;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import config.Config;
import net.hammereditor.designutilities.config.*;

public class Logs
{
	public static Log log = new Log(configv2.Config.config);
	
	public static String getUTCtimeStr()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        return sdf.format(System.currentTimeMillis());
    }
	
	public static String changeDecimalPlaces(double d, int places)
	{
		/*String s = "#.";
		for (int i = 0; i < places; i++)
			s += "0";
		DecimalFormat df = new DecimalFormat(s); 
		return df.format(d);*/
		return String.format("%." + places + "f", d);
	}
}
