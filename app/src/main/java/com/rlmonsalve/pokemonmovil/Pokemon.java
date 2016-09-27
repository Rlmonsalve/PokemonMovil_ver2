package com.rlmonsalve.pokemonmovil;

/**
 * Created by ROBERTO on 20/09/2016.
 */
public class Pokemon {

    private int id;
    private String name;
    private Type type;
    private int maxHp;
    private int maxAttack;
    private int maxDefense;
    private int evolution;
    private String imgUrl;

    public Pokemon(int id, String nombre, int hp, int atk, int def, int evo, String img){
        this.id = id;
        this.name = nombre;
        this.maxHp = hp;
        this.maxAttack = atk;
        this.maxDefense = def;
        this.evolution = evo;
        this.imgUrl = img;
    }

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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getMaxDefense() {
        return maxDefense;
    }

    public void setMaxDefense(int maxDefense) {
        this.maxDefense = maxDefense;
    }

    public int getEvolution() {
        return evolution;
    }

    public void setEvolution(int evolution) {
        this.evolution = evolution;
    }

    public int getMaxAttack() {
        return maxAttack;
    }

    public void setMaxAttack(int maxAttack) {
        this.maxAttack = maxAttack;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
