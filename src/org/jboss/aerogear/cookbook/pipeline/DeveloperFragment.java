/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.cookbook.pipeline;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import org.jboss.aerogear.cookbook.R;
import org.jboss.aerogear.cookbook.model.Developer;

import java.util.List;

public class DeveloperFragment extends Fragment {

    private final Context context;
    private final List<Developer> developers;

    public DeveloperFragment(Context context, List<Developer> developers) {
        this.context = context;
        this.developers = developers;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.developer_list, null);

        DeveloperAdapater adapter = new DeveloperAdapater(context, developers);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(adapter);

        return view;
    }
}
