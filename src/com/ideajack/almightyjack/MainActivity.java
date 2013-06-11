package com.ideajack.almightyjack;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ideajack.almightyjack.service.CoreService;
import com.ideajack.almightyjack.service.ICoreService;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "MainActivity";
    private Button btnBindServer;
    private Button btnUnBindServer;
    private boolean mIsBound = false;
    private ICoreService mCoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBindServer = (Button) findViewById(R.id.btn_bind_core_server);
        btnBindServer.setOnClickListener(clickListener);
        btnUnBindServer = (Button) findViewById(R.id.btn_unbind_core_server);
        btnUnBindServer.setOnClickListener(clickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy called.");
        if (mIsBound) {
            Log.d(LOG_TAG, "onDestroy going to unbindService.");
            unbindService(serviceConn);
            mIsBound = false;
        }
        super.onDestroy();
    }

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
            case R.id.btn_bind_core_server:
                if (!mIsBound) {
                    Intent intent = new Intent(MainActivity.this, CoreService.class);
                    try {
                        bindService(intent, serviceConn, BIND_AUTO_CREATE);
                        mIsBound = true;
                    } catch (SecurityException ex) {
                        // TODO
                        Log.d(LOG_TAG, "In onClick, " + ex.getMessage());
                    }
                }
                break;
            case R.id.btn_unbind_core_server:
                if (mIsBound) {
                    unbindService(serviceConn);
                    mIsBound = false;
                }
                break;
            default:
                break;
            }
        }

    };

    private ServiceConnection serviceConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            Log.d(LOG_TAG, "onServiceConnected called.");
            // start listen server
            mCoreService = ICoreService.Stub.asInterface(service);
            try {
                mCoreService.StartSocketServer();
            } catch (RemoteException ex) {
                // TODO
                Log.d(LOG_TAG, "In onServiceConnected, " + ex.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Log.d(LOG_TAG, "onServiceDisconnected called.");
            // stop listen server
        }

    };

}
