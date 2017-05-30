/**
 * 
 */
package application;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author kb
 *
 */
public class ClientSettings extends Dialog {
	private ClientController cc;
	private JTextField jtfServerName;
	private JTextField jtfServerPort;
	private JTextField jtfClientTimeOut;
	private JTextField jtfClientAsServerPort;
	private ClientWindow frame;

	public ClientSettings(JFrame frame, String title, boolean modal, ClientController cc) {
		super(frame, title, modal);
		this.cc = cc;
		this.frame = (ClientWindow) frame;
		
		
		JPanel pnlCenter = new JPanel();
		JLabel lblServerName = new JLabel("Server IP");
		JLabel lblServerPort = new JLabel("Server port");
		JLabel lblClientTimeOut = new JLabel("Time out");
		JLabel lblClientAsServerPort = new JLabel("Port to share files");
		jtfServerName = new JTextField(cc.getServerName());
		jtfServerPort = new JTextField(cc.getServerPort());
		jtfClientTimeOut = new JTextField(cc.getClientTimeOut());
		jtfClientAsServerPort = new JTextField(cc.getClientAsServerPort());
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new SaveAction());
		
		pnlCenter.setLayout(new GridLayout(4, 2));
		pnlCenter.add(lblServerName);
		pnlCenter.add(jtfServerName);
		pnlCenter.add(lblServerPort);
		pnlCenter.add(jtfServerPort);
		pnlCenter.add(lblClientTimeOut);
		pnlCenter.add(jtfClientTimeOut);
		pnlCenter.add(lblClientAsServerPort);
		pnlCenter.add(jtfClientAsServerPort);
		
		add(pnlCenter, BorderLayout.CENTER);
		add(btnSave, BorderLayout.SOUTH);
		
		addWindowListener(new CloseAction());
		
		pack();
	}
	
	class CloseAction implements WindowListener{

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosing(WindowEvent e) {
			// TODO Auto-generated method stub
			dispose();
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	class SaveAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			cc.setClientAsServerPort(jtfClientAsServerPort.getText());
			cc.setClientTimeOut(jtfClientTimeOut.getText());
			cc.setServerName(jtfServerName.getText());
			cc.setServerPort(jtfServerPort.getText());
			if(cc.saveSettings()){
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
