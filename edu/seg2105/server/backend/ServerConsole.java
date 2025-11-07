package edu.seg2105.server.backend;

import java.util.Scanner;
import edu.seg2105.client.common.ChatIF;

/**
 *
/*
  ServerConsole (what it does):
  - It's like a simple command center for the server, similar to the ClientConsole used by clients.
  - How it all goes down:
      * I hand over an EchoServer when creating this thing, and I keep a Scanner pointed at the keyboard input.
      * accept() just loops, grabbing one line at a time from the server console.
      * If a line starts with '#', I figure it's a server command, and I pass it on to
        server.handleServerCommand(...). (Like: #stop, #start, #close, #setport 6000, #getport, #quit)
      * If it's not a command, then some human server operator typed a normal message. I send that using
        server.handleMessageFromServerUI(...), which shows it on the server screen and sends it to all connected clients,
        tagged with SERVER MSG&gt; (the EchoServer code adds that tag).
  - Why ChatIF? The whole system uses ChatIF as a standard way to display things. Implementing it here lets the
    server print messages the same way everywhere else (display just does a simple println).
  - How to make it run: Inside EchoServer.main(...) I build a new ServerConsole(server) and then call accept()
    – then I can type messages (to send to everyone) or use the #commands listed up above.
*/

public class ServerConsole implements ChatIF {
	  private final EchoServer server;
	  private final Scanner fromConsole = new Scanner(System.in);

	  public ServerConsole(EchoServer server) {
	    this.server = server;
	  }

	  public void accept() {
	    while (true) {
	      String line = fromConsole.nextLine().trim();
	      if (line.startsWith("#")) {
	        server.handleServerCommand(line);
	      } else {
	        server.handleMessageFromServerUI(line);
	      }
	    }
	  }

	  @Override
	  public void display(String message) {
	    System.out.println(message);
	  }
	}