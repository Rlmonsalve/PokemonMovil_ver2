package com.rlmonsalve.pokemonmovil;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PokeInfoActivity extends AppCompatActivity {

    TinyDB tinydb;
    ImageView pokeImage;
    TextView txName, txInfo, txHp;
    Button btnPotion, btnMaxPotion;
    ProgressBar pb;
    ArrayList<PkmonInstance> pk_team;
    int id;
    PkmonInstance pokemon;
    Bag bag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poke_info);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        pokeImage = (ImageView)findViewById(R.id.imageSprite);
        txName = (TextView)findViewById(R.id.textName);
        txInfo = (TextView)findViewById(R.id.textInfo);
        txHp = (TextView)findViewById(R.id.textHp);
        pb = (ProgressBar)findViewById(R.id.ProgressBarInfo);
        btnPotion = (Button)findViewById(R.id.btnPotion);
        btnMaxPotion = (Button)findViewById(R.id.btnMaxPotion);

        tinydb = new TinyDB(this);
        id = tinydb.getInt("poke_id");

        pk_team = tinydb.getListObject("pk_team",PkmonInstance.class);
        pokemon = pk_team.get(id);
        bag = tinydb.getObject("bag",Bag.class);



        Picasso.with(this).load(pokemon.getKind().getImgFront()).into(pokeImage);
        txName.setText(pokemon.getKind().getName());
        String info = "Ataque: "+pokemon.getCurrentAtk()+"/"+pokemon.getMaxAtk()+"\nDefensa: "+pokemon.getCurrentDef()+"/"+pokemon.getMaxDef();
        txInfo.setText(info);

        txHp.setText((int)pokemon.getCurrentHp()+" hp/"+(int)pokemon.getHp()+" hp");
        pb.setMax((int) pokemon.getHp());
        pb.setProgress((int) pokemon.getCurrentHp());

        btnPotion.setText("USE POTION x"+bag.getPotionNo());
        btnMaxPotion.setText("USE MAX POTION x"+bag.getMaxPotNo());

    }

    public void potionEvent(View view){
        if (pokemon.getCurrentHp()!=pokemon.getHp()){
            if (bag.getPotionNo()>0){
                double healfactor = pokemon.getHp()/2;
                double new_hp = pokemon.getCurrentHp()+healfactor;
                if (new_hp>pokemon.getHp()){
                    new_hp=pokemon.getHp();
                }
                pokemon.setCurrentHp((int) new_hp);
                int pnum = bag.getPotionNo();
                bag.setPotionNo(pnum-1);
                txHp.setText((int)pokemon.getCurrentHp()+" Hp/"+(int)pokemon.getHp()+ "Hp");
                pb.setProgress((int) pokemon.getCurrentHp());
                btnPotion.setText("USE POTION x"+bag.getPotionNo());
            }
        }
    }

    public void maxPotionEvent(View view){
        if (pokemon.getCurrentHp()!=pokemon.getHp()) {
            if (bag.getPotionNo() > 0) {
                double new_hp = pokemon.getHp();
                pokemon.setCurrentHp((int) new_hp);
                int pnum = bag.getMaxPotNo();
                bag.setMaxPotNo(pnum-1);
                txHp.setText((int)pokemon.getCurrentHp()+" Hp/"+(int)pokemon.getHp()+ "Hp");
                pb.setProgress((int) pokemon.getCurrentHp());
                btnMaxPotion.setText("USE MAX POTION x"+bag.getMaxPotNo());
            }
        }
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
        tinydb.putListObject("pk_team",pk_team);
        tinydb.putObject("bag",bag);
        Intent intent = new Intent(PokeInfoActivity.this,
                TeamActivity.class);
        startActivity(intent);
        finish();
    }
}
