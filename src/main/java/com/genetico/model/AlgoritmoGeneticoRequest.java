package com.genetico.model;

import java.util.List;

public record AlgoritmoGeneticoRequest(int idRoteirizacao,
                                       List<DistanciaRequest> distancias,
                                       List<Endereco> enderecos,
                                       int tamanhoPopulacao,
                                       int quantidadeGeracoes,
                                       int chanceOcorrenciaMutacao,
                                       int chanceOcorrenciaCrossover) {

}
