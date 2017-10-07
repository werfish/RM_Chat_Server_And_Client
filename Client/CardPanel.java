package Client;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Common.Credentials;
import Common.Message;
import Common.MessageType;
import Common.User;


public class CardPanel extends JPanel implements ActionListener{
	//Configuration variables
	JFrame mainClientFrame = new JFrame(Client.APPNAME);
	
	//A panel which will hold the login panel
	JPanel loginScreenPanel = new LoginScreen();
	
	//Connection handler singleton
	ConnectionHandler conn;
	
	//Card layout
	private CardLayout card;
	private static CardPanel uniqueInstance;
	
	private CardPanel(){
		setLayout(new CardLayout());
		add(loginScreenPanel, "login");
		card = (CardLayout) getLayout();
		setFrame();
		addCloseAdapter();
		conn = ConnectionHandler.getInstance();
	}
	
	public static CardPanel getInstance(){
		  if(uniqueInstance == null){
			  synchronized (CardPanel.class){
				  uniqueInstance = new CardPanel();
			  }
		  }
		  return uniqueInstance;
	}
	
	public CardLayout getCardLayout() {
		return this.card;
	}

	private void setFrame(){
		mainClientFrame.setSize(500, 400);
		mainClientFrame.add(this);
		mainClientFrame.setVisible(true);
		mainClientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	//important exit logoutStuff
	private void addCloseAdapter() {
		mainClientFrame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(conn.isLoggedIn() == true){
					conn.sendRequest(new Message("content",new User("App"),MessageType.LOGOUT));
					System.out.println("Client is logging out..............");
				}
				conn.sendRequest(new Message("content",new User("App"),MessageType.DISCONNECT));
				System.out.println("Client is disconnecting..............");
				System.exit(1);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
