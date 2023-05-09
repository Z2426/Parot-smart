package hcmute.edu.vn;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;

import hcmute.edu.vn.Lib.ML;


public class upload extends AppCompatActivity {

    private Button buttonback ,buttonImage;
    private ImageView imgphoto;
    String path;
    Uri uri;
    ML ml;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_upload);
        buttonback=findViewById(R.id.btn_back);
        buttonImage=findViewById(R.id.btn_image);
        imgphoto=findViewById(R.id.ImageView);
        ml=new ML();
        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(upload.this)
                        .crop()
                        .maxResultSize(1080,1080)
                        .start(101);


            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,@NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            // Get the image URI from the result inten
            uri = data.getData();
            ml.runTextRecognition(upload.this, uri, new ML.OnDetectionListener() {
                @Override
                public void onDetected(String result) {
                    // Display result
                    runOnUiThread(() -> {
                        Intent intent = new Intent(upload.this, MainActivity.class);
                        intent.putExtra("text_result", result);
                        startActivity(intent);
                        Toast.makeText(upload.this, result, Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(Exception e) {
                    // Display error message
                    runOnUiThread(() -> {
                        Toast.makeText(upload.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });


        }else{
            Toast.makeText(getApplicationContext(),"no image selected",Toast.LENGTH_SHORT).show();
        }
    }
    public void gotoFirstLayout(View view) {

        finish();
    }
}
