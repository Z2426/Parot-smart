package hcmute.edu.vn.Lib;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.IOException;

public class ML {
    public interface OnDetectionListener {
        void onDetected(String result);
        void onError(Exception e);
    }

    public void runTextRecognition(Context context, Uri imageUri, OnDetectionListener listener) {
        InputImage image;
        try {
            image = InputImage.fromFilePath(context, imageUri);
        } catch (IOException e) {
            e.printStackTrace();
            listener.onError(e);
            return;
        }

        TextRecognizer recognizer = TextRecognition.getClient();
        recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text texts) {
                        StringBuilder sb = new StringBuilder();
                        for (Text.TextBlock block : texts.getTextBlocks()) {
                            for (Text.Line line : block.getLines()) {
                                for (Text.Element element : line.getElements()) {
                                    sb.append(element.getText()).append(" ");
                                }
                                sb.append("\n");
                            }
                        }
                        String result = sb.toString();
                        // Detect activity

                        listener.onDetected(result);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        listener.onError(e);
                    }
                });
    }
}