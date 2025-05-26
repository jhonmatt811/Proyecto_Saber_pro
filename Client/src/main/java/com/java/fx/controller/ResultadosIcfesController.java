package com.java.fx.controller;

import com.java.fx.model.ResultadoIcfes;
import com.java.fx.service.ResultadoService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ResultadosIcfesController {

    @Autowired private ResultadoService icfesService;

    // Campos del formulario de búsqueda
    @FXML private TextField txtLimit;
    @FXML private TextField txtPeriodo;

    // Tabla de resultados
    @FXML private TableView<ResultadoIcfes> tablaResultados;
    @FXML private TableColumn<ResultadoIcfes, Integer> colPeriodo;
    @FXML private TableColumn<ResultadoIcfes, String> colTipoDocumento;
    @FXML private TableColumn<ResultadoIcfes, String> colNumRegistro;
    @FXML private TableColumn<ResultadoIcfes, Integer> colSniesProgramaAcademico;
    @FXML private TableColumn<ResultadoIcfes, Integer> coltipoEvaluado;
    @FXML private TableColumn<ResultadoIcfes, String> colPrograma;
    @FXML private TableColumn<ResultadoIcfes, String> colCiudad;
    @FXML private TableColumn<ResultadoIcfes, Integer> colRCuantitativo;
    @FXML private TableColumn<ResultadoIcfes, Integer> colLecturaCritica;
    @FXML private TableColumn<ResultadoIcfes, Integer> colComEscrita;
    @FXML private TableColumn<ResultadoIcfes, Integer> colComEscritaDes;
    @FXML private TableColumn<ResultadoIcfes, Integer> colIngles;
    @FXML private TableColumn<ResultadoIcfes, Integer> colInglesDes;
    @FXML private TableColumn<ResultadoIcfes, Integer> colCompCiudadana;

    @FXML
    public void initialize() {
        configurarColumnas();
    }

    private void configurarColumnas() {
        colPeriodo.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getPeriodo()).asObject());
        colTipoDocumento.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipoDocumento()));
        colNumRegistro.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNumeroRegistro()));

        colPrograma.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPrograma()));
        colCiudad.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCiudad()));
        colRCuantitativo.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getRazonamientoCuantitativo()).asObject());
        colLecturaCritica.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getLecturaCritica()).asObject());
    }

    @FXML
    private void handleBuscar() {
        try {
            // Obtener y validar parámetros
            Integer limit = txtLimit.getText().isEmpty() ? null : Integer.parseInt(txtLimit.getText());
            Integer periodo = validarPeriodo(txtPeriodo.getText());

            // Obtener resultados con offset=0 por defecto
            List<ResultadoIcfes> resultados = icfesService.obtenerResultadosIcfes(
                    limit,
                    periodo,
                    2  // Offset por defecto
            );

            tablaResultados.getItems().setAll(resultados);

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Formato numérico inválido", Alert.AlertType.ERROR);
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al obtener datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Integer validarPeriodo(String periodoStr) {
        try {
            Integer periodo = Integer.parseInt(periodoStr);
            String periodoPattern = "20(1[89]|2\\d)(1|2|3|4|5|6|8|9|0)?"; // Regex para validar periodos ICFES

            if (!periodoStr.matches(periodoPattern)) {
                throw new IllegalArgumentException("Formato de periodo inválido. Ejemplos válidos: 20183, 20204");
            }
            return periodo;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El periodo debe ser un número entero");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}