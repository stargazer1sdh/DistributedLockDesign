package com.sjtu.sdh;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Server is the class for both leader and follower servers which accepts
 * scokets from clients.
 *
 * Server creates a thread for each request.
 */
public class Server {

	/**
	 * the replicated map
	 */
	public static ConcurrentHashMap<String, String> lockMap = new ConcurrentHashMap<String, String>();

	// static Tuple followsAndLeader = null;
	public static List<ServerBean> follows = new ArrayList<ServerBean>();
//	public static ServerBean newfollow = null;

	public static void main(String[] args) {
		ServerBean server;
		// follower
		if (args.length == 2) {
			server = new ServerBean(args[0], Integer.parseInt(args[1]), false);
			follows.add(server);
		} else { // leader
			server = SystemUtil.loadLeader();
		}

		// List<ServerBean> servers = SystemUtil.loadServers();
		// followsAndLeader = SystemUtil.getFollowsAndLeader(servers);

		try {

			ServerSocket serverSocket = new ServerSocket(server.port);

			if (server.isLeader) {
				System.out.println("Serve as the leader server at " + server.address + ":" + server.port);
				while (true) {
					new Thread(new ServerRunable(serverSocket.accept(), server.isLeader)).start();
				}
			} else {
				System.out.println("Serve as a follower server at " + server.address + ":" + server.port);
				
				//send a message to leader to refresh its follows
				ServerBean leaderBean = SystemUtil.loadLeader();
				Socket socket = new Socket(leaderBean.address, leaderBean.port);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				out.println(Message.FOLLOW + " " + server.address + " " + server.port);
				System.out
						.println(Message.FOLLOW + " leader at " + leaderBean.address + " " + leaderBean.port + " " + in.readLine());
				out.close();
				while (true) {
					new Thread(new ServerRunable(serverSocket.accept(), server.isLeader)).start();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

/**
 * This class is servered for multi-thread
 */
class ServerRunable implements Runnable {

	private Socket socket = null;
	private boolean isLeader = false;

	/**
	 *
	 * @param socket
	 *            the accepted socket from client
	 * @param isLeader
	 *            true if it is in Leader; false otherwise
	 */
	public ServerRunable(Socket socket, boolean isLeader) {
		this.socket = socket;
		this.isLeader = isLeader;
	}

	/**
	 * @see ServerProtocol
	 * @see Message To process the request based on different message prototol
	 */
	@Override
	public void run() {
		try {

			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			ServerProtocol serverProtocol;
			if (isLeader) {
				// serverProtocol = new LeaderServerProtocol((List<ServerBean>)
				// Server.followsAndLeader.first);
				serverProtocol = new LeaderServerProtocol(Server.follows);
			} else {
				// serverProtocol = new FollowerServerProtocol((ServerBean)
				// Server.followsAndLeader.second);
				serverProtocol = new FollowerServerProtocol(SystemUtil.loadLeader()/*, Server.newfollow*/);
			}

			String input = in.readLine();

			while (input != null && !input.equals(Message.BYE)) {
				// follow¡¨»Î leader to refresh its follows
				if (input.startsWith(Message.FOLLOW)) {
					String[] ss = input.split("\\s+");
					String newip = ss[1];
					int newport = Integer.parseInt(ss[2]);
					Server.follows.add(new ServerBean(newip, newport, false));
					out.println("SUCCESS");
					System.out.println("Follow at " + newip + ":" + newport + " joins.");
				} else if (!input.startsWith(Message.HELLO)) {

					String echo = serverProtocol.processInput(input);

					if (echo != null && echo.equals(Message.ECHO_BROADCAST)) {
						break;
					}

					if (echo != null) {
						out.println(echo);
					}

				}else {       //client firstly HELLO 
					String[] ss = input.split("\\s+");
					String clientid = ss[1];
					System.out.println("client: "+clientid+" connected");
				}

				input = in.readLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}