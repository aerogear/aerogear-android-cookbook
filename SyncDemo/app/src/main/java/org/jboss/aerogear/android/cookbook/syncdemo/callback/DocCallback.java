package org.jboss.aerogear.android.cookbook.syncdemo.callback;

import android.util.Log;
import android.widget.Toast;

import org.jboss.aerogear.android.cookbook.syncdemo.DocsActivity;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;
import org.jboss.aerogear.android.cookbook.syncdemo.vo.Doc;

import java.util.List;

public class DocCallback extends AbstractActivityCallback<Doc> {

    private static final String TAG = "DocsCallback";

    public DocCallback(){
        super(TAG);
    }
    
    @Override
    public void onSuccess(Doc doc) {
        final DocsActivity activity = (DocsActivity) getActivity();
        PipeManager.getPipe("docs", activity).read(new AbstractActivityCallback<List<Doc>>() {
            @Override
            public void onSuccess(List<Doc> docs) {
                activity.refreshDocs(docs);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        });
        
        Toast.makeText(activity, "Save successful", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(Exception e) {
        Log.e(TAG, e.getMessage(), e);
    }
}
