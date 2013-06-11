/**
 * 
 */
package com.ideajack.almightyjack.service;

import java.net.Socket;

/**
 * @author Chenwei
 *
 */
public class HoldSocketManager {

    private Socket mAvailableSocket;

    static class HoldSocketManagerHolder {
        static HoldSocketManager instance = new HoldSocketManager();
    }

    public HoldSocketManager getInstance() {
        return HoldSocketManagerHolder.instance;
    }

    public Socket getSocket() {
        return mAvailableSocket;
    }

    public void updateSocket(Socket newSocket) {
        mAvailableSocket = newSocket;
    }
}
