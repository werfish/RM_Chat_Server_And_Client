package Client;
import javax.swing.JButton;
import javax.swing.JFrame;
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
	final JPanel rightPanel = new JPanel();
	final JPanel leftPanel = new JPanel();
	final JPanel usersListPanel = new JPanel();
	final JTextArea msgArea = new JTextArea(10,25);

	final JTextField inputField = new JTextField(25);
	
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
	
	public ChatWindow(User user) {
		this.user = user;
		add(chatPanel);
		rightPanel.setLayout(new BorderLayout());
		leftPanel.setLayout(new BoxLayout());
		chatPanel.setLayout(new BorderLayout());
		usersListPanel.setLayout(new BoxLayout());
		setPanel();
		setActionListeners();
		msgListener = null;
		addMessageListenerTimer();
	}
	
	private void setPanel(){
		chatPanel.add(rightPanel,BorderLayout.WEST);
		chatPanel.add(leftPanel,BorderLayout.EAST);
		rightPanel.add(new JScrollPane(msgArea),BorderLayout.NORTH);
		//Space for formating buttons later on
		rightPanel.add(inputField,BorderLayout.CENTER);
		rightPanel.add(sendButton,BorderLayout.SOUTH);
		leftPanel.add(logoutButton);
		leftPanel.add(usersLabel);
		leftPanel.add(usersListPanel);
		
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
}
