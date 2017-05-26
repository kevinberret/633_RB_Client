package application;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class ClientWindow extends JFrame{
	// GUI Elements
	private JFileChooser jfcChoose;
	private JMenuBar jmbMenuBar;
	private JMenu jmFile;
	private JMenuItem jmiShareFiles;
	
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
		jmFile.add(jmiShareFiles);
		jmbMenuBar.add(jmFile);
		setJMenuBar(jmbMenuBar);		
		
		// Display client ip address
		JLabel lblIPAddress = new JLabel(controller.getClientName());
		
		// Add elements to the frame
		add(lblIPAddress, BorderLayout.SOUTH);
		
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
}