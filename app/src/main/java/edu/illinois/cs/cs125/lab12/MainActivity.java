package edu.illinois.cs.cs125.lab12;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ImageButton;
import android.app.Activity;


import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;






import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Main class for our UI design .
 */
public final class MainActivity extends AppCompatActivity {
    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "MP7:Main";

    /** Request queue for our API requests. */
    private static RequestQueue requestQueue;

    EditText latitudeText;
    EditText longitudeText;

    /**
     * Run when this activity comes to the foreground.
     *
     * @param savedInstanceState unused
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        // Set up the queue for our API requests
        requestQueue = Volley.newRequestQueue(this);

        setContentView(R.layout.activity_main);


        /*
         * Implmentaiton of Edit Texts to read lat/lon inputs
         */

        // Set up handles for each edit text.
        latitudeText = (EditText) findViewById(R.id.Lattitude);
        longitudeText = (EditText) findViewById(R.id.Longitude);

        /*
         * Set up handlers for each button in our UI. These run when the buttons are clicked.
         */
        final Button go_button = findViewById(R.id.go_button);

        go_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG,"");
                Log.d(TAG, "Go Button Clicked");
                String textOutput[] = startAPICall();
                //Log.d(TAG, textOutput[0]);
                String finalOutput = "\n";
                for (int i = 1; i < textOutput.length; i++) {
                    if (textOutput[i] != null) {
                        Log.d(TAG,"Text output line " + i + ": " + textOutput[i]);
                    finalOutput = finalOutput + textOutput[i] + "\n";
                    }
                }


                TextView textView = findViewById(R.id.text);
                textView.setText(finalOutput);
                textView.setText(textOutput[0] + "\n" + finalOutput);
                textView.setVisibility(View.VISIBLE);
                jsonResult = "No planes in the sky rn";

            }
        });

        // Implementation of Seekbar to set radius.
        SeekBar radiusBar = findViewById(R.id.seekBar2);

        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar radiusBar, int progress, boolean fromUser) {

                range = progress*5;
                TextView units = findViewById(R.id.radiusDisplay);
                units.setText(String.valueOf(range));
                units.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar radiusBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar radiusBar) {

            }
        });


    }

    /**
     * Run when this activity is no longer visible.
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    // Global string variable to store JSON response
    String jsonResult = "No planes in the sky rn";

    // Global range variable to store user range
    float range = 0;

    // Global lat/lon variables to store user position (will be dynamic later)
    double latitude = 33.748995;
    double longitude =   -84.387982;

    // AIzaSyCpNAw_Pk0Tk2YxLgojO0XL11bJdMRvX_k

    /**
     * Make a call to the open-sky network API.
     */
     String[] startAPICall() {

         // Create REST API url call based on user inputs.

         // Read position from Edit Text.

         String latText = latitudeText.getText().toString();
         String lonText = longitudeText.getText().toString();

         if(!latText.isEmpty())
             try
             {
                 latitude= Double.parseDouble(latText);
                 // it means it is double
             } catch (Exception e1) {
                 // this means it is not double
                 e1.printStackTrace();
             }

         if(!lonText.isEmpty())
             try
             {
                 longitude= Double.parseDouble(lonText);
                 // it means it is double
             } catch (Exception e1) {
                 // this means it is not double
                 e1.printStackTrace();
             }
         Log.d(TAG, "");
         Log.d(TAG, "latitude input: " + latitude);
         Log.d(TAG,"");
         Log.d(TAG, "longitude input: " + longitude);
         Log.d(TAG,"");
         // Radius [miles] > [degrees]
         float ra = range/69;

         // Create bounding box based on location + radius
         double lamin; double lamax; double lomin; double lomax;
         double la = latitude; double lo = longitude;
         lamin = la - ra; lamax = la + ra; lomin = lo - ra; lomax = lo +ra;

         String query = "https://opensky-network.org/api/states/all?" +
                 "lamin=" + lamin + "&lomin=" + lomin + "&lamax=" + lamax +
                 "&lomax=" + lomax;

         Log.w(TAG,"query: " + query);

         // MAKE THE CALL, AND STORE THE RESULT IN 'jsonResult'

             try {
                 JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                         Request.Method.GET,
                         "\n" +
                                 query,
                         null,
                         new Response.Listener<JSONObject>() {
                             @Override
                             public void onResponse(final JSONObject response) {
                                 //Log.d(TAG, response.toString());
                                 jsonResult = response.toString();
                             }
                         }, new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(final VolleyError error) {
                         Log.w(TAG, error.toString());
                     }
                 });
                 requestQueue.add(jsonObjectRequest);
             } catch (Exception e) {
                 e.printStackTrace();
             }

             Log.d(TAG, "");
             Log.d(TAG, "jsonResult:");
             Log.d(TAG, jsonResult);
             Log.d(TAG, "");

        String[] backup = {"API Call or Time parse had no result"};
        // IF THERE WAS A RESULT, DO THIS
        if (!jsonResult.equals("No planes in the sky rn")) {


            // Get JSON Object with everything
            JsonParser parser = new JsonParser();
            JsonObject rootObject = parser.parse(jsonResult).getAsJsonObject();
            JsonArray states = rootObject.get("states").getAsJsonArray();

            // Get data timestamp as string (formattedDate)
            int unixTimeStamp = rootObject.get("time").getAsInt();
            Date date = new java.util.Date(unixTimeStamp*1000L);
            SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            String formattedDate = sdf.format(date);


            // Create array of strings describing each aircraft
            JsonArray temp;
            String[] aircraftList = new String[states.size()];
            String  callsign;
            float[] position = new float[2];
            float   altitude;
            float   velocity;
            float   heading;
            for (int i = 1; i < aircraftList.length; i++) {
                temp = states.get(i).getAsJsonArray();

                callsign = temp.get(1).getAsString();
                position[0] = temp.get(6).getAsFloat();
                position[1] = temp.get(5).getAsFloat();
                altitude = temp.get(7).getAsFloat();
                velocity = temp.get(9).getAsFloat();
                heading = temp.get(10).getAsFloat();

                aircraftList[i] = "Callsign " + callsign
                        + " is at " + position[0] + " lat, "
                        + position[1] + " lon, " + altitude +
                        " m altitude, " + "with a velocity of "
                        + velocity + " m/s and heading of " + heading
                        + " degrees.";
            }
            aircraftList[0] = "This data was recorded: " + formattedDate;
            return aircraftList;
        } else {

            return backup;
        }
    }

}
