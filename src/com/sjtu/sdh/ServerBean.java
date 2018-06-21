package com.sjtu.sdh;

/**
 *
 * ServerBean is a java bean, describing the basic server infomation,
 * including address, port, and whether it is a leader
 */
public class ServerBean {

    String address;
    int port;
    boolean isLeader;

    /**
     *
     * @param address Server address
     * @param port  listening port
     * @param isLeader true if it is leader; false if it is follower
     */
    ServerBean(String address, int port, boolean isLeader) {
        this.address = address;
        this.port = port;
        this.isLeader = isLeader;
    }

	@Override
	public String toString() {
		return "ServerBean [address=" + address + ", port=" + port + ", isLeader=" + isLeader + "]";
	}
}