package Client;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class StatusBar extends JPanel {
//This class will be used to display the connection status, display messages when it is trying to reconnect etc
//This is the first class to implement the singleton  design patter, the panel should reside at the bottom of the
//application
	
  private JLabel connLabel;
  private JLabel msgLabel;
  private volatile static StatusBar uniqueInstance;

  private StatusBar() {
    this.setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
    connLabel = new JLabel();
    msgLabel = new JLabel();
    this.add(Box.createRigidArea(new Dimension(5,5)));
    this.add(connLabel);
    this.add(Box.createRigidArea(new Dimension(100,5)));
    this.add(msgLabel);
  }
  
  public static StatusBar getInstance() {
	  if(uniqueInstance == null){
		  synchronized (StatusBar.class){
			  uniqueInstance = new StatusBar();
		  }
	  }
	  return uniqueInstance;
  }
  
  public String getConnectionStatus() {
	  return connLabel.getText();
  }
  
  public void setConnectionStatus(boolean status) {
	  if(status == true){
		  setConnected();
	  }else{
		  setNotConnected();
	  }
  }
  
  private void setNotConnected() {
      connLabel.setText("Not Connected");
  }
  
  private void setConnected() {
	  connLabel.setText("Connected");
  }
  
  //the message should be displayed only for max 2-3 seconds
  public void displayMessage(String msg) {
	  msgLabel.setText(msg);
  }
  
  public void displayMessage(String msg, int seconds){
	  seconds = seconds * 1000;
	  Timer msgTime = new Timer(seconds, new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if(msg.equals(msgLabel.getText())){
				msgLabel.setText("");
			}
		}
		  
	  });
	  msgLabel.setText(msg);
	  msgTime.setRepeats(false);
	  msgTime.start();
  }

}
