package edu.illinois.cs.cs125.lab12;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.Arrays;

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

        // Create button
        final Button refreshButton = findViewById(R.id.go_button);

        /*
         * Set up handlers for each button in our UI. These run when the buttons are clicked.
         */
        final Button go_button = findViewById(R.id.go_button);
        go_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Open file button clicked");
                TextView textView = findViewById(R.id.text);
                textView.setText("Hello bitch");
                textView.setVisibility(View.VISIBLE);
                //startGo_button();
            }
        });

        /**
         * Make the go_button do something .
         */
//        private void startGo_button() {
//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("image/*");
//            startActivityForResult(intent, READ_REQUEST_CODE);
//        }



        //specify an action when the button is pressed,
        // set a click listener on the button object in the corresponding activity code:
        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                // Code here executes on main thread after user presses button


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

    /**
     * Make a call to the open-sky network API.
     */
    void startAPICall() {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://opensky-network.org/api/states/all?time=0",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            //Log.d(TAG, response.toString());
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
    }

    /**
     * Refresh weather button.
     * @param view not sure what this does
     *
     */
    public void getWeather(final View view) {
        Log.d(TAG, "button was pressed");
    }
}
