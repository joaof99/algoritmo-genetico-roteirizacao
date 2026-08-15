package com.genetico.distancia;

import com.genetico.model.Endereco;
import com.genetico.model.MatrizDistancias;

import java.util.List;

public interface MetodoCalculoDistancia {
    double calcularDistancia(Endereco origem, Endereco Destino);
    MatrizDistancias inicializarDistancias(List<Endereco> enderecos);
}
