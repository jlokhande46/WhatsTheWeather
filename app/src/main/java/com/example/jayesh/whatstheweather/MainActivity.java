package com.example.jayesh.whatstheweather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends Activity {

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result= new StringBuilder();
            URL url ;
            HttpURLConnection connection;

            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();

                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result.append(current);
                    data = reader.read();
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint("ShowToast")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                StringBuilder message = new StringBuilder();
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0;i<arr.length();i++)
                {
                    JSONObject part = arr.getJSONObject(i);

                    Log.i("main",part.getString("main"));
                    Log.i("description",part.getString("description"));

                    String main;
                    String description;

                    main = part.getString("main");
                    description = part.getString("description");

                    if(!main.equals("") && !description.equals(""))
                    {
                        message.append(main).append(":").append(description).append("\r\n");
                    }
                }
                if(!message.toString().equals(""))
                {
                    resultTextView.setText(message.toString());
                }
                else{

                }

            } catch (Exception e) {
                Toast.makeText(MainActivity.this,
                        "Can't Find Weather", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
    EditText cityName;
    TextView resultTextView;
    public void giveWeather(View view){
        InputMethodManager imr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imr.hideSoftInputFromWindow(cityName.getApplicationWindowToken(),0);

        try {
            String city = URLEncoder.encode(cityName.getText().toString(),"UTF-8");
            DownloadTask task =new DownloadTask();
            task.execute("https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=0a7cbbfc879566a7fe6b193c3a7d3a71");

        } catch (UnsupportedEncodingException e) {
            Toast.makeText(MainActivity.this,
                    "Can't Find Weather", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.city);
        resultTextView = findViewById(R.id.resultTextView);
    }
}