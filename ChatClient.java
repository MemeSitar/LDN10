import java.io.*;
import java.net.*;
import java.util.Scanner;

import org.json.simple.parser.*;
import org.json.simple.*;

public class ChatClient extends Thread
{
	protected int serverPort = 8888;


	public static void main(String[] args) throws Exception {
		String username;
		// DO NOT FIX THIS IT BREAKS THE CLIENT
		@SuppressWarnings({ "resource" }) 
		Scanner sc = new Scanner(System.in);
			System.out.printf("Please input username: \n");
			username = sc.next();
		
		new ChatClient(username);
	}

	public ChatClient(String username) throws Exception {
		Socket socket = null;
		DataInputStream in = null;
		DataOutputStream out = null;

		// connect to the chat server
		try {
			System.out.println("[system] connecting to chat server ...");
			socket = new Socket("localhost", serverPort); // create socket connection
			in = new DataInputStream(socket.getInputStream()); // create input stream for listening for incoming messages
			out = new DataOutputStream(socket.getOutputStream()); // create output stream for sending messages

			Message loginMessage = new Message("LOGIN", username, "");
			this.sendMessage(loginMessage, out);
			
			System.out.println("[system] connected");

			ChatClientMessageReceiver message_receiver = new ChatClientMessageReceiver(in); // create a separate thread for listening to messages from the chat server
			message_receiver.start(); // run the new thread
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}

		// read from STDIN and send messages to the chat server
		BufferedReader std_in = new BufferedReader(new InputStreamReader(System.in));
		String userInput;
		while ((userInput = std_in.readLine()) != null) { // read a line from the console
			Message message = new Message("PUBLIC", username, userInput);
			this.sendMessage(message, out); // send the message to the chat server
		}

		// cleanup
		out.close();
		in.close();
		std_in.close();
		socket.close();
	}

	private void sendMessage(Message message, DataOutputStream out) {
		try {
			out.writeUTF(message.toJSONString()); // send the message to the chat server
			out.flush(); // ensure the message has been sent
		} catch (IOException e) {
			System.err.println("[system] could not send message");
			e.printStackTrace(System.err);
		}
	}
}

// wait for messages from the chat server and print the out
class ChatClientMessageReceiver extends Thread {
	private DataInputStream in;

	public ChatClientMessageReceiver(DataInputStream in) {
		this.in = in;
	}

	public void run() {
		try {
			String msg_received;
			Message message;
			while ((msg_received = this.in.readUTF()) != null) { // read new message (from DataInputStream)
				message = new Message(msg_received);
				System.out.printf("%s", message); // print the message to the console
			}
		} catch (Exception e) {
			System.err.println("[system] could not read message");
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
}
