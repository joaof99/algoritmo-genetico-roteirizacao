package com.genetico.distancia;

import com.genetico.model.Endereco;

import java.util.List;

public interface MetodoCalculoDistancia {
    double calcularDistancia(Endereco origem, Endereco destino);
    void registrarDistancias(List<Endereco> enderecos);
}
