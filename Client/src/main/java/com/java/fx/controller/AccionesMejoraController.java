package com.java.fx.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.java.fx.model.AccionesDeMejora.*;
import com.java.fx.service.ResultadoService;
import javafx.application.Platform;
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

import javafx.concurrent.Task;       // Para la clase Task
//mostrar analisis de la ia en ventana emergente
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.control.ButtonBar;

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
    @FXML private Button btnAnalisisIA;

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
    private void handleObtenerAnalisisIA() {
        if (sugerenciaSeleccionada == null) {
            mostrarAlerta("Error", "Selecciona una mejora de la tabla", Alert.AlertType.ERROR);
            return;
        }

        Alert progreso = new Alert(Alert.AlertType.INFORMATION);
        progreso.setTitle("Generando análisis");
        progreso.setHeaderText("Solicitando análisis a Gemmini AI");
        progreso.setContentText("Por favor espere...");
        progreso.show();

        Task<AnalisisMejora> task = new Task<>() {
            @Override
            protected AnalisisMejora call() throws Exception {
                return resultadoService.obtenerAnalisisIA(sugerenciaSeleccionada.getId());
            }
        };

        task.setOnSucceeded(e -> {
            progreso.close();
            AnalisisMejora analisis = task.getValue();

            // Validación del porcentaje
            String headerText;
            if (analisis.getPorcentajeMejora() == null
                    || Double.isInfinite(analisis.getPorcentajeMejora())
                    || Double.isNaN(analisis.getPorcentajeMejora())) {
                headerText = "Porcentaje de mejora: N/A (No disponible)";
            } else {
                headerText = String.format("Porcentaje de mejora: %.2f%%", analisis.getPorcentajeMejora());
            }
            // Crear alerta personalizada
            Alert alertaResultado = new Alert(Alert.AlertType.INFORMATION);
            alertaResultado.setTitle("Análisis de IA");
            alertaResultado.setHeaderText(headerText);

            // Área de texto con scroll
            TextArea textoAnalisis = new TextArea(analisis.getMessage());
            textoAnalisis.setEditable(false);
            textoAnalisis.setWrapText(true);
            textoAnalisis.setPrefSize(600, 400); // Tamaño personalizable

            ScrollPane scroll = new ScrollPane(textoAnalisis);
            scroll.setFitToWidth(true);

            alertaResultado.getDialogPane().setContent(scroll);
            alertaResultado.getDialogPane().setPrefSize(620, 450);
            alertaResultado.setResizable(true);

            // Botón adicional para copiar
            ButtonType copiarBoton = new ButtonType("Copiar análisis", ButtonBar.ButtonData.OTHER);
            alertaResultado.getButtonTypes().add(copiarBoton);

            alertaResultado.showAndWait().ifPresent(buttonType -> {
                if (buttonType == copiarBoton) {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putString(analisis.getMessage());
                    clipboard.setContent(content);
                }
            });
        });

        task.setOnFailed(e -> {
            progreso.close();
            mostrarAlerta("Error", "Error al generar análisis: " + task.getException().getMessage(),
                    Alert.AlertType.ERROR);
        });

        new Thread(task).start();
    }

    @FXML
    private void handleLimpiarFormulario() {
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
    }
}