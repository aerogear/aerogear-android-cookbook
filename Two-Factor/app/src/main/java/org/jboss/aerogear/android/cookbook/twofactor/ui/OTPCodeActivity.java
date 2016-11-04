/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.android.cookbook.twofactor.ui;

import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jboss.aerogear.android.cookbook.twofactor.R;
import org.jboss.aerogear.security.otp.Totp;

public class OTPCodeActivity extends AppCompatActivity {

    private static final int COUNTDOWN_DURATION = 30000;
    private static final int COUNTDOWN_STEP = 100; // only 100 ms for smoother action

    private TextView totpDisplay;
    private TextView nameDisplay;
    private ProgressBar progressBar;

    private Totp totp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        totpDisplay = (TextView) findViewById(R.id.totp);
        nameDisplay = (TextView) findViewById(R.id.name);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        parseOtpPath();

        progressBar.setMax(COUNTDOWN_DURATION / COUNTDOWN_STEP);

        new CountDownTimer(COUNTDOWN_DURATION, COUNTDOWN_STEP) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress((int) (millisUntilFinished / COUNTDOWN_STEP));
            }

            @Override
            public void onFinish() {
                updateOTP();
                this.start();
            }
        }.start();

    }

    private void parseOtpPath() {
        String otpauth = getIntent().getStringExtra("otpauth");
        Uri otpUri = Uri.parse(otpauth);

        String name = otpUri.getQueryParameter("issuer");
        String secret = otpUri.getQueryParameter("secret");

        if (name != null) {
            nameDisplay.setText(name);
        }

        totp = new Totp(secret);

        updateOTP();
    }

    private void updateOTP() {
        totpDisplay.setText(totp.now());
    }

}
