/* File: MainActivity.java
 * Author: Chao Gu, Qiuju Zhu, Weikai Li, Di Yuan, Binh Minh Do
 * Date: April 2018
 * Based strongly on work by Deitel et. al. 2015
 * Reference:
 * Paul Deitel, Harvey Deitel, Alexander Wald. (2015). Android™ 6 for Programmers: An App-Driven Approach, Third Edition
 * Prentice Hall, ISBN: 0-13-428936-6. Chapter 7 WeatherViewer App pp256-285
 */


package com.algonquin.fishstickandroidclient_rest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


import android.content.Context;
import android.os.AsyncTask;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<FishStick> fsList = new ArrayList<>();
    private FishStickArrayAdaper fsArrayAdapter;
    private ListView fsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // create ArrayAdapter to bind FishStickList to the FishStickListView
        fsListView = (ListView) findViewById(R.id.fsListView);
        fsArrayAdapter = new FishStickArrayAdaper(this, fsList);
        fsListView.setAdapter(fsArrayAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // get text from fsIdEditText and create web service URL
                    EditText fsIDEditText =
                            (EditText) findViewById(R.id.fsIDEditText);
                    URL url = createURL(fsIDEditText.getText().toString().trim());

                    // hide keyboard and initiate a GetFishStickTask to download
                    // stuff data from our FishStick server in a separate thread
                    if (url != null) {
                        dismissKeyboard(fsIDEditText);
                        GetFishStickTask getLocalStuffTask = new GetFishStickTask();
                        getLocalStuffTask.execute(url);
                    } else {
                        Snackbar.make(findViewById(R.id.coordinatorLayout),
                                R.string.invalid_url, Snackbar.LENGTH_LONG).show();
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    // programmatically dismiss keyboard when user touches FAB
    private void dismissKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // create web service URL using id specified, if there was an id provided by user
    private URL createURL(String id) {
        String baseUrl = getString(R.string.web_service_url);
        String urlString = null;
        try {
            if(id.length() < 1) { // search field was empty
                urlString = baseUrl;
            }
            else{ // search field has an id
                urlString = baseUrl + URLEncoder.encode(id, "UTF-8");
            }
            return new URL(urlString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null; // URL was malformed
    }

    // makes the REST web service call to get FishStick data and
    // saves the data to a local HTML file
    private class GetFishStickTask
            extends AsyncTask<URL, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(URL... params) {
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) params[0].openConnection();

                // See:
                // from: https://stackoverflow.com/questions/17735442/how-to-switch-glassfish-from-outputing-in-json-to-xml
                // from: https://stackoverflow.com/questions/44572315/change-the-default-rest-response-to-json-instead-xml?rq=1
                // from: https://stackoverflow.com/questions/14343453/java-set-accept-on-http-get
                connection.addRequestProperty("Accept","application/json");

                int response = connection.getResponseCode();

                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();

                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))) {

                        String line;

                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    }
                    catch (IOException e) {
                        Snackbar.make(findViewById(R.id.coordinatorLayout),
                                R.string.read_error, Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    // when the full set of records is returned it is enclosed in [ and ] as a JSON array
                    // when one record is returned the [ and ] are missing and the new JSONArray(string) fails
                    // This is a 'hack' for now to modify the string to enclose it in [ and ] if the [ is missing.
                    // more research needed...
                    String json = builder.toString();
                    if ( ! json.startsWith("[") ){
                        json = String.format("[%s]", json);
                    }

                    return new JSONArray(json);
                }
                else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout),
                            R.string.connect_error, Snackbar.LENGTH_LONG).show();
                }
            }
            catch (Exception e) {
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        R.string.connect_error, Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            }
            finally {
                connection.disconnect(); // close the HttpURLConnection
            }

            return null;
        }

        // process JSON response and update ListView
        @Override
        protected void onPostExecute(JSONArray fs) {
            convertJSONtoArrayList(fs); // repopulate fsList
            fsArrayAdapter.notifyDataSetChanged(); // rebind to ListView
            fsListView.smoothScrollToPosition(0); // scroll to top
        }
    }
    // create FishStick objects from JSONArray containing the FishStick records
    private void convertJSONtoArrayList(JSONArray list) {
        fsList.clear(); // clear old FishStick data

        try {

            // convert each element of list to a FishStick object
            for (int i = 0; i < list.length(); ++i) {
                JSONObject fishStick = list.getJSONObject(i); // get one fishstick's data

                // add new FishStick object to fsList
                fsList.add(new FishStick(
                        fishStick.getString("id"),
                        fishStick.getString("recordNumber"),
                        fishStick.getString("omega"),
                        fishStick.getString("lambda"),
                        fishStick.getString("uuid")));

            }
        }
        catch (JSONException e) {
            e.printStackTrace();

        }
    }
}
