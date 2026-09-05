package com.genetico.distancia;

import com.genetico.exception.RotaClientException;
import com.genetico.factory.RotaClientFactory;
import com.genetico.model.Endereco;
import com.genetico.model.MatrizDistancias;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class CalculoDistanciaAPI implements MetodoCalculoDistancia {
    private static final Logger log = LogManager.getLogger(CalculoDistanciaAPI.class);

    @Override
    public void registrarDistancias(List<Endereco> enderecos) throws RotaClientException {
        if (enderecos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum endereço encontrado. Impossível iniciar a roteirização");
        }

        if (enderecos.size() < 6) {
            throw new IllegalArgumentException(String.format("É necessário no mínimo 6 endereços para a roteirização. Encontrado %d", enderecos.size()));
        }

        log.info("Inicializando distâncias via API");

        var idsEnderecos = enderecos.stream().map(Endereco::id).toList();
        var distancias = RotaClientFactory.getRotaClient().buscarDistanciasEnderecos(idsEnderecos);


        for (var distancia : distancias) {
            MatrizDistancias.setDistancia(distancia.idOrigem(), distancia.idDestino(), distancia.distancia());
        }
    }

    @Override
    public double calcularDistancia(Endereco origem, Endereco destino) {
        return 0.5;
    }
}
