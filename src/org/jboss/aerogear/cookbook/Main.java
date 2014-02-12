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
package org.jboss.aerogear.cookbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import org.jboss.aerogear.cookbook.authentication.HowToUseAuthentication;
import org.jboss.aerogear.cookbook.authentication.HowToUseHttpBasicAuthentication;
import org.jboss.aerogear.cookbook.datamanager.HowToUseMemoryStorage;
import org.jboss.aerogear.cookbook.pipeline.HowToUseCustomPipe;
import org.jboss.aerogear.cookbook.pipeline.HowToUsePipe;
import org.jboss.aerogear.cookbook.pipeline.HowToUsePipeWithPagination;
import org.jboss.aerogear.cookbook.push.HowToUsePushActivity;
import org.jboss.aerogear.cookbook.upload.HowToUseMultipartUpload;

public class Main extends Activity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button pipe = (Button) findViewById(R.id.pipe);
        pipe.setOnClickListener(this);

        Button paginationPipe = (Button) findViewById(R.id.paginationPipe);
        paginationPipe.setOnClickListener(this);

        Button customPipe = (Button) findViewById(R.id.customPipe);
        customPipe.setOnClickListener(this);

        Button memoryStorage = (Button) findViewById(R.id.memoryStorage);
        memoryStorage.setOnClickListener(this);

        Button authentication = (Button) findViewById(R.id.authetication);
        authentication.setOnClickListener(this);

        Button httpBasicAuthentication = (Button) findViewById(R.id.httpBasicAuthetication);
        httpBasicAuthentication.setOnClickListener(this);

        Button pushDemo = (Button) findViewById(R.id.pushDemo);
        pushDemo.setOnClickListener(this);

        Button upload = (Button) findViewById(R.id.upload);
        upload.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Class display = null;

        switch (view.getId()) {
        case R.id.pipe:
            display = HowToUsePipe.class;
            break;
        case R.id.paginationPipe:
            display = HowToUsePipeWithPagination.class;
            break;
        case R.id.customPipe:
            display = HowToUseCustomPipe.class;
            break;
        case R.id.memoryStorage:
            display = HowToUseMemoryStorage.class;
            break;
        case R.id.authetication:
            display = HowToUseAuthentication.class;
            break;
        case R.id.httpBasicAuthetication:
            display = HowToUseHttpBasicAuthentication.class;
            break;
        case R.id.pushDemo:
            display = HowToUsePushActivity.class;
            break;
        case R.id.upload:
            display = HowToUseMultipartUpload.class;
            break;
        }

        startActivity(new Intent(this, display));
    }
}
