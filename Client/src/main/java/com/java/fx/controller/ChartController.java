package com.java.fx.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.java.fx.model.dto.ComparacionResponse;
import com.java.fx.model.dto.ModuloGrupo;
import com.java.fx.model.dto.ResultadoEstudiante;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ChartController {

    @FXML private TextField txtId;
    @FXML private TextField txtYear;
    @FXML private TextField txtPrograma;
    @FXML private TextField txtGrupo;
    @FXML private BarChart<String, Number> barChart;
    @FXML private ProgressIndicator progressIndicator;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public void configurarParametros(String id, String year, String programa, String grupo) {
        Platform.runLater(() -> {
            txtId.setText(validarNumero(id) ? id : "");
            txtYear.setText(validarNumero(year) ? year : "");
            txtPrograma.setText(programa != null ? programa : "");
            txtGrupo.setText(grupo != null ? grupo : "");

            if(!txtId.getText().isEmpty()) {
                iniciarCargaDatos();
            }
        });
    }

    @FXML
    private void handleComparar() {
        final String id = txtId.getText().trim();

        if(!validarId(id)) {
            mostrarAlerta("Error de Validación", "ID debe ser numérico");
            return;
        }

        iniciarCargaDatos();
    }

    private void iniciarCargaDatos() {
        barChart.getData().clear();
        progressIndicator.setVisible(true);

        String url = construirURL();
        realizarPeticion(url);
    }

    private String construirURL() {
        StringBuilder urlBuilder = new StringBuilder("http://localhost:8080/comparar/persona/")
                .append(txtId.getText().trim());

        List<String> params = new ArrayList<>();
        agregarParametro(params, "year", txtYear.getText().trim());
        agregarParametro(params, "programa", txtPrograma.getText().trim());
        agregarParametro(params, "grupo", txtGrupo.getText().trim());

        if(!params.isEmpty()) {
            urlBuilder.append("?").append(String.join("&", params));
        }

        return urlBuilder.toString();
    }

    private void agregarParametro(List<String> params, String nombre, String valor) {
        if(valor != null && !valor.isEmpty()) {
            params.add(nombre + "=" + valor);
        }
    }

    private void realizarPeticion(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if(response.statusCode() != 200) {
                        throw new RuntimeException("Error en el servidor: Código " + response.statusCode());
                    }
                    return response.body();
                })
                .thenAccept(this::procesarRespuesta)
                .exceptionally(ex -> {
                    Platform.runLater(() ->
                            mostrarAlerta("Error de Conexión", ex.getCause().getMessage()));
                    return null;
                })
                .thenRun(() -> Platform.runLater(() -> progressIndicator.setVisible(false)));
    }

    private void procesarRespuesta(String json) {
        Platform.runLater(() -> {
            try {
                ComparacionResponse respuesta = gson.fromJson(json, ComparacionResponse.class);
                if(respuesta == null || respuesta.getResultadosEstudiante() == null) {
                    throw new JsonSyntaxException("Estructura JSON inválida");
                }
                actualizarGrafica(respuesta);
            } catch (JsonSyntaxException e) {
                mostrarAlerta("Error de Datos", "Formato de respuesta inválido");
            }
        });
    }

    private void actualizarGrafica(ComparacionResponse respuesta) {
        barChart.getData().clear();
        barChart.setTitle("Comparación: " + respuesta.getNombreEstudiante());

        XYChart.Series<String, Number> serieEstudiante = crearSerie("Estudiante", respuesta.getResultadosEstudiante());
        XYChart.Series<String, Number> serieGrupo = crearSerieGrupo(respuesta);

        barChart.getData().addAll(serieEstudiante, serieGrupo);
    }

    private XYChart.Series<String, Number> crearSerie(String nombre, List<ResultadoEstudiante> resultados) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName(nombre);

        resultados.forEach(r ->
                serie.getData().add(new XYChart.Data<>(
                        r.getNombreModulo(),
                        r.getPuntajeModulo()
                ))
        );

        return serie;
    }

    private XYChart.Series<String, Number> crearSerieGrupo(ComparacionResponse respuesta) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Grupo");

        respuesta.getResultadosEstudiante().forEach(estudiante -> {
            respuesta.getResultadosGrupo().stream()
                    .filter(g -> g.getNombreModulo().equals(estudiante.getNombreModulo()))
                    .findFirst()
                    .ifPresent(grupo ->
                            serie.getData().add(new XYChart.Data<>(
                                    grupo.getNombreModulo(),
                                    grupo.getPromedioGrupoModulo()
                            ))
                    );
        });

        return serie;
    }

    private boolean validarId(String id) {
        return validarNumero(id);
    }

    private boolean validarNumero(String input) {
        return input != null && input.matches("\\d+");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}