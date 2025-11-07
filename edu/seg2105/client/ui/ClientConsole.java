package edu.seg2105.client.ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String host, int port, String loginID) 
  {
    try 
    {
      client= new ChatClient(host, port, this, loginID);
      
      
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() {
    try {
      while (true) { 
    	  String line = fromConsole.nextLine().trim();
    	  
    	  if (line.startsWith("#")) {
    		  handleClientCommand(line); 
    	  } else {
    		  client.handleMessageFromClientUI(line);
    	  }
      }
      
    } catch (Exception ex) {
      System.out.println("Unexpected error while reading from console!");
    }
  }
 
//Parses console lines that start with '#' and executes client-side commands.
//Processes the "#quit" or "#logoff" command by properly shutting down the socket, "#quit" quits the
//while #logoff will leave the client running. Implements requirement that #sethost/#setport
//only work when disconnected, validates inputs, and opens a connection on
//login (#login is sent by ChatClient.connectionEstablished() with

 
  private void handleClientCommand(String line) {
	  String[] parts = line.split("\\s+", 2);
	  String cmd = parts[0].toLowerCase();
	  String arg = (parts.length > 1) ? parts[1] : null;

	  try {
	    if (cmd.equals("#quit")) {
	      if (client.isConnected()) {
	        try {
	          client.setUserInitiatedClose(true);
	          client.closeConnection();
	        } catch (IOException ignored) {}
	      }
	      System.out.println("Client quitting...");
	      System.exit(0);

	    } else if (cmd.equals("#logoff")) {
	      if (client.isConnected()) {
	        try {
	          client.setUserInitiatedClose(true);
	          client.closeConnection();
	        } catch (IOException ignored) {}
	        System.out.println("Logged Off!");
	      } else {
	        System.out.println("Not connected.");
	      }

	    } else if (cmd.equals("#sethost")) {
	      if (client.isConnected()) {
	        System.out.println("Error: log off before changing the host.");
	      } else if (arg == null || arg.isBlank()) {
	        System.out.println("Usage: #sethost <host>");
	      } else {
	        client.setHost(arg.trim());
	        System.out.println("Host set to: " + client.getHost());
	      }

	    } else if (cmd.equals("#setport")) {
	      if (client.isConnected()) {
	        System.out.println("Error: log off before changing the port.");
	      } else if (arg == null) {
	        System.out.println("Usage: #setport <port>");
	      } else {
	        try {
	          int p = Integer.parseInt(arg.trim());
	          client.setPort(p);
	          System.out.println("Port set to: " + client.getPort());
	        } catch (NumberFormatException e) {
	          System.out.println("Invalid port: " + arg);
	        }
	      }

	    } else if (cmd.equals("#login")) {
	      if (client.isConnected()) {
	        System.out.println("Error: already connected.");
	      } else {
	        try {
	          client.openConnection();
	          System.out.println("Connected.");
	        } catch (IOException e) {
	          System.out.println("Login failed: " + e.getMessage());
	        }
	      }

	    } else if (cmd.equals("#gethost")) {
	      System.out.println("Current host: " + client.getHost());

	    } else if (cmd.equals("#getport")) {
	      System.out.println("Current port: " + client.getPort());

	    } else {
	      System.out.println("Command not known. " + cmd);
	    }
	  } catch (Exception e) {
	    System.out.println("Command Error. " + e.getMessage());
	  }
	}


  
  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   * Assignment 2.0 - Exercise 1.0 
   */
  public static void main(String[] args) 
  {
	  // -- order must be: <loginID> [host] [port]
	 if (args.length < 1 || args[0].isBlank()) {
		  System.out.println("Usage: java ClientConsole <login> [host] [port]");
		  System.exit(1);
	 }
	  
	String loginID = args[0].trim();
	
    String host = "localhost";
    int port = DEFAULT_PORT;
    
    if (args.length >= 2 && args[1] != null && !args[1].isEmpty()) {
    		host = args[1];
    }
    
    if (args.length >= 3) {
    		 
    		try {
    			port = Integer.parseInt(args[2]);
    			
    		} catch (NumberFormatException e) {
    			System.out.println("Invalid port " + args[2] + ". Using default: " + DEFAULT_PORT);
    			port = DEFAULT_PORT;
    			
    		}
    }
    
    
    	// This starts the client on the chosen host/port	
    ClientConsole chat= new ClientConsole(host, port, loginID);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class
