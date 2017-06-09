package application;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.sun.glass.events.KeyEvent;

public class ClientWindow extends JFrame{
	// GUI Elements
	private JFileChooser jfcChoose;
	private JMenuBar jmbMenuBar;
	private JMenu jmFile;
	private JMenu jmEdit;
	private JMenuItem jmiShareFiles;
	private JMenuItem jmiGetClients;
	private JMenuItem jmiSettings;
	private JButton btnGetFiles;
	private JList<String> jlClients;
	private JScrollPane listClientsScroller;
	private JList<String> jlFiles;
	private JComboBox<String> jcbNetworkInterfaces;
	private JButton btnValidate;
	private ClientProgressBar jpbCurrentProgress;
	
	// Application Elements
	private String clientAsServerIP;
	private ArrayList<String> clients;
	private LinkedHashMap<String, Client> clientsList;	
	private ClientController controller;
	private JPanel pnlBottom;
	private ClientModel model;
		
	private void generateGUI(){
		// Don't stop the program when frame is closed		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		// Get system presentation
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		// Set desired size
		setPreferredSize(new Dimension(1024,768));
		
		// Set the frame title
		setTitle("Client p2p");
		
		// Create menu bar and menu items and assign a task for share menu item
		jmbMenuBar = new JMenuBar();
		jmFile = new JMenu("File");
		jmiShareFiles = new JMenuItem("Share", KeyEvent.VK_O);
		jmiShareFiles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		jmiShareFiles.addActionListener(new ShareFilesAction());
		jmiShareFiles.setEnabled(false);
		jmiGetClients = new JMenuItem("Get clients", KeyEvent.VK_G);
		jmiGetClients.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		jmiGetClients.addActionListener(new GetClientAction());
		jmiGetClients.setEnabled(false);
		jmFile.add(jmiShareFiles);
		jmFile.add(jmiGetClients);
		jmEdit = new JMenu("Edit");
		jmiSettings = new JMenuItem("Settings", KeyEvent.VK_S);
		jmiSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		jmiSettings.addActionListener(new SettingsAction(this));
		jmEdit.add(jmiSettings);		
		jmbMenuBar.add(jmFile);
		jmbMenuBar.add(jmEdit);
		setJMenuBar(jmbMenuBar);		
		
		// Display client ip address
		JPanel pnlTop = new JPanel(new BorderLayout());
		pnlTop.setBorder(new EmptyBorder(10, 10, 10, 10));
		JLabel lblIPAddress = new JLabel("Select your network interface...");		
		jcbNetworkInterfaces = new JComboBox<String>();
		btnValidate = new JButton("Validate");
		btnValidate.addActionListener(new ChooseIPAction());
		
		pnlTop.add(lblIPAddress, BorderLayout.WEST);
		pnlTop.add(jcbNetworkInterfaces, BorderLayout.CENTER);
		pnlTop.add(btnValidate, BorderLayout.EAST);
		
		for (String netint : controller.getNetworkInterfaces()) {
			jcbNetworkInterfaces.addItem(netint);
		}
		
		// Create progress bars
		jpbCurrentProgress = new ClientProgressBar(model);
		JPanel pnlCurrent = new JPanel();
		JLabel lblCurrentProgress = new ClientFileName(model);
		pnlCurrent.add(lblCurrentProgress, BorderLayout.WEST);
		pnlCurrent.add(jpbCurrentProgress, BorderLayout.SOUTH);		
		
		// Add button to get files from other client
		btnGetFiles = new JButton("Get files");
		btnGetFiles.addActionListener(new GetFiles());
		btnGetFiles.setEnabled(false);
		pnlBottom = new JPanel();
		pnlBottom.add(btnGetFiles, BorderLayout.WEST);
		pnlBottom.add(pnlCurrent, BorderLayout.SOUTH);
		pnlBottom.setVisible(false);
		
		// Add elements to the frame
		add(pnlTop, BorderLayout.NORTH);
		//add(btnGetFiles, BorderLayout.EAST);
		add(pnlBottom, BorderLayout.SOUTH);
				
		// add closing listener
		addWindowListener(new CloseWindow(this, controller));
		
		pack();
	}	
	
	public ClientWindow(ClientController controller, ClientModel model) {		
		this.controller = controller;
		this.model = model;
		
		// Generate the gui
		generateGUI();
	}
	
	public void showErrorDialog(String title, String error, int type){
		JOptionPane.showMessageDialog(this, error, title, type);
	}
	
	private class SettingsAction implements ActionListener{
		private JFrame frame;		
		
		public SettingsAction(JFrame frame) {
			this.frame = frame;
			
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Dialog modal = new ClientSettings(frame, "Settings", true, controller);
			modal.setVisible(true);
		}		
	}
	
