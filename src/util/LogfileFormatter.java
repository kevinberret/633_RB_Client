package util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This class allow to format a log following a specific schema
 * @author Daniel
 *
 */
public class LogfileFormatter extends Formatter {

	/**
	 * Default constructor.
	 */
	public LogfileFormatter() {
		super() ; 
	}
	
	
	@Override
	public String format(LogRecord record) {
		// Create a StringBuffer to contain the formatted record
		StringBuffer sb = new StringBuffer();
		String timestamp ;
		
		// Get current timestamp
		Util u = new Util();
		timestamp = u.getTimeStamp() ; 
		
		// Format log with timestamp, class name, level and message
		sb.append(timestamp);
		sb.append(" (" + record.getSourceClassName() + "." + record.getSourceMethodName() + ") ");
		sb.append(" [" + record.getLevel().getName() + "] ");
		sb.append(formatMessage(record));
		sb.append("\r\n");

		return sb.toString();	
	}
}