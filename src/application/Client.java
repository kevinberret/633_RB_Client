package application;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents a client which wants to connect to the server.
 * @author Kevin
 *
 */
public class Client implements Serializable {
	/**
	 * Unique unchangable identifier for class serialization
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Unique id for client
	 */
	private String uuid;
	
	/**
	 * Client's ip address
	 */
	private String clientIp;
	
	/**
	 * Files shared by client
	 */
	private ArrayList<String> files;
	
	/**
	 * Client's full constructor
	 * @param uuid Unique client identifier
	 * @param clientIp Client's IP address
	 * @param files Files shared by client
	 */
	public Client(String uuid, String clientIp, ArrayList<String> files) {
		this.uuid = uuid;
		this.clientIp = clientIp;
		this.files = files;
	}
	
	/**
	 * Client's constructor with uuid and client's ip. Files are set to null
	 * @param uuid Unique client identifier
	 * @param clientIp Client's IP address
	 */
	public Client(String uuid, String clientIp) {
		this(uuid, clientIp, null);
	}
	
	/**
	 * Client's constructor only with uuid. Client's ip and files are set to null
	 * @param uuid Unique client identifier
	 */
	public Client(String uuid) {
		this(uuid, null, null);
	}
	
	/**
	 * Client's minimal constructor. Initialize a client with null values
	 */
	public Client() {
		this(null, null, null);
	}

	/**
	 * Returns client's unique id
	 * @return Client's unique ID as String
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Set client's unique id
	 * @param uuid Unique client identifier
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * Returns client's ip address
	 * @return Client's ip address as String
	 */
	public String getClientIp() {
		return clientIp;
	}

	/**
	 * Set client's ip address
	 * @param clientIp Client's ip address
	 */
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	/**
	 * Returns files shared by client
	 * @return Files shared by client as ArrayList of String
	 */
	public ArrayList<String> getFiles() {
		return files;
	}

	/**
	 * This methos allows to set the files shared by the client
	 * @param files
	 */
	public void setFiles(ArrayList<String> files) {
		this.files = files;
	}
	
	@Override
	public String toString() {
		return getClientIp();
	}
}