/**
 * 
 */
package application;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * This class represents a dialog that allows to set the settings for the application
 * @author Kevin
 *
 */
public class ClientSettings extends Dialog {
	/**
	 * The controller that allow to get and set settings
	 */
	private ClientController controller;
	
	/**
	 * The textfield for server ip address
	 */
	private JTextField jtfServerIP;
	
	/**
	 * The textfield for server port
	 */
	private JTextField jtfServerPort;
	
	/**
	 * The textfield for timeout
	 */
	private JTextField jtfClientTimeOut;
	
	/** 
	 * The textfield for port when client has to send files
	 */
	private JTextField jtfClientAsServerPort;
	
	/**
	 * The main frame to which the dialog is related
	 */
	private ClientWindow frame;

	/**
	 * Default constructor
	 * @param frame The main frame to which the dialog is related
	 * @param title the title of the dialog or null if this dialog has no title
	 * @param modal specifies whether dialog blocks user input to other top-level windows when shown. If false, the dialog is MODELESS; if true, the modality type property is set to DEFAULT_MODALITY_TYPE
	 * @param controler The client controller 
	 */
	public ClientSettings(JFrame frame, String title, boolean modal, ClientController controler) {
		super(frame, title, modal);
		this.controller = controler;
		this.frame = (ClientWindow) frame;	
		
		// generate gui
		generateGUI();
	}
	
	private void generateGUI(){
		// Generate gui
		JPanel pnlCenter = new JPanel();
		pnlCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
		JLabel lblServerName = new JLabel("Server IP");
		JLabel lblServerPort = new JLabel("Server port");
		JLabel lblClientTimeOut = new JLabel("Time out");
		JLabel lblClientAsServerPort = new JLabel("Port to share files");
		jtfServerIP = new JTextField(controller.getServerIP());
		jtfServerPort = new JTextField(controller.getServerPort());
		jtfClientTimeOut = new JTextField(controller.getClientTimeOut());
		jtfClientAsServerPort = new JTextField(controller.getClientAsServerPort());
		
		JPanel pnlBottom = new JPanel();
		pnlBottom.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new SaveAction());		
		pnlBottom.add(btnSave);
		
		pnlCenter.setLayout(new GridLayout(4, 2));
		pnlCenter.add(lblServerName);
		pnlCenter.add(jtfServerIP);
		pnlCenter.add(lblServerPort);
		pnlCenter.add(jtfServerPort);
		pnlCenter.add(lblClientTimeOut);
		pnlCenter.add(jtfClientTimeOut);
		pnlCenter.add(lblClientAsServerPort);
		pnlCenter.add(jtfClientAsServerPort);
		
		add(pnlCenter, BorderLayout.CENTER);
		add(pnlBottom, BorderLayout.SOUTH);
		
		addWindowListener(new CloseAction());
		
		pack();
	}
	
	/**
	 * This inner class is used to modify the behaviour of the dialog when it's closed
	 * @author Kevin
	 *
	 */
	class CloseAction extends WindowAdapter{
		@Override
		public void windowClosing(WindowEvent e) {
			// close dialog
			dispose();
		}		
	}

	/**
	 * This inner class saves the settings entered by the user
	 * @author Kevin
	 *
	 */
	class SaveAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// Get settings in inputs and set their values in controller
			controller.setClientAsServerPort(jtfClientAsServerPort.getText());
			controller.setClientTimeOut(jtfClientTimeOut.getText());
			controller.setServerIP(jtfServerIP.getText());
			controller.setServerPort(jtfServerPort.getText());
			
			// Try to save settings and display a dialog if success or if error
			if(controller.saveSettings()){
				dispose();
				frame.showErrorDialog("Saved", "Settings saved successfully", JOptionPane.INFORMATION_MESSAGE);
			}
			else{
				dispose();
				frame.showErrorDialog("Error", "Settings couldn't be saved....", JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
}