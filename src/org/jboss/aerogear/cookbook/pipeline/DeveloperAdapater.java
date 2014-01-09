package org.jboss.aerogear.cookbook.pipeline;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.jboss.aerogear.cookbook.R;
import org.jboss.aerogear.cookbook.model.Developer;

import java.util.List;

public class DeveloperAdapater extends BaseAdapter {

    private final Context context;
    private final List<Developer> developers;

    public DeveloperAdapater(Context context, List<Developer> developers) {
        this.context = context;
        this.developers = developers;
    }

    @Override
    public int getCount() {
        return developers.size();
    }

    @Override
    public Developer getItem(int position) {
        return developers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View developerView = LayoutInflater.from(context).inflate(R.layout.developer, null);

        final Developer developer = getItem(position);

        ImageView photo = (ImageView) developerView.findViewById(R.id.photo);

        Picasso.with(context)
                .load(developer.getPhotoURL())
                .placeholder(R.drawable.developer_placeholder)
                .into(photo);

        TextView name = (TextView) developerView.findViewById(R.id.name);
        name.setText(developer.getName());

        TextView twitter = (TextView) developerView.findViewById(R.id.twitter);
        twitter.setText("@" + developer.getTwitter());
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri adress = Uri.parse("http://twitter.com/" + developer.getTwitter());
                Intent browser = new Intent(Intent.ACTION_VIEW, adress);
                context.startActivity(browser);
            }
        });

        return developerView;
    }

}
