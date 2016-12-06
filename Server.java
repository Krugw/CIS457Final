import java.net.*;
import java.io.*;
import java.util.*;

public class Server{
	Food[] foodlist;
	public Server(int port) throws IOException{
		foodlist = new Food[32];
		for (int i = 0; i < foodlist.length; i++){
			foodlist[i] = new Food("SEND", "NUDES", 6, 9);
		}
		ServerSocket connectionListener = new ServerSocket(port);
		ServerSocket dataListener = new ServerSocket(1235);
		System.out.println("Server started at port " + port + "...");
		while (true){
			Socket client = connectionListener.accept();
			System.out.println("Client at " + client.getInetAddress().getHostAddress() + " connected to server.");
			Handler handler = new Handler(client, dataListener, foodlist);
			handler.start();
		}
	}
	
	public static void main (String args[]) throws IOException { 
		if (args.length != 1) {
			throw new RuntimeException ("Syntax: java Server <listen_port>\nSuggested port: 1234"); 
		}
		new Server(Integer.parseInt (args[0])); 
	}
}