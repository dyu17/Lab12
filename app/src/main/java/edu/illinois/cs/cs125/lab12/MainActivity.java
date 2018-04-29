package edu.illinois.cs.cs125.lab12;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;

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

    /**
     * Run when this activity comes to the foreground.
     *
     * @param savedInstanceState unused
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the queue for our API requests
        requestQueue = Volley.newRequestQueue(this);

        setContentView(R.layout.activity_main);

        /*
         * Set up handlers for each button in our UI. These run when the buttons are clicked.
         */
        final Button go_button = findViewById(R.id.go_button);

        go_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String textOutput[] = startAPICall();
                Log.d(TAG, "Open file button clicked");
                Log.d(TAG, textOutput[0]);
                String finalOutput = "\n";
                for (int i = 1; i < textOutput.length; i++) {
                    if (textOutput[i] != null) {
                    finalOutput = finalOutput + textOutput[i] + "\n";
                    }
                }


                TextView textView = findViewById(R.id.text);
                textView.setText(finalOutput);
                textView.setText(textOutput[0] + "\n" + finalOutput);
                textView.setVisibility(View.VISIBLE);

            }
        });


        /**
         * Make the go_button do something .
         */
//         void startGo_button() {
//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("image/*");
//            startActivityForResult(intent, READ_REQUEST_CODE);
//        }



        //specify an action when the button is pressed,
        // set a click listener on the button object in the corresponding activity code:

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

    /**
     * Make a call to the open-sky network API.
     */
     String[] startAPICall() {
        // NOTE: The current REST URL has an ICAO filter on the end to temporarily
        //       make the return string smaller. The final version will
        //       call without the fitler and parse the data.


         // MAKE THE CALL, AND STORE THE RESULT IN 'jsonResult'
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "\n" +
                            "https://opensky-network.org/api/states/" +
                            "all?lamin=33.53160370277882" +
                            "&lomin=-84.60537329722118" +
                            "&lamax=33.966386297221185" +
                            "&lomax=-84.17059070277881",
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
                        + position[1] + " lon, " + "with a velocity of "
                        + velocity + " m/s and heading of " + heading
                        + " degrees.";
            }
            aircraftList[0] = "This data was recorded: " + formattedDate;
            return aircraftList;
        }

        return backup;
    }

}
