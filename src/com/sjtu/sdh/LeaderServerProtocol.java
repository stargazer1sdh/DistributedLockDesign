package com.sjtu.sdh;

import java.util.List;

/**
 *
 * @see ServerProtocol
 *
 * This class inherits from ServerProtocol,
 * for handling the message to Leader Server.
 */
public class LeaderServerProtocol extends ServerProtocol {

    private List<ServerBean> follows;

    /**
     *
     * @param follows  Follower Servers
     * @see ServerBean
     */
    public LeaderServerProtocol(List<ServerBean> follows) {
        super();
        this.follows = follows;
    }

    /**
     * broadcast the new map to each follower server
     * when the map is modified.
     */
    private void broadcast() {

        ClientMsg broadcastMsg = new ClientMsg();
        broadcastMsg.setMessageType(Message.BROADCAST);
        broadcastMsg.setMessageContent(gson.toJson(Server.lockMap));
        String msg = gson.toJson(broadcastMsg);

        for (ServerBean followServer : follows) {
        	 SystemUtil.sendTCPMsg(msg, followServer.address, followServer.port, true);
//            SystemUtil.sendTCPMsg(msg, SystemUtil.LOCALADDRESS, followServer.port, true);
        }
    }

    /**
     *
     * process the message of applying the lock
     *
     * @param clientId an UUID string represents client ID
     * @param lockKey the key of a lock
     * @return the json message of SimpleMsg
     * @see SimpleMsg
     */
    @Override
    public String handleClientApply(String clientId, String lockKey) {
        SimpleMsg msg = new SimpleMsg();
        msg.setMessageType(Message.ECHOAPPLY);
        msg.setMessageContent(false);
        if (Server.lockMap.get(lockKey) == null) {
            // if is null, nobody lock it
            // this client can lock it
            Server.lockMap.put(lockKey, clientId);
            msg.setMessageContent(true);

            broadcast();

        }

        return gson.toJson(msg);
    }

    /**
     *
     * process the message of releasing the lock
     * @param clientId an UUID string represents client ID
     * @param lockKey the key of a lock
     * @return the json message of SimpleMsg
     * @see SimpleMsg
     */
    @Override
    public String handleClientRelease(String clientId, String lockKey) {
        SimpleMsg msg = new SimpleMsg();
        msg.setMessageType(Message.ECHORELEASE);
        msg.setMessageContent(false);

        String owner = Server.lockMap.get(lockKey);

        if (owner != null && owner.equals(clientId)) {
            Server.lockMap.remove(lockKey);
            msg.setMessageContent(true);

            // broadcast
            broadcast();
        }

        return gson.toJson(msg);
    }

    /**
     * process the input message based on its type
     * @param input input message, the json of ClientMsg
     * @return the json message of SimpleMsg
     * @see Message
     * @see ClientMsg
     */
    @Override
    public String processInput(String input) {

        ClientMsg msg = gson.fromJson(input, ClientMsg.class);
        String result;

        switch (msg.getMessageType()) {
            case Message.CHECKISOWN:
                result = handleClientCheckOwn(msg.getClientId(),
                        (String)msg.getMessageContent());
                break;
            case Message.APPLY:
                result = handleClientApply(msg.getClientId(),
                        (String)msg.getMessageContent());
                break;
            case Message.RELEASE:
                result = handleClientRelease(msg.getClientId(),
                        (String)msg.getMessageContent());
                break;
            default:
                result = "";

        }
        return result;
    }

}
