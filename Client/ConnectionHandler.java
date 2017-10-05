package Client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import Common.Commands;
import Common.Message;
import Common.MessageType;

public class ConnectionHandler implements Runnable {
	//The outgoing que, all messages to server go here
	private static BlockingQueue<Message> serverQue = new ArrayBlockingQueue<Message>(10);
	
	//This is the separate channels ques
	private static BlockingQueue<Message> logRegQue = new ArrayBlockingQueue<Message>(10); //Login and registration channel(there is not much messages there and back) 
	private static BlockingQueue<Message> msgQue = new ArrayBlockingQueue<Message>(10); //que that will hold all Messeges for chat
	private static BlockingQueue<Message> dataQue = new ArrayBlockingQueue<Message>(10); // this is the channel on wchich UsersList thread will be observing
	private static BlockingQueue<Message> errorQue = new ArrayBlockingQueue<Message>(10); //this is the channel which will be obvserved for any errors or server shutdowns, or maintenance messages

	
	private boolean CONNECTION_OPEN = true;
	private static boolean LOGGED_IN = false;
	private Connection conn;
	
	public ConnectionHandler(){
		try {
			conn = new Connection();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Connection Handler initialized!!!");
		while(CONNECTION_OPEN == true){
			if(!serverQue.isEmpty()){
				try {
					System.out.println("What about the conitnue statement?");
					Message toServer;
					toServer = takeOffQue(serverQue);
					System.out.println(toServer.getContent() + " " + toServer.getUser().getUsername() + " " + toServer.getType().toString());
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
					e.printStackTrace();
				}
				
			}
			
		}
	}
	
	public static void resetChatQueues() {
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
	
	public static boolean isLoggedIn() {
		
		return LOGGED_IN;
	}

	
	private static void addToQue(Message msg, BlockingQueue<Message> que){
		try {
			que.put(msg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Message takeOffQue(BlockingQueue<Message> que){
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
	
	
	public static void sendRequest(Message msg){
		addToQue(msg,serverQue);
		System.out.println("Message Sent!!!");
	}
	
	public static Message checkLogRegQue(){
		if(!logRegQue.isEmpty()){
			return takeOffQue(logRegQue);
		}
		return null;
	}
	
	public static Message checkMsgQue(){
		if(!msgQue.isEmpty()){
			return takeOffQue(msgQue);
		}
		return null;
	}
	
	public static Message checkErrorQue(){
		if(!errorQue.isEmpty()){
			return takeOffQue(errorQue);
		}
		return null;
	}
	
	public static Message checkDataQue(){
		if(!dataQue.isEmpty()){
			return takeOffQue(dataQue);
		}
		return null;
	}


}
