package org.ddosm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class JSONParserUtils {

	public static String getDateString(long time, String timeZoneID) {
		
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm aaa, zzzz");
        sdf.setTimeZone(TimeZone.getTimeZone(timeZoneID));
        return sdf.format(date);
        
	}
}
