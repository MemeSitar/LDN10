import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;


public class ChatServer {

	protected int serverPort = 8888;
	protected List<Socket> clients = new ArrayList<Socket>(); // list of clients
	protected Map<Socket, String> clientUserMap = new HashMap<Socket, String>();

	public static void main(String[] args) throws Exception {
		new ChatServer();
	}

	public ChatServer() {
		ServerSocket serverSocket = null;

		// create socket
		try {
			serverSocket = new ServerSocket(this.serverPort); // create the ServerSocket
		} catch (Exception e) {
			System.err.println("[system] could not create socket on port " + this.serverPort);
			e.printStackTrace(System.err);
			System.exit(1);
		}

		// start listening for new connections
		System.out.println("[system] listening ...");
		try {
			while (true) {
				Socket newClientSocket = serverSocket.accept(); // wait for a new client connection
				synchronized(this) {
					clients.add(newClientSocket); // add client to the list of clients
					clientUserMap.put(newClientSocket, null);
				}
				ChatServerConnector conn = new ChatServerConnector(this, newClientSocket); // create a new thread for communication with the new client
				conn.start(); // run the new thread
			}
		} catch (Exception e) {
			System.err.println("[error] Accept failed.");
			e.printStackTrace(System.err);
			System.exit(1);
		}

		// close socket
		System.out.println("[system] closing server socket ...");
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	// send a message to all clients connected to the server
	public void sendToAllClients(Message message) throws Exception {
		Iterator<Socket> i = clients.iterator();
		while (i.hasNext()) { // iterate through the client list
			Socket socket = (Socket) i.next(); // get the socket for communicating with this client
			
			if (clientUserMap.get(socket).equals(message.getSender())){
				System.out.printf("[system] did not send message to client [%s]: IS SENDER\n", socket.getPort());
			} else if (clientUserMap.get(socket) != null){
				try {
					DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // create output stream for sending messages to the client
					out.writeUTF(message.toJSONString()); // send message to the client
				} catch (Exception e) {
					System.err.printf("[system] could not send message to client [%s]\n", socket.getPort());
					e.printStackTrace(System.err);
				}
			} else {
				System.err.printf("[system] could not send message to client [%s]: NO USERNAME\n", socket.getPort());
			}
		}
	}

	public void removeClient(Socket socket) {
		synchronized(this) {
			clients.remove(socket);
		}
	}

	public void addUsernameToMap(Socket socket, String username){
		if (clientUserMap.get(socket) == null){
			clientUserMap.replace(socket, username);
			System.out.printf("[system] client [%s] assigned username [%s]\n", socket.getPort(), username);
		}
	}
}

class ChatServerConnector extends Thread {
	private ChatServer server;
	private Socket socket;

	public ChatServerConnector(ChatServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	public void run() {
		System.out.println("[system] connected with " + this.socket.getInetAddress().getHostName() + ":" + this.socket.getPort());

		DataInputStream in;
		try {
			in = new DataInputStream(this.socket.getInputStream()); // create input stream for listening for incoming messages
		} catch (IOException e) {
			System.err.println("[system] could not open input stream!");
			e.printStackTrace(System.err);
			this.server.removeClient(socket);
			return;
		}


		
		while (true) { // infinite loop in which this thread waits for incoming messages and processes them
			String msg_received;
			Message message;
			try {
				msg_received = in.readUTF(); // read the message from the client
				message = new Message(msg_received);
			} catch (Exception e) {
				System.err.println("[system] there was a problem while reading message client on port " + this.socket.getPort() + ", removing client");
				//e.printStackTrace(System.err);
				this.server.removeClient(this.socket);
				return;
			}

			if (msg_received.length() == 0) // invalid message
				continue;
			System.out.printf("[%s]:%s", this.socket.getPort(), message.toString());
			// prints out the raw JSON, for debugging.
			//System.out.println("[RKchat] [" + this.socket.getPort() + "] : " + msg_received); // print the incoming message in the console
			
			if (message.getType().equals("PUBLIC")){
				try {
					this.server.sendToAllClients(message); // send message to all clients
				} catch (Exception e) {
					System.err.println("[system] there was a problem while sending the message to all clients");
					e.printStackTrace(System.err);
					continue;
				}
			}
			if (message.getType().equals("LOGIN")){
				this.server.addUsernameToMap(this.socket, message.getSender());
			}
		}
	}
}
