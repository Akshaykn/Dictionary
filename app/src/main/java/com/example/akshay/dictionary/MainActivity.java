package com.example.akshay.dictionary;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity{
    TextView defination;
    Button search_Button;
    EditText search_word;
    String myWord;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        myWord=null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            }
        }
        search_Button=findViewById(R.id.search_button);
        defination= findViewById(R.id.definition);
        defination.setVisibility(View.INVISIBLE);
        search_word=findViewById(R.id.search_Word);







        search_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myWord = String.valueOf(search_word.getText());
                if(myWord==null){
                    Toast.makeText(getApplicationContext(),"Please select the word before search",Toast.LENGTH_SHORT).show();
                }
                if(myWord!=null) {
                    new CallbackTask().execute(dictionaryEntries(myWord));
                }else
                {
                    Toast.makeText(getApplicationContext(),"Please select the word before search",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private String dictionaryEntries(String words) {
        final String language = "en";
        final String word_id = words.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id;
    }

    private class CallbackTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            //TODO: replace with your own app id and app key
            final String app_id = "509f14c2";
            final String app_key = "4a61ff646845dddc629e82d4538b1a38";
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestProperty("app_id",app_id);
                urlConnection.setRequestProperty("app_key",app_key);

                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                return stringBuilder.toString();

            }
            catch (Exception e) {
                e.printStackTrace();

                return e.toString();
            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            defination.setVisibility(View.VISIBLE);
            String def = null;

            try {
                JSONObject myobject = new JSONObject(s);
                JSONArray  results  = myobject.getJSONArray("results");

                JSONObject lentries =  results.getJSONObject(0);
                JSONArray  results1  = lentries.getJSONArray("lexicalEntries");

                JSONObject entries = results1.getJSONObject(0);
                JSONArray  results2 = entries.getJSONArray("entries");

                JSONObject  sences = results2.getJSONObject(0);
                JSONArray   results3 = sences.getJSONArray("senses");

                JSONObject  definate= results3.getJSONObject(0);
                JSONArray   defination = definate.getJSONArray("definitions");

                def = defination.getString(0);


            } catch (JSONException e) {
                e.printStackTrace();
            }
               defination.setText(def);

        }
    }
}






