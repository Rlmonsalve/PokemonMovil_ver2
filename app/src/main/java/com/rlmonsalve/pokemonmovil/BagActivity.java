package com.rlmonsalve.pokemonmovil;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

public class BagActivity extends AppCompatActivity {

    TinyDB tinydb;
    Bag bag;
    ImageView imPoke, imPot, imMpot;
    TextView txPoke, txPot, txMpot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tinydb = new TinyDB(this);
        bag = tinydb.getObject("bag",Bag.class);

        imPoke = (ImageView)findViewById(R.id.imagePokeball);
        imPot = (ImageView)findViewById(R.id.imagePotion);
        imMpot = (ImageView)findViewById(R.id.imageMpotion);

        txPoke = (TextView)findViewById(R.id.textPokeball);
        txPot = (TextView)findViewById(R.id.textPotion);
        txMpot = (TextView)findViewById(R.id.textMpotion);

        txPoke.setText("Pokeball x" + bag.getPokeballNo());
        txPot.setText("Potion x"+bag.getPotionNo());
        txMpot.setText("Max Potion x"+bag.getMaxPotNo());

    }

    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        //tinydb.putListObjectItem("Objetos_List",objList);
        Intent intent = new Intent(BagActivity.this,
                MapsActivity.class);
        startActivity(intent);
        finish();
    }
}
