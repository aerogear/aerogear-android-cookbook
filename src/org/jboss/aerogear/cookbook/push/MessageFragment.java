package org.jboss.aerogear.cookbook.push;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.jboss.aerogear.cookbook.R;

public class MessageFragment extends Fragment {

    private final String message;

    public MessageFragment(String message) {
        this.message = message;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message, null);

        TextView textView = (TextView) view.findViewById(R.id.message);
        textView.setText(message);

        return view;
    }
}
