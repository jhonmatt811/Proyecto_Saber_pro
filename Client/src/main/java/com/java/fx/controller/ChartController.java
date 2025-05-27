package com.java.fx.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.java.fx.Usuarios_y_Roles.Sesion;
import com.java.fx.model.dto.ComparacionResponse;
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

    @FXML private TextField txtId, txtYear, txtPrograma, txtGrupo;
    @FXML private BarChart<String, Number> barChart;
    @FXML private ProgressIndicator progressIndicator;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public void configurarParametros(String id, String year, String programa, String grupo) {
        Platform.runLater(() -> {
            txtId.setText(isNumeric(id) ? id : "");
            txtYear.setText(isNumeric(year) ? year : "");
            txtPrograma.setText(safeText(programa));
            txtGrupo.setText(safeText(grupo));

            if (!txtId.getText().isEmpty()) {
                iniciarCargaDatos();
            }
        });
    }

    @FXML
    private void handleComparar() {
        String id = txtId.getText().trim();
        if (!isNumeric(id)) {
            mostrarAlerta("Error de Validación", "El ID debe ser numérico.");
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
        StringBuilder url = new StringBuilder("http://localhost:8080/comparar")
                .append(txtId.getText().trim());

        List<String> params = new ArrayList<>();
        agregarParametro(params, "year", txtYear.getText());
        agregarParametro(params, "programa", txtPrograma.getText());
        agregarParametro(params, "grupo", txtGrupo.getText());

        if (!params.isEmpty()) {
            url.append("?").append(String.join("&", params));
        }

        return url.toString();
    }

    private void agregarParametro(List<String> params, String clave, String valor) {
        if (valor != null && !valor.trim().isEmpty()) {
            params.add(clave + "=" + valor.trim());
        }
    }

    private void realizarPeticion(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + Sesion.jwtToken)
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        throw new RuntimeException("Error del servidor: " + response.statusCode());
                    }
                    return response.body();
                })
                .thenAccept(this::procesarRespuesta)
                .exceptionally(ex -> {
                    Platform.runLater(() -> mostrarAlerta("Error", ex.getMessage()));
                    return null;
                })
                .thenRun(() -> Platform.runLater(() -> progressIndicator.setVisible(false)));
    }

    private void procesarRespuesta(String json) {
        Platform.runLater(() -> {
            try {
                ComparacionResponse respuesta = gson.fromJson(json, ComparacionResponse.class);
                if (respuesta == null || respuesta.getResultadosEstudiante() == null) {
                    throw new JsonSyntaxException("Estructura JSON inválida");
                }
                actualizarGrafica(respuesta);
            } catch (JsonSyntaxException e) {
                mostrarAlerta("Error de Datos", "Formato de respuesta inválido.");
            }
        });
    }

    private void actualizarGrafica(ComparacionResponse respuesta) {
        barChart.setTitle("Comparación: " + respuesta.getNombreEstudiante());
        barChart.getData().setAll(
                crearSerie("Estudiante", respuesta.getResultadosEstudiante()),
                crearSerieGrupo(respuesta)
        );
    }

    private XYChart.Series<String, Number> crearSerie(String nombre, List<ResultadoEstudiante> resultados) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName(nombre);

        for (ResultadoEstudiante r : resultados) {
            serie.getData().add(new XYChart.Data<>(r.getNombreModulo(), r.getPuntajeModulo()));
        }
        return serie;
    }

    private XYChart.Series<String, Number> crearSerieGrupo(ComparacionResponse respuesta) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Grupo");

        for (ResultadoEstudiante est : respuesta.getResultadosEstudiante()) {
            respuesta.getResultadosGrupo().stream()
                    .filter(g -> g.getNombreModulo().equals(est.getNombreModulo()))
                    .findFirst()
                    .ifPresent(grupo -> serie.getData().add(new XYChart.Data<>(
                            grupo.getNombreModulo(), grupo.getPromedioGrupoModulo()
                    )));
        }
        return serie;
    }

    private boolean isNumeric(String text) {
        return text != null && text.matches("\\d+");
    }

    private String safeText(String text) {
        return text != null ? text.trim() : "";
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
