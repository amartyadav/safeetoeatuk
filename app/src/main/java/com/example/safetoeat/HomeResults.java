package com.example.safetoeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeResults extends AppCompatActivity {

    public static final int locationRequestCode = 1000;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private double wayLatitude = 0.0, wayLongitude = 0.0;

    //ArrayLists for various information received from the API
    public ArrayList<String> businessNames = new ArrayList<String>();
    public ArrayList<String> businessTypes = new ArrayList<String>();
    public ArrayList<String> businessRatings = new ArrayList<String>();
    public ArrayList<String> businessAddress = new ArrayList<String>();
    public ArrayList<String> localAuthorityName = new ArrayList<String>();
    public ArrayList<String> localAuthorityWebsite = new ArrayList<String>();
    public ArrayList<String> localAuthorityEmail = new ArrayList<String>();

    private String[] name;
    private String[] address;
    private String[] ratings;
    private String[] localName;
    private String[] email;
    private String[] website;

    public HomeResults() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_results);

//        //converting arrayLists to arrays
//
//        name = new String[businessNames.size()];
//        name = businessNames.toArray(name);
//        address = new String[businessAddress.size()];
//        address = businessAddress.toArray(address);
//        ratings = new String[businessRatings.size()];
//        ratings = businessRatings.toArray(ratings);
//        localName = new String[localAuthorityName.size()];
//        localName = businessNames.toArray(localName);
//        email = new String[localAuthorityEmail.size()];
//        email = businessNames.toArray(email);
//        website = new String[localAuthorityWebsite.size()];
//        website = businessNames.toArray(website);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_restaurants);

//        RestaurantsAdapter adapter = new RestaurantsAdapter(getApplicationContext(), name, address, ratings, localName, email, website);
//
//        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        // initialising the ArrayLists
        businessNames = new ArrayList<String>();
        businessTypes = new ArrayList<String>();
        businessRatings = new ArrayList<String>();
        businessAddress = new ArrayList<String>();
        localAuthorityName = new ArrayList<String>();
        localAuthorityWebsite = new ArrayList<String>();
        localAuthorityEmail = new ArrayList<String>();

        RequestQueue queue = Volley.newRequestQueue(this);

        TextView tv_results = findViewById(R.id.tv_results);
        // below URL is for actual user location. Currently using hardcoded Aberdeen's Union Street's lats and longs
//        String url = "https://api.ratings.food.gov.uk/Establishments?latitude="+wayLatitude+"&longitude="+wayLongitude+"&maxDistanceLimit=5";

        //values for union street
        // 57.14544702928209, -2.1030610993024093
        String url = "https://api.ratings.food.gov.uk/Establishments?latitude="+"57.14544702928209"+"&longitude="+"-2.1030610993024093"+"&maxDistanceLimit=5";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            tv_results.setText(response.getJSONArray("establishments").toString());

                            //iterating through the response to process the JSONObject and retrieve relevant information in ArrayLists;
                            JSONArray jsonArrayResults = response.getJSONArray("establishments");
                            for (int i = 0, j = jsonArrayResults.length(); i < j; i++) {
                                JSONObject restaurant = jsonArrayResults.getJSONObject(i);
                                businessNames.add(restaurant.getString("BusinessName"));
                                businessAddress.add(restaurant.getString("AddressLine1") + restaurant.getString("AddressLine2") + restaurant.getString("AddressLine3") + restaurant.getString("AddressLine4"));
                                businessTypes.add(restaurant.getString("BusinessType"));
                                businessRatings.add(restaurant.getString("RatingValue"));
                                localAuthorityName.add(restaurant.getString("LocalAuthorityName"));
                                localAuthorityEmail.add(restaurant.getString("LocalAuthorityEmailAddress"));
                                localAuthorityWebsite.add(restaurant.getString("LocalAuthorityWebSite"));
//                                Log.d("SafeToEat", restaurant.getString("BusinessName"));
                            }

                            //converting arrayLists to arrays
                            name = new String[businessNames.size()];
                            name = businessNames.toArray(name);
                            address = new String[businessAddress.size()];
                            address = businessAddress.toArray(address);
                            ratings = new String[businessRatings.size()];
                            ratings = businessRatings.toArray(ratings);
                            localName = new String[localAuthorityName.size()];
                            localName = localAuthorityName.toArray(localName);
                            email = new String[localAuthorityEmail.size()];
                            email = localAuthorityEmail.toArray(email);
                            website = new String[localAuthorityWebsite.size()];
                            website = localAuthorityWebsite.toArray(website);

                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_restaurants);

                            RestaurantsAdapter adapter = new RestaurantsAdapter(getApplicationContext(), name, address, ratings, localName, email, website);

                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
        queue.add(jsonObjectRequest);
    }

}