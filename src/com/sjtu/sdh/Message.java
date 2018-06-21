package com.sjtu.sdh;

/**
 *
 * Message class includes constant message type.
 */
public class Message {
    /**
     * hello
     */
    public static final String HELLO = "hello";
    public static final String BYE = "bye";
    public static final String FOLLOW = "follow";

    /**
     * echo broadcast
     */
    public static final String ECHO_BROADCAST = "echo_broadcast";

    /**
     * check whether own lock
     */
    public static final int CHECKISOWN = 0;

    /**
     * ehco checking whether own lock
     */
    public static final int ECHOCHECKISOWN = 1;

    /**
     * appy for lock
     */
    public static final int APPLY = 2;

    /**
     * echo for applying for lock
     */
    public static final int ECHOAPPLY = 3;

    /**
     * release lock
     */
    public static final int RELEASE = 4;

    /**
     * echo for releasing lock
     */
    public static final int ECHORELEASE = 5;

    /**
     * broadcast
     */
    public static final int BROADCAST = 6;

}
