package com.java.fx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.java.fx.model.AccionesDeMejora.*;
import com.java.fx.service.ResultadoService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.control.ComboBox;
import java.io.IOException;
import java.util.*;


@Component
public class AccionesMejoraController {
    //formulario
    @Autowired private ResultadoService resultadoService; // Inyectar el servicio
    @FXML private ComboBox<Programa> comboProgramas;
    @FXML private ComboBox<Modulo> comboModulos;
    @FXML private TextArea txtSugerencia;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;

    // Campos de la tabla
    @FXML private TableView<SugerenciaMejora> tablaMejoras;
    @FXML private TableColumn<SugerenciaMejora, String> columnaId;
    @FXML private TableColumn<SugerenciaMejora, String> columnaPrograma;
    @FXML private TableColumn<SugerenciaMejora, String> columnaModulo;
    @FXML private TableColumn<SugerenciaMejora, String> columnaSugerencia;
    @FXML private TableColumn<SugerenciaMejora, Integer> columnaYearInicio;
    @FXML private TableColumn<SugerenciaMejora, Integer> columnaYearFin;

    @FXML private TextArea txtAnalisis; // mostrar el análisis

    @FXML
    public void initialize() {
        configurarColumnasTabla();
        cargarDatosIniciales();
        cargarDatosDesdeAPI();
    }


    private void configurarColumnasTabla() {
        columnaPrograma.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPrograma().getNombre()));
        columnaModulo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getModulo().getNombre()));
        columnaSugerencia.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSugerenciaMejora()));
        columnaYearInicio.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getYearInicio()).asObject());
        columnaYearFin.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getYearFin()).asObject());
    }

    private void cargarDatosIniciales() {
        try {
            // Cargar ComboBox
            comboProgramas.getItems().setAll(resultadoService.obtenerProgramas());
            comboModulos.getItems().setAll(resultadoService.obtenerModulos());

            // Cargar Tabla
            tablaMejoras.getItems().setAll(resultadoService.obtenerMejoras());

        } catch (IOException | InterruptedException e) {
            mostrarAlerta("Error", "Error al cargar datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarDatosDesdeAPI() {
        try {
            // Obtener todos los programas
            List<Programa> programas = resultadoService.obtenerProgramas();
            comboProgramas.getItems().setAll(programas);

            // Obtener resultados para listar módulos
            List<Modulo> Modulos = resultadoService.obtenerModulos();
            comboModulos.getItems().setAll(Modulos);

        } catch (IOException | InterruptedException e) {
            mostrarAlerta("Error", "Error al cargar datos.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleGuardarAccion() {
        try {
            // Validaciones
            if (comboProgramas.getValue() == null || comboModulos.getValue() == null) {
                mostrarAlerta("Error", "Selecciona un programa y un módulo.", Alert.AlertType.ERROR);
                return;
            }
            if (dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null) {
                mostrarAlerta("Error", "Selecciona fechas válidas.", Alert.AlertType.ERROR);
                return;
            }
            if (txtSugerencia.getText().isBlank()) {
                mostrarAlerta("Error", "La sugerencia no puede estar vacía.", Alert.AlertType.ERROR);
                return;
            }

            // Crear objeto
            SugerenciaMejora sugerencia = new SugerenciaMejora();
            sugerencia.setPrograma(comboProgramas.getValue());
            sugerencia.setModulo(comboModulos.getValue());
            sugerencia.setSugerenciaMejora(txtSugerencia.getText());
            sugerencia.setYearInicio(dpFechaInicio.getValue().getYear());
            sugerencia.setYearFin(dpFechaFin.getValue().getYear());

            // Serializar
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(sugerencia);
            System.out.println("JSON enviado: " + json);

            // Enviar al API
            resultadoService.enviarSugerencia(json);

            // Éxito
            mostrarAlerta("Éxito", "Sugerencia guardada.", Alert.AlertType.INFORMATION);
            handleLimpiarFormulario();

            // Obtener el análisis después de guardar
            AnalisisMejora analisis = resultadoService.obtenerAnalisisMejora(sugerencia);

            // Mostrar el análisis en el TextArea
            String textoAnalisis = String.format(
                    "Porcentaje de Mejora: %.2f%%\n\n%s",
                    analisis.getPorcentajeMejora(),
                    analisis.getMessage()
            );
            txtAnalisis.setText(textoAnalisis);

        } catch (JsonProcessingException e) {
            mostrarAlerta("Error", "Error en el JSON: " + e.getOriginalMessage(), Alert.AlertType.ERROR);
        } catch (IOException | InterruptedException e) {
            mostrarAlerta("Error", "Error de conexión: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void handleLimpiarFormulario() {
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
    }
}