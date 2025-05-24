package com.java.fx.service;

import com.java.fx.controller.ResultadosController;

import com.java.fx.model.Resultado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Componente que carga archivos y envío de resultados al backend.
 */
@Component
public class ResultadoUploader {

    private final ResultadoService resultadoService;

    @Autowired
    public ResultadoUploader(ResultadoService resultadoService) {
        this.resultadoService = resultadoService;
    }

    /**
     * Procesa un archivo de resultados: carga, mapea y envía al backend.
     * @param archivo archivo con datos (CSV, JSON, XLSX,...)
     * @param ciclo   ciclo académico
     * @param year    año académico
     * @throws IOException si falla lectura o envío
     */
    public void subirArchivo(File archivo, int ciclo, int year) throws IOException {
        // Cargar datos desde archivo
        List<Resultado> resultados = resultadoService.cargarDatosDesdeArchivo(archivo, ciclo, year);
        if (resultados.isEmpty()) {
            throw new IOException("No se encontraron datos válidos en el archivo: " + archivo.getName());
        }
        // Enviar al backend
        resultadoService.enviarResultadosAlBackend(resultados);
    }
}
