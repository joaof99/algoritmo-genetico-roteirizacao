package com.genetico.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genetico.exception.AlgoritmoGeneticoHttpServerException;
import com.genetico.handler.AlgoritmoGeneticoHttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Servidor {
    private static final Logger log = LogManager.getLogger();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String hostname;
    private final AlgoritmoGeneticoHttpHandler algoritmoGeneticoHttpHandler;
    private final int porta;
    private final String url;
    private HttpServer server;

    public Servidor(int porta, String hostname, AlgoritmoGeneticoHttpHandler algoritmoGeneticoHttpHandler) throws AlgoritmoGeneticoHttpServerException {
        this.porta = porta;
        this.hostname = hostname;
        this.algoritmoGeneticoHttpHandler = algoritmoGeneticoHttpHandler;
        this.url = definirUrl();
    }

    private String definirUrl() throws AlgoritmoGeneticoHttpServerException {
        var urlAlgoritmoGeneticoHttpServer = System.getenv("AG_SERVER_URL");

        if (urlAlgoritmoGeneticoHttpServer == null || urlAlgoritmoGeneticoHttpServer.isBlank()) {
            throw new AlgoritmoGeneticoHttpServerException("Variável de ambiente: AG_SERVER não configurada");
        }

        return urlAlgoritmoGeneticoHttpServer;
    }

    public void iniciarServidor() throws AlgoritmoGeneticoHttpServerException {
        try {
            server = HttpServer.create(new InetSocketAddress(hostname, porta), 0);
            server.createContext(url, algoritmoGeneticoHttpHandler);
            server.start();

            log.info("Aguardando requisições na porta {}...", porta);
        } catch (IOException e) {
            throw new AlgoritmoGeneticoHttpServerException("Houve um erro de I/O ao subir o servidor", e);
        }
    }
}
