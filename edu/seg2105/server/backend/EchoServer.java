package edu.seg2105.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import ocsf.server.*;
// import the following for Exercise 3.0
import java.io.*;


/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  
  // Assignment 2.0 - Exercise 3.0 
  /** 
  * - Match only on the first message of type ‘#login <id>’
  * - Save login id via setInfo/getInfo
  * - Prefix each echoed message with the sender's login id
  * - If #login is missing at first or follows, raise an error and close
  */
  
  /**
   * // handleMessageFromClient logic explanation:
//
// When client connects, the first message received must be “#login <id>”.
//   The login ID, if correct, is then stored on the server side by client.setInfo()
//   so that the system can identify who sent the messages in the future.
     //   The value is
// - In the case that the initial message is absent or malformed, or if any client attempts
//   to send another "#login" after already logging in, the server sends
//   an error message and closes the connection.
// For all normal messages sent after the login process, the server prefixes each with
//   with the user's login ID and transmits it to all connected clients.
   */
  
  
  // === Exercise 3.0 =====
  
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
	  String text = String.valueOf(msg);
	  String id = (String) client.getInfo("loginId"); // null until logged in

	  if (id == null) {
	    // First message MUST be "#login <id>"
	    if (text.startsWith("#login ")) {
	      String proposed = text.substring("#login ".length()).trim();
	      if (proposed.isEmpty()) {
	        try { client.sendToClient("ERROR: Missing login ID. Closing connection."); } catch (IOException ignored) {}
	        try { client.close(); } catch (IOException ignored) {}
	        return;
	      }
	      client.setInfo("loginId", proposed);
	      System.out.println("[Server] " + proposed + " logged in.");
	      try { client.sendToClient("Welcome, " + proposed + "!"); } catch (IOException ignored) {}
	      return; // do not echo the #login line
	    } else {
	      // No login on first message -> error + close
	      try { client.sendToClient("ERROR: First message must be '#login <id>'. Closing connection."); } catch (IOException ignored) {}
	      try { client.close(); } catch (IOException ignored) {}
	      return;
	    }
	  } else {
	    // Already logged in
	    if (text.startsWith("#login")) {
	      try { client.sendToClient("ERROR: Already logged in. Closing connection."); } catch (IOException ignored) {}
	      try { client.close(); } catch (IOException ignored) {}
	      return;
	    }
		 
		  // Normal path: prefix with loginId and broadcast to all clients
		    String tagged = id + "> " + text;
		    System.out.println("[Server] From " + id + ": " + text);
		    this.sendToAllClients(tagged);
	  }
  }
  
  
  // === Exercise 2(b): echo server-operator messages to all clients ===
  public void handleMessageFromServerUI(String message) {
	  String tagged = "SERVER MSG > " + message;
	  System.out.println(tagged);
	  sendToAllClients(tagged);
  }
  
  // === Exercise 2(c): server-side commanfs ====
  public void handleServerCommand(String line) {
	  
	  String[] parts = line.trim().split("\\s+", 2);
	  String cmd = parts[0].toLowerCase();
	  String arg = (parts.length > 1) ? parts[1] : null;
	  
	  
	  try {
		  if (cmd.equals("#quit")) {
			  System.out.println("Server quitting....");
			  System.exit(0);
			  
		  } else if (cmd.equals("#stop")) {
			  if (isListening()) {
				  stopListening();
				  System.out.println("Stopped listening for new clients");
			  } else {
				  System.out.println("Already stopped. ");
			  }
			  
		  } else if (cmd.equals("#close")) {
		      if (isListening()) stopListening();
		      try {
		        close(); // disconnect all existing clients
		        System.out.println("Closed server and disconnected all clients.");
		      } catch (IOException e) {
		        System.out.println("Close failed: " + e.getMessage());
		      }

		    } else if (cmd.equals("#setport")) {
		      if (arg == null) {
		        System.out.println("Usage: #setport <port>");
		      } else if (isListening() || getNumberOfClients() > 0) {
		        System.out.println("Error: close the server before changing port.");
		      } else {
		        try {
		          int p = Integer.parseInt(arg);
		          setPort(p);
		          System.out.println("Port set to: " + getPort());
		        } catch (NumberFormatException e) {
		          System.out.println("Invalid port: " + arg);
		        }
		      }

		    } else if (cmd.equals("#start")) {
		      if (!isListening()) {
		        listen();
		        System.out.println("Server listening for connections on port " + getPort());
		      } else {
		        System.out.println("Already listening.");
		      }

		    } else if (cmd.equals("#getport")) {
		      System.out.println("Current port: " + getPort());

		    } else {
		      System.out.println("Unknown server command: " + cmd);
		    }
		  } catch (Exception e) {
		    System.out.println("Server command error: " + e.getMessage());
		  }
		}
	 
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  
  // === Lifecycle Hooks ====
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  // Assignment 2 - Exercise 1.0
//This method is automatically called by OCSF (AbstractServer)
  // @Override
//whenever a new client successfully connects to the server.
  @Override
  protected void clientConnected(ConnectionToClient client) {
    System.out.println(" [Server] Client connected: " + client.getInetAddress().getHostAddress());
  }

//This is automatically called when a CLIENT DISCONNECT EVENT occurs 
  // (either by closing its connection or because of a network problem).
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
    System.out.println(" [Server] Client disconnected: " + client.getInetAddress().getHostAddress());
  }

 
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  // === Main Method ===
  public static void main(String[] args) 
  {
    int port;
    
    
    try {
    	
    		port = Integer.parseInt(args[0]);
    } catch (Throwable t ) {
    		port = DEFAULT_PORT;
    }
    
    
    EchoServer sv = new EchoServer(port);
    
    
    try {
    		sv.listen();
    		
    		System.out.println("Server listening for connections on port " + sv.getPort());
    } catch (Exception ex) {
    		System.out.println("ERROR - Could not listen for clients! You can use #setport then use #stat.");
    		
    }
    
    ServerConsole sc = new ServerConsole(sv);
	sc.accept();  // type messages or #commands here
  }
  
}
//End of EchoServer class
   
    
    
    