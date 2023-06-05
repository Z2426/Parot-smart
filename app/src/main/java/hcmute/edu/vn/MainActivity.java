package hcmute.edu.vn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public String target;
    public String source;

    private TextToSpeech tts1, tts2;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    String selected_result_src,selected_result_des;
    Spinner select_src,select_des;
    Button buttonUpload , buttonTranslated;
    EditText  idEdtSource;
    ImageView iv_mic, iv_sound1, iv_sound2;
    TextView result;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonUpload=findViewById(R.id.bt_upload);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUploadLayout(v);
            }
        });

        iv_mic = findViewById(R.id.idIVmic);
        iv_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak(source);
            }
        });

        //menu select
        // Get reference to the Spinner
        select_src = findViewById(R.id.idFromSpinner);
        result=findViewById(R.id.idTVTranslatedTV);

        // Create an ArrayAdapter and set it as the adapter for the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.language_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_src.setAdapter(adapter);

        // Set an item selected listener to capture the user's selection
        select_src.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                selected_result_src = parent.getItemAtPosition(position).toString();
                source = selected_result_src;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //pass

            }
        });

        // Get reference to the Spinner
        select_des = findViewById(R.id.idToSpinner);

        // Create an ArrayAdapter and set it as the adapter for the Spinner
        ArrayAdapter<CharSequence> adapter_des = ArrayAdapter.createFromResource(
                this,
                R.array.language_options,
                android.R.layout.simple_spinner_item
        );
        adapter_des.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_des.setAdapter(adapter_des);

        // Set an item selected listener to capture the user's selection
        select_des.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                selected_result_des = parent.getItemAtPosition(position).toString();
                target = selected_result_des;
                // Do something with the selected item

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "Please select !!", Toast.LENGTH_SHORT).show();

            }
        });
        //show text src
        String textResult = getIntent().getStringExtra("text_result");
        idEdtSource  = findViewById(R.id.idEdtSource);
        idEdtSource.setText(textResult);
        buttonTranslated = findViewById(R.id.idBtnTranslate);
        buttonTranslated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateText(target, source);
            }
        });
        iv_sound1 = findViewById(R.id.idIVsound1);
        tts1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR){
                    if (source == "en"){
                        tts1.setLanguage(Locale.ENGLISH);
                    }else if (source == "vi"){
                        tts1.setLanguage(new Locale("vi"));
                    }else {
                        Toast.makeText(MainActivity.this, "Language not supported!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        iv_sound1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = idEdtSource.getText().toString();
                tts1.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        iv_sound2 = findViewById(R.id.idIVsound2);
        tts2 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR){
                    if (target == "en"){
                        tts1.setLanguage(Locale.ENGLISH);
                    }else if (target == "vi"){
                        tts1.setLanguage(new Locale("vi"));
                    }else {
                        Toast.makeText(MainActivity.this, "Language not supported!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        iv_sound2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = result.getText().toString();
                tts2.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

    }
    public void translateText(String target, String source){
        if(TextUtils.isEmpty(idEdtSource.getText().toString())){
            Toast.makeText(MainActivity.this, "No text allowed", Toast.LENGTH_SHORT).show();
        }else{
            TranslatorOptions options = new TranslatorOptions.Builder()
                    .setTargetLanguage(target)
                    .setSourceLanguage(source)
                    .build();
            Translator translator = Translation.getClient(options);

            String sourceText = idEdtSource.getText().toString();
            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Downloading the translation model...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                }
            });


            Task<String> results = translator.translate(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String s) {
                    result.setText(s);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void speak(String source) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, source);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please Speaking");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:{
                if (resultCode == RESULT_OK && null != data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    idEdtSource.setText(result.get(0));
                }
                break;
            }
        }
    }
    public void gotoUploadLayout(View view) {
        Intent intent = new Intent(MainActivity.this, upload.class);
        startActivity(intent);
    }

}