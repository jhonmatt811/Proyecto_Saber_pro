package com.java.fx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccionesMejoraController {
    @FXML private TextField txtNombre;
    @FXML private TextArea txtObjetivo;
    @FXML private ComboBox<String> cbArea;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtResponsable;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaFin;
    @FXML private TextArea txtRecursos;
    @FXML private Button btnAdjuntar;
    @FXML private Label lblArchivoAdjunto;

    private File archivoEvidencia;

    @FXML
    public void initialize() {
        // Configurar áreas disponibles
        cbArea.getItems().addAll(
                "Comunicación Escrita",
                "Razonamiento Cuantitativo",
                "Inglés",
                "Competencias Ciudadanas",
                "Lectura Crítica"
        );
    }

    @FXML
    private void handleAdjuntarEvidencia() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar evidencia");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documentos", "*.pdf", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        archivoEvidencia = fileChooser.showOpenDialog(btnAdjuntar.getScene().getWindow());
        if (archivoEvidencia != null) {
            lblArchivoAdjunto.setText(archivoEvidencia.getName());
        }
    }

    @FXML
    private void handleGuardarAccion() {
        // Validar campos obligatorios
        if (txtNombre.getText().isEmpty() || cbArea.getValue() == null ||
                dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null) {
            mostrarAlerta("Campos requeridos", "Debe completar los campos obligatorios: Nombre, Área y Fechas");
            return;
        }

        if (dpFechaFin.getValue().isBefore(dpFechaInicio.getValue())) {
            mostrarAlerta("Error en fechas", "La fecha de fin no puede ser anterior a la fecha de inicio");
            return;
        }

        // Crear nueva acción de mejora
        AccionMejora nuevaAccion = new AccionMejora(
                txtNombre.getText(),
                txtObjetivo.getText(),
                cbArea.getValue(),
                txtDescripcion.getText(),
                txtResponsable.getText(),
                dpFechaInicio.getValue(),
                dpFechaFin.getValue(),
                txtRecursos.getText(),
                archivoEvidencia
        );

        // Guardar en la base de datos o lista
        System.out.println("Guardando acción: " + nuevaAccion);

        // Limpiar formulario
        handleLimpiarFormulario();

        mostrarAlerta("Éxito", "Acción de mejora guardada correctamente");
    }

    @FXML
    private void handleLimpiarFormulario() {
        txtNombre.clear();
        txtObjetivo.clear();
        cbArea.getSelectionModel().clearSelection();
        txtDescripcion.clear();
        txtResponsable.clear();
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
        txtRecursos.clear();
        lblArchivoAdjunto.setText("Ningún archivo adjunto");
        archivoEvidencia = null;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static class AccionMejora {
        private final String nombre;
        private final String objetivo;
        private final String area;
        private final String descripcion;
        private final String responsable;
        private final LocalDate fechaInicio;
        private final LocalDate fechaFin;
        private final String recursos;
        private final File evidencia;

        public AccionMejora(String nombre, String objetivo, String area, String descripcion,
                            String responsable, LocalDate fechaInicio, LocalDate fechaFin,
                            String recursos, File evidencia) {
            this.nombre = nombre;
            this.objetivo = objetivo;
            this.area = area;
            this.descripcion = descripcion;
            this.responsable = responsable;
            this.fechaInicio = fechaInicio;
            this.fechaFin = fechaFin;
            this.recursos = recursos;
            this.evidencia = evidencia;
        }

        // Getters
        public String getNombre() { return nombre; }
        public String getObjetivo() { return objetivo; }
        public String getArea() { return area; }
        public String getDescripcion() { return descripcion; }
        public String getResponsable() { return responsable; }
        public LocalDate getFechaInicio() { return fechaInicio; }
        public LocalDate getFechaFin() { return fechaFin; }
        public String getRecursos() { return recursos; }
        public File getEvidencia() { return evidencia; }

        @Override
        public String toString() {
            return "AccionMejora{" + "nombre=" + nombre + ", area=" + area +
                    ", responsable=" + responsable + ", fechaFin=" + fechaFin + '}';
        }
    }
}