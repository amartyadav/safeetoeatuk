package com.example.safetoeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

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
                }
            });
        }
    }

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

}