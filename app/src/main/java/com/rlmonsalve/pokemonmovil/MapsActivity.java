package com.rlmonsalve.pokemonmovil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static String TAG = "ElTag";
    private GoogleMap mGoogleMap;
    private ProgressDialog pDialog;
    private Context context;
    private ArrayList pos, pokes;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    LatLng latLng, pokeLatLng;
    SupportMapFragment mFragment;
    Marker currLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.context = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap gMap) {
        mGoogleMap = gMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        buildGoogleApiClient();

        mGoogleApiClient.connect();

        onVerificarRed();

    }

    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fire_red_animated));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);

            int i =0;
            Posicion pokepos;
            Pokemon imgBank;
            while(i<pos.size()){
                pokepos = (Posicion) pos.get(i);
                imgBank = (Pokemon) pokes.get(i);
                pokeLatLng = new LatLng(pokepos.getLatitud(),pokepos.getLongitud());
                MarkerOptions pokeMarkerOptions = new MarkerOptions();
                markerOptions.position(pokeLatLng);
                markerOptions.title("Pokemon Position");
                markerOptions.icon(BitmapDescriptorFactory.fromPath(imgBank.getImgUrl()));
                currLocationMarker = mGoogleMap.addMarker(markerOptions);
                i++;
            }


        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);



    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        //place marker at current position
        //mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fire_red_animated));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();

        //zoom to current position:
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(19).build();

        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }



    public boolean verificarRed(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            //Toast.makeText(this,"Network is available ",Toast.LENGTH_LONG).show();
            return true;
        }else{
            Toast.makeText(this,"Network not available ",Toast.LENGTH_LONG)
                    .show();
            return false;
        }
    }

    public void onVerificarRed(){
        if (verificarRed() == true){
            new getData().execute();
        }
    }

    protected static String getPosData(){
        Log.d(TAG,"get");
        String response = null;
        try {
            URL url = null;
            url = new URL("http://190.144.171.172/function3.php?lat=11.0199414&lng=-74.8487154");
            URLConnection yc = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                //Log.d(TAG,inputLine);
                response = inputLine;
            }
            in.close();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected static String getPokeData(){
        Log.d(TAG,"get");
        String response = null;
        try {
            URL url = null;
            url = new URL("https://raw.githubusercontent.com/FTorrenegraG/Pokemon_json_example/master/example.json");
            URLConnection yc = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                //Log.d(TAG,inputLine);
                response = inputLine;
            }
            in.close();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class getData extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MapsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String response = getPosData();
            int id, hp, atk, def, evo = 0;
            String latitud, longitud, name, img = "";
            int i = 0;
            pos = new ArrayList<Posicion>();

            if (response != null){
                try {
                    JSONArray posiciones = new JSONArray(response);
                    for (i=0; i < posiciones.length();i++){
                        JSONObject c = posiciones.getJSONObject(i);
                        latitud = c.getString("lt");
                        longitud = c.getString("lng");
                        pos.add(new Posicion(latitud,longitud));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            response = getPokeData();
            i=0;
            pokes = new ArrayList<Pokemon>();
            if (response != null){
                try {
                    JSONArray pokemons = new JSONArray(response);
                    for (i=0; i < pokemons.length();i++){
                        JSONObject c = pokemons.getJSONObject(i);
                        id = Integer.parseInt(c.getString("Id"));
                        name = c.getString("Name");
                        hp = Integer.parseInt(c.getString("Hp"));
                        atk = Integer.parseInt(c.getString("Attack"));
                        def = Integer.parseInt(c.getString("Defense"));
                        evo = Integer.parseInt(c.getString("ev_id"));
                        img = c.getString("ImgUrl");
                        pokes.add(new Pokemon(id,name,hp,atk,def,evo,img));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(pDialog.isShowing()){
                pDialog.dismiss();
            }
        }
    }
}
