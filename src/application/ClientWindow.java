package application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.NetworkInterface;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
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
	private JScrollPane listClientsScroller;
	private JList<String> jlFiles;
	
	private String clientAsServerIP;
	private ArrayList<String> clients;
	private ArrayList<Object> clientsList;
	
	private ClientController controller;
	private JComboBox<String> jcbNetworkInterfaces;
	private JButton btnValidate;
	
	private void generateGUI(){
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// Set desired size
		setPreferredSize(new Dimension(1024,768));
		
		// Set the frame title
		setTitle("Client p2p");
		
		// Create menu bar and menu items and assign a task for share menu item
		jmbMenuBar = new JMenuBar();
		jmFile = new JMenu("File");
		jmiShareFiles = new JMenuItem("Share");
		jmiShareFiles.addActionListener(new ShareFilesAction());
		jmiShareFiles.setEnabled(false);
		jmiGetClients = new JMenuItem("Get clients");
		jmiGetClients.addActionListener(new GetClientAction());
		jmiGetClients.setEnabled(false);
		jmFile.add(jmiShareFiles);
		jmFile.add(jmiGetClients);
		jmbMenuBar.add(jmFile);
		setJMenuBar(jmbMenuBar);		
		
		// Display client ip address
		JPanel pnlBottom = new JPanel(new BorderLayout());
		JLabel lblIPAddress = new JLabel("Select your network interface...");		
		jcbNetworkInterfaces = new JComboBox<String>();
		btnValidate = new JButton("Validate");
		btnValidate.addActionListener(new ChooseIPAction());
		
		pnlBottom.add(lblIPAddress, BorderLayout.WEST);
		pnlBottom.add(jcbNetworkInterfaces, BorderLayout.CENTER);
		pnlBottom.add(btnValidate, BorderLayout.EAST);
		
		for (String netint : controller.getNetworkInterfaces()) {
			jcbNetworkInterfaces.addItem(netint);
		}
		
		// Add button to get files from other client
		btnGetFiles = new JButton("Get files");
		btnGetFiles.addActionListener(new GetFiles());
		btnGetFiles.setEnabled(false);
		
		// Add elements to the frame
		add(pnlBottom, BorderLayout.SOUTH);
		add(btnGetFiles, BorderLayout.EAST);
		
		pack();
	}
	
	public ClientWindow(ClientController controller) {		
		this.controller = controller;
		
		// Generate the gui
		generateGUI();				
	}
	
	private void showErrorDialog(String title, String error, int type){
		JOptionPane.showMessageDialog(this, error, title, type);
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
			System.out.println("get files button clicked");
			if(!controller.getFiles(clientAsServerIP, jlFiles.getSelectedValuesList()))
				showErrorDialog("Error", "Error while connecting to the server...", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	private class GetClientAction implements ActionListener{	

		@Override
		public void actionPerformed(ActionEvent e) {
			// Get all the clients
			clientsList = controller.getClientsList();
			
			// Create the model for the jlist
			DefaultListModel<String> model;
			
			// Create the jlist or modify its model
			if(jlClients != null){
				model = (DefaultListModel<String>)jlClients.getModel();
				model.clear();
			}else{
				model = new DefaultListModel<String>();
				jlClients = new JList(model);
				
				jlClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				jlClients.setLayoutOrientation(JList.VERTICAL);
				jlClients.setVisibleRowCount(-1);
				jlClients.addListSelectionListener(new SelectClient());
				
				listClientsScroller = new JScrollPane(jlClients);
				listClientsScroller.setPreferredSize(new Dimension(250, 80));
				
				add(listClientsScroller, BorderLayout.WEST);
			}
			
			// Add clients to the model
			for(int i = 0 ; i < clientsList.size()  ; i++){
				String client = ((ArrayList<String>)clientsList.get(i)).get(0);
				//if(!client.equals(controller.getClientName()))
					model.addElement(client);
			}
			
			revalidate();
		}		
	}
	
	class SelectClient implements ListSelectionListener {
		private JScrollPane listFilesScroller;
		
	    public void valueChanged(ListSelectionEvent e) {
	    	 if (e.getValueIsAdjusting()){
	    		// Get the jlist from which the command came
				JList source = (JList)e.getSource();
				ArrayList<String> client = (ArrayList<String>)clientsList.get(source.getSelectedIndex());
				clientAsServerIP = source.getSelectedValue().toString();
				
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
				for (int i = 1; i < client.size(); i++) {
				    model.addElement(client.get(i));
				}				
				
				revalidate();
	         }
	    }
	}
}