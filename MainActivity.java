
package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.speech.tts.TextToSpeech;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.Locale;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageButton imageButton;
    ImageButton camerab;
    ImageView imgview;
    Button sendButton;
    TextView texter;
    int count = 0;
    EditText editText;

    String respon;

    TextToSpeech textToSpeech;
    //private JsonObjectRequest jsonObjectRequest;
    JSONObject payload = new JSONObject();
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButton = findViewById(R.id.button);
        editText = findViewById(R.id.edittext);

        sendButton = findViewById(R.id.send);
        texter = findViewById(R.id.texter);

        camerab= findViewById(R.id.camera);
        imgview = findViewById(R.id.imageView);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);

        }
        ;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 100);

        }
        final SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query=editText.getText().toString();
                String temp= editText.getText().toString().replace(" ","");
                if (temp.length()<2){
                    texter.setText("No Query!");
                    textToSpeech.speak("No Query!",TextToSpeech.QUEUE_FLUSH,null);
                    return;
                }
                texter.setText("Chitti-ing! Please wait.");
                String url = ""+query;////GET request URL for FLASK server
                String res;

               StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        respon=response.toString();
                        respon=respon.replace("\"message\":","");
                        respon=respon.replace("\\n"," ");
                        respon=respon.replace("\\t","");
                        texter.setText("Query: "+query+" \n Response: "+respon);
                        editText.setText("");
                        textToSpeech.speak(respon,TextToSpeech.QUEUE_FLUSH,null);
                        //This code is executed if the server responds, whether or not the response contains data.
                    }
                }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //This code is executed if there is an error.
                        respon="ERROR";
                        texter.setText("Query: "+query+" \n Response: "+respon);
                        editText.setText("");
                        textToSpeech.speak(respon,TextToSpeech.QUEUE_FLUSH,null);
                    }
                });

                ExampleRequestQueue.add(ExampleStringRequest);

                //texter.setText(respon);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count == 0) {
                    count = 1;
                    //start listening
                    speechRecognizer.startListening(speechRecognizerIntent);
                } else {
                    //stop listeing
                    count = 0;
                    speechRecognizer.stopListening();
                }
            }

        });

        camerab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                imgview.setImageBitmap(null);
                Intent cameraintent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraintent,100);
            }

        });

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);
                editText.setText(data.get(0));
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String temp= editText.getText().toString().replace(" ","");
        if (temp.length()<2){
            editText.setText("Describe what you see");
        }
        textToSpeech.speak(editText.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
        texter.setText("Chitti-ing! Please wait.");
        if(requestCode==100){
            Bitmap bitmap=(Bitmap) data.getExtras().get("data");
            imgview.setImageBitmap(bitmap);
            //Toast.makeText(this, "here1111", Toast.LENGTH_SHORT).show();
            String url = "";//POST request URL for FLASK server
            StringRequest stringRequestpost=new StringRequest(Request.Method.POST,url,
                    response ->{String message="";
                        try {
                            JSONObject api_response = new JSONObject(response);
                            message = api_response.getString("desc");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        texter.setText(message);
                        textToSpeech.speak(message,TextToSpeech.QUEUE_FLUSH,null);
                    //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        editText.setText("");
                        },
                    error ->{
                        texter.setText("ERROR CONNECTING.");
                        editText.setText("Describe what you see");
            }){
                @Override
                protected Map<String,String> getParams() throws AuthFailureError{
                    Map<String,String> params = new HashMap<>();
                    params.put("image",convertBitmapToBase64(bitmap));
                    params.put("prompt",editText.getText().toString());
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(stringRequestpost);

        }

    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "GRANTED", Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(this, "DENIED", Toast.LENGTH_SHORT);
            }
        }
    }

};



