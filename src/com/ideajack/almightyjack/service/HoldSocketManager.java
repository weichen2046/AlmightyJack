/**
 * 
 */
package com.ideajack.almightyjack.service;

import static com.ideajack.almightyjack.service.ICmdConstants.*;
import static com.ideajack.almightyjack.service.ICommonConstants.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

import android.util.Log;

/**
 * @author Chenwei
 *
 */
public class HoldSocketManager {

    private static final String LOG_TAG = "HoldSocketManager";
    private volatile boolean mSocketAvailable = false;
    private volatile Socket  mSocket;

    static class HoldSocketManagerHolder {
        static HoldSocketManager instance = new HoldSocketManager();
    }

    public static HoldSocketManager getInstance() {
        return HoldSocketManagerHolder.instance;
    }

    public boolean isSocketAvailable() {
        return mSocketAvailable;
    }

    public Socket getSocket() {
        synchronized(HoldSocketManagerHolder.instance) {
            if(mSocketAvailable) {
                mSocketAvailable = false;
                sendRenewSocketCmd();
                return mSocket;
            } else {
                return null;
            }
        }
    }

    public boolean updateSocket(Socket newSocket) {
        synchronized(HoldSocketManagerHolder.instance) {
            if(!mSocketAvailable) {
                mSocket = newSocket;
                mSocketAvailable = true;
                return true;
            } else {
                return false;
            }
        }
    }

    private void sendRenewSocketCmd() {
        try {
            final BufferedOutputStream out = new BufferedOutputStream(
                    mSocket.getOutputStream());
            final byte[] datas = ByteBuffer.allocate(INT_BYTES)
                    .putInt(CMD_RENEW_HOLD_SOCKET).array();
            final byte[] dataLenBytes = ByteBuffer.allocate(LONG_BYTES)
                    .putLong(datas.length).array();
            out.write(dataLenBytes);
            out.write(datas);
        } catch (IOException ex) {
            // TODO
            Log.d(LOG_TAG,
                    "In sendRenewSocketCmd, exception: " + ex.getMessage());
        }
    }
}
