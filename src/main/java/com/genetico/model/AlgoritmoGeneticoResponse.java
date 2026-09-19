package com.genetico.model;

import java.util.List;

public record AlgoritmoGeneticoResponse(List<DistanciaResponse> distancias,
                                        List<Endereco> enderecos,
                                        int tamanhoPopulacao,
                                        int quantidadeGeracoes,
                                        int chanceOcorrenciaMutacao,
                                        int chanceOcorrenciaCrossover) {}
