package org.jboss.aerogear.android.cookbook.chucknorrisjokes.service;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.jboss.aerogear.android.cookbook.chucknorrisjokes.model.Joke;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.PipeManager;

import java.util.List;

public class JokeFetchListener extends WearableListenerService {

    private static final String TAG = "JokeListener";
    private static final String INCOMING_JOKE = "/jokes/incoming";
    private static final String REQUEST_JOKE = "/jokes/request";
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        Log.d(TAG, "DataChanged " + dataEvents);
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        Log.d(TAG, "Peer connected" + peer.getDisplayName());
    }

    private void connectToPlayServices() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                        // Request access only to the Wearable API
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.blockingConnect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "message received"+ messageEvent.getPath());
        connectToPlayServices();
        Log.d(TAG, "listener created");
        super.onMessageReceived(messageEvent);
        if (messageEvent.getPath().equals(REQUEST_JOKE)) {
            Log.d(TAG, "joke requested from wear");
            PipeManager.getPipe("chuckNorris").read(new Callback<List<Joke>>() {
                @Override
                public void onSuccess(List<Joke> list) {
                    sendJoke(list.get(0));
                    mGoogleApiClient.disconnect();
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    mGoogleApiClient.disconnect();
                }
            });
        }
    }

    private void sendJoke(Joke joke) {
        Log.d(TAG, "sending Joke"+ joke.getJoke());
        MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                mGoogleApiClient, getNode().getId(), INCOMING_JOKE, joke.getJoke().getBytes()).await();
        if (!result.getStatus().isSuccess()) {
            Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
        }
    }

    private Node getNode() {
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        return nodes.getNodes().get(0);
    }

}
