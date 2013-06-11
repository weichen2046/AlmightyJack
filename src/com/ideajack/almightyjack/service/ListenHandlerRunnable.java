/**
 * 
 */
package com.ideajack.almightyjack.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

/**
 * @author Chenwei
 *
 */
public class ListenHandlerRunnable implements Runnable {

    private static final String LOG_TAG = "ListenerHandlerRunnable";
    private static final int DEFAULT_LISTEN_PORT = 8989;
    private volatile ServerSocket mListenSocket;
    private volatile boolean exit = false;
    private Thread clientThread = null;

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        Log.d(LOG_TAG, "Listen thread running...");
        try {
            mListenSocket = new ServerSocket(DEFAULT_LISTEN_PORT);
            while (true) {
                final Socket mClient = mListenSocket.accept();
                if (exit) {
                    break;
                }
                Log.d(LOG_TAG, "New client connetion arrivaled.");
                clientThread = new Thread(
                        new ConnectionHandlerRunnable(mClient));
                clientThread.setName("Client Thread");
                clientThread.start();
            }
        } catch (IOException ex) {
            // TODO something
            Log.d(LOG_TAG,
                    "In run, some exception occurred, " + ex.getMessage());
        } finally {
            if (mListenSocket != null) {
                try {
                    mListenSocket.close();
                } catch (IOException ex2) {
                    Log.d(LOG_TAG,
                            "In run, exception occurred when close listen socket: "
                                    + ex2.getMessage());
                }
            }
        }
        Log.d(LOG_TAG, "Listen thread exit.");
    }

    public void terminate() {
        exit = true;
        // Dummy connect
        Socket dummy = null;
        try {
            dummy = new Socket("127.0.0.1", DEFAULT_LISTEN_PORT);
        } catch (UnknownHostException unEx) {
            Log.d(LOG_TAG, "In terminate, exception: " + unEx.getMessage());
        } catch (IOException ex) {
            Log.d(LOG_TAG, "In terminate, exception: " + ex.getMessage());
        } finally {
            if (dummy != null) {
                try {
                    dummy.close();
                } catch (IOException ex2) {
                    // TODO
                    Log.d(LOG_TAG,
                            "In terminate, when close dummy socket, exception: "
                                    + ex2.getMessage());
                }
            }
        }
    }

}
