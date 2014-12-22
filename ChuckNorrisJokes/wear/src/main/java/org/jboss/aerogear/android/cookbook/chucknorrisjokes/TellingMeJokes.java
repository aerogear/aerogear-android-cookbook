package org.jboss.aerogear.android.cookbook.chucknorrisjokes;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class TellingMeJokes extends Activity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    private static final String TAG = "ManageTokens";
    private GridViewPager pager;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private String jokeText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telling_me_jokes);

        jokeText = getResources().getString(R.string.swipe_to_fetch_joke);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        pager = (GridViewPager) findViewById(R.id.pager);
        pager.setBackgroundResource(R.drawable.chuck_norris);
        pager.setAdapter(new GridPagerAdapter() {
            @Override
            public int getRowCount() {
                return 1;
            }

            @Override
            public int getColumnCount(int i) {
                    return 2;
            }



            @Override
            protected Object instantiateItem(ViewGroup viewGroup, int row, int col) {
                final View view;


                switch (col) {
                    case 0:
                        view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.joke, viewGroup, false);
                        ((TextView)view.findViewById(R.id.joketext)).setText(jokeText);
                        break;
                    case 1:

                        view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.fetch_joke, viewGroup, false);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                                    @Override
                                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                                        Log.d(TAG, "sending fetchRequest");
                                        for (Node n : getConnectedNodesResult.getNodes()) {
                                            Log.d(TAG, "Node : "+ n.getId());
                                            Wearable.MessageApi.sendMessage(mGoogleApiClient, n.getId(), "/jokes/request", null);
                                        }
                                    }
                                });

                            }
                        });
                        break;

                    default:
                        throw new IllegalStateException(getResources().getString(R.string.view_overflow_error));
                }


                viewGroup.addView(view);
                return view;
            }

            @Override
            protected void destroyItem(ViewGroup viewGroup, int row, int col, Object o) {
                viewGroup.removeView((View) o);
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "Connected to Google Api Service");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Suspended Connection to Google Api Service");
    }

    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Connection to Google Api Service Failed");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Message Received : " + new String(messageEvent.getData()));
        jokeText = new String(messageEvent.getData());
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                pager.getAdapter().notifyDataSetChanged();
                pager.scrollTo(0, 0);
            }
        });

    }
}
