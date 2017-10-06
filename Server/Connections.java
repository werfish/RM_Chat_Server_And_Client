package Server;


import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Common.User;

public class Connections {
	
	List<Connection> connections = null;
	Object lock = new Object();
	
	public Connections() {
		connections = new ArrayList<Connection>();
	}
	
	public int count() {
		return connections.size();
	}
	
	public Connection getConnection(int i){
		return connections.get(i);
	}
	
	public Connection getConnection(User user) {
		for(Connection conn : connections) {
			if(!(conn.getLogin() == null) && user.getUsername().equals(conn.getLogin())){
				return conn;
			}
		}
		
		return null;
	}
	
	public void addConnection(Connection connection){
		this.connections.add(connection);
	}
	
	public void addConnection(Socket socket, String login){
		Connection newConnection = new Connection(socket, login);
		this.connections.add(newConnection);
	}
	
	public void addUserToConnection(Socket socket,User user) {
		for(Connection conn: connections){
			if(conn.getSocket() == socket){
				conn.setLogin(user.getUsername());
				if(conn.isQueInitialized() == false){
					conn.initializeQue();
				}
			}
		}
	}
	
	public void removeConnection(Connection connection){
		synchronized(lock){
			connections.remove(connection);
		}
	}
	
	public void removeConnection(String login){
		synchronized(lock){
			for(int i = 0; i < connections.size();i++){
				if(connections.get(i).getLogin().equals(login)){
					connections.remove(i);
				}
			}	            
		}
	}
	
	
	
	
}
