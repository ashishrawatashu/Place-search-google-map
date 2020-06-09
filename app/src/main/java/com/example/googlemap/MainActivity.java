package com.example.googlemap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Marker mCurrLocationMarker;
    Button button;
    public static String destination_address = "", destination_latlng = "";
    public static Double dest_latitude = 0.0, dest_longitude = 0.0, src_latitude = 0.0, src_longitude = 0.0;
    String CAMERA="location";
    private AutocompleteSupportFragment autocompleteFragment;
    LinearLayout maoLL;
    TextView addressTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.findMap);
        maoLL = findViewById(R.id.mapLL);
        addressTV= findViewById(R.id.addressTV);

        Places.initialize(getApplicationContext(), "", Locale.US);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {

                Log.e("TAG", "Place: " + place.getName() + ", " + place.getId()+", ");
                destination_latlng = place.getLatLng().latitude + "," + place.getLatLng().longitude;
                addressTV.setText(place.getAddress().trim());
                destination_address = place.getAddress().trim();
                destination_latlng = place.getLatLng().latitude + "," + place.getLatLng().longitude;
                dest_longitude = place.getLatLng().longitude;
                dest_latitude = place.getLatLng().latitude;
                Log.e("dest_address", destination_address + "\n" + destination_latlng);
                CAMERA = "dest";
                mMap.clear();
                onMapReady(mMap);

            }

            @Override
            public void onError(Status status) {
                Log.e("TAG", "An error occurred: " + status);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                maoLL.setVisibility(View.GONE);
                addressTV.setVisibility(View.VISIBLE);


//                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS);
//                Intent intent = new Autocomplete.IntentBuilder(
//                        AutocompleteActivityMode.FULLSCREEN, fields)
//                        .build(MainActivity.this);
//                startActivityForResult(intent, 1);


            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
                Place place = Autocomplete.getPlaceFromIntent(data);

                Log.e("TAG", "Place: " + place.getName() + ", " + place.getId()+", ");

                destination_latlng = place.getLatLng().latitude + "," + place.getLatLng().longitude;

                //Place place = Autocomplete.getPlaceFromIntent(data);
                addressTV.setText(place.getAddress().trim());
                destination_address = place.getAddress().trim();
                destination_latlng = place.getLatLng().latitude + "," + place.getLatLng().longitude;
                dest_longitude = place.getLatLng().longitude;
                dest_latitude = place.getLatLng().latitude;
                Log.e("dest_address", destination_address + "\n" + destination_latlng);
                CAMERA = "dest";
                mMap.clear();
                onMapReady(mMap);


            }
        }
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        src_latitude = location.getLatitude();
        src_longitude = location.getLongitude();
        LatLng latLng = new LatLng(src_latitude, src_longitude);
//        mMap.addMarker(new MarkerOptions()
//                .position(latLng));
        if (CAMERA.equals("location")) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
                double lat = 0;
                double log =0;
                    lat = Double.parseDouble(String.valueOf(dest_latitude));
                    log = Double.parseDouble(String.valueOf(dest_longitude));
                    Log.e("llll11", String.valueOf(lat));
                    Log.e("llll22", String.valueOf(log));
                    LatLng location = new LatLng(lat, log);
                    mMap.addMarker(new MarkerOptions().position(location));
        CameraPosition camPos = new CameraPosition.Builder()
                .target(new LatLng(dest_latitude, dest_longitude))
                .zoom(18)
                .build();
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        googleMap.animateCamera(camUpd3);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
