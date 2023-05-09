package hcmute.edu.vn;

import static android.app.PendingIntent.getActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;


public class voice  extends AppCompatActivity {

    Button buttonDocument;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_voice);

        buttonDocument=findViewById(R.id.btn_document);


    }

    public void gotoFirstLayout(View view) {
        finish();
    }
}
