
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
                String url = ""//POST api hit url for Flask server
+query;
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
        if(requestCode==100){
            Bitmap bitmap=(Bitmap) data.getExtras().get("data");
            imgview.setImageBitmap(bitmap);
            Toast.makeText(this, "here1", Toast.LENGTH_SHORT).show();
            //UploadImage(bitmap);


            try {
                payload.put("image", convertBitmapToBase64(bitmap));
                payload.put("prompt", "Describe");
                // Add more key-value pairs as needed
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Make a POST request using Volley
            makePostRequest(payload);
        }

    }


    private void makePostRequest(JSONObject payload) {
        String url = "";//POST api hit url for Flask server

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, payload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the response from the server
                        Log.d("Volley Response111", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Log.e("Volley Erro2222r", "Error occurred", error);
                    }
                });

        // Add the request to the request queue
        VolleyHelper.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
    private void UploadImage(Bitmap bitmap) {
        String b64im=convertBitmapToBase64(bitmap);
        String prompt;
        String temp= String.valueOf(editText.getText());
        temp=temp.replace(" ","");
        String imurl="";//POST api hit url for Flask server

        if (temp.length()<2){
            prompt="Describe what you can make out from the image.";
        }
        else{
            prompt=String.valueOf(editText.getText());
        }

        try {
            Toast.makeText(this, "here2", Toast.LENGTH_SHORT).show();;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("image", b64im);
            jsonObject.put("prompt", prompt);

            jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,
                    ""//POST api hit url for Flask server
, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Toast.makeText(MainActivity.this, "here5", Toast.LENGTH_SHORT).show();;
                                String msg= response.getString("desc");
                                texter.setText(msg);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                   Toast.makeText(MainActivity.this, "hereer", Toast.LENGTH_SHORT).show();;
                    texter.setText("Error in handling image.");
                    Log.e("Volley Error", "Error occurred", error);

                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        Log.e("Volley Error2", "Status Code: " + networkResponse.statusCode);
                        Log.e("Volley Error33", "Response Body: " + new String(networkResponse.data));
                    }
                }
            }
            );
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        Toast.makeText(this, "here3", Toast.LENGTH_SHORT).show();;
        RequestQueue imreq = Volley.newRequestQueue(MainActivity.this);
        imreq.add(jsonObjectRequest);
        Toast.makeText(this, "here4", Toast.LENGTH_SHORT).show();;
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

/*private static class HttpGetTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
        String apiUrl = params[0];
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            Log.e("HTTP GET", "Error making GET request", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Log.d("HTTP GET", "Response: " + result);
            // Handle the response here
        } else {
            Log.e("HTTP GET", "Failed to get response");
        }
    }
}
};*/


