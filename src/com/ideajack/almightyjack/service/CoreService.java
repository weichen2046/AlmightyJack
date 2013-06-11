/**
 * 
 */
package com.ideajack.almightyjack.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author Chenwei
 *
 */
public class CoreService extends Service {

    private static final String LOG_TAG = "CoreService";

    /* (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate called.");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy called.");
        try {
            mBinder.StopSocketServer();
        } catch (RemoteException ex) {
            // TODO
            Log.d(LOG_TAG, "In onDestroy, " + ex.getMessage());
        }
        super.onDestroy();
    }

    private final ICoreService.Stub mBinder = new ICoreService.Stub() {

        private final ListenHandlerRunnable listenHandlerRunnable = new ListenHandlerRunnable();
        private final Thread listenThread = new Thread(listenHandlerRunnable);
        private volatile boolean listenServerRunning = false;

        @Override
        public synchronized void StartSocketServer() throws RemoteException {
            Log.d(LOG_TAG, "StartSocketServer called.");
            if (!listenServerRunning) {
                Log.d(LOG_TAG,
                        "In StartSocketServer, going to start listen socket.");
                listenThread.start();
                listenServerRunning = true;
            }
        }

        @Override
        public synchronized void StopSocketServer() throws RemoteException {
            Log.d(LOG_TAG, "StopSocketServer called.");
            if (listenServerRunning) {
                Log.d(LOG_TAG,
                        "In StopSocketServer, going to terminate listen socket.");

                /*
                 * because use network in main thread will cause throw
                 * andorid.os.NetworkOnMainThreadException, so we do it in a new
                 * thread but I don't know whether this is a good implementation
                 */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        listenHandlerRunnable.terminate();
                    }
                }).start();

                try {
                    listenThread.join();
                } catch (InterruptedException ex) {
                    // TODO
                    Log.d(LOG_TAG, "In StopSocketServer, " + ex.getMessage());
                }
                listenServerRunning = false;
            }
        }
    };
}
