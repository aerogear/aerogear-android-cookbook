package org.jboss.aerogear.syncdemo.sync;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;

import org.jboss.aerogear.android.sync.SyncServerConnectionListener;
import org.jboss.aerogear.android.sync.SyncService;

/**
 * Created by summers on 1/28/15.
 */
public abstract class SyncActivity extends ActionBarActivity implements SyncServerConnectionListener {

    private SyncService service;
    private ServiceConnection serviceConnection;


    @Override
    protected void onStart() {
        super.onStart();
        serviceConnection  = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                final SyncService syncService = ((SyncService.SyncServiceBinder)service).getService();
                SyncActivity.this.service = syncService;
                syncService.subscribe(SyncActivity.this);

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                finish();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent syncServiceIntent = new Intent(this, SyncService.class);
        bindService(syncServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getSyncService() != null) {
            service.unsubscribe(this);
            service = null;
        }
        unbindService(serviceConnection);
    }

    public SyncService getSyncService() {
        return service;
    }

}
