package Common;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class Users {
//This class is meant to hold users on the servers and implements methods that query it
//it is meant to be only one on the server and it will be used by several threads
		List<User> usersList = null;
		Object lock = new Object();
		
		public Users(){
			usersList = new ArrayList<User>();
		}
		
		public Users(Message msg){
			usersList = fromMessage(msg);
		}

		
		public void addUser(User user){
			this.usersList.add(user);
		}
		
		public void removeUser(User user){
			synchronized(lock){
				for(User item : usersList){
					if(item.getUsername().equals(user.getUsername())){
						usersList.remove(item);
					}
				}
			}
		}
		public User getUser(int number) {
			return usersList.get(number);
		}
		
		public int size() {
			return usersList.size();
		}
		
		public boolean isUser(User user) {
			for(User item : this.usersList){
				if(item.getUsername().equals(user.getUsername())){
					return true;
				}
			}
			return false;
		}
		
		public boolean compareUsersLists(Users users) {
			boolean amountTheSame; //if amount is not the same then return false straight away
			
			amountTheSame = usersList.size() == users.size(); //if this is false then the function returns false
			
			if(!amountTheSame){
				return false;
			}else{
				for(User item: usersList){ 
					if(!users.isUser(item)){ //if any of the users from usersList is not present on the parameter users list return false
						return false;
					}
				}
			}
			return true; //only if all the validation and traos are passed
		}
		
		public Message toMessage() {
			Message listMsg;
			String content = "";
			for(User item : this.usersList){
				int i = 0;
				if(i == 0){
					content = item.getUsername();
				}else{
					content = content + " " +item.getUsername();
					i++;
				}
			}
			listMsg = new Message(content,new User("Server"),MessageType.DATA);
			return listMsg;
		}
		
		private List<User> fromMessage(Message msg){
			List<User> list;
			StringTokenizer st;
			String username;
			
			list = new ArrayList<User>();
			st = new StringTokenizer(msg.getContent());
			
			while(st.hasMoreTokens()){
				username = st.nextToken();
				list.add(new User(username));
			}
			
			return list;
			
		}
}
