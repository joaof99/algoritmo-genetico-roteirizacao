package com.genetico.model;

import java.util.List;

public record AlgoritmoGeneticoRequest(List<DistanciaResponse> distancias,
                                       List<Endereco> enderecos,
                                       int tamanhoPopulacao,
                                       int quantidadeGeracoes,
                                       int chanceOcorrenciaMutacao,
                                       int chanceOcorrenciaCrossover) {

}
