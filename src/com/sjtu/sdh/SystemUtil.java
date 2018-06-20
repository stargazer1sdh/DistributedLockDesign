package com.sjtu.sdh;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * System Util class, including some basic functions, like load servers, send
 * TCP message.
 */

public class SystemUtil {

	/**
	 * get all the servers from config file (serverConfig.xml)
	 * 
	 * @see ServerBean
	 * @return servers
	 */
	public static ServerBean loadLeader() {

		ServerBean server = null;

		try {
			File configFile = new File("leaderServerConfig.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(configFile);

			doc.getDocumentElement().normalize();

			Node node = doc.getElementsByTagName("server").item(0);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				String address = element.getElementsByTagName("address").item(0).getTextContent();
				int port = Integer.valueOf(element.getElementsByTagName("port").item(0).getTextContent());
//				boolean isLeader = Boolean.valueOf(element.getElementsByTagName("isLeader").item(0).getTextContent());
				server = new ServerBean(address, port, true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return server;
	}

	/**
	 * get the tuple of both leader and follower
	 *
	 * @see Tuple
	 *
	 * @param servers
	 *            all the servers in system
	 * @return the tuple of both leader and follower servers
	 */
//	public static Tuple<List<ServerBean>, ServerBean> getFollowsAndLeader(List<ServerBean> servers) {
//
//		List<ServerBean> follows = new ArrayList<ServerBean>();
//		ServerBean leader = null;
//		for (int i = 0; i < servers.size(); i++) {
//			ServerBean bean = servers.get(i);
//			if (!bean.isLeader) {
//				follows.add(servers.get(i));
//			} else {
//				leader = bean;
//			}
//		}
//		return new Tuple(follows, leader);
//	}

	/**
	 *
	 * @param msg
	 *            message, in json string
	 * @param address
	 *            address
	 * @param port
	 *            linstening port
	 * @param isBroadCast
	 *            true if it is a broadcast messge, false otherwise
	 * @return echo message
	 */
	public static String sendTCPMsg(String msg, String address, int port, boolean isBroadCast) {
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			socket = new Socket(address, port);
			out = new PrintWriter(socket.getOutputStream(), true);

			out.println(msg);

			if (!isBroadCast) {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				return in.readLine();
			}
			// if is broadcast, return ECHO_BROADCAST
			return Message.ECHO_BROADCAST;

		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("UnknowHost Error");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO Error");
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}

/**
 * Tuple class
 *
 * @param <X>
 * @param <Y>
 */
//class Tuple<X, Y> {
//	public final X first;
//	public final Y second;
//
//	public Tuple(X x, Y y) {
//		this.first = x;
//		this.second = y;
//	}
//}

/**
 * Simple message format class for JSON. It includes message type and message
 * content.
 */
class SimpleMsg {

	protected int messageType;
	protected Object messageContent;

	public SimpleMsg() {

	}

	/**
	 * @see Message
	 * @param messageType
	 *            message type
	 * @param messageContent
	 *            message content, is mainly lock key
	 */
	public SimpleMsg(int messageType, Object messageContent) {

		this.messageType = messageType;
		this.messageContent = messageContent;
	}

	/**
	 * getter
	 * 
	 * @see Message
	 * @return message type
	 */
	public int getMessageType() {
		return messageType;
	}

	/**
	 * setter
	 * 
	 * @see Message
	 * @param messageType
	 *            message type
	 */
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	/**
	 * getter
	 * 
	 * @return message content
	 */
	public Object getMessageContent() {
		return messageContent;
	}

	/**
	 * setter
	 * 
	 * @param messageContent
	 *            message content
	 */
	public void setMessageContent(Object messageContent) {
		this.messageContent = messageContent;
	}

	@Override
	public String toString() {
		return "SimpleMsg [messageType= " + messageType + ", messageContent= " + messageContent + "]";
	}
}

/**
 * @see SimpleMsg The class is client message, inherits SimpleMsg.
 *
 *      Add the client id attribute.
 */
class ClientMsg extends SimpleMsg {

	private String clientId;

	/**
	 * default constructor
	 */
	public ClientMsg() {

	}

	/**
	 * @see Message
	 * @param messageType
	 *            message type
	 * @param messageContent
	 *            message content
	 * @param clientId
	 *            client id
	 */
	public ClientMsg(int messageType, Object messageContent, String clientId) {

		super(messageType, messageContent);

		this.clientId = clientId;
	}

	/**
	 * getter
	 * 
	 * @return client id
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * setter
	 * 
	 * @param clientId
	 *            client id
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public String toString() {
		return "ClientMsg [messageType= " + messageType + ", messageContent= " + messageContent + ", clientId= "
				+ clientId + "]";

	}
}
