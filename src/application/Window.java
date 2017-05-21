package application;

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
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Window extends JFrame{
	// GUI Elements
	private JFileChooser jfcChoose;
	
	private InetAddress serverAddress;
	private String message_distant = "";
	private Socket mySocket;
	private BufferedReader buffin;
	private PrintWriter pout;
	private String serverName;
	private String clientName;
	
	public static void main(String[] args) {
		new Window();
	}
	
	private void getResources(){
		ResourceBundle bundle = ResourceBundle.getBundle("application.properties.config");
		serverName = bundle.getString("server.ip");
		clientName = bundle.getString("client.ip");
		
	}
	
	public Window() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setTitle("Client p2p");
		
		getResources();
		
		JPanel pnlTop = new JPanel();
		
		JLabel lblTop = new JLabel("Choose folder to share");
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new BrowseAction());
		
		JButton btnSend = new JButton("Send to server");
		
		pnlTop.add(lblTop, BorderLayout.WEST);
		pnlTop.add(btnBrowse, BorderLayout.EAST);
		pnlTop.add(btnSend, BorderLayout.EAST);
		
		JLabel lblIPAddress = new JLabel(clientName);
		
		add(pnlTop, BorderLayout.NORTH);
		add(lblIPAddress, BorderLayout.SOUTH);
		
		pack();
		setVisible(true);		
	}
	
	public void connectToServer(String srvName, File[] files){		
		try {			
			serverAddress = InetAddress.getByName(srvName);
			System.out.println("Get the address of the server : "+ serverAddress);

			//try to connect to the server
			mySocket = new Socket(serverAddress, 45000);

			// Get an output stream to send data to the server
			pout = new PrintWriter(mySocket.getOutputStream());			
			
			// Send client name to server
			pout.println(clientName);
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
			
			// Send end of transmission message
			pout.println("EOT");
			pout.flush();

			// close connection with server
			pout.close();
			buffin.close();
			mySocket.close();
		}catch (UnknownHostException e) {
			e.printStackTrace();
		}catch (IOException e) {
			System.out.println("server connection error, dying.....");
		}
	}
	
	private class BrowseAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// Generate jfilechooser
			jfcChoose = new JFileChooser(); 
			jfcChoose.setCurrentDirectory(new File("files"));
			jfcChoose.setDialogTitle("Choose a folder to share");
			jfcChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			jfcChoose.setAcceptAllFileFilterUsed(false);
			
			// Get desired folder and file list
			String selectedFolder;			
		    if (jfcChoose.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		    	selectedFolder = jfcChoose.getSelectedFile().toString();
		    	File[] files = new File(selectedFolder).listFiles();
			    
			    //connectToServer(serverName, files);
		    }
		}
		
	}
}