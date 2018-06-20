package com.sjtu.sdh;

import com.google.gson.Gson;

/**
 *
 * ServerProtocol is the abstract class for message protocol.
 *
 * @see LeaderServerProtocol
 * @see FollowerServerProtocol
 */

public abstract class ServerProtocol {

    protected Gson gson;

    /**
     * default constructor
     *
     * init Gson object
     */
    public ServerProtocol() {
        gson = new Gson();
    }

    /**
     *
     * check whether the client owns the lock
     *
     * @param clientId  an UUID string represents client ID
     * @param lockKey   the key of a lock
     * @return the json message of SimpleMsg
     *
     * @see SimpleMsg
     */
    public String handleClientCheckOwn(String clientId, String lockKey) {
        SimpleMsg msg = new SimpleMsg();
        msg.setMessageType(Message.ECHOCHECKISOWN);
        msg.setMessageContent(false);

        String v = Server.lockMap.get(lockKey);
        if (v != null && v.equals(clientId)) {
            msg.setMessageContent(true);
        }
        return gson.toJson(msg);
    }

    /**
     *  process the applying of lock from client
     *
     * @param clientId an UUID string represents client ID
     * @param lockKey the key of a lock
     * @return the json message of SimpleMsg
     *
     * @see SimpleMsg
     */
    public abstract String handleClientApply(String clientId, String lockKey);

    /**
     *  process the release of lock from client
     *
     * @param clientId an UUID string represents client ID
     * @param lockKey the key of a lock
     * @return the json message of SimpleMsg
     *
     * @see SimpleMsg
     */
    public abstract String handleClientRelease(String clientId, String lockKey);

    /**
     *
     * @param input the input messge as json
     * @return the json message of SimpleMsg
     *
     * @see SimpleMsg
     */
    public abstract String processInput(String input);
}