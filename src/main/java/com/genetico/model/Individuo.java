package com.genetico.model;


public class Individuo {
    private final Cromossomo cromosso;

    public Individuo() {
        this.cromosso = new Cromossomo();
    }

    public Cromossomo getCromosso() {
        return this.cromosso;
    }
}

