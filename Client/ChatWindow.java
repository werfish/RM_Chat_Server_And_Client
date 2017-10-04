package Client;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import Common.Commands;
import Common.Message;
import Common.MessageType;
import Common.User;
import Common.Users;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatWindow extends JPanel implements ActionListener {
	
/*-------------------CONSTANTS-----------------------------
*The below ChatWindow class is the main UI of the aplication
*It consists of three JTextFields, one for Input and one for showing
*chat messages and last one for showing participants.
*One JButton will be responsible for Sending the message to server
*/
	
	//GUI Variables
	//It should now display all elements, remember about changing the name of rightPanel to left and the other way around in eclipse
	final JPanel chatPanel = new JPanel();
	final JPanel leftPanel = new JPanel();
	final JPanel rightPanel = new JPanel();
	final JPanel usersListPanel = new JPanel();
	final JTextArea msgArea = new JTextArea(15,30);

	final JTextField inputField = new JTextField(30);
	
	final JLabel usersLabel = new JLabel("Active Users");
	final JButton logoutButton = new JButton("Accept");
	final JButton sendButton = new JButton("Send");
	
	//Data Variables
	Users users;
	User user;
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //Date format for the char
	
	//Listeners and tasks and timers
	MessageListener msgListener;
	Timer msgListenerTimer;
	final int msgSpeed = 500;
	final int msgPause = 1000;
	UsersListener usrListener;
	Timer usrListenerTimer;
	final int usrSpeed = 1000;
	final int usrPause = 1500;
	
	public ChatWindow(User user) {
		this.user = user;
		users = null;
		msgArea.setEditable(false);
		add(chatPanel);
		leftPanel.setLayout(new BorderLayout());
		rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.PAGE_AXIS));
		chatPanel.setLayout(new BorderLayout());
		usersListPanel.setLayout(new BoxLayout(usersListPanel,BoxLayout.PAGE_AXIS));
		setPanel();
		setActionListeners();
		msgListener = null;
		usrListener = null;
		addUsersListenerTimer();
		addMessageListenerTimer();
	}
	
	private void setPanel(){
		chatPanel.add(leftPanel,BorderLayout.WEST);
		chatPanel.add(rightPanel,BorderLayout.EAST);
		leftPanel.add(new JScrollPane(msgArea),BorderLayout.NORTH);
		//Space for formating buttons later on
		leftPanel.add(inputField,BorderLayout.CENTER);
		leftPanel.add(sendButton,BorderLayout.SOUTH);
		rightPanel.add(logoutButton);
		rightPanel.add(usersLabel);
		rightPanel.add(usersListPanel);
		
	}
	
	private void setActionListeners() {
		// TODO Auto-generated method stub
		sendButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == sendButton){
			String message = inputField.getText();
			if(!message.equals("")){
				Message outgoingMsg;
				outgoingMsg = new Message(message,user,MessageType.MESSAGE);
				inputField.setText("");
				String msgString;
				Date date = new Date();
				msgString = "[" + outgoingMsg.getUsername() + "]@[" + dateFormat.format(date) + "] :" + outgoingMsg.getContent() + "\n";
				msgArea.append(msgString);
				ConnectionHandler.sendRequest(outgoingMsg);
			}
		}
		
	}
	
	private void addUsersListenerTimer() {
		usrListenerTimer = new Timer(usrSpeed, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				runUserListener();
			}
			
		});
		usrListenerTimer.setInitialDelay(usrPause);
		usrListenerTimer.start(); 
	}
	
	private void runUserListener() {
		if(usrListener == null || usrListener.isDone == true){
			usrListener = new UsersListener();
			SwingUtilities.invokeLater(usrListener);
		}
	}
	
	private void addMessageListenerTimer() {
		msgListenerTimer = new Timer(msgSpeed, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				runMessageListener();
			}
			
		});
		msgListenerTimer.setInitialDelay(msgPause);
		msgListenerTimer.start(); 
	}
	
	private void runMessageListener() {
		if(msgListener == null || msgListener.isDone == true){
			msgListener = new MessageListener();
			SwingUtilities.invokeLater(msgListener);
		}
	}
	
	private class MessageListener implements Runnable {
		private boolean isDone;

		public MessageListener() {
			isDone = false;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			isDone = false;
			//first ask the server if there is any new messeges waiting for this user
			Message msgAmountQuery;
			msgAmountQuery = new Message(Commands.ClientMsgFlush.toString(),user,MessageType.COMMAND);
			ConnectionHandler.sendRequest(msgAmountQuery);
			
			Message amountResponse = null;
			while(amountResponse == null){
				amountResponse = ConnectionHandler.checkMsgQue();
			}
			System.out.println("Message amount on the server: " + amountResponse.getContent());
			
			int msgAmount = Integer.parseInt(amountResponse.getContent());
			//if no messages in the server then isDone should be true and the runnable should break
			if(msgAmount == 0){
				isDone = true;
				return; //exit the thread
			}else{
				for(int i = 1; i <= msgAmount;i++){
					System.out.println("Client: Inside the meesage loop" + i);
					Message incomingMsg = null;
					while(incomingMsg == null){
						incomingMsg = ConnectionHandler.checkMsgQue();
					}
					
					String msgString;
					Date date = new Date();
					msgString = "[" + incomingMsg.getUsername() + "]@[" + dateFormat.format(date) + "] :" + incomingMsg.getContent() + "\n"; 
					msgArea.append(msgString);
				}
				isDone = true;
			}
		}
		
	}
	
	private class UsersListener implements Runnable{
		private boolean isDone;
		
		public UsersListener() {
			isDone = false;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			isDone = false;
			Message userDataQuery;
			userDataQuery = new Message(Commands.GetLoggedInUsers.toString(),user,MessageType.COMMAND);
			ConnectionHandler.sendRequest(userDataQuery);
			
			//getting the answer
			Message queryResponse = null;
			while(queryResponse == null){
				queryResponse = ConnectionHandler.checkDataQue();
			}
			System.out.println("Users LOGGED IN: " + queryResponse.getContent());
			
			//Filling the users variable if Users = null
			//this.Users is not equal null then compare if something changed
			//if it did refresh the users Panel
			if(users == null){
				users = new Users(queryResponse);
				//fill usersPanel with users
				fillUsersPanel(users);
			}else if(!users.compareUsersLists(new Users(queryResponse))){ //only replace the user list and refresh panel if there is a change on the list
				//remove all users labels from panel
				removeUsersFromPanel();
				//fill usersPanel with users
				fillUsersPanel(new Users(queryResponse));
			}
						
			//set isDone to true so the timer knows the task is finished
			isDone = true;	
		}
		
		private void fillUsersPanel(Users users){
			for(int i = 0; i < users.size();i++){
				User usr = users.getUser(i);
				if(!usr.getUsername().equals(user.getUsername())){
					usersListPanel.add(new JLabel(usr.getUsername()));
				}
			}
			//if there is no components just add none
			Component[] components = usersListPanel.getComponents();
			if(components.length == 0){
				usersListPanel.add(new JLabel("None"));
			}
			
			usersListPanel.revalidate();
			usersListPanel.repaint();
		}
		
		private void removeUsersFromPanel(){
			usersListPanel.removeAll();
			usersListPanel.revalidate();
			usersListPanel.repaint();
		}
		
	}
}
