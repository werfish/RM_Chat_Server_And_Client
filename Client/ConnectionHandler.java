package Client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import Common.Commands;
import Common.Message;
import Common.MessageType;
import Common.User;

public class ConnectionHandler implements Runnable {
	//This class handles all the connections of the clint part of the applications
	//It has one outgoing Que which holds messges to be sent to server and 4 incoming 
	//blocking queues to capture incoming messges from the server
	//4 queue are monitored by interested listeners across the client UI classes
	//This class is being changed to a singleton as there should only be one instance of it at the same time
	
	//The below is the instance for the Singleton implementation
	private static ConnectionHandler singleton;
	
	//The connection handler will be steering the Status Bar
	private StatusBar connBar;
	
	//The outgoing que, all messages to server go here
	private BlockingQueue<Message> serverQue = new ArrayBlockingQueue<Message>(10);
	
	//This is the separate channels ques
	private BlockingQueue<Message> logRegQue = new ArrayBlockingQueue<Message>(10); //Login and registration channel(there is not much messages there and back) 
	private BlockingQueue<Message> msgQue = new ArrayBlockingQueue<Message>(10); //que that will hold all Messeges for chat
	private BlockingQueue<Message> dataQue = new ArrayBlockingQueue<Message>(10); // this is the channel on wchich UsersList thread will be observing
	private BlockingQueue<Message> errorQue = new ArrayBlockingQueue<Message>(10); //this is the channel which will be obvserved for any errors or server shutdowns, or maintenance messages

	//The Client network flags
	private boolean CONNECTION_OPEN;
	private boolean APPLICATION_RUNNING = true;
	private boolean LOGGED_IN = false;
	private Connection conn;
	
	private ConnectionHandler(){
		
		CONNECTION_OPEN = startConnection();
		connBar = StatusBar.getInstance();
		connBar.setConnectionStatus(CONNECTION_OPEN);
	}
	
	private boolean startConnection() {
		conn = new Connection();
		try {
			 conn.connect();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}
	
	public static ConnectionHandler getInstance(){
		  if(singleton == null){
			  synchronized (ConnectionHandler.class){
				 singleton  = new ConnectionHandler();
			  }
		  }
		  return singleton;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Connection Handler initialized!!!");
		while(APPLICATION_RUNNING){
			if(!serverQue.isEmpty() && CONNECTION_OPEN == true){
				try {
					System.out.println("What about the conitnue statement?");
					Message toServer;
					toServer = takeOffQue(serverQue);
					System.out.println(toServer.getContent() + " " + toServer.getUser().getUsername() + " " + toServer.getType().toString());
					if(toServer.getType() == MessageType.ERROR){
						addToQue(toServer,errorQue);
						continue;
					}
					
					//Here the message gets sent
					conn.sendMessage(toServer);
					
					if(toServer.getType() == MessageType.DISCONNECT) {
						System.out.println("Connection handler Closing");
						closeConnection();
						break;
					}else if(toServer.getType() == MessageType.MESSAGE){
						System.out.println("Message Sent for ditribition, don't wait for answer");
						continue;
					}else if(toServer.getType() == MessageType.LOGOUT) {
						LOGGED_IN = false;
						continue; //this type of message does not need an answer
					}
					System.out.println("Connection handler Sent Message");
					
					Message answer = conn.getMessage();
					
					
					//now handle the protocol by assigning answer to the proper channel
					if(answer.getType() == MessageType.REGISTER || answer.getType() == MessageType.LOGIN){
						addToQue(answer,logRegQue);
						if(answer.getType() == MessageType.LOGIN && answer.getContent().contains("SUCCESS")) {
							LOGGED_IN = true;
						}
					}else if(answer.getType() == MessageType.LOGOUT){
						addToQue(answer,errorQue);
					}else if(answer.getType() == MessageType.DATA){
						addToQue(answer,dataQue);
						System.out.println(" THE LIST OF USERS INCOMING");
						System.out.println(answer.getContent());
					}else if(answer.getType() == MessageType.MESSAGE){
						addToQue(answer,msgQue);
						int msgCount = Integer.parseInt(answer.getContent());
						
						for(int i = 1; i <= msgCount;i++){
							Message msg;
							msg = conn.getMessage();
							addToQue(msg,msgQue);
						}
					}
						
				} catch (IOException e) {
					// TODO Auto-generated catch block
					addToQue(new Message(ClientErrors.LOST_CONNECTION_TO_SERVER.toString(),new User("Client"),MessageType.ERROR)),errorQue);
					conn.closeConnection();
					CONNECTION_OPEN = false;
				}
				
			}else if(CONNECTION_OPEN == false){
				Thread timer = new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						connBar.displayMessage("Connection has been lost. Preparing to reconnect...", 2);
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for(int i = Client.TIMEOUT; i >= 0;i--){
							connBar.displayMessage("Reconnecting to server in " + String.valueOf(i) + " seconds....",1);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
				});	
				
				timer.start();
				try {
					timer.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				CONNECTION_OPEN = startConnection();
				connBar.setConnectionStatus(CONNECTION_OPEN);
			}
			
		}
	}
	
	public void resetChatQueues() {
		if(!msgQue.isEmpty()){
			for(int i = 0; i < msgQue.size();i++){
				takeOffQue(msgQue);
			}
		}
		if(!dataQue.isEmpty()){
			for(int i = 0; i < dataQue.size();i++){
				takeOffQue(dataQue);
			}
		}
	}
	
	public boolean isLoggedIn() {
		
		return LOGGED_IN;
	}

	
	private void addToQue(Message msg, BlockingQueue<Message> que){
		try {
			que.put(msg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Message takeOffQue(BlockingQueue<Message> que){
		Message workArround;
		try {
			workArround = que.take();
			return workArround;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			workArround = null;
		}
		return workArround;
	}
	
	public void closeConnection() {
		CONNECTION_OPEN = false;
		conn.closeConnection();
	}
	
	
	public void sendRequest(Message msg){
		addToQue(msg,serverQue);
		System.out.println("Message Sent!!!");
	}
	
	public void showActionOnBar(Message msg){
		if(msg.getType() == MessageType.LOGIN){
			connBar.displayMessage("Login Request Processing....", 2);
		}else if(msg.getType() == MessageType.REGISTER) {
			connBar.displayMessage("Register Request Processing....", 2);
		}
	}
	
	public Message checkLogRegQue(){
		if(!logRegQue.isEmpty()){
			return takeOffQue(logRegQue);
		}
		return null;
	}
	
	public Message checkMsgQue(){
		if(!msgQue.isEmpty()){
			return takeOffQue(msgQue);
		}
		return null;
	}
	
	public Message checkErrorQue(){
		if(!errorQue.isEmpty()){
			return takeOffQue(errorQue);
		}
		return null;
	}
	
	public Message checkDataQue(){
		if(!dataQue.isEmpty()){
			return takeOffQue(dataQue);
		}
		return null;
	}


}
