package com.rlmonsalve.pokemonmovil;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    TinyDB tinydb;
    Button cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        tinydb = new TinyDB(this);
        cont = (Button)findViewById(R.id.buttonCont);
        if (tinydb.getBoolean("firstrun_map")){
            cont.setEnabled(false);
        }
    }

    public void startMapsActivity(View view){
        Intent intent = new Intent(LoginActivity.this,
                MapsActivity.class);
        startActivity(intent);
        finish();
    }

    public void newStart(View view){
        tinydb.putBoolean("firstrun_map",true);
        Intent intent = new Intent(LoginActivity.this,
                MapsActivity.class);
        startActivity(intent);
        finish();
    }
}
