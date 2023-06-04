package hcmute.edu.vn;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class Camera_detect extends AppCompatActivity implements SurfaceHolder.Callback {

    private Camera camera;
    private TextView result_detect;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private Button captureButton,backButton;

    private TextRecognizer textRecognizer;
    private boolean isProcessing = false;
    private Handler handler;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final long TEXT_RECOGNITION_INTERVAL = 7000; // 10 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_detect);

        surfaceView = findViewById(R.id.surfaceView);

        backButton=findViewById(R.id.backButton);
        result_detect=findViewById(R.id.txt_result);
        TextRecognizerOptions options = new TextRecognizerOptions.Builder()
                // Add any desired text recognition options here
                .build();
        textRecognizer = TextRecognition.getClient(options);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoFirstLayout(v);
            }
        });

        handler = new Handler();
        startTextRecognition();
    }

    private void startTextRecognition() {
        handler.postDelayed(textRecognitionRunnable, TEXT_RECOGNITION_INTERVAL);
    }

    private Runnable textRecognitionRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isProcessing && camera != null) {
                captureImage();
            }
            startTextRecognition();
        }
    };

    private void captureImage() {
        isProcessing = true;
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                processImage(bitmap);
                camera.startPreview();
            }
        });
    }

    private void processImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        Task<Text> result = textRecognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text visionText) {
                        String resultText = visionText.getText();
                        result_detect.setText(resultText);
                        //showToast(resultText);
                        isProcessing = false;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        showToast("Recognized text error");
                        isProcessing = false;
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(Camera_detect.this, message, Toast.LENGTH_SHORT).show();
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startCameraPreview();
        }
    }

    private void startCameraPreview() {
        try {
            camera = Camera.open();
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        requestCameraPermission();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraPreview();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void gotoFirstLayout(View view) {
        finish();
    }

}
