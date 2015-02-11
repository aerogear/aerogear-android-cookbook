package org.jboss.aerogear.syncdemo;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;
import org.jboss.aerogear.syncdemo.sync.DiffSyncMainActivity;
import org.jboss.aerogear.syncdemo.vo.Doc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class DocsActivity extends ActionBarActivity {


    private static final String TAG = DocsActivity.class.getSimpleName();
    private ListView listView;
    private static final String IGNORE_EXTRAS = "MessageActivity.ignore_extras";
    private List<Doc> documents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_docs);

        View emptyView = findViewById(R.id.empty);
        listView = (ListView) findViewById(R.id.messages);
        listView.setEmptyView(emptyView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newIntent = new Intent(getApplicationContext(), DiffSyncMainActivity.class);
                newIntent.putExtra(DiffSyncMainActivity.DOCUMENT_ID, documents.get(position).getDocId());
                startActivity(newIntent);
            }
        });

        ListView listView = (ListView) findViewById(R.id.messages);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(listView);
        fab.setOnClickListener(showAddDoc);
    }

    @Override
    protected void onResume() {
        super.onResume();

        displayMessages();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IGNORE_EXTRAS, true);
    }

    private void displayMessages() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.document_item, new String[]{});
        listView.setAdapter(adapter);
        PipeManager.getPipe("docs", this).read(new AbstractActivityCallback<List<Doc>>() {
            @Override
            public void onSuccess(List<Doc> docs) {
                ((DocsActivity) getActivity()).refreshDocs(docs);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        });
    }

    private View.OnClickListener showAddDoc = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new DialogFragment() {

                {
                    setTitle("Doc Name?");
                }

                @Nullable
                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                    final View layout = inflater.inflate(R.layout.add_doc_dialog, null);

                    layout.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });

                    layout.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            createDoc(((TextView) layout.findViewById(R.id.document_name)).getText());
                            dismiss();
                        }
                    });

                    return layout;
                }
            }.show(getFragmentManager(), ":");
        }
    };

    private void createDoc(CharSequence text) {
        Doc doc = new Doc();
        doc.setDocName(text.toString());
        doc.setDocId(UUID.randomUUID().toString());
        PipeManager.getPipe("docs", this).save(doc, new DocCallback());
    }

    public void refreshDocs(List<Doc> docs) {
         this.documents = docs;
        ArrayList<String> docNameArray = new ArrayList<>(docs.size());
        for (Doc doc : docs) {
            docNameArray.add(doc.getDocName());
        }

        PipeManager.getPipe("docs", this).reset();//data load done

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.document_item, docNameArray.toArray(new String[docs.size()]));
        listView.setAdapter(adapter);
    }
}
