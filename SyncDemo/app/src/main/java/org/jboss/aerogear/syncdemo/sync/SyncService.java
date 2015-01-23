package org.jboss.aerogear.syncdemo.sync;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class SyncService extends IntentService {

    public static final class SyncServiceBinder extends Binder {
        
        private final SyncService service;

        public SyncServiceBinder(SyncService service) {
            this.service = service;
        }
        
        public SyncService getService() {
            return service;
        }
        
    }
    
    public SyncService() {
        super(SyncService.class.getName());
        
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new SyncServiceBinder(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(SyncService.class.getName(), "onHandleIntent: " + intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(SyncService.class.getName(), "onCreated");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(SyncService.class.getName(), "onDestroy");
    }
}
