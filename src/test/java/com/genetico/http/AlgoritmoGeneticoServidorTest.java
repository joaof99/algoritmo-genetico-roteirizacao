package com.genetico.http;

import com.genetico.exception.AlgoritmoGeneticoServidorException;
import com.genetico.handler.AlgoritmoGeneticoHttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AlgoritmoGeneticoServidorTest {
    @Test
    @DisplayName("Deve criar o servidor, registrar o handler e iniciar")
    void deveCriarServidorRegistrarHandlerEIniciar() throws AlgoritmoGeneticoServidorException {
        var servidorHttp = mock(HttpServer.class);
        var handler = mock(AlgoritmoGeneticoHttpHandler.class);

        try (MockedStatic<HttpServer> httpServer = Mockito.mockStatic(HttpServer.class)) {
            httpServer.when(() -> HttpServer.create(new InetSocketAddress("localhost", 8081), 0))
                    .thenReturn(servidorHttp);

            new AlgoritmoGeneticoServidor(8081, "localhost", handler).iniciarServidor();

            verify(servidorHttp).createContext("/ag/iniciar", handler);
            verify(servidorHttp).start();
            httpServer.verify(() -> HttpServer.create(new InetSocketAddress("localhost", 8081), 0));
        }
    }

    @Test
    @DisplayName("Deve converter falha de I/O ao criar o servidor em exceção própria")
    void deveConverterFalhaDeIoEmExcecaoPropria() {
        var erroDeIo = new IOException("Falha ao abrir a porta");

        try (MockedStatic<HttpServer> httpServer = Mockito.mockStatic(HttpServer.class)) {
            httpServer.when(() -> HttpServer.create(new InetSocketAddress("localhost", 8081), 0))
                    .thenThrow(erroDeIo);

            var excecao = assertThrows(AlgoritmoGeneticoServidorException.class,
                    () -> new AlgoritmoGeneticoServidor(8081, "localhost", mock(AlgoritmoGeneticoHttpHandler.class))
                            .iniciarServidor());

            assertEquals("Houve um erro de I/O ao subir o servidor", excecao.getMessage());
            assertSame(erroDeIo, excecao.getCause());
        }
    }
}
