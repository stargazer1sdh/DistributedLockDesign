package com.sjtu.sdh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

import com.google.gson.Gson;

public class Client {
	BufferedReader keyin; //用来录入键盘input
    public String clientId;
    private boolean isConnected;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Gson gson;
    
    public static void main(String[] args ) {
    	 if (args.length != 2) {
             System.err.println("Error: client --address --port");
             System.exit(1);
         }
    	 
    	 String serverIp = args[0];
    	 int portNumber = Integer.parseInt(args[1]);
    	 
    	 Client client = new Client(serverIp,portNumber);
    	 client.keyin = new BufferedReader(new InputStreamReader(System.in));  
    	 while (true) {
			try {
				String userinput = client.keyin.readLine();
				String[] s = userinput.split("\\s+");
				String command = s[0];
				String atrlock = s[1];
				if (userinput != null && userinput.length() > 0) {
					 if("CHECKOWN".equalsIgnoreCase(command)) {
						 System.out.println("try to "+userinput);
						 System.out.println(userinput+": "+client.checkIsOwn(atrlock));
					 }else if("LOCK".equalsIgnoreCase(command)) {
						 System.out.println("try to "+userinput);
						 System.out.println(userinput+": "+client.tryLock(atrlock));
					 }else if("UNLOCK".equalsIgnoreCase(command)) {
						 System.out.println("try to "+userinput);
						 System.out.println(userinput+": "+client.unLock(atrlock));
					 }
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}  
    }

    /**
     *
     * @param serverAddress the address of server
     * @param portNumber 
     */
    public Client(String serverAddress, int portNumber) {
        this.isConnected = connectToServer(serverAddress,portNumber);
        if (this.isConnected) {
            this.clientId = getClientId();
            this.gson = new Gson();
        }
    }

    /**
     * @see Message
     * @see ClientMsg
     *
     * @param messageType message type
     * @param messageContent message content
     * @return true if success, false otherwise
     */
    private boolean sendMsg(int messageType, String messageContent) {

        ClientMsg msg = new ClientMsg(messageType, messageContent, clientId);
 //       System.out.println(gson.toJson(msg));
        out.println(gson.toJson(msg));

        try {
        	String r = in.readLine();
            SimpleMsg echoMsg = gson.fromJson(r, SimpleMsg.class);
            // for java 6 compiler
            return ((Boolean)echoMsg.getMessageContent()).booleanValue();
//            return (boolean)echoMsg.getMessageContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * try to get the lock
     *
     * @param lockKey lock key
     * @return true if success, false otherwise
     */
    public boolean tryLock(String lockKey) {

        return sendMsg(Message.APPLY, lockKey);
    }

    /**
     * release the lock
     *
     * @param lockKey lock key
     * @return true if success, false otherwise
     */
    public boolean unLock(String lockKey) {

        return sendMsg(Message.RELEASE, lockKey);
    }

    /**
     * check whether it owns the lock
     *
     * @param lockKey
     * @return true if owns, false otherwise
     */
    public boolean checkIsOwn(String lockKey) {

        return sendMsg(Message.CHECKISOWN, lockKey);
    }

    // connect to server
    private boolean connectToServer(String serverAddress, int portNumber) {
        boolean success = false;
        try {
            socket = new Socket(serverAddress, portNumber);
            in =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            out =
                    new PrintWriter(socket.getOutputStream(), true);
            out.println(Message.HELLO+"  "+getClientId());//向服务器端发送hello,服务器在run()的in.readLine()收到
            success =  true;
            System.out.println("connected to "+serverAddress+":"+portNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    // generate client id using UUID
    private String getClientId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "Client " + clientId;
    }

}


