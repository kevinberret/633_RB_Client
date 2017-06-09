package application;

import java.io.Serializable;
import java.util.ArrayList;

public class Client implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String uuid;
	private String clientIp;
	private ArrayList<String> files;
	
	public Client(String uuid, String clientIp, ArrayList<String> files) {
		this.uuid = uuid;
		this.clientIp = clientIp;
		this.files = files;
	}
	
	public Client(String uuid, String clientIp) {
		this(uuid, clientIp, null);
	}
	
	public Client(String uuid) {
		this(uuid, null, null);
	}
	
	public Client() {
		this(null, null, null);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public ArrayList<String> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<String> files) {
		this.files = files;
	}
}