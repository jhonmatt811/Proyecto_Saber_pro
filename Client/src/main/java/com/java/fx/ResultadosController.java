package com.java.fx;

//controlador de los resultados saber pro
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;
import javafx.scene.control.Label;


import java.io.File;

@Component
public class ResultadosController {

    @FXML
    private TableView<Resultado> tablaResultados;

    @FXML
    private TableColumn<Resultado, String> colEstudiante;

    @FXML
    private TableColumn<Resultado, String> colCohorte;

    @FXML
    private TableColumn<Resultado, String> colPrograma;

    @FXML
    private TableColumn<Resultado, Integer> colPuntaje;

    @FXML
    private ComboBox<String> filtroAno;

    @FXML
    private ComboBox<String> filtroPrograma;

    @FXML
    private Button btnSubirArchivo;

    @FXML
    public void initialize() {
        // Enlazar las columnas con las propiedades de Resultado
        colEstudiante.setCellValueFactory(new PropertyValueFactory<>("estudiante"));
        colCohorte.setCellValueFactory(new PropertyValueFactory<>("cohorte"));
        colPrograma.setCellValueFactory(new PropertyValueFactory<>("programa"));
        colPuntaje.setCellValueFactory(new PropertyValueFactory<>("puntaje"));

        // Cargar datos de ejemplo
        loadSampleData();
    }

    // Método para cargar datos de ejemplo en la tabla
    private void loadSampleData() {
        tablaResultados.getItems().add(new Resultado("Juan Pérez", "2020", "Ingeniería", 85));
        tablaResultados.getItems().add(new Resultado("Ana Gómez", "2021", "Arquitectura", 92));
        tablaResultados.getItems().add(new Resultado("Carlos Díaz", "2020", "Medicina", 78));
    }

    @FXML
    private Label archivoCargadoLabel;

    // Método para manejar la carga de archivo
    @FXML
    public void handleSubirArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo Excel");

        // Filtro para que solo se vean archivos .xlsx y .xls
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivos Excel (*.xlsx, *.xls)", "*.xlsx", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter);

        // Abre el explorador de archivos
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            // Aquí puedes hacer lo que quieras con el archivo
            archivoCargadoLabel.setText( "  \uD83D\uDCE4 Archivo cargado: " + selectedFile.getName());
            System.out.println("Archivo seleccionado: " + selectedFile.getAbsolutePath());

            // Por ejemplo: cargarlo, leerlo, etc.
        } else {
            archivoCargadoLabel.setText("No se seleccionó ningún archivo.");
            System.out.println("No se seleccionó ningún archivo.");
        }
    }

    // Clase interna para representar los resultados
    public static class Resultado {
        private String estudiante;
        private String cohorte;
        private String programa;
        private int puntaje;

        public Resultado(String estudiante, String cohorte, String programa, int puntaje) {
            this.estudiante = estudiante;
            this.cohorte = cohorte;
            this.programa = programa;
            this.puntaje = puntaje;
        }

        public String getEstudiante() {
            return estudiante;
        }

        public String getCohorte() {
            return cohorte;
        }

        public String getPrograma() {
            return programa;
        }

        public int getPuntaje() {
            return puntaje;
        }
    }
}
