package app;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Window extends JFrame  implements ActionListener{
	// GUI Elements
	private JButton btnBrowse;
	private JLabel lblIPAddress;
	private JFileChooser jfcChoose;
	private JTextField jtfServerIp;
	
	private InetAddress serverAddress;
	private String message_distant = "";
	private Socket mySocket;
	private BufferedReader buffin;
	private PrintWriter pout;
	private String serverName = "192.168.108.10";
	private InetAddress localAddress = null;
	private String defaultFolder = "C:\temp\633-2";
	
	public static void main(String[] args) {
		new Window();
	}
	
	public Window() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setTitle("Client p2p");
		
		String computerNameAndIP = "";
		
		try {
			computerNameAndIP = String.format("%s - %s", InetAddress.getLocalHost().getHostName(), InetAddress.getLocalHost().getHostAddress());
			lblIPAddress = new JLabel(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();		
		}
		
		lblIPAddress = new JLabel(computerNameAndIP);
		
		btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(this);
		
		jtfServerIp = new JTextField();
		
		add(btnBrowse, BorderLayout.CENTER);
		add(lblIPAddress, BorderLayout.SOUTH);
		
		pack();
		setVisible(true);
		
		getIpAddresses();
	}
	
	private void getIpAddresses(){
		try {
			// Send IP address
			//Create the LocalAdress object using localhost
			Enumeration<NetworkInterface> inetAddresses = NetworkInterface.getNetworkInterfaces();
			
			while(inetAddresses.hasMoreElements())
			{
			    NetworkInterface ni = inetAddresses.nextElement();
			    Enumeration<InetAddress> addresses =  ni.getInetAddresses();
			    while(addresses.hasMoreElements()) {
			    	InetAddress ia = addresses.nextElement();			    
		    		if(!ia.isLinkLocalAddress()) {
	    				if(!ia.isLoopbackAddress()) {
	    					System.out.println(ni.getName() + " " + ia.getHostAddress());
    					}
			        }
			    }
			}
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
	}
	
	public void actionPerformed(ActionEvent e){
		jfcChoose = new JFileChooser(); 
		jfcChoose.setCurrentDirectory(new File("files"));
		jfcChoose.setDialogTitle("Choose a folder to share");
		jfcChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    //
	    // disable the "All files" option.
	    //
		jfcChoose.setAcceptAllFileFilterUsed(false);
	    //
		
		String selectedFolder;
		
	    if (jfcChoose.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
	    	selectedFolder = jfcChoose.getSelectedFile().toString();
	    	
	    	File[] files = new File(selectedFolder).listFiles();
		    
		    connectToServer(serverName, files);
	    }
	}
	
	public void connectToServer(String srvName, File[] files){		
		try {			
			serverAddress = InetAddress.getByName(srvName);
			System.out.println("Get the address of the server : "+ serverAddress);

			//try to connect to the server
			mySocket = new Socket(serverAddress, 45000);

			// Get an output stream to send data to the server
			pout = new PrintWriter(mySocket.getOutputStream());			
			
			pout.println("192.168.1.100");
			pout.flush();
			
			// TODO: get server confirmation
			
			// Send list of all available files
			for (File file : files) {
				if(!file.isDirectory()){
					System.out.println(file.getName());
					pout.println(file.getName());
					pout.flush();
				}
			}
			
			pout.println("EOT");
			pout.flush();

			//send back the message to the server to kill it
			pout.close();
			buffin.close();
			mySocket.close();

			System.out.println("\nTerminate program...");
		}catch (UnknownHostException e) {
			e.printStackTrace();
		}catch (IOException e) {
			System.out.println("server connection error, dying.....");
		}
	}
}