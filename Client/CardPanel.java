package Client;
import java.awt.BorderLayout;
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
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import Client.ChatWindow.MessageListener;
import Client.ChatWindow.UsersListener;
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
	
	//Status bar
	StatusBar conStatus;
	
	//Card layout
	private CardLayout card;
	private static CardPanel uniqueInstance;
	
	//Listeners and tasks and timers
	private ErrorListener errListener;
	private Timer errListenerTimer;
	final int errSpeed = 500;
	final int errPause = 500;
	
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
		conStatus = StatusBar.getInstance();
		mainClientFrame.setSize(500, 400);
		mainClientFrame.setLayout(new BorderLayout());
		mainClientFrame.add(this,BorderLayout.NORTH);
		mainClientFrame.add(conStatus,BorderLayout.SOUTH);
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
	
	private void addErrorListenerTimer() {
		errListenerTimer = new Timer(errSpeed, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				runErrorListener();
			}
			
		});
		errListenerTimer.setInitialDelay(errPause);
		errListenerTimer.start(); 
	}
	
	private void runErrorListener() {
		if(errListener == null || errListener.isDone == true){
			errListener = new ErrorListener();
			SwingUtilities.invokeLater(errListener);
		}
	}
	
	private class ErrorListener implements Runnable{
		private boolean isDone;
		
		public ErrorListener() {
			isDone = false;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			isDone = false;
			
			Message error = null;
			while(error == null){
				error = conn.checkErrorQue();
			}
			System.out.println("Error incoming: " + error.getContent().toString());
			
			//translating error conetcnt to the enum type
			String errType = error.getContent();
			ClientErrors err;
			err = err.valueOf(errType);
			
			//ERRROR enumerations will be listed here for every and each error
			switch(err) {
				case WRONG_PASS:
					showError("Wrong pasword, please try again!");
					break;
					
				case USER_ALREADY_LOGGED_IN:
					showError("Users is already loggged in!");
					break;
					
				case USERNAME_TAKEN:
					showError("This username is already taken in  the system!");
					break;
				
				case PASSWORD_REPEAT_WRONG:
					showError("Repeated password does not match with password!");
					break;
				
				case LOST_CONNECTION_TO_SERVER:
					showError("Connection to server has been lost!");
					break;
			}
			//WRONG_USER, USER_ALREADY_LOGGED_IN, USERNAME_TAKEN, PASSWORD_REPEAT_WRONG
			
			//ERRRORS END
			
			
			isDone = true;
		}
		
		private void showError(String message) {
			 JOptionPane.showMessageDialog(mainClientFrame, message, "Dialog", JOptionPane.ERROR_MESSAGE);
		}
	}
	
}
