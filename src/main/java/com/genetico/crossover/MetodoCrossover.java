package com.genetico.crossover;

import com.genetico.model.Cromossomo;

public interface MetodoCrossover {
    Cromossomo[] realizarCrossover(Cromossomo pai1, Cromossomo pai2);
}
