package com.example.googlemap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
public class Fragmentactivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Button button;
    String destination_address = "", destination_latlng = "";
    Double dest_latitude = 0.0, dest_longitude = 0.0, src_latitude = 0.0, src_longitude = 0.0;
    String CAMERA="location";
    private AutocompleteSupportFragment autocompleteFragment;
    LinearLayout maoLL;
    Context context;
    public static String mapclear = "";
    String addresss = "", city ="", state ="";
    Polyline polyline = null;

    LatLng latLng;
    String mapready ="";
    Marker mCurrLocationMarker;
    LatLng latlngNew;
    ArrayList directionList;


    private static final int LOCATION_REQUEST = 500;
    ArrayList<LatLng> listPoints;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragmentactivity);
        button = findViewById(R.id.findMap);
        maoLL = findViewById(R.id.mapLL);
        Places.initialize(this, "AIzaSyClXYwahInayLuwd5sQpm5k2jVW2Oc8490", Locale.US);
        listPoints = new ArrayList<>();

        mapFragment();
        autoComp();
        autocompClick();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mapready = "ready";
                if (listPoints.size()==1){
                    Toast.makeText(context, "ajshkahs", Toast.LENGTH_SHORT).show();

                }else {

                    String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
                    Log.e("URL",url);
                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                //drawRouteOnMap(mGoogleMap, directionList);
              //  mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));


//                float result[] = new float[10];
//                Location.distanceBetween(src_latitude,src_longitude,dest_latitude,dest_longitude,result);
//                Log.e("result", String.valueOf(result[0]));
//                if (result[0]>5000.00){
//                    Toast.makeText(Fragmentactivity.this, "out Of range", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(Fragmentactivity.this, "Order deliverded", Toast.LENGTH_SHORT).show();
//                }

//                Intent intent = new Intent();
//                intent.putExtra("addrerss",destination_address);
//                intent.putExtra("city",city);
//                intent.putExtra("state",state);
//                setResult(RESULT_OK,intent);
//                onBackPressed();


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
                if (listPoints.size() == 2) {
                    listPoints.clear();
                    mMap.clear();
                    listPoints.add(latLng);
                }
                destination_latlng = place.getLatLng().latitude + "," + place.getLatLng().longitude;
                destination_address = place.getAddress().trim();
                destination_latlng = place.getLatLng().latitude + "," + place.getLatLng().longitude;
                dest_longitude = place.getLatLng().longitude;
                dest_latitude = place.getLatLng().latitude;
                Log.e("dest_address", destination_address + "\n" + destination_latlng);
                CAMERA = "dest";
                mMap.clear();
                latlngNew = new LatLng(dest_latitude, dest_longitude);
                MarkerOptions markerOptionx = new MarkerOptions();
                markerOptionx.position(latlngNew);
                mMap.addMarker(markerOptionx);
                listPoints.add(latlngNew);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                mCurrLocationMarker = mMap.addMarker(markerOptions);
                //drawRouteOnMap(mMap, directionList);
               // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
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
        context = this;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        directionList = new ArrayList();

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
        //Place current location marker
        src_latitude = location.getLatitude();
        src_longitude = location.getLongitude();
        latLng = new LatLng(src_latitude, src_longitude);
        listPoints.add(latLng);

        mMap.addMarker(new MarkerOptions()
               .position(latLng));
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


        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //Reset marker when already 2
                if (listPoints.size() == 2) {
                    listPoints.clear();
                    mMap.clear();
                }
                //Save first point select
                listPoints.add(latLng);
                //Create marker
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                if (listPoints.size() == 1) {
                    //Add first marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else {
                    //Add second marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                mMap.addMarker(markerOptions);

                if (listPoints.size() == 2) {
                    //Create the URL to get request from first marker to second marker
                    String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
            }
        });
//        double lat = 0;
//        double log =0;
//        lat = Double.parseDouble(String.valueOf(dest_latitude));
//        log = Double.parseDouble(String.valueOf(dest_longitude));
//        Log.e("llll11", String.valueOf(lat));
//        Log.e("llll22", String.valueOf(log));
//        LatLng location = new LatLng(lat, log);
//        directionList.add(location);
//        mMap.addMarker(new MarkerOptions().position(location));
//        CameraPosition camPos = new CameraPosition.Builder()
//                .target(new LatLng(dest_latitude, dest_longitude))
//                .zoom(18)
//                .build();
//        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
//        googleMap.animateCamera(camUpd3);

    }

    private String getRequestUrl(LatLng origin, LatLng dest) {

        //Value of origin
        String str_org = "origin=" + origin.latitude +","+origin.longitude;
        //Value of destination
        String str_dest = "destination=" + dest.latitude+","+dest.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org +"&" + str_dest + "&" +sensor+"&" +mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key=" + "AIzaSyClXYwahInayLuwd5sQpm5k2jVW2Oc8490";
     //   Log.e("URL",url);

        return url;

    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void finish() {

        super.finish();

    }

    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions){
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.addAll(positions);
        if(polyline!=null){
            polyline.remove();
            polyline = map.addPolyline(options);
        }else{
            polyline = map.addPolyline(options);
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(positions.get(1).latitude, positions.get(1).longitude))
                .zoom(17)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        directionList.clear();
        directionList.add(latLng);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }



    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }


    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }



}
