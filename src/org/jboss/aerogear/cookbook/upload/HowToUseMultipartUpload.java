package org.jboss.aerogear.cookbook.upload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.jboss.aerogear.android.Pipeline;
import org.jboss.aerogear.android.impl.pipeline.MultipartRequestBuilder;
import org.jboss.aerogear.android.impl.pipeline.PipeConfig;
import org.jboss.aerogear.android.pipeline.AbstractActivityCallback;
import org.jboss.aerogear.android.pipeline.LoaderPipe;
import org.jboss.aerogear.cookbook.Constants;
import org.jboss.aerogear.cookbook.R;
import org.jboss.aerogear.cookbook.model.Avatar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class HowToUseMultipartUpload extends Activity {

    private int requestCode = 1;
    private ImageView image;
    private TextView name;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);

        image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, requestCode);
            }
        });

        name = (TextView) findViewById(R.id.name);

        Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((resultCode == RESULT_OK) && (requestCode == this.requestCode)) {
            Uri imageURI = data.getData();
            image.setImageURI(imageURI);
        }
    }

    private void sendImage() {
        try {
            progressDialog = ProgressDialog.show(HowToUseMultipartUpload.this,
                    "Sending...", "Wait, the data is seding", true);

            URL url = new URL(Constants.URL_BASE);

            PipeConfig config = new PipeConfig(url, Avatar.class);
            config.setRequestBuilder(new MultipartRequestBuilder<Avatar>());

            Pipeline pipeline = new Pipeline(url);
            pipeline.pipe(Avatar.class, config);

            LoaderPipe<Avatar> pipe = pipeline.get("avatar", this);
            pipe.save(createAvatar(), new SaveCallBack());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void displaySuccess() {
        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
    }

    public void displayError() {
        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
    }

    private Avatar createAvatar() {
        Avatar avatar = new Avatar();
        avatar.setName(name.getText().toString());

        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bitmapdata);

        avatar.setPhoto(inputStream);

        return avatar;
    }

    private static class SaveCallBack extends AbstractActivityCallback<Avatar> {
        
        public SaveCallBack() {
            super(SaveCallBack.class);
        }
        
        @Override
        public void onSuccess(Avatar data) {
            HowToUseMultipartUpload activity = (HowToUseMultipartUpload) getActivity();
            activity.displaySuccess();
        }

        @Override
        public void onFailure(Exception e) {
            HowToUseMultipartUpload activity = (HowToUseMultipartUpload) getActivity();
            activity.displayError();
        }
    }

}
