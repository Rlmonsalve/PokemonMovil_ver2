package com.rlmonsalve.pokemonmovil;

/**
 * Created by Robert on 27/09/2016.
 */

public class Bag {

    private int pokeballNo;
    private int potionNo;
    private int maxPotNo;
    private int mxPkballs;
    private int mxPtion;
    private int mxMxPtion;

    public Bag(int pokeballNo, int potionNo, int maxPotNo) {
        this.pokeballNo = pokeballNo;
        this.potionNo = potionNo;
        this.maxPotNo = maxPotNo;
        this.mxPkballs = 100;
        this.mxPtion = 100;
        this.mxMxPtion = 100;
    }

    public int getPokeballNo() {
        return pokeballNo;
    }

    public void setPokeballNo(int pokeballNo) {
        this.pokeballNo = pokeballNo;
    }

    public int getPotionNo() {
        return potionNo;
    }

    public void setPotionNo(int potionNo) {
        this.potionNo = potionNo;
    }

    public int getMaxPotNo() {
        return maxPotNo;
    }

    public void setMaxPotNo(int maxPotNo) {
        this.maxPotNo = maxPotNo;
    }
}
