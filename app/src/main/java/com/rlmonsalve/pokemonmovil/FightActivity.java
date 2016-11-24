package com.rlmonsalve.pokemonmovil;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class FightActivity extends AppCompatActivity {

    TinyDB tinydb;
    ArrayList<PkmonInstance> pokemonTeam;
    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private boolean isCameraviewOn = false;
    private int id;
    private JSONArray jsonPokemon;
    String hps1, hps2, prompt, n_prompt;
    ImageView pk1, pk2;
    TextView name1, name2, hp1, hp2, prmt, ht1, ht2, txHeal, pkBall, potion, maxPot;
    RelativeLayout rl1, rl2, rl3;
    ProgressBar bar1, bar2;
    PkmonInstance ally, enemy;
    Button Close;
    double typeMultiplier, damage, percent;
    int potionCounter;
    Bag bag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        String json = intent.getStringExtra("jsonArray");
        try {
            jsonPokemon = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        id = Integer.parseInt(intent.getStringExtra("enemy_id"));
        id = id-1;
        //setupLayout();

        tinydb = new TinyDB(this);
        pokemonTeam=tinydb.getListObject("pk_team",PkmonInstance.class);
        bag=tinydb.getObject("bag",Bag.class);
        prmt = (TextView)findViewById(R.id.textPrompt);
        pk1 = (ImageView) findViewById(R.id.imagePoke1);
        pk2 = (ImageView) findViewById(R.id.imagePoke2);
        name1 = (TextView) findViewById(R.id.textView1);
        name2 = (TextView) findViewById(R.id.textView2);
        hp1 = (TextView) findViewById(R.id.textHp1);
        hp2 = (TextView) findViewById(R.id.textHp2);
        bar1 = (ProgressBar) findViewById(R.id.ProgressBar1);
        bar2 = (ProgressBar) findViewById(R.id.ProgressBar2);
        ht1 = (TextView)findViewById(R.id.textHit1);
        ht2 = (TextView)findViewById(R.id.textHit2);
        txHeal = (TextView)findViewById(R.id.textHeal);

        GeneratePartyPokemon();
        GenerateEnemyPokemon(id);

        Picasso.with(this).load(enemy.getKind().getImgFront()).into(pk1);
        Picasso.with(this).load(ally.getKind().getImgBack()).into(pk2);
        name1.setText(enemy.getKind().getName());
        name2.setText(ally.getKind().getName());
        bar1.setMax((int) enemy.getHp());
        bar2.setMax((int) ally.getHp());
        bar1.setProgress((int) enemy.getCurrentHp());
        bar2.setProgress((int) ally.getCurrentHp());

        hp1.setText((int)enemy.getCurrentHp()+"/"+(int)enemy.getHp());
        hp2.setText((int)ally.getCurrentHp()+"/"+(int)ally.getHp());

        prompt = "A wild "+enemy.getKind().getName()+" has appeared!\n";
        prmt.setText(prompt);

        //pk1.setImageBitmap(BitmapFactory.decodeFile(enemy.getKind().getImgFront()));
        //pk2.setImageBitmap(BitmapFactory.decodeFile(ally.getKind().getImgBack()));

    }

    public void startMapsActivity(View view){
        tinydb.putObject("bag",bag);
        tinydb.putListObject("pk_team",pokemonTeam);
        Intent intent = new Intent(FightActivity.this,
                MapsActivity.class);
        startActivity(intent);
        finish();
    }

    public void attackEvent(View view){
        //Toast.makeText(this, "Tu pokemon ataca.", Toast.LENGTH_SHORT).show();
        n_prompt = ally.getKind().getName()+" attacks!\n";
        prompt = n_prompt.concat(prompt);
        if (enemy.getKind().getWeakness().equals(ally.getKind().getType())){
            n_prompt = "It´s super effective!\n";
            prompt = n_prompt.concat(prompt);
            typeMultiplier = 2;
        }else if (enemy.getKind().getStrength().equals(ally.getKind().getType())){
            n_prompt = "It´s not very effective...\n";
            prompt = n_prompt.concat(prompt);
            typeMultiplier = 0.5;
        }else{
            typeMultiplier = 1;
        }
        damage = (ally.getCurrentAtk()-enemy.getCurrentDef()) * typeMultiplier;
        if (damage<=0){
            damage = 1;
        }
        double prevHp = enemy.getCurrentHp();
        enemy.setCurrentHp((int) (prevHp-damage));

        if (enemy.getCurrentHp()<0){
            enemy.setCurrentHp(0);
        }

        animateDamage(0, (int) damage);
        hp1.setText((int)enemy.getCurrentHp()+"/"+(int)enemy.getHp());
        bar1.setProgress((int) enemy.getCurrentHp());
        prmt.setText(prompt);

        if (enemy.getCurrentHp()==0){
            finalizarPelea(view,1);
        }else{
            enemyAttackEvent(view);
        }
    }

    public void enemyAttackEvent(View view){
        //Toast.makeText(this, "El enemigo ataca.", Toast.LENGTH_SHORT).show();
        n_prompt = "The wild "+enemy.getKind().getName()+" attacks!\n";
        prompt = n_prompt.concat(prompt);
        if (ally.getKind().getWeakness().equals(enemy.getKind().getType())){
            n_prompt = "It´s super effective!\n";
            prompt = n_prompt.concat(prompt);
            typeMultiplier = 2;
        }else if (ally.getKind().getStrength().equals(enemy.getKind().getType())){
            n_prompt = "It´s not very effective...\n";
            prompt = n_prompt.concat(prompt);
            typeMultiplier = 0.5;
        }else{
            typeMultiplier = 1;
        }
        damage = (enemy.getCurrentAtk()-ally.getCurrentDef()) * typeMultiplier;
        if (damage<=0){
            damage = 1;
        }
        double prevHp = ally.getCurrentHp();
        ally.setCurrentHp((int) (prevHp-damage));

        if (ally.getCurrentHp()<0){
            ally.setCurrentHp(0);
        }

        animateDamage(1, (int) damage);
        hp2.setText((int)ally.getCurrentHp()+"/"+(int)ally.getHp());
        bar2.setProgress((int) ally.getCurrentHp());
        prmt.setText(prompt);

        if (ally.getCurrentHp()==0){
            finalizarPelea(view,2);
        }
    }

    public void itemsEvent(View view){
        showPopup(view);
    }

    public void pokemonEvent(View view){
        Toast.makeText(this, "No puedes cambiar de pokemon!", Toast.LENGTH_SHORT).show();
    }

    public void runEvent(View view){
        n_prompt= "Has escapado de la pelea.\n";
        prompt = n_prompt.concat(prompt);

        prmt.setText(prompt);
        finalizarPelea(view, 4);
    }

    private PopupWindow pw;
    private void showPopup(View view) {
        try {
// We need to get the instance of the LayoutInflater
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            LayoutInflater inflater = (LayoutInflater) FightActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_bag,
                    (ViewGroup) findViewById(R.id.popup_bag));
            int mHeight = (int) ((displaymetrics.heightPixels)*0.75);
            int mWidth = (int) ((displaymetrics.widthPixels)*0.9);
            pw = new PopupWindow(layout, mWidth, mHeight, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
            pkBall = (TextView) layout.findViewById(R.id.textPokeball);
            potion = (TextView) layout.findViewById(R.id.textPotion);
            maxPot = (TextView) layout.findViewById(R.id.textMpotion);
            rl1 = (RelativeLayout)layout.findViewById(R.id.rlPokeBall);
            rl2 = (RelativeLayout)layout.findViewById(R.id.rlPotion);
            rl3 = (RelativeLayout)layout.findViewById(R.id.rlMaxPotion);
            Close = (Button) layout.findViewById(R.id.close_popup);

            pkBall.setText("Pokeball x" + bag.getPokeballNo());
            potion.setText("Potion x"+bag.getPotionNo());
            maxPot.setText("Max Potion x"+bag.getMaxPotNo());

            rl1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bag.getPokeballNo()>0){
                        pokeballEvent(v);
                    }
                }
            });

            rl2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bag.getPotionNo()>0){
                        healEvent(v, 1);
                    }
                }
            });

            rl3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bag.getMaxPotNo()>0){
                        healEvent(v, 2);
                    }
                }
            });

            Close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closePopup();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closePopup(){
        pw.dismiss();
    }

    public void pokeballEvent(final View view){
        closePopup();
        n_prompt= "You used a pokeball.\n";
        int pnum = bag.getPokeballNo();
        bag.setPokeballNo(pnum-1);
        prompt = n_prompt.concat(prompt);

        prmt.setText(prompt);
        Picasso.with(this).load(R.drawable.pokeball).into(pk1);
        pk1.animate().translationY(20).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                txHeal.setVisibility(View.INVISIBLE);
                txHeal.animate().translationY(0).setDuration(100).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        verifyCapture(view);
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();

    }

    public void healEvent(View view, int chk){
        double heal;
        if (chk==1){
            double healfactor = ally.getHp()/2;
            double new_hp = ally.getCurrentHp()+healfactor;
            heal = new_hp-ally.getCurrentHp();
            if (new_hp>ally.getHp()){
                new_hp=ally.getHp();
            }
            ally.setCurrentHp((int) new_hp);
            n_prompt = "You used a potion!\n";
            prompt = n_prompt.concat(prompt);
            int pnum = bag.getPotionNo();
            bag.setPotionNo(pnum-1);


        }else{
            heal = ally.getHp()-ally.getCurrentHp();
            double new_hp = ally.getHp();
            ally.setCurrentHp((int) new_hp);
            n_prompt = "You used a MAX potion!\n";
            prompt = n_prompt.concat(prompt);
            int pnum = bag.getMaxPotNo();
            bag.setMaxPotNo(pnum-1);

        }

        closePopup();
        animateHeal((int) heal);
        hp2.setText((int)ally.getCurrentHp()+"/"+(int)ally.getHp());
        bar2.setProgress((int) ally.getCurrentHp());
        prmt.setText(prompt);
        enemyAttackEvent(view);

    }

    public void verifyCapture(View view){
        Random r = new Random();
        int randStat = r.nextInt(100);
        percent = enemy.getCurrentHp()/enemy.getHp();
        if (percent>0.5){
            if(randStat<20){
                captureSuccess(view);
            }else{
                captureFailure(view);
            }
        }else{
            if (randStat<50){
                captureSuccess(view);
            }else{
                captureFailure(view);
            }
        }
    }

    public void captureSuccess(View view){
        n_prompt= "Success!!\n";
        prompt = n_prompt.concat(prompt);

        prmt.setText(prompt);
        pokemonTeam.add(enemy);
        finalizarPelea(view, 3);
    }

    public void captureFailure(View view){
        n_prompt= "The wild pokemon broke free!\n";
        prompt = n_prompt.concat(prompt);
        Picasso.with(this).load(enemy.getKind().getImgFront()).into(pk1);
        enemyAttackEvent(view);
    }

    public void finalizarPelea(final View view, int value){
        if (value == 1){
            AlertDialog alertDialog = new AlertDialog.Builder(FightActivity.this).create();
            alertDialog.setTitle("Felicitaciones");
            alertDialog.setMessage("Has ganado la pelea!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            levelupEvent();
                            dialog.dismiss();
                            startMapsActivity(view);
                        }
                    });
            alertDialog.show();
        }else if (value==2){
            AlertDialog alertDialog = new AlertDialog.Builder(FightActivity.this).create();
            alertDialog.setTitle("Lo sentimos");
            alertDialog.setMessage("Has perdido la pelea!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startMapsActivity(view);
                        }
                    });
            alertDialog.show();
        }else if (value == 3){
            AlertDialog alertDialog = new AlertDialog.Builder(FightActivity.this).create();
            alertDialog.setTitle("Felicitaciones");
            alertDialog.setMessage("Has capturado al pokemon!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startMapsActivity(view);
                        }
                    });
            alertDialog.show();
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(FightActivity.this).create();
            alertDialog.setTitle("Felicitaciones");
            alertDialog.setMessage("Has escapado de la pelea.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startMapsActivity(view);
                        }
                    });
            alertDialog.show();
        }

    }

    public void levelupEvent(){
        int plus;
        if(ally.getCurrentAtk()>ally.getMaxAtk()){
            plus = ally.getCurrentAtk()+1;
            ally.setCurrentAtk(plus);
        }
        if (ally.getCurrentDef()>ally.getMaxDef()){
            plus = ally.getCurrentDef()+1;
            ally.setCurrentDef(plus);
        }
        if (ally.getCurrentAtk()==ally.getMaxAtk() && ally.getCurrentDef()==ally.getMaxDef()){
            evolutionEvent();
        }
    }

    public void evolutionEvent(){
        int evoId = ally.getKind().getEvolution();
        Pokemon evoPoke;
        JSONObject poke = null;
        try {
            poke = jsonPokemon.getJSONObject(evoId-1);
            evoPoke = new Pokemon(poke.getString("id"),poke.getString("name"),poke.getString("type"),poke.getString("strength"),poke.getString("weakness"),poke.getString("hp_max"),poke.getString("ataque_max"),poke.getString("defensa_max"),poke.getString("ev_id"),poke.getString("ImgFront"),poke.getString("ImgBack"));
            PkmonInstance evoPk = new PkmonInstance(evoPoke);
            pokemonTeam.set(0,evoPk);
            tinydb.putListObject("pk_team",pokemonTeam);
            Toast.makeText(this,"Tu pokemon ha evolucinado!",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void animateDamage(int chk, int damage){
        if (chk == 0){
            ht1.setVisibility(View.VISIBLE);
            ht1.setText("-"+damage);
            ht1.animate().translationY(-40).setDuration(1000).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ht1.setVisibility(View.INVISIBLE);
                    ht1.animate().translationY(0).setDuration(100).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }
                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }).start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
        }else{
            ht2.setVisibility(View.VISIBLE);
            ht2.setText("-"+damage);
            ht2.animate().translationY(-40).setDuration(1000).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ht2.setVisibility(View.INVISIBLE);
                    ht2.animate().translationY(0).setDuration(100).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }
                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }).start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
        }
    }

    public void animateHeal(int heal){
        txHeal.setVisibility(View.VISIBLE);
        txHeal.setText("+"+heal);
        txHeal.animate().translationY(-80).setDuration(1000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                txHeal.setVisibility(View.INVISIBLE);
                txHeal.animate().translationY(0).setDuration(100).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    public void GenerateEnemyPokemon(int i){
        try {
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
            Pokemon pk = new Pokemon(id,name,type,strength,weakness,maxHp,maxAtk,maxDef,ev,imgFront,imgBack);
            enemy = new PkmonInstance(pk);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void GeneratePartyPokemon(){
        ally=pokemonTeam.get(0);
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

        tinydb.putObject("bag",bag);
        tinydb.putListObject("pk_team",pokemonTeam);
        Intent intent = new Intent(FightActivity.this,
                MapsActivity.class);
        startActivity(intent);
        finish();
    }

}
