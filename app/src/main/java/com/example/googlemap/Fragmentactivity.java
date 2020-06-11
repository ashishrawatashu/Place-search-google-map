package com.example.googlemap;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.Locale;

public class Fragmentactivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private GoogleMap mMap;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Marker mCurrLocationMarker;
    Button button;
    String destination_address = "", destination_latlng = "";
    Double dest_latitude = 0.0, dest_longitude = 0.0, src_latitude = 0.0, src_longitude = 0.0;
    String CAMERA="location";
    private AutocompleteSupportFragment autocompleteFragment;
    LinearLayout maoLL;
    Context context;
    public static String mapclear = "";
    String addresss = "", city ="", state ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragmentactivity);
        button = findViewById(R.id.findMap);
        maoLL = findViewById(R.id.mapLL);
        Places.initialize(this, "AIzaSyClXYwahInayLuwd5sQpm5k2jVW2Oc8490", Locale.US);

        mapFragment();
        autoComp();
        autocompClick();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("addrerss",destination_address);
                intent.putExtra("city",city);
                intent.putExtra("state",state);
                setResult(RESULT_OK,intent);
                onBackPressed();


                //finish();

//                HomeFragment homeFragment = new HomeFragment ();
//                Bundle bundle = new Bundle();
//                bundle.putString("addrerss", destination_address);
//                bundle.putString("city",city);
//                bundle.putString("state",state);
//
//                Log.e("bundel", String.valueOf(bundle));
//                bundle.putString("state", "YourValue");
//                bundle.putString("city", "YourValue");
//                homeFragment.setArguments(bundle);

//Inflate the fragment
//                getSupportFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();
                //getFragmentManager().beginTransaction().remove(MapView.this,bundle).commit();
//                getSupportFragmentManager().popBackStack();
            }

        });

    }

    private void autocompClick() {

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.e("TAG", "Place: " + place.getName() + ", " + place.getId()+", ");
                Log.e("componet",place.getAddressComponents().toString());
                if (place.getAddressComponents().asList().size()<=1){
                    destination_address = place.getAddressComponents().asList().get(0).getName();

                }else {
                    city = place.getAddressComponents().asList().get(0).getName();
                    state = place.getAddressComponents().asList().get(2).getName();
                    destination_address=place.getAddress();

                }
                mapclear = "map";
                destination_latlng = place.getLatLng().latitude + "," + place.getLatLng().longitude;
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

    }

    private void autoComp() {

        autocompleteFragment = (AutocompleteSupportFragment)getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS,Place.Field.ADDRESS_COMPONENTS));

    }

    private void mapFragment() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

    }

    @Override
    public void finish() {

        super.finish();

    }
}
