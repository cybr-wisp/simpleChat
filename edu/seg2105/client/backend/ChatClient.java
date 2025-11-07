// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

// -- import the following ---
import ocsf.client.*;
import java.io.*;
import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  // -- new variables to remember who the client is --
  private String loginID;

  // -- variables to distinguish user-initiated closes (#logoff/quit) from server shutdowns 
  private boolean userInitiatedClose = false;
  public void setUserInitiatedClose(boolean v) { 
	  this.userInitiatedClose = v; 
  }

  
  //Constructors ****************************************************
  
  // 3 arg-constructor so ClientConsole(new ChatLClient(host, port, this)) compiles and user a default loginID
  public ChatClient(String host, int port, ChatIF clientUI)  throws IOException {
	 this(host, port, clientUI, "guest");
  }
  
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   * @param loginID the user id to send at login 
   */
 
  
  public ChatClient(String host, int port, ChatIF clientUI, String loginID) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    
    openConnection(); // connect the client right away 
} 
  

 
  //Instance methods ************************************************
  
  // -- called by OCSF on every successful connect (initial connect and any reconnect)
  @Override 
  protected void connectionEstablished() {
	  try {
		  sendToServer("#login " + loginID);
		  
	  } catch (IOException e ) {
		  if (clientUI != null) {
			  clientUI.display("Could not send login command :( " + e.getMessage());
		  }
	  }
  } 
  
  @Override 
  protected void connectionClosed() {
    if (userInitiatedClose) {
      if (clientUI != null) clientUI.display("Disconnected from server");
      return; 
    }

    if (clientUI != null) clientUI.display("The Server has shut down. Client will now exit.");
    System.exit(0);
  }
  
  
 @Override 
 protected void connectionException(Exception exception) {
	 clientUI.display(" Lost connection to server : " + exception + " ");
	 quit();
 }
 
 // === Message Handlers ===
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
	  if (clientUI != null) clientUI.display(msg.toString());
  }
  

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      sendToServer(message);
    }
    catch(IOException e) {
    		if (clientUI != null) {
    			clientUI.display("Could not send message to server.  Terminating client");
    		}
    		
      quit();
    }
  }

  
  // === Shutdown ====
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try {
    		userInitiatedClose = true; // marks the intentional close
    	    closeConnection();
    } catch (IOException e) {}
    System.exit(0);
  }
  
    
  

}
//End of ChatClient class
