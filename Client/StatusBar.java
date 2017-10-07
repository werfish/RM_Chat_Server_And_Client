import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusBar extends JPanel {
//This class will be used to display the connection status, display messages when it is trying to reconnect etc
//This is the first class to implement the 
  private JLabel connLabel;
  private JLabel msgLabel;

  public StatusBar() {
    this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
  }
  
  public void setNotConnected() {
      
  }
  
  public void setConnected() {
  
  }
  
  //the message should be displayed only for max 2-3 seconds
  public void displayMessage() {
  
  }

}
