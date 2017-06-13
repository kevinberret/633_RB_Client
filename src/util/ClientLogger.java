package util;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a custom logger for our application
 * @author Daniel
 *
 */
public class ClientLogger {
	/**
	 * The base logger
	 */
	private static Logger logger;
	
	/**
	 * The util library to get useful functions
	 */
	private static Util u;	
	
	/**
	 * The filepath where logs will be saved
	 */
	private static String filePath = "logfile/";
	
	/**
	 * The log filename
	 */
	private static String fileName;

	/**
	 * The filehandler used when logging
	 */
	private static FileHandler fh;	
	
	
	/**
	 * Default constructor
	 */
	public ClientLogger (){
		initLogger();
	}
	
	/**
	 * This methods inits the custom logger
	 */
	private static void initLogger(){
		// instanciate util object
		u = new Util();
		
		// get the current month
		String date = u.getDateMonth();
		fileName = date+".log";		
		
		// creates the logger
		logger = Logger.getLogger(fileName); 

		try {
	        // if folder for logs doesn't exist, create it so logs will work
			File logFolder = new File (filePath);
	        if (!logFolder.exists() ) {
	        	logFolder.mkdirs();
	        }
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
		
		// prepare filename
		filePath += fileName;
		
		// create the file
		try {
			fh = new FileHandler(filePath, true);
			logger.addHandler(fh);

			// use a custom formatter for logs
			LogfileFormatter myFormatter = new LogfileFormatter();
			fh.setFormatter(myFormatter);
			
			// log the start of logging
			logger.log(Level.INFO, "Logger started");
		} catch (SecurityException e) {
			// log of severe exception
			logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (IOException e) {
			// log of severe exception
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	/**
	 * Get application's custom logger
	 * @return The application's logger
	 */
	public static Logger getLogger(){
		// init the logger if not already
		if(logger == null)
			initLogger();		
		
		return logger;
	}
	
	/**
	 * Allows to correctly close the filehandler and avoid locking issues when closing application
	 */
	public static void closeFileHandler(){
		if(fh != null)
			fh.close();
	}
}