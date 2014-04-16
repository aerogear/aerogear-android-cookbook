package net.saga.oauthtestsing.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DriveFragment extends Fragment {

    private final List<Files> fileses;

    public DriveFragment(List<Files> fileses) {
        this.fileses = fileses;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drive_list, null);

        ListView driveItems = (ListView) view.findViewById(R.id.drive_items);

        driveItems.setAdapter(new ArrayAdapter<Files>(getActivity(), android.R.layout.simple_list_item_1, fileses) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Files files = fileses.get(position);

                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.drive_list_item, null);
                }

                ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                Picasso.with(getActivity()).load(files.getIconLink()).into(imageView);

                TextView name = (TextView) convertView.findViewById(R.id.textView);
                name.setText(files.getTitle());

                return convertView;
            }
        });

        return view;
    }

}
