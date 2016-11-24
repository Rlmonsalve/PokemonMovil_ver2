package com.rlmonsalve.pokemonmovil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;

/**
 * Created by ROBERTO on 20/09/2016.
 */
public class Pokemon{

    private int id;
    private String name;
    private String type;
    private String strength;
    private String weakness;
    private int maxHp;
    private int maxAttack;
    private int maxDefense;
    private int evolution;
    private String imgFront;
    private String imgBack;

    public Pokemon(String id, String name, String type, String strength, String weakness, String maxHp, String maxAttack, String maxDefense, String evolution, String imgFront, String imgBack) {
        this.id = Integer.parseInt(id);
        this.name = name;
        this.type = type;
        this.strength = strength;
        this.weakness = weakness;
        this.maxHp = Integer.parseInt(maxHp);
        this.maxAttack = Integer.parseInt(maxAttack);
        this.maxDefense = Integer.parseInt(maxDefense);
        this.evolution = Integer.parseInt(evolution);
        this.imgFront = imgFront;
        this.imgBack = imgBack;
    }

    /*public Pokemon(int id, String nombre, int hp, int atk, int def, int evo, String img){
        this.id = id;
        this.name = nombre;
        this.maxHp = hp;
        this.maxAttack = atk;
        this.maxDefense = def;
        this.evolution = evo;
        this.imgUrl = img;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getWeakness() {
        return weakness;
    }

    public void setWeakness(String weakness) {
        this.weakness = weakness;
    }

    public int getEvolution() {
        return evolution;
    }

    public void setEvolution(int evolution) {
        this.evolution = evolution;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getMaxAttack() {
        return maxAttack;
    }

    public void setMaxAttack(int maxAttack) {
        this.maxAttack = maxAttack;
    }

    public int getMaxDefense() {
        return maxDefense;
    }

    public void setMaxDefense(int maxDefense) {
        this.maxDefense = maxDefense;
    }

    public String getImgFront() {
        return imgFront;
    }

    public void setImgFront(String imgFront) {
        this.imgFront = imgFront;
    }

    public String getImgBack() {
        return imgBack;
    }

    public void setImgBack(String imgBack) {
        this.imgBack = imgBack;
    }

}
