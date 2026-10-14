package com.genetico.model;

import java.util.List;

public record AlgoritmoGeneticoRequest(int idRota,
                                       List<DistanciaResponse> distancias,
                                       List<Endereco> enderecos,
                                       int tamanhoPopulacao,
                                       int quantidadeGeracoes,
                                       int chanceOcorrenciaMutacao,
                                       int chanceOcorrenciaCrossover) {

}
