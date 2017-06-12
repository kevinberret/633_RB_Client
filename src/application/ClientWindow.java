package application;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This class is the main view of our application.
 * @author Kevin
 *
 */
public class ClientWindow extends JFrame{
	/**
	 * This file chooser is used when you have to choose which folder you want to share
	 */
	private JFileChooser jfcChoose;
	
	/**
	 * Top menu bar
	 */
	private JMenuBar jmbMenuBar;
	
	/**
	 * File menu
	 */
	private JMenu jmFile;
	
	/**
	 * Edit menu
	 */
	private JMenu jmEdit;
	
	/**
	 * Share files menu item
	 */
	private JMenuItem jmiShareFiles;
	
	/**
	 * Get clients menu item
	 */
	private JMenuItem jmiGetClients;
	
	/**
	 * Settings menu item
	 */
	private JMenuItem jmiSettings;
	
	/**
	 * Download files button
	 */
	private JButton btnDownloadFiles;
	
	/**
	 * List of all clients
	 */
	private JList<String> jlClients;
	
	/**
	 * Model for list of all clients
	 */
	private Vector<Element> clientsListModel;
	
	/**
	 * Scroller if a lot a clients are available
	 */
	private JScrollPane listClientsScroller;
	
	/**
	 * List of available files
	 */
	private JList<String> jlFiles;
	
	/**
	 * Model for list of available files
	 */
	private DefaultListModel<String> filesModel;
	
	/**
	 * Progressbar when download files
	 */
	private ClientProgressBar jpbCurrentProgress;
	
	/**
	 * IP when client is used as server
	 */
	private String clientAsServerIP;
	
	/**
	 * All clients list
	 */
	private LinkedHashMap<String, Client> clientsList;
	
	/**
	 * Controller that makes the link between view and model
	 */
	private ClientController controller;
	
	/**
	 * Panel used for progessbar, clientfilename and download button 
	 */
	private JPanel pnlBottom;
	
	/**
	 * Model that contains data used for observers
	 */
	private ClientModel model;
	
	
	/**
	 * This method allows to generate the GUI
	 */
	private void generateGUI(){
		// application doesn't terminate on jframe close
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		// Get default's os display style
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
		
		// set properties
		setPreferredSize(new Dimension(1024,768));
		setTitle("Client p2p");
		
		// create menubar, menu, menuitems and mnemonics
		jmbMenuBar = new JMenuBar();
		jmFile = new JMenu("File");
		jmiShareFiles = new JMenuItem("Share", KeyEvent.VK_O);
		jmiShareFiles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		jmiShareFiles.addActionListener(new ShareFilesAction());
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
		
		// create progress bars
		jpbCurrentProgress = new ClientProgressBar(model);
		JPanel pnlCurrent = new JPanel();
		JLabel lblCurrentProgress = new ClientFileName(model);
		pnlCurrent.add(lblCurrentProgress, BorderLayout.WEST);
		pnlCurrent.add(jpbCurrentProgress, BorderLayout.SOUTH);		
		
		// add button to download files
		btnDownloadFiles = new JButton("Download files");
		btnDownloadFiles.addActionListener(new GetFiles());
		btnDownloadFiles.setEnabled(false);
		pnlBottom = new JPanel();
		pnlBottom.add(btnDownloadFiles, BorderLayout.WEST);
		pnlBottom.add(pnlCurrent, BorderLayout.SOUTH);
		pnlBottom.setVisible(false);
		
		// add all panels to frame
		add(pnlBottom, BorderLayout.SOUTH);
				
		// add listener to closing window (to ask question do you really want to quit?)
		addWindowListener(new CloseWindow(this, controller));
		
		pack();
	}	
	
	/**
	 * Default constructor
	 * @param controller The controller
	 * @param model The model
	 */
	public ClientWindow(ClientController controller, ClientModel model) {		
		this.controller = controller;
		this.model = model;
		
		// GUI generation
		generateGUI();
	}
	
	/**
	 * This method allows to display a dialog with questions or not
	 * @param title The dialog title
	 * @param message The  message
	 * @param type The message type
	 */
	public void showErrorDialog(String title, String message, int type){
		JOptionPane.showMessageDialog(this, message, title, type);
	}
	
	/**
	 * This inner class allows to open the settings modal when called
	 * @author Kevin
	 *
	 */
	private class SettingsAction implements ActionListener{
		/**
		 * The frame to which the dialog will be attached
		 */
		private JFrame frame;		
		
		/**
		 * Default constructor
		 * @param frame The frame to which the dialog will be attached
		 */
		public SettingsAction(JFrame frame) {
			this.frame = frame;
			
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// Open the dialog
			Dialog modal = new ClientSettings(frame, "Settings", true, controller);
			modal.setVisible(true);
		}		
	}
	
	/**
	 * This inner class allows to deal with closing window
	 * @author Kevin
	 *
	 */
	private class CloseWindow extends WindowAdapter{
		/**
		 * The frame to which the dialog will be attached
		 */
		private JFrame frame;
		
		/**
		 * The controller that links our view and model
		 */
		private ClientController controller;
		
		/**
		 * Default constructor
		 * @param frame The frame to which the dialog will be attached
		 * @param controller The controller that links our view and model
		 */
		public CloseWindow(JFrame frame, ClientController controller) {
			this.frame = frame;
			this.controller = controller;
		}
		
		@Override
	    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
	        if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to close this window?", "Close the window", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
	        	// close sockets
	            controller.closeConnections();
	        	
	            // terminate application
	            setDefaultCloseOperation(EXIT_ON_CLOSE);
	        }
	    }
	}

	/**
	 * This inner class allows to choose the folder to share
	 * @author Kevin
	 *
	 */
	private class ShareFilesAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// Generate jfilechooser
			jfcChoose = new JFileChooser(); 
			jfcChoose.setCurrentDirectory(new File("files"));
			jfcChoose.setDialogTitle("Choose a folder to share");
			jfcChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			jfcChoose.setAcceptAllFileFilterUsed(false);
			
			// Get the desired folder and set it in controller. display an error message if impossible to share the folder
			String selectedFolder;			
		    if (jfcChoose.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		    	selectedFolder = jfcChoose.getSelectedFile().toString();
		    	if(controller.selectFolder(selectedFolder))
		    		jmiGetClients.setEnabled(true);
		    	else
		    		showErrorDialog("Error", "Error while connecting to the server to share files...", JOptionPane.ERROR_MESSAGE);		    	
		    }
		}		
	}
	
	/**
	 * This inner class allows to download the chosen files
	 * @author Kevin
	 *
	 */
	private class GetFiles implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(!controller.getFiles(clientAsServerIP, jlFiles.getSelectedValuesList()))
				showErrorDialog("Error", "Error while connecting to the server to download files...", JOptionPane.ERROR_MESSAGE);
		}		
	}
	
	/**
	 * This inner class allows to get the clients list
	 * @author Kevin
	 *
	 */
	private class GetClientAction implements ActionListener{		

		@Override
		public void actionPerformed(ActionEvent e) {
			// Get all clients
			clientsList = controller.getClientsList();
			
			// Creates the list or modify it
			if(jlClients != null){
				clientsListModel.clear();
				filesModel.clear();
				jlClients.clearSelection();
				jlFiles.clearSelection();
			}else{
				clientsListModel = new Vector<Element>();
				jlClients = new JList(clientsListModel);
				
				jlClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				jlClients.setLayoutOrientation(JList.VERTICAL);
				jlClients.setVisibleRowCount(-1);
				jlClients.addListSelectionListener(new SelectClient());
				
				listClientsScroller = new JScrollPane(jlClients);
				listClientsScroller.setPreferredSize(new Dimension(250, 80));
				
				add(listClientsScroller, BorderLayout.WEST);
			}
			
			// Add clients to the model (all except us)
			for (String key : clientsList.keySet())
			{			    
			    Client tmpClient = clientsList.get(key);
		    	clientsListModel.addElement(new Element(tmpClient.getUuid(), tmpClient.getClientIp()));			    
			}
			
			revalidate();
			
			// make visible the panel to download files
			if(!pnlBottom.isVisible())
				pnlBottom.setVisible(true);
		}	
	}
	
	/**
	 * This class allows to select a client and get his files
	 * @author Kevin
	 *
	 */
	private class SelectClient implements ListSelectionListener {
		private JScrollPane listFilesScroller;		
		
	    public void valueChanged(ListSelectionEvent e) {
	    	 if (e.getValueIsAdjusting()){
	 			
	    		// Get the source list
				JList source = (JList)e.getSource();
		        Element elmt = (Element)source.getSelectedValue();		        
				
				Client client = controller.getClientByUuid(elmt.getUuid());
				clientAsServerIP = client.getClientIp();
				
				// Create or modify the list
				if(jlFiles != null){
					filesModel.clear();
				}else{
					filesModel = new DefaultListModel<String>();
					jlFiles = new JList(filesModel);				
					btnDownloadFiles.setEnabled(true);
		 			
					jlFiles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
					jlFiles.setLayoutOrientation(JList.VERTICAL);
					jlFiles.setVisibleRowCount(-1);
					
					listFilesScroller = new JScrollPane(jlFiles);
					listFilesScroller.setPreferredSize(new Dimension(250, 80));				
					add(listFilesScroller, BorderLayout.CENTER);
				}
				
				// Add all files to the list's model
				ArrayList<String> files = client.getFiles();
				for (String file : files) {
				    filesModel.addElement(file);
				}			
				
				revalidate();
	         }
	    }
	}
	
	/**
	 * This class allows to display the ip address of clients and bind it with the correct client's uuid
	 * @author student
	 *
	 */
	private class Element{
		/**
		 * client's uuid
		 */
		private String uuid;
		
		/**
		 * client's ip address
		 */
		private String ip;
		
		/**
		 * default constructor
		 * @param uuid the client's unique id
		 * @param ip the client's ip address
		 */
		public Element(String uuid, String ip){
			this.uuid = uuid;
			this.ip = ip;
		}
		
		/**
		 * Get client's uuid
		 * @return client's uuid
		 */
		public String getUuid() {
			return uuid;
		}
		
		/**
		 * Sets client's uuid
		 * @param uuid The client's uuid
		 */
		public void setUuid(String uuid) {
			this.uuid = uuid;
		}
		
		/**
		 * Get client's ip address
		 * @return client's ip address
		 */
		public String getIp() {
			return ip;
		}
		
		/**
		 * Sets the client's ip address
		 * @param ip The client's ip address
		 */
		public void setIp(String ip) {
			this.ip = ip;
		}
		
		@Override
		public String toString() {
			return getIp();
		}
	}
}