package application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ClientWindow extends JFrame{
	// GUI Elements
	private JFileChooser jfcChoose;
	private JMenuBar jmbMenuBar;
	private JMenu jmFile;
	private JMenuItem jmiShareFiles;
	private JMenuItem jmiGetClients;
	private JButton btnGetFiles;
	private JList<String> jlClients;
	private JList<String> jlFiles;
	
	private String client;
	private ArrayList<String> clients;
	private ArrayList<Object> clientsList;
	
	private ClientController controller;
	
	private void generateGUI(){
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// Set the frame title
		setTitle("Client p2p");
		
		// Create menu bar and menu items and assign a task for share menu item
		jmbMenuBar = new JMenuBar();
		jmFile = new JMenu("File");
		jmiShareFiles = new JMenuItem("Share");
		jmiShareFiles.addActionListener(new BrowseAction());
		jmiGetClients = new JMenuItem("Get clients");
		jmiGetClients.addActionListener(new GetClientAction());
		jmFile.add(jmiShareFiles);
		jmFile.add(jmiGetClients);
		jmbMenuBar.add(jmFile);
		setJMenuBar(jmbMenuBar);		
		
		// Display client ip address
		JLabel lblIPAddress = new JLabel(controller.getClientName());
		
		// Add button to get files from other client
		btnGetFiles = new JButton("Get files");
		btnGetFiles.addActionListener(new GetFiles());
		btnGetFiles.setEnabled(false);
		
		// Add elements to the frame
		add(lblIPAddress, BorderLayout.SOUTH);
		add(btnGetFiles, BorderLayout.EAST);
		
		pack();
	}
	
	public ClientWindow(ClientController controller) {		
		this.controller = controller;
		
		// Generate the gui
		generateGUI();				
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
		    	if(controller.selectFolder(selectedFolder))
		    		System.out.println("connection ok");
		    	else{
		    		System.out.println("connection ko");
		    	}
		    }
		}		
	}
	
	private class GetFiles implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("get files button clicked");
			controller.getFiles(client, jlFiles.getSelectedValuesList());
		}
		
	}
	
	private class GetClientAction implements ActionListener{		

		@Override
		public void actionPerformed(ActionEvent e) {
			clientsList = controller.getClientsList();
			clients = new ArrayList<String>();
			
			// Create the list
			if(clientsList != null){
				for(int i = 0 ; i < clientsList.size()  ; i++){
					String client = ((ArrayList<String>)clientsList.get(i)).get(0);
					//if(!client.equals(controller.getClientName()))
						clients.add(client);
				}
				
				jlClients = new JList(clients.toArray());
			}
			else
				jlClients = new JList();
			
			jlClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jlClients.setLayoutOrientation(JList.VERTICAL);
			jlClients.setVisibleRowCount(-1);
			jlClients.addListSelectionListener(new SelectClient());
			JScrollPane listScroller = new JScrollPane(jlClients);
			listScroller.setPreferredSize(new Dimension(250, 80));
			
			add(listScroller, BorderLayout.WEST);
		}		
	}
	
	class SelectClient implements ListSelectionListener {
		private ArrayList<String> files = new ArrayList<String>();
		
	    public void valueChanged(ListSelectionEvent e) {
	    	 if (!e.getValueIsAdjusting()){
				JList source = (JList)e.getSource();
				source.getSelectedValue().toString();
				ArrayList<String> client = (ArrayList<String>)clientsList.get(source.getSelectedIndex());
				
				for (int i = 1; i < client.size(); i++) {
					files.add(client.get(i));
				}
				
				if(files != null){
					jlFiles = new JList(files.toArray());
					btnGetFiles.setEnabled(true);
				}
				else
					jlFiles = new JList();
	 			
				jlFiles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				jlFiles.setLayoutOrientation(JList.VERTICAL);
				jlFiles.setVisibleRowCount(-1);
				JScrollPane listFilesScroller = new JScrollPane(jlFiles);
				listFilesScroller.setPreferredSize(new Dimension(250, 80));
				
				add(listFilesScroller, BorderLayout.CENTER);
	         }
	    }
	}
}