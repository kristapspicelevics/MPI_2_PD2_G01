package lv.kristaps.mapreader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ApiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);
        new ParseTask().execute();
    }
    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;

        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try {
                String $url_json = "https://www.balldontlie.io/api/v1/teams";
                URL url = new URL($url_json);


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }


        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            final ListView lView = (ListView) findViewById(R.id.lvMain);

            String[] from = {"name_item"};
            int[] to = {R.id.name_item};
            ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> hashmap;

            try {
                JSONObject json = new JSONObject(strJson);
                JSONArray jsonArray = json.getJSONArray("data");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject friend = jsonArray.getJSONObject(i);

                    String teamName = friend.getString("full_name");
                    String conference = friend.getString("conference");
                    String abbreviation = friend.getString("abbreviation");

                    hashmap = new HashMap<String, String>();
                    hashmap.put("name_item", "" + conference + " / "+abbreviation +" / "+ teamName );
                    arrayList.add(hashmap);
                }

                final SimpleAdapter adapter = new SimpleAdapter(ApiActivity.this, arrayList, R.layout.item, from, to);
                lView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.map:
                intent = new Intent(ApiActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}