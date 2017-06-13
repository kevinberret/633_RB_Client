package util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class contains all useful methods to perform repetitive tasks
 * @author Daniel
 *
 */
public class Util {

	/**
	 * This method returns the current time following a specific format
	 * @return The date in format dd-MM-yyyy HH:mm:ss
	 */
	public String getTimeStamp(){
		// create a maskformat for the date
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		
		// get the current date with the appropriate format and return it
		return dateFormat.format(new Date());
	}
	
	/**
	 * This method returns the current month following a specific format
	 * @return The date in format YYYY_MMMM
	 */
	public String getDateMonth(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY_MMMM") ;

		return dateFormat.format(new Date()) ; 
	}
}