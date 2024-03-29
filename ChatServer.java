import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.ssl.*;
import java.security.*;


public class ChatServer {

	protected int serverPort = 8888;
	protected List<Socket> clients = new ArrayList<Socket>(); // list of clients
	protected Map<Socket, String> clientUserMap = new HashMap<Socket, String>();
	String passphrase = "serverpwd";

	public static void main(String[] args) throws Exception {
		new ChatServer();
	}

	public ChatServer() {
		SSLServerSocket serverSocket = null;
		SSLContext sslContext = null;

		try {
			// preberi datoteko z odjemalskimi certifikati
			KeyStore clientKeyStore = KeyStore.getInstance("JKS"); // KeyStore za shranjevanje odjemalevih javnih(certifikatov)
			clientKeyStore.load(new FileInputStream("client.public"), "public".toCharArray());

			// preberi datoteko s svojim certifikatom in tajnim kljum
			KeyStore serverKeyStore = KeyStore.getInstance("JKS"); // KeyStore za shranjevanje  tajnega in javnega 
			serverKeyStore.load(new FileInputStream("server.private"), passphrase.toCharArray());

			// vzpostavi SSL kontekst (komu zaupamo,in certifikati)
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(clientKeyStore);
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(serverKeyStore, passphrase.toCharArray());
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), (new SecureRandom()));
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}


		// create socket
		try {
			SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
			serverSocket = (SSLServerSocket) factory.createServerSocket(this.serverPort);
			serverSocket.setNeedClientAuth(true); // tudi odjemalec se MORA predstaviti s certifikatom
			serverSocket.setEnabledCipherSuites(new String[] {"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"});
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
				((SSLSocket) newClientSocket).startHandshake(); 
				String username = ((SSLSocket) newClientSocket).getSession().getPeerPrincipal().getName().substring(3);
				System.out.println("Client connected with name " + username);
				synchronized(this) {
					clients.add(newClientSocket); // add client to the list of clients
					clientUserMap.put(newClientSocket, username);

					Message joinNotification;
					//System.out.printf("[system] client [%s] assigned username [%s]\n", socket.getPort(), username);
					
					joinNotification = new Message("JOIN", username, null, "joined the chat");
					try {
						this.sendToAllClients(joinNotification); // send message to all clients
					} catch (Exception e) {
						System.err.println("[system] there was a problem while sending the message to all clients");
						e.printStackTrace(System.err);
					}
					
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

	public void privateSendToClient(Message message, Socket senderSocket){
		Iterator<Socket> i = clients.iterator();
		while (i.hasNext()) { // iterate through the client list
			Socket socket = (Socket) i.next();
			if (clientUserMap.get(socket).equals(message.getReceiver())){
				try {
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					out.writeUTF(message.toJSONString()); // send message to the client
				} catch (Exception e) {
					System.err.printf("[system] could not send message to client [%s]\n", socket.getPort());
					e.printStackTrace(System.err);
				}
				return;
			}
		}
		System.err.printf("[system] could not send message to client [%s]: RECEIVING CLIENT NOT FOUND\n",
		 message.getReceiver());
		String errorText = String.format("Client [%s] not found", message.getReceiver());
		Message errorMessage = new Message("ERROR", "system", null, errorText);
		sendErrorToClient(errorMessage, senderSocket);
	}

	public void sendErrorToClient(Message message, Socket senderSocket){

		//? multiple types of errors in one method?//

		try {
			DataOutputStream out = new DataOutputStream(senderSocket.getOutputStream());
			out.writeUTF(message.toJSONString()); // send message to the client
		} catch (Exception e) {
			System.err.printf("[system] could not send message to client [%s]\n", senderSocket.getPort());
			e.printStackTrace(System.err);
		}
	}

	public void removeClient(Socket socket) {
		Message leftNotification;

		synchronized(this) {
			clients.remove(socket);
			String removedUser = clientUserMap.remove(socket);

			leftNotification = new Message("LEAVE", removedUser, null, "left the chat");
			try {
				this.sendToAllClients(leftNotification); // send message to all clients
			} catch (Exception e) {
				System.err.println("[system] there was a problem while sending the message to all clients");
				e.printStackTrace(System.err);
			}			
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
				message = Message.fromJSON(msg_received);
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
			if (message.getType().equals("PRIVATE")){
				this.server.privateSendToClient(message, this.socket);
			}
		}
	}
}
