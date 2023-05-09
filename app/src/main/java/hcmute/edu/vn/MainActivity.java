package hcmute.edu.vn;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button buttonUpload , buttonVoice;
    EditText  idEdtSource;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonUpload=findViewById(R.id.btn_upload);

        buttonVoice=findViewById(R.id.btn_voice);
        String textResult = getIntent().getStringExtra("text_result");
        idEdtSource  = findViewById(R.id.idEdtSource);
        idEdtSource.setText(textResult);


    }
    public void gotoUploadLayout(View view) {
        Intent intent = new Intent(MainActivity.this, upload.class);
        startActivity(intent);
    }
    public void gotoMainAvtivity(View view) {
        Intent intent = new Intent(MainActivity.this, voice.class);
        startActivity(intent);
    }


}