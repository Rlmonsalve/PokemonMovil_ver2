package com.rlmonsalve.pokemonmovil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {

    private static String TAG = "ElTag";
    private GoogleMap mGoogleMap;
    private ProgressDialog pDialog;
    private Context context;

    TinyDB tinydb;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    LatLng latLng, pokeLatLng;
    SupportMapFragment mFragment;
    Marker currLocationMarker, pokeMarker1,pokeMarker2,pokeMarker3,pokeMarker4;
    JSONArray jsonLocations;
    JSONArray jsonPokemon;
    Location lastset;
    ArrayList<Bitmap> bitmaps;
    ArrayList<Marker> pokemarkers;
    ArrayList<PkmonInstance> pokemonTeam;
    Bag bag;
    Pokemon playerPokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.context = this;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        tinydb = new TinyDB(this);

        buildGoogleApiClient();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        bitmaps = new ArrayList<>();
        pokemarkers = new ArrayList<>();
        TinyDB tinydb = new TinyDB(context);

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
        mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        //mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setBuildingsEnabled(true);

        mGoogleMap.setOnMarkerClickListener(this);

        //mGoogleApiClient.connect();

        //onVerificarRed();
        FirstTimeLoad firstTimeLoad = new FirstTimeLoad();
        firstTimeLoad.execute();

    }

    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();
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
        Location location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        //place marker at current position
        //mGoogleMap.clear();
        /*latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fire_red_animated));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);*/

        lastset = null;
        SetPokemon(location);
        setPokestops(location);

        newLocation(location);

            /*int i =0;
            Posicion pokepos;
            Pokemon imgBank;
            while(i<pos.size()){
                pokepos = (Posicion) pos.get(i);
                imgBank = (Pokemon) pokes.get(i);
                pokeLatLng = new LatLng(pokepos.getLatitud(),pokepos.getLongitud());
                MarkerOptions pokeMarkerOptions = new MarkerOptions();
                pokeMarkerOptions.position(pokeLatLng);
                pokeMarkerOptions.title("Pokemon Position");
                pokeMarkerOptions.icon(BitmapDescriptorFactory.fromPath(imgBank.getImgUrl()));
                currLocationMarker = mGoogleMap.addMarker(pokeMarkerOptions);
                i++;
            }*/



        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);
    }


    @Override
    public void onConnectionSuspended(int i) {
        //Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        newLocation(location);
    }

    public void newLocation(Location location) {
        //place marker at current position
        //mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        /*MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fire_red_animated));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);*/

        //Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();

        //zoom to current position:
        float b = mGoogleMap.getCameraPosition().bearing;
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(18).bearing(b).build();

        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        SetPokemon(location);

        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
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
        if (verificarRed()){
            FirstTimeLoad firstTimeLoad = new FirstTimeLoad();
            firstTimeLoad.execute();
        }
    }

    public void FinishSetPokemon(Location location) {

        try {
            if (lastset == null || location.distanceTo(lastset) >= 0.0002f) {
                //Toast.makeText(this, "Entre a Finish set pokemons", Toast.LENGTH_SHORT).show();
                //JSONObject object = new JSONObject(g.returnstring);
                //JSONArray jlocations = object.getJSONArray("");
                for(int i=0; i<jsonLocations.length(); i++)
                {
                    JSONObject pos = jsonLocations.getJSONObject(i);
                    JSONObject poke = jsonPokemon.getJSONObject(i);
                    String id = poke.getString("id");
                    String name = poke.getString("name");
                    String type = poke.getString("type");
                    String strength = poke.getString("strength");
                    String weakness = poke.getString("weakness");
                    String maxHp = poke.getString("hp_max");
                    String maxAtk = poke.getString("ataque_max");
                    String maxDef = poke.getString("defensa_max");
                    String imgFront = poke.getString("ImgFront");
                    String imgBack = poke.getString("ImgBack");
                    String ev = poke.getString("ev_id");
                    String lt = pos.getString("lt");
                    String lat = pos.getString("lng");
                    LatLng postition = new LatLng(Double.parseDouble(lt), Double.parseDouble(lat));
                    pokemarkers.add(mGoogleMap.addMarker(new MarkerOptions()
                            .position(postition)
                            .title(id)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmaps.get(i)))));
                    //Pokemon pk = new Pokemon(id,name,type,strength,weakness,maxHp,maxAtk,maxDef,ev,imgFront,imgBack);
                    //pokemonList.add(pk);
                }

                lastset = location;
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(postition, 17.0f));

            }

        }catch (Exception e){e.printStackTrace();}


    }

    private void verifyFirstTime(){
        if (tinydb.getBoolean("firstrun_map")) {
            // Do first run stuff here then set 'firstrun' as false
            GeneratePlayerPokemon();
            GeneratePlayerBag();
            // using the following line to edit/commit prefs
            tinydb.putBoolean("firstrun_map", false);
        }else{
            pokemonTeam=tinydb.getListObject("pk_team",PkmonInstance.class);
            bag=tinydb.getObject("bag",Bag.class);
        }
    }

    private void GeneratePlayerPokemon() {
        pokemonTeam = new ArrayList<PkmonInstance>();
        Random r = new Random();
        int il = r.nextInt(3);
        JSONObject poke = null;
        try {
            poke = jsonPokemon.getJSONObject(il);
            playerPokemon = new Pokemon(poke.getString("id"),poke.getString("name"),poke.getString("type"),poke.getString("strength"),poke.getString("weakness"),poke.getString("hp_max"),poke.getString("ataque_max"),poke.getString("defensa_max"),poke.getString("ev_id"),poke.getString("ImgFront"),poke.getString("ImgBack"));
            pokemonTeam.add(new PkmonInstance(playerPokemon));
            tinydb.putListObject("pk_team",pokemonTeam);
            //Toast.makeText(this,"Team created",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void GeneratePlayerBag(){
        bag = new Bag(10,20,10);
        tinydb.putObject("bag",bag);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (marker.getTitle().equals("Pokestop")){
            addItems();
        }else if(!marker.getTitle().matches("My Position")) {
            //Toast.makeText(MapsActivity.this, "Poke Event", Toast.LENGTH_SHORT).show();// display toast
            marker.remove();
            String id = marker.getTitle();
            startFightActivity(id);
            return true;
        }

        return false;
    }

    private void startFightActivity(String id) {
        Intent intent = new Intent(MapsActivity.this,
                FightActivity.class);
        intent.putExtra("jsonArray", jsonPokemon.toString());
        intent.putExtra("enemy_id",id);

        startActivity(intent);
        finish();
    }

    public void startTeamActivity(View view){
        Intent intent = new Intent(MapsActivity.this,
                TeamActivity.class);
        startActivity(intent);
        finish();
    }

    public void startBagActivity(View view){
        Intent intent = new Intent(MapsActivity.this,
                BagActivity.class);
        startActivity(intent);
        finish();
    }

    public void SetPokemon(Location location) {
        try {
            //Toast.makeText(this,"Entre a set pokemons",Toast.LENGTH_SHORT).show();
            if (jsonLocations == null || location.distanceTo(lastset) >= 200f) {

                getPokemonLocation g = new getPokemonLocation();
                g.execute(location);

                getPokemonIcons getPokemonIcons = new getPokemonIcons();
                getPokemonIcons.execute();


                //Toast.makeText(this,"Entre a getpokemons",Toast.LENGTH_SHORT).show();

                //if (g.returnstring != null)
                //{

                //}

            }

            //lastset = location;
        }
        catch (Exception e){e.printStackTrace();}
    }

    public void setPokestops(Location location){
        double spotLat = 11.019178;
        double spotLng = -74.849916;
        pokeLatLng = new LatLng(spotLat, spotLng);
        pokeMarker1 = mGoogleMap.addMarker(new MarkerOptions()
                .position(pokeLatLng)
                .title("Pokestop")
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pokestop)));

        spotLat = 11.019630;
        spotLng = -74.850753;
        pokeLatLng = new LatLng(spotLat, spotLng);
        pokeMarker2 = mGoogleMap.addMarker(new MarkerOptions()
                .position(pokeLatLng)
                .title("Pokestop")
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pokestop)));
        spotLat = 11.017976;
        spotLng = -74.850931;
        pokeLatLng = new LatLng(spotLat, spotLng);
        pokeMarker3 = mGoogleMap.addMarker(new MarkerOptions()
                .position(pokeLatLng)
                .title("Pokestop")
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pokestop)));
        spotLat = 11.019822;
        spotLng = -74.851253;
        pokeLatLng = new LatLng(spotLat, spotLng);
        pokeMarker4 = mGoogleMap.addMarker(new MarkerOptions()
                .position(pokeLatLng)
                .title("Pokestop")
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pokestop)));
    }

    public void addItems(){
        Random r = new Random();
        int randPokeball = r.nextInt(6);
        int randPotion = r.nextInt(5);
        int randMaxPotion = r.nextInt(3);
        int pokeball = bag.getPokeballNo();
        int potion = bag.getPotionNo();
        int mpotion = bag.getMaxPotNo();
        bag.setPokeballNo(randPokeball+pokeball);
        bag.setPotionNo(randPotion+potion);
        bag.setMaxPotNo(randMaxPotion+mpotion);
        Toast.makeText(this, "Pokeballs + "+randPokeball+"\nPotions + "+randPotion+"\nMax Potions + "+randMaxPotion, Toast.LENGTH_SHORT).show();
        savePrefs();
    }

    private class FirstTimeLoad extends AsyncTask<Void, Void, String>
    {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String returnline = s;
            s = " { \"MyArray\":"+ returnline+"}";

            try {
                //JSONObject jsonObject = new JSONObject(s);
                String json = "[{\"Id\": 1,\n" +
                        "   \"Name\": \"Bulbasaur\",\n" +
                        "   \"Type\": \"GRASS-POISON\",\n" +
                        "   \"Total\": 318,\n" +
                        "   \"HP\": 45,\n" +
                        "   \"Attack\": 49,\n" +
                        "   \"Defense\": 49,\n" +
                        "   \"Sp. Atk\": 65,\n" +
                        "   \"Sp. Def\": 65,\n" +
                        "   \"Speed\": 45,\n" +
                        "   \"ImgFront\": \"https://img.pokemondb.net/sprites/black-white/normal/bulbasaur.png\",\n" +
                        "   \"ImgBack\": \"https://img.pokemondb.net/sprites/black-white/back-normal/bulbasaur.png\",\n" +
                        "   \"GifFront\": \"https://img.pokemondb.net/sprites/black-white/anim/normal/bulbasaur.gif\",\n" +
                        "   \"GifBack\": \"https://img.pokemondb.net/sprites/black-white/anim/back-normal/bulbasaur.gif\",\n" +
                        "   \"ImgUrl\": \"http://190.144.171.172//proyectoMovil//Pokemon-DB//img//001Bulbasaur.png\",\n" +
                        "   \"ev_id\": 2}]";

                JSONObject jsonObject = new JSONObject(s);
                jsonPokemon =  jsonObject.getJSONArray("MyArray");

                getPokemonIcons getPokemonIcons = new getPokemonIcons();
                getPokemonIcons.execute();
                verifyFirstTime();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            URL url = null;
            String inputline = "";

            try {
                url = new URL ("http://190.144.171.172/proyectoMovil/pokemonlist17.php");
                URLConnection urlConnection = url.openConnection();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String iline = bufferedReader.readLine();


                while(iline!=null)
                {

                    inputline += iline;
                    iline = bufferedReader.readLine();
                }
                bufferedReader.close();


            } catch (Exception e) {
                e.printStackTrace();
            }


            return inputline;

        }
    }

    private class getPokemonIcons extends AsyncTask <Void, Void, Void>
    {

        //ArrayList<Bitmap> bitmaps;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //bitmaps = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try
            {
                String pngurl = "";
                for(int i=0; i<jsonPokemon.length();i++)
                {
                    pngurl = jsonPokemon.getJSONObject(i).getString("ImgFront");

                    URL url = new URL(pngurl);

                    URLConnection urlConnection = url.openConnection();

                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();

                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                    Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);

                    bitmaps.add(bitmap);

                    bufferedInputStream.close();
                    inputStream.close();

                }

            }
            catch(Exception e){e.printStackTrace();}
            return null;
        }
    }

    private class getPokemonLocation extends AsyncTask<Location, Void, String>
    {
        Location loc;

        @Override
        protected String doInBackground(Location... params) {
            Location location = params[0];
            loc = location;
            String r = "";
            try
            {
                String ur = "http://190.144.171.172/function3.php?lat="+location.getLatitude()+"&lng="+location.getLongitude();

                URL url = new URL(ur);

                URLConnection urlConnection = url.openConnection();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String iline = bufferedReader.readLine();

                while (iline!=null)
                {
                    r += iline;
                    iline = bufferedReader.readLine();
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return r;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String returnstring = s;

            s = " { \"MyArray\":"+ returnstring+"}";

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                jsonLocations =  jsonObject.getJSONArray("MyArray");

                FinishSetPokemon(loc);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void savePrefs(){
        tinydb.putListObject("pk_team",pokemonTeam);
        tinydb.putObject("bag",bag);
    }
}
