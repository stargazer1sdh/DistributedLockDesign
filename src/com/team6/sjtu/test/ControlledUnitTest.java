package com.team6.sjtu.test;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sjtu.sdh.DistributedClient;

import java.util.ArrayList;
import java.util.*;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by chenzhongpu on 3/20/16.
 *
 * This is the contolled unit test
 */
public class ControlledUnitTest{

    private static List<String> lockKeys;

    private static DistributedClient clientOne;

    private static DistributedClient clientTwo;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        String[] locks = new String[]{"lockkey1", "lockkey2", "lockkey3", "lockkey4", "lockkey5"};

        lockKeys = new ArrayList<String>();
        lockKeys.addAll(Arrays.asList(locks));

        List<String> serverAddressList = new ArrayList<String>();
        serverAddressList.add("127.0.0.1");
        serverAddressList.add("127.0.0.2");
        serverAddressList.add("127.0.0.3");

        Random randomizer = new Random();

        clientOne = new DistributedClient(serverAddressList.get(randomizer.nextInt(serverAddressList.size())));
        clientTwo = new DistributedClient(serverAddressList.get(randomizer.nextInt(serverAddressList.size())));

    }

    @Test
    public void testControlled() {

        List<Boolean> results = new ArrayList<Boolean>();

        Random randomizer = new Random();
        int randomIndex = randomizer.nextInt(lockKeys.size());
        String lockKeyOne = lockKeys.get(randomIndex);
        String lockKeyTwo = lockKeys.get((randomIndex + 1) % lockKeys.size());

        results.add(clientOne.checkIsOwn(lockKeyOne)); // false
        results.add(clientOne.tryLock(lockKeyOne));  // true
        results.add(clientTwo.tryLock(lockKeyOne)); // false
        results.add(clientTwo.tryLock(lockKeyTwo)); // true
        results.add(clientOne.unLock(lockKeyOne)); // true
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("Thread Error");
        }
        results.add(clientOne.checkIsOwn(lockKeyOne)); // false
        results.add(clientTwo.tryLock(lockKeyOne)); // true
        results.add(clientTwo.unLock(lockKeyTwo)); // true
        results.add(clientTwo.unLock(lockKeyOne)); // true

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("Thread Error");
        }


        assertArrayEquals(results.toArray(),
                new Boolean[]{false, true, false, true, true, false, true, true, true});

    }
}

