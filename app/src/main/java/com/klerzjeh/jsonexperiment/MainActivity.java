package com.klerzjeh.jsonexperiment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextView resultText;
    Button button1;
    EditText cityName;
    ImageView imageView;
    int odabir;
    SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button1 = (Button) findViewById(R.id.button1);
        cityName = (EditText) findViewById(R.id.cityName);
        resultText = (TextView) findViewById(R.id.resultText);
        imageView = (ImageView) findViewById(R.id.imageView);

        pref = this.getSharedPreferences("com.klerzjeh.jsonexperiment", Context.MODE_PRIVATE);

        int savedOdabir = pref.getInt("pozadina", odabir);
        switch (savedOdabir) {
            case 0:
                imageView.setImageResource(R.drawable.aurora);
                odabir = 0;
                break;
            case 1:
                imageView.setImageResource(R.drawable.sky);
                odabir = 1;
                break;
            case 2:
                imageView.setImageResource(R.drawable.desert);
                odabir = 2;
                break;
            case 3:
                imageView.setImageResource(R.drawable.grass);
                odabir = 3;
                break;
            case 4:
                imageView.setImageResource(R.drawable.iceberg);
                odabir = 4;
                break;
            case 5:
                imageView.setImageResource(R.drawable.nightsky);
                odabir = 5;
                break;
        }

    }


    public void findWeather(View view) {

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {

            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");

            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&APPID=e9b769b654b5d28e6f844bec0afc26a6");


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
        }
    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String message = "";
            try {
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray arrWeather = new JSONArray(weatherInfo);
                for (int i = 0; i < arrWeather.length(); i++) {
                    JSONObject jsonPart = arrWeather.getJSONObject(i);

                    String main;
                    String desc;

                    main = jsonPart.getString("main");
                    desc = jsonPart.getString("description");

                    if (!"".equals(main) && !"".equals(desc)) {
                        message += "Weather: " + main + "\r\n" + "Detailed description: " + desc;
                    }
                }
                if (!"".equals(message)) {
                    resultText.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.imagePicker) {
            return true;

        }
        switch (item.getItemId()) {
            case R.id.Aurora:
                imageView.setImageResource(R.drawable.aurora);
                odabir = 0;
                break;
            case R.id.Sky:
                imageView.setImageResource(R.drawable.sky);
                odabir = 1;
                break;
            case R.id.Desert:
                imageView.setImageResource(R.drawable.desert);
                odabir = 2;
                break;
            case R.id.Grass:
                imageView.setImageResource(R.drawable.grass);
                odabir = 3;
                break;
            case R.id.Iceberg:
                imageView.setImageResource(R.drawable.iceberg);
                odabir = 4;
                break;
            case R.id.Night_sky:
                imageView.setImageResource(R.drawable.nightsky);
                odabir = 5;
                break;
        }
        pref.edit().putInt("pozadina", odabir).apply();
        return super.onOptionsItemSelected(item);
    }


}
