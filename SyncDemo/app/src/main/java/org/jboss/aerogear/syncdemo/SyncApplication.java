package org.jboss.aerogear.syncdemo;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import org.jboss.aerogear.android.authorization.AuthorizationManager;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;
import org.jboss.aerogear.syncdemo.vo.Doc;
import org.jboss.aerogear.syncdemo.vo.DocUser;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by summers on 1/29/15.
 */
public class SyncApplication extends MultiDexApplication {


    private static final String DOC_SERVER_URL = "http://192.168.11.160:8080";
    private static final String MODULE_NAME = "KeyCloakAuthz";
    private static final String TAG = SyncApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        KeycloakHelper.init();
        try {
            PipeManager.config("userPipe", RestfulPipeConfiguration.class).module(AuthorizationManager.getModule(MODULE_NAME))
                    .withUrl(new URL(DOC_SERVER_URL + "/sync/api/user"))
                    .forClass(DocUser.class);


            PipeManager.config("docs", RestfulPipeConfiguration.class).module(AuthorizationManager.getModule(MODULE_NAME))
                    .withUrl(new URL(DOC_SERVER_URL + "/sync/api/docs"))
                    .forClass(Doc.class);
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }
}
