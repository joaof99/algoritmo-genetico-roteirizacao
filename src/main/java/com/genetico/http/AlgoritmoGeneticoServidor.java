package com.genetico.http;

import com.genetico.exception.AlgoritmoGeneticoServidorException;
import com.genetico.handler.AlgoritmoGeneticoHttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

public class AlgoritmoGeneticoServidor {
    private static final Logger log = LogManager.getLogger();
    private final String hostname;
    private final AlgoritmoGeneticoHttpHandler algoritmoGeneticoHttpHandler;
    private final int porta;
    private HttpServer server;

    public AlgoritmoGeneticoServidor(int porta, String hostname, AlgoritmoGeneticoHttpHandler handler) throws AlgoritmoGeneticoServidorException {
        this.porta = porta;
        this.hostname = hostname;
        this.algoritmoGeneticoHttpHandler = handler;
    }

    public void iniciarServidor() throws AlgoritmoGeneticoServidorException {
        try {
            server = HttpServer.create(new InetSocketAddress(hostname, porta), 0);
            server.createContext("/ag/iniciar", algoritmoGeneticoHttpHandler);
            server.start();

            log.info("Aguardando requisições na porta {}...", porta);
        } catch (IOException e) {
            throw new AlgoritmoGeneticoServidorException("Houve um erro de I/O ao subir o servidor", e);
        }
    }
}
