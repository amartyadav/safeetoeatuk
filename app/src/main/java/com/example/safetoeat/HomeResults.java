package com.example.safetoeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeResults extends AppCompatActivity {

    public static final int locationRequestCode = 1000;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private double wayLatitude = 0.0, wayLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_results);

        requestLocationPermission();

    }

    private void requestLocationPermission() {
        TextView tv_location = findViewById(R.id.tv_you_are_in);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //request for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationRequestCode);
        }
        else {
            // permission granted
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                if(location != null) {
                    wayLatitude = location.getLatitude();
                    wayLongitude = location.getLongitude();
                    tv_location.setText(String.format(Locale.UK, "%s -- %s", wayLatitude, wayLongitude));
                    Log.d("SafeToEat", wayLatitude + " " + wayLongitude);
                    getRestaurantInfo();
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        TextView tv_location = findViewById(R.id.tv_you_are_in);

        switch(requestCode) {
            case 1000: {
                // if request is cancelled, array results are empty
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                        if(location != null) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            tv_location.setText(String.format(Locale.UK, "%s -- %s", wayLatitude, wayLongitude));
                            Log.d("SafeToEat", wayLatitude + " " + wayLongitude);
                            getRestaurantInfo();
                        }
                    });
                }
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    // method to make the request to api and retrieve the results
    private void getRestaurantInfo () {
        RequestQueue queue = Volley.newRequestQueue(this);

        TextView tv_results = findViewById(R.id.tv_results);
        // below URL is for actual user lcoation. currently using hardcoded Aberdeen's Union Street's lats and longs
//        String url = "https://api.ratings.food.gov.uk/Establishments?latitude="+wayLatitude+"&longitude="+wayLongitude+"&maxDistanceLimit=5";

        //values for union street
        // 57.14544702928209, -2.1030610993024093
        String url = "https://api.ratings.food.gov.uk/Establishments?latitude="+"57.14544702928209"+"&longitude="+"-2.1030610993024093"+"&maxDistanceLimit=5";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        tv_results.setText(response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tv_results.setText(error.getMessage());
//                        Log.d("SafeToEat", error.getMessage());
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-api-version", "2");

                return params;
            }
        };
        queue.add(jsonArrayRequest);
    }

}