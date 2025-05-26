package com.java.fx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.java.fx.model.AccionesDeMejora.Modulo;
import com.java.fx.model.AccionesDeMejora.Programa;
import com.java.fx.model.AccionesDeMejora.SugerenciaMejora;
import com.java.fx.service.ResultadoService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
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

    @FXML
    public void initialize() {
        configurarColumnasTabla();
        cargarDatosIniciales();
        cargarDatosDesdeAPI();
    }


    private void configurarColumnasTabla() {
        columnaId.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getId()));
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

    private String getSueggest(){
        try {
            String id = tablaMejoras.getSelectionModel().getSelectedItem().getId();
            Integer yearInicio = tablaMejoras.getSelectionModel().getSelectedItem().getYearInicio();
            Integer yearFin = tablaMejoras.getSelectionModel().getSelectedItem().getYearFin();
            String response = resultadoService.getSuggest(id,yearInicio,yearFin);
            ObjectMapper objectMapper = new ObjectMapper();

            // Parseamos el JSON a un Map
            Map<String, Object> map = objectMapper.readValue(response, Map.class);

            // Obtener el objeto "accionMejora"
            Map<String, Object> accionMejora = (Map<String, Object>) map.get("accionMejora");

            // Acceder a "sugerenciaMejora"
            String sugerencia = (String) accionMejora.get("sugerenciaMejora");

            // Imprimir la sugerencia
            System.out.println("Sugerencia: " + sugerencia);

            // También puedes acceder a los anidados, por ejemplo "programa" > "nombre":
            Map<String, Object> programa = (Map<String, Object>) accionMejora.get("programa");
            String nombrePrograma = (String) programa.get("nombre");
            System.out.println("Programa: " + nombrePrograma);
        }catch (IOException | InterruptedException e){
            this.mostrarAlerta("Error", "Error al obtener sueggestión: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        return "";
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

            // Enviar al API
            resultadoService.enviarSugerencia(json);

            // Éxito
            mostrarAlerta("Éxito", "Sugerencia guardada.", Alert.AlertType.INFORMATION);
            handleLimpiarFormulario();

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