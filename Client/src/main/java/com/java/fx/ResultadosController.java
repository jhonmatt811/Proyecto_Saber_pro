package com.java.fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.stream.Collectors;

@Component
public class ResultadosController {

    // Elementos de la tabla
    @FXML private TableView<Resultado> tablaResultados;
    @FXML private TableColumn<Resultado, String> colAno;
    @FXML private TableColumn<Resultado, String> colEstudiante;
    @FXML private TableColumn<Resultado, String> colCohorte;
    @FXML private TableColumn<Resultado, String> colPrograma;
    @FXML private TableColumn<Resultado, String> colArea;
    @FXML private TableColumn<Resultado, Integer> colPuntaje;

    // Filtros
    @FXML private ComboBox<String> filtroAno;
    @FXML private ComboBox<String> filtroEstudiante;
    @FXML private ComboBox<String> filtroCohorte;
    @FXML private ComboBox<String> filtroArea;
    @FXML private ComboBox<String> filtroPrograma;

    // Gráfica
    @FXML private BarChart<String, Number> graficaPuntajes;

    // Etiquetas
    @FXML private Label archivoCargadoLabel;

    private ObservableList<Resultado> datosOriginales;
    private FilteredList<Resultado> datosFiltrados;

    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla
        colAno.setCellValueFactory(new PropertyValueFactory<>("ano"));
        colEstudiante.setCellValueFactory(new PropertyValueFactory<>("estudiante"));
        colCohorte.setCellValueFactory(new PropertyValueFactory<>("cohorte"));
        colPrograma.setCellValueFactory(new PropertyValueFactory<>("programa"));
        colArea.setCellValueFactory(new PropertyValueFactory<>("area"));
        colPuntaje.setCellValueFactory(new PropertyValueFactory<>("puntaje"));

        // Cargar datos de ejemplo
        loadSampleData();

        // Configurar gráfica
        configurarGrafica();

        // Configurar listeners para los filtros
        configurarFiltros();
    }

    private void loadSampleData() {
        datosOriginales = FXCollections.observableArrayList(
                new Resultado("2023", "Juan Pérez", "2020", "Ingeniería Civil", "Comunicación Escrita", 85),
                new Resultado("2023", "Ana Gómez", "2021", "Medicina", "Razonamiento Cuantitativo", 92),
                new Resultado("2023", "Carlos Díaz", "2020", "Derecho", "Inglés", 78),
                new Resultado("2022", "María López", "2022", "Psicología", "Competencias Ciudadanas", 88),
                new Resultado("2022", "Pedro Ramírez", "2021", "Administración", "Comunicación Escrita", 76),
                new Resultado("2023", "Luisa Fernández", "2020", "Ingeniería de Sistemas", "Razonamiento Cuantitativo", 95)
        );

        datosFiltrados = new FilteredList<>(datosOriginales);
        tablaResultados.setItems(datosFiltrados);

        actualizarFiltros();
    }

    private void configurarGrafica() {
        graficaPuntajes.setTitle("Distribución de Puntajes por Área");
        actualizarGrafica();
    }

    private void actualizarGrafica() {
        graficaPuntajes.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        datosFiltrados.stream()
                .collect(Collectors.groupingBy(
                        Resultado::getArea,
                        Collectors.averagingDouble(Resultado::getPuntaje)
                ))
                .forEach((area, promedio) ->
                        series.getData().add(new XYChart.Data<>(area, promedio))
                );

        graficaPuntajes.getData().add(series);
    }

    private void configurarFiltros() {
        filtroAno.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        filtroEstudiante.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        filtroCohorte.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        filtroArea.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        filtroPrograma.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }

    private void actualizarFiltros() {
        filtroAno.setItems(FXCollections.observableArrayList(
                datosOriginales.stream()
                        .map(Resultado::getAno)
                        .distinct()
                        .collect(Collectors.toList())
        ));

        filtroEstudiante.setItems(FXCollections.observableArrayList(
                datosOriginales.stream()
                        .map(Resultado::getEstudiante)
                        .distinct()
                        .collect(Collectors.toList())
        ));

        filtroCohorte.setItems(FXCollections.observableArrayList(
                datosOriginales.stream()
                        .map(Resultado::getCohorte)
                        .distinct()
                        .collect(Collectors.toList())
        ));

        filtroArea.setItems(FXCollections.observableArrayList(
                datosOriginales.stream()
                        .map(Resultado::getArea)
                        .distinct()
                        .collect(Collectors.toList())
        ));

        filtroPrograma.setItems(FXCollections.observableArrayList(
                datosOriginales.stream()
                        .map(Resultado::getPrograma)
                        .distinct()
                        .collect(Collectors.toList())
        ));
    }

    private void aplicarFiltros() {
        datosFiltrados.setPredicate(resultado -> {
            boolean coincideAno = filtroAno.getValue() == null ||
                    filtroAno.getValue().equals(resultado.getAno());
            boolean coincideEstudiante = filtroEstudiante.getValue() == null ||
                    filtroEstudiante.getValue().equals(resultado.getEstudiante());
            boolean coincideCohorte = filtroCohorte.getValue() == null ||
                    filtroCohorte.getValue().equals(resultado.getCohorte());
            boolean coincideArea = filtroArea.getValue() == null ||
                    filtroArea.getValue().equals(resultado.getArea());
            boolean coincidePrograma = filtroPrograma.getValue() == null ||
                    filtroPrograma.getValue().equals(resultado.getPrograma());

            return coincideAno && coincideEstudiante && coincideCohorte &&
                    coincideArea && coincidePrograma;
        });

        actualizarGrafica();
    }

    @FXML
    public void handleCargarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de resultados");

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Archivos de datos (*.xlsx, *.csv, *.json)", "*.xlsx", "*.csv", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            archivoCargadoLabel.setText("Archivo cargado: " + selectedFile.getName());
            // Aquí iría la lógica para cargar y procesar el archivo
        }
    }

    @FXML
    public void handleConectarCFES() {
        System.out.println("Conectando al CFES...");
        // Implementar lógica de conexión al CFES
    }

    @FXML
    public void handleExportarPDF() {
        System.out.println("Exportando a PDF...");
        // Implementar lógica de exportación a PDF
    }

    @FXML
    public void handleExportarExcel() {
        System.out.println("Exportando a Excel...");
        // Implementar lógica de exportación a Excel
    }

    public static class Resultado {
        private final String ano;
        private final String estudiante;
        private final String cohorte;
        private final String programa;
        private final String area;
        private final int puntaje;

        public Resultado(String ano, String estudiante, String cohorte, String programa, String area, int puntaje) {
            this.ano = ano;
            this.estudiante = estudiante;
            this.cohorte = cohorte;
            this.programa = programa;
            this.area = area;
            this.puntaje = puntaje;
        }

        public String getAno() { return ano; }
        public String getEstudiante() { return estudiante; }
        public String getCohorte() { return cohorte; }
        public String getPrograma() { return programa; }
        public String getArea() { return area; }
        public int getPuntaje() { return puntaje; }
    }
}