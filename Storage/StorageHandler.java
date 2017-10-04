package Storage;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import Common.Commands;
import Common.Message;
import Common.MessageType;
import Common.User;
import Server.Server;

public class StorageHandler implements Runnable {
	private static BlockingQueue<Message> dataRail = new ArrayBlockingQueue<Message>(10);
	private static BlockingQueue<Message> responseRail = new ArrayBlockingQueue<Message>(1);
	
	Storage storage;
	
	public StorageHandler(String storageType) {
		storage = new Storage(storageType);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			//Check if there is new message added to Server Que
			if(dataRail.size() > 0 && responseRail.size() == 0){
				//if yes then send it
				System.out.println("Storage Handler Rail not empty!");
				//if yes then send it
				Message receivedQuery = takeOffRail();
				System.out.println("Query Received");
				Message response;
					
				//Check the message type to send the right query
				if(receivedQuery.getType() == MessageType.REGISTER){
					storage.insertRegDetails(receivedQuery);
					System.out.println("Registration conducted");
					response = new Message("SUCCESS", new User("Database"), MessageType.REGISTER);
					addToResponse(response);
				}else if(receivedQuery.getType() == MessageType.LOGIN){
					response = storage.getCredentials(receivedQuery.getUser());
					addToResponse(response);
				}else if(receivedQuery.getType() == MessageType.COMMAND){
					if(receivedQuery.getContent().equals(Commands.GetUsers.toString())) {
						response = storage.getUsersList();
						addToResponse(response);
					}
				}
			}
		}
		
	}
	
	public static Message sendQuery(Message msg){
		addToRail(msg);
		
		Message answer;
		
		System.out.println("Query Sent!!");
		while(responseRail.isEmpty()){
			
		}
		answer = takeOffResponse();
		return answer;
	}
	
	private static void addToRail(Message msg) { 
		try {
			dataRail.put(msg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Message takeOffRail() {
		Message workArround;
		try {
			workArround = dataRail.take();
			return workArround;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			workArround = null;
		}
		return workArround;
	}
	
	private static void addToResponse(Message msg) {
		try {
			responseRail.put(msg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Message takeOffResponse() {
		Message workArround;
		try {
			workArround = responseRail.take();
			return workArround;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			workArround = null;
		}
		return workArround;
	}

}
