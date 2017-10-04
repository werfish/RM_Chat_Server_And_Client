package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Common.Commands;
import Common.Message;
import Common.MessageType;
import Common.User;
import Common.Users;
import Storage.StorageHandler;

public class Server {
	final static int PORT = 5217;
	final static int THREADS = 15;
	final static String STORAGE = "Custom";
	
	boolean isServerRunning;
	MainServerThread mainThread;
	StorageHandler database;
	
	Server() {
		this.isServerRunning = false;
	}
	
	public boolean isRunning() {
		return this.isServerRunning;
	}
	
	private void setDataContainers() {
		
	}
	
	private void start() throws IOException {
		mainThread = new MainServerThread();
		database = new StorageHandler(STORAGE);
		Thread mainThreadInstance = new Thread(mainThread);
		Thread databaseThread = new Thread(database);
		mainThreadInstance.start();
		databaseThread.start();
		this.isServerRunning = true;
	}
	
	private void stop() {
		
	}
	
	private void getPort() {
		
	}
	
	private void getHostname(){
		
	}
	
	private void summary() {
		
	}

	public static void main(String[] args) throws IOException {
		
		boolean isRunning;
		
		isRunning = true;
		Server server = new Server();
		// TODO Auto-generated method stub
		//Creating the command line interface
		System.out.println("#########################################");
		System.out.println("# RM Chat Solution 2017 Version 0.2.Pie");
		System.out.println("#");
		System.out.println("# To start the server type \"start\".");
		System.out.println("#");
		System.out.println("# To get help type \"help\"");
		
		//Geting the command into a variable
		Scanner commandIn = new Scanner(System.in);
		
		while(isRunning){
			
			String command = commandIn.nextLine();
			
			if(command.equals(new String("start")) && server.isRunning() == false){
				server.start();
				System.out.println("The server has started and is ready to accept connections. ");
			} else {
				System.out.println("Command " + command + " not recognized");
		}
		}

	}
	
	class MainServerThread implements Runnable {
		Connections connections;
		ServerSocket serverSocket;
		Users users;
		ExecutorService executor;
		
		public MainServerThread() throws IOException{
			serverSocket = new ServerSocket(Server.PORT);
			connections = new Connections();
			executor = Executors.newFixedThreadPool(Server.THREADS);
			users = new Users();
			
			System.out.println("Initializing users que");
			System.out.println("Users Que intialized");
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				try {
					System.out.println("Server thread: Running");
					Socket client = serverSocket.accept();
					executor.submit(new ClientSession(connections,new Connection(client), users));
					System.out.println("New client connected: PORT " + client.getPort() + " IP: " + client.getInetAddress());
					System.out.println(users.toMessage().getContent());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