	private class CloseWindow extends WindowAdapter{
		private JFrame frame;
		private ClientController cc;
		
		public CloseWindow(JFrame frame, ClientController cc) {
			this.frame = frame;
			this.cc = cc;
		}
		
		@Override
	    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
	        if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to close this window?", "Close the window", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
	        	// close the sockets
	            cc.closeConnections();
	        	
	            // quit the application
	            setDefaultCloseOperation(EXIT_ON_CLOSE);
	        }
	    }
	}
	
	private class ChooseIPAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// Set IP address choosed by user in the controller
			controller.setClientIp(jcbNetworkInterfaces.getSelectedItem().toString());
			
			// Modify the gui
			btnValidate.setEnabled(false);
			jcbNetworkInterfaces.setEnabled(false);
			jmiShareFiles.setEnabled(true);
		}		
	}

	private class ShareFilesAction implements ActionListener{
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
		    	if(controller.selectFolder(selectedFolder))
		    		jmiGetClients.setEnabled(true);
		    	else
		    		showErrorDialog("Error", "Error while connecting to the server...", JOptionPane.ERROR_MESSAGE);		    	
		    }
		}		
	}
	
	private class GetFiles implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(!controller.getFiles(clientAsServerIP, jlFiles.getSelectedValuesList()))
				showErrorDialog("Error", "Error while connecting to the server...", JOptionPane.ERROR_MESSAGE);
		}		
	}
	
	private class GetClientAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// Get all the clients
			clientsList = controller.getClientsList();
			System.out.println("ok");
			// Create the model for the jlist
			Vector model;
			
			// Create the jlist or modify its model
			if(jlClients != null){
				model = (Vector)jlClients.getModel();
				model.clear();
			}else{
				model = new Vector();
				jlClients = new JList(model);
				
				jlClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				jlClients.setLayoutOrientation(JList.VERTICAL);
				jlClients.setVisibleRowCount(-1);
				jlClients.addListSelectionListener(new SelectClient());
				
				listClientsScroller = new JScrollPane(jlClients);
				listClientsScroller.setPreferredSize(new Dimension(250, 80));
				
				add(listClientsScroller, BorderLayout.WEST);
			}
			
			// Ajout des clients au modele (tous les autres que nous)
			for (String key : clientsList.keySet())
			{			    
			    if(!key.equals(controller.getThisClient().getUuid())){
			    	Client tmpClient = clientsList.get(key);
			    	model.addElement(new Element(tmpClient.getUuid(), tmpClient.getClientIp()));
			    }
			    
			}
			
			revalidate();
			
			if(!pnlBottom.isVisible())
				pnlBottom.setVisible(true);
		}	
	}
	
	private class SelectClient implements ListSelectionListener {
		private JScrollPane listFilesScroller;
		
	    public void valueChanged(ListSelectionEvent e) {
	    	 if (e.getValueIsAdjusting()){
	 			
	    		// Get the jlist from which the command came
				JList source = (JList)e.getSource();
		        Element elmt = (Element)source.getSelectedValue();		        
				
				Client client = controller.getClientByUuid(elmt.getUuid());
				clientAsServerIP = client.getClientIp();
				
				System.out.println(client.getClientIp() + " - " + client.getUuid());
				
				DefaultListModel<String> model;
				
				// Create the jlist or modify its model
				if(jlFiles != null){
					model = (DefaultListModel<String>)jlFiles.getModel();
					model.clear();
				}else{
					model = new DefaultListModel<String>();
					jlFiles = new JList(model);				
					btnGetFiles.setEnabled(true);
		 			
					jlFiles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
					jlFiles.setLayoutOrientation(JList.VERTICAL);
					jlFiles.setVisibleRowCount(-1);
					
					listFilesScroller = new JScrollPane(jlFiles);
					listFilesScroller.setPreferredSize(new Dimension(250, 80));				
					add(listFilesScroller, BorderLayout.CENTER);
				}
				
				// Begin at index 1 to get only files and not ip address
				ArrayList<String> files = client.getFiles();
				for (String file : files) {
					System.out.println(file);
				    model.addElement(file);
				}			
				
				revalidate();
	         }
	    }
	}
	
	private class Element{
		private String uuid;
		private String ip;
		
		public Element(String uuid, String ip){
			this.uuid = uuid;
			this.ip = ip;
		}
		
		public String getUuid() {
			return uuid;
		}
		
		public void setUuid(String uuid) {
			this.uuid = uuid;
		}
		
		public String getIp() {
			return ip;
		}
		
		public void setIp(String ip) {
			this.ip = ip;
		}
		
		@Override
		public String toString() {
			return getIp();
		}
	}
}