/**
 * 
 */
package com.ideajack.almightyjack.service;

import static com.ideajack.almightyjack.service.ICmdConstants.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;

import android.os.SystemProperties;
import android.util.Log;
/**
 * @author Chenwei
 *
 */
public class ConnectionHandlerRunnable implements Runnable {

    private static final String LOG_TAG = "ConnectionHandlerRunnable";
    private final Socket mClient;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private static final String RESPONSE_STATUS_OK = "OK";
    private static final String RESPONSE_STATUS_ERROR = "ERROR";
    private static final int LONG_BYTES = Long.SIZE / 8;

    public ConnectionHandlerRunnable(Socket client) {
        mClient = client;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        Log.d(LOG_TAG, "Client thread is running...");
        try {
            in = new BufferedInputStream(mClient.getInputStream());
            out = new BufferedOutputStream(mClient.getOutputStream());

            if (writeResponseStatus(true)) {
                // read client cmd
                final int cmd = readClientCmd();
                Log.d(LOG_TAG, "Cmd from client is " + cmd);
                processClientCmd(cmd);
            }
        } catch (IOException ex) {
            // TODO
            Log.d(LOG_TAG, "In run, exception: " + ex.getMessage());
        } finally {
            if (mClient != null) {
                try {
                    mClient.close();
                } catch (IOException ex2) {
                    Log.d(LOG_TAG,
                            "In run, exception occurred when close the socket: "
                                    + ex2.getMessage());
                }
            }
        }
        Log.d(LOG_TAG, "Client thread exit.");
    }

    private boolean writeResponseStatus(boolean isOK) {
        boolean writeSuccessfully = false;
        final String status = (isOK) ? RESPONSE_STATUS_OK
                : RESPONSE_STATUS_ERROR;
        long totalLen = 0;
        try {
            byte[] datas = status.getBytes("UTF-8");
            totalLen += datas.length;

            // write data totalLength
            byte[] totalLenBytes = ByteBuffer.allocate(LONG_BYTES)
                    .putLong(totalLen).array();
            out.write(totalLenBytes);
            // write datas
            out.write(datas);
            out.flush();
            writeSuccessfully = true;
        } catch (NullPointerException nullEx) {
            Log.d(LOG_TAG,
                    "In writeResponseStatus, exception: " + nullEx.getMessage());
        } catch (IOException ex) {
            // TODO
            Log.d(LOG_TAG,
                    "In writeResponseStatus, exception: " + ex.getMessage());
        }
        return writeSuccessfully;
    }

    private int readClientCmd() {
        int cmd = CMD_UNKOWN;

        final byte[] cmdBuf = new byte[4];
        try {
            if(in.read(cmdBuf) > 0) {
            	cmd = ByteBuffer.wrap(cmdBuf).getInt();
            }
        } catch (NullPointerException nullEx) {
            Log.d(LOG_TAG,
                    "In readClientCmd, exception: " + nullEx.getMessage());
        } catch (IOException ex) {
            // TODO
            Log.d(LOG_TAG, "In readClientCmd, exception: " + ex.getMessage());
        }

        return cmd;
    }

    private void processClientCmd(int cmd) {
        switch (cmd) {
        case CMD_HOLD_SOCKET:
            writeResponseStatus(true);
            break;
        case CMD_GET_DISPLAY_VERSION:
            processGetDisplayVersion();
            break;
        case CMD_UNKOWN:
        default:
            writeResponseStatus(false);
            break;
        }
    }

    private void processGetDisplayVersion() {
        final String displayVer = SystemProperties.get("ro.build.display.id",
                "Unkown version");
        byte[] datas = getUTF8Bytes(displayVer);
        if(datas != null) {
            long totalLen = datas.length;
            byte[] totalLenBytes = ByteBuffer.allocate(LONG_BYTES)
                    .putLong(totalLen).array();
            if (writeResponseStatus(true)) {
                try {
                    out.write(totalLenBytes);
                    out.write(datas);
                    out.flush();
                } catch (IOException ex) {
                    Log.d(LOG_TAG, "In processGetDisplayVersion, exception: "
                            + ex.getMessage());
                }
            }
        } else {
            writeResponseStatus(false);
        }
    }

    private byte[] getUTF8Bytes(String str) {
        byte[] datas = null;
        try {
            datas = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Log.d(LOG_TAG, "In getUTF8Bytes, exception: " + ex.getMessage());
        }
        return datas;
    }

}
