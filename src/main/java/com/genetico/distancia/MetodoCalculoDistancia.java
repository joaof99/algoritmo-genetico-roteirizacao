package com.genetico.distancia;

import com.genetico.model.Endereco;

import java.util.List;

public interface MetodoCalculoDistancia {
    double calcularDistancia(Endereco origem, Endereco Destino);
    void registrarDistancias(List<Endereco> enderecos);
}
