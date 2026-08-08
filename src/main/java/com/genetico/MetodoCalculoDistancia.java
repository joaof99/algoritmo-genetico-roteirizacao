package com.genetico;

import com.genetico.model.Endereco;
import com.genetico.model.MatrizDistancias;

import java.util.List;

public interface MetodoCalculoDistancia {
    MatrizDistancias inicializarDistancias(List<Endereco> enderecos);
    double calcularDistancia(Endereco origem, Endereco Destino);
}
