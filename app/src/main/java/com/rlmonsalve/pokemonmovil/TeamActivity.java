package com.rlmonsalve.pokemonmovil;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TeamActivity extends AppCompatActivity {

    TinyDB tinydb;
    int id;
    ArrayList<PkmonInstance> pokemonTeam;
    private ArrayList<ImageView> imageList;
    private ArrayList<TextView> textList;
    private ImageView im1,im2,im3,im4,im5,im6;
    private TextView tx1,tx2,tx3,tx4,tx5,tx6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        tinydb = new TinyDB(this);
        pokemonTeam=tinydb.getListObject("pk_team",PkmonInstance.class);

        imageList = new ArrayList<ImageView>();
        textList = new ArrayList<TextView>();

        im1 = (ImageView)findViewById(R.id.imageTeam1);
        im2 = (ImageView)findViewById(R.id.imageTeam2);
        im3 = (ImageView)findViewById(R.id.imageTeam3);
        im4 = (ImageView)findViewById(R.id.imageTeam4);
        im5 = (ImageView)findViewById(R.id.imageTeam5);
        im6 = (ImageView)findViewById(R.id.imageTeam6);

        imageList.add(im1);
        imageList.add(im2);
        imageList.add(im3);
        imageList.add(im4);
        imageList.add(im5);
        imageList.add(im6);

        tx1 = (TextView)findViewById(R.id.textTeam1);
        tx2 = (TextView)findViewById(R.id.textTeam2);
        tx3 = (TextView)findViewById(R.id.textTeam3);
        tx4 = (TextView)findViewById(R.id.textTeam4);
        tx5 = (TextView)findViewById(R.id.textTeam5);
        tx6 = (TextView)findViewById(R.id.textTeam6);

        textList.add(tx1);
        textList.add(tx2);
        textList.add(tx3);
        textList.add(tx4);
        textList.add(tx5);
        textList.add(tx6);

        setupLayout();
    }

    public void setupLayout(){
        for (int pos = 0;pos<pokemonTeam.size();pos++){
            PkmonInstance pk = pokemonTeam.get(pos);
            int currHp = (int) pk.getCurrentHp();
            int mHp = (int) pk.getHp();
            imageList.get(pos).setVisibility(View.VISIBLE);
            textList.get(pos).setVisibility(View.VISIBLE);
            Picasso.with(this).load(pk.getKind().getImgFront()).into(imageList.get(pos));
            textList.get(pos).setText(pk.getKind().getName()+"\n"+currHp+"/"+mHp);
        }
    }

    public void startPokeInfoActivity(View view){
        id = Integer.parseInt((String) view.getTag());
        if (id<=pokemonTeam.size()){
            tinydb.putInt("poke_id", id);
            //Toast.makeText(this,"Id: " +id,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TeamActivity.this,
                    PokeInfoActivity.class);
            startActivity(intent);
            finish();
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
        Intent intent = new Intent(TeamActivity.this,
                MapsActivity.class);
        startActivity(intent);
        finish();
    }
}
