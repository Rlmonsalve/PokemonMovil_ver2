package com.rlmonsalve.pokemonmovil;

import java.util.Random;

/**
 * Created by Robert on 27/09/2016.
 */

public class PkmonInstance {

    private int id;
    private Pokemon kind;
    private double currentHp;
    private double hp;
    private int currentAtk;
    private int currentDef;
    private int maxHp;
    private int maxAtk;
    private int maxDef;
    private Boolean status;

    public PkmonInstance(Pokemon p) {
        this.id = p.getId();
        this.kind = p;
        this.hp=genetateStat(p.getMaxHp());
        this.currentHp = hp;
        this.currentAtk=genetateStat(p.getMaxAttack());
        this.currentDef=genetateStat(p.getMaxDefense());
        this.maxHp=p.getMaxHp();
        this.maxAtk=p.getMaxAttack();
        this.maxDef=p.getMaxDefense();
        this.kind=p;

    }

    public int getId() {
        return id;
    }

    public Pokemon getKind() {
        return kind;
    }

    public double getCurrentHp() {
        return currentHp;
    }

    public int getCurrentAtk() {
        return currentAtk;
    }

    public int getCurrentDef() {
        return currentDef;
    }

    public void setCurrentDef(int currentDef) {
        this.currentDef = currentDef;
    }

    public void setCurrentAtk(int currentAtk) {
        this.currentAtk = currentAtk;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getMaxAtk() {
        return maxAtk;
    }

    public int getMaxDef() {
        return maxDef;
    }

    public Boolean getStatus() {
        return status;
    }

    public int genetateStat(int stat) {
        Random r = new Random();
        int minStat= stat-10;
        int randStat = r.nextInt((stat+1) - minStat) + minStat;
        return randStat;
    }

    public double getHp() {
        return hp;
    }
}
