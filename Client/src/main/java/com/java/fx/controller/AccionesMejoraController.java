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
import java.time.LocalDate;
import java.time.Year;
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
    @FXML private Button btnGuardar;

    // Campos de la tabla
    @FXML private TableView<SugerenciaMejora> tablaMejoras;
    @FXML private TableColumn<SugerenciaMejora, String> columnaId;
    @FXML private TableColumn<SugerenciaMejora, String> columnaPrograma;
    @FXML private TableColumn<SugerenciaMejora, String> columnaModulo;
    @FXML private TableColumn<SugerenciaMejora, String> columnaSugerencia;
    @FXML private TableColumn<SugerenciaMejora, Integer> columnaYearInicio;
    @FXML private TableColumn<SugerenciaMejora, Integer> columnaYearFin;

    @FXML private TextArea txtAnalisis; // mostrar el análisis

    private SugerenciaMejora sugerenciaSeleccionada;

    @FXML
    public void initialize() {
        configurarColumnasTabla();
        cargarDatosIniciales();
        configurarSeleccionTabla();
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
    private void handleEditarMejora() {
        if (sugerenciaSeleccionada == null) {
            mostrarAlerta("Error", "Selecciona una mejora de la tabla", Alert.AlertType.ERROR);
            return;
        }

        // Validar vigencia de 1 año
        int añoActual = Year.now().getValue();
        if (añoActual - sugerenciaSeleccionada.getYearInicio() >= 1) {
            mostrarAlerta("Error", "No se puede modificar después de 1 año de vigencia", Alert.AlertType.ERROR);
            return;
        }

        // Cargar datos en el formulario
        comboProgramas.setValue(sugerenciaSeleccionada.getPrograma());
        comboModulos.setValue(sugerenciaSeleccionada.getModulo());
        txtSugerencia.setText(sugerenciaSeleccionada.getSugerenciaMejora());
        dpFechaInicio.setValue(LocalDate.of(sugerenciaSeleccionada.getYearInicio(), 1, 1));
        dpFechaFin.setValue(LocalDate.of(sugerenciaSeleccionada.getYearFin(), 1, 1));

        // Cambiar comportamiento del botón Guardar
        btnGuardar.setOnAction(e -> handleActualizarMejora());
    }

    @FXML
    private void handleActualizarMejora() {
        try {
            // Validaciones
            if (!validarCamposActualizacion()) return;

            // Construir objeto actualizado
            SugerenciaMejora mejoraActualizada = construirMejoraDesdeFormulario();
            mejoraActualizada.setId(sugerenciaSeleccionada.getId());

            // Serializar y enviar
            String json = new ObjectMapper().writeValueAsString(mejoraActualizada);
            resultadoService.actualizarMejora(mejoraActualizada.getId(), json);

            // Actualizar UI
            cargarDatosIniciales();
            mostrarAlerta("Éxito", "Mejora actualizada", Alert.AlertType.INFORMATION);
            resetearFormulario();

        } catch (JsonProcessingException e) {
            mostrarAlerta("Error", "Error en formato JSON", Alert.AlertType.ERROR);
        } catch (IOException | InterruptedException e) {
            mostrarAlerta("Error", "Error de conexión: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validarCamposActualizacion() {
        if (dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null) {
            mostrarAlerta("Error", "Fechas inválidas", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private SugerenciaMejora construirMejoraDesdeFormulario() {
        SugerenciaMejora mejora = new SugerenciaMejora();
        mejora.setPrograma(comboProgramas.getValue());
        mejora.setModulo(comboModulos.getValue());
        mejora.setSugerenciaMejora(txtSugerencia.getText());
        mejora.setYearInicio(dpFechaInicio.getValue().getYear());
        mejora.setYearFin(dpFechaFin.getValue().getYear());
        return mejora;
    }

    private void resetearFormulario() {
        handleLimpiarFormulario();
        btnGuardar.setOnAction(e -> handleGuardarAccion());
        sugerenciaSeleccionada = null;
    }

    private String getSueggest(){
        try {
            String id = tablaMejoras.getSelectionModel().getSelectedItem().getId();
            String response = resultadoService.getSuggest(id);
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

    private void configurarSeleccionTabla() {
        tablaMejoras.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            sugerenciaSeleccionada = newVal;
            tablaMejoras.getStyleClass().remove("tabla-seleccion-activa");
            if (newVal != null) {
                tablaMejoras.getStyleClass().add("tabla-seleccion-activa");
            }
        });
    }

    @FXML
    private void handleEliminarMejora() {
        if (sugerenciaSeleccionada == null) {
            mostrarAlerta("Error", "Selecciona una mejora de la tabla", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar esta acción de mejora?");
        confirmacion.setContentText("Esta acción no se puede deshacer");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                resultadoService.eliminarMejora(sugerenciaSeleccionada.getId());
                cargarDatosIniciales(); // Recarga la tabla
                mostrarAlerta("Éxito", "Mejora eliminada correctamente", Alert.AlertType.INFORMATION);
            } catch (IOException | InterruptedException e) {
                mostrarAlerta("Error", "Error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }


    @FXML
    private void handleLimpiarFormulario() {
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
    }
}