package com.java.fx.controller;

import com.java.fx.model.Resultado;
import com.java.fx.service.AutenticacionService;
import com.java.fx.service.ResultadoService;
import com.java.fx.service.ResultadoUploader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ResultadosController {
    @FXML private TextField inputYear;
    @FXML private TextField inputCiclo;
    @FXML private TableView<Resultado> tablaResultados;
    @FXML private TableColumn<Resultado, String> colTipoDocumento;
    @FXML private TableColumn<Resultado, String> colDocumento;
    @FXML private TableColumn<Resultado, String> colNombre;
    @FXML private TableColumn<Resultado, String> colNumeroRegistro;
    @FXML private TableColumn<Resultado, String> colTipoEvaluado;
    @FXML private TableColumn<Resultado, String> colSniesPrograma;
    @FXML private TableColumn<Resultado, String> colPrograma;
    @FXML private TableColumn<Resultado, String> colCiudad;
    @FXML private TableColumn<Resultado, String> colGrupoReferencia;
    @FXML private TableColumn<Resultado, String> colPuntajeGlobal;
    @FXML private TableColumn<Resultado, String> colPercentilNacionalGlobal;
    @FXML private TableColumn<Resultado, String> colPercentilNacionalNbc;
    @FXML private TableColumn<Resultado, String> colModulo;
    @FXML private TableColumn<Resultado, String> colPuntajeModulo;
    @FXML private TableColumn<Resultado, String> colNivelDesempeno;
    @FXML private TableColumn<Resultado, String> colPercentilNacionalModulo;
    @FXML private TableColumn<Resultado, String> colPercentilGrupoNbcModulo;
    @FXML private TableColumn<Resultado, String> colNovedades;

    @FXML private TextField inputDocumento;
    @FXML private ComboBox<String> filtroPrograma;
    @FXML private ComboBox<String> filtroModulo;
    @FXML private BarChart<String, Number> graficaPuntajes;
    @FXML private Label archivoCargadoLabel;

    private ObservableList<Resultado> datosOriginales;
    private FilteredList<Resultado> datosFiltrados;
    // Inyectamos los servicios por campo
    @Autowired private ResultadoService resultadoService;
    @Autowired private ResultadoUploader uploader;
    @Autowired private AutenticacionService authService;

    @FXML private Button btnObtenerResultados;

    @Autowired
    private ResultadoService apiService;

    @FXML
    public void handleObtenerResultados() {
        try {
            // Leer filtros (si el campo está vacío, lo pasamos como null)
            Integer year    = inputYear.getText().isBlank()    ? null : Integer.parseInt(inputYear.getText());
            Integer ciclo   = inputCiclo.getText().isBlank()   ? null : Integer.parseInt(inputCiclo.getText());
            Long documento  = inputDocumento.getText().isBlank()? null : Long.parseLong(inputDocumento.getText());
            Integer progId  = null; // si tu ComboBox de programa guardara un ID, haz algo similar

            // Llamada al API
            List<Resultado> lista = apiService.obtenerResultados(year, ciclo, documento, progId);

            // **Aquí imprimimos en consola cuántas filas vinieron**
            System.out.println("Filas recibidas desde la API: " + lista.size());

            // Actualizar la tabla y gráfica
            datosOriginales.setAll(lista);
            datosFiltrados = new FilteredList<>(datosOriginales, r->true);
            tablaResultados.setItems(datosFiltrados);
            actualizarOpcionesFiltros();
            aplicarFiltros();
            actualizarGrafica();

        } catch (NumberFormatException nfe) {
            mostrarAlerta("Filtro inválido", "Año, ciclo o documento no tienen un formato numérico correcto.", Alert.AlertType.ERROR);
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();  // para ver el stack en la consola
            mostrarAlerta("Error al obtener resultados", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
    /*
    @FXML
    public void handleObtenerResultados() {
        try {
            Integer year = inputYear.getText().isBlank()
                    ? null : Integer.parseInt(inputYear.getText());
            Integer ciclo = inputCiclo.getText().isBlank()
                    ? null : Integer.parseInt(inputCiclo.getText());
            Long doc = inputDocumento.getText().isBlank()
                    ? null : Long.parseLong(inputDocumento.getText());
            // Si quisieras filtrar por programaId numérico, podrías parsearlo aquí:
            Integer programaId = null;

            List<Resultado> lista = resultadoService
                    .obtenerResultados(year, ciclo, doc, programaId);

            datosOriginales.setAll(lista);
            datosFiltrados = new FilteredList<>(datosOriginales, r -> true);
            tablaResultados.setItems(datosFiltrados);

            actualizarOpcionesFiltros();
            aplicarFiltros();
            actualizarGrafica();

        } catch (NumberFormatException nfe) {
            mostrarAlerta("Error", "Año, ciclo o documento inválido.", Alert.AlertType.ERROR);
        } catch (Exception ex) {
            mostrarAlerta("Error al obtener resultados", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
    */
    public ResultadosController() {
        // constructor vacío para JavaFX
    }

    public ResultadosController(ResultadoService resultadoService,
                                ResultadoUploader uploader,
                                AutenticacionService authService) {
        this.resultadoService = resultadoService;
        this.uploader = uploader;
        this.authService = authService;
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatosEjemplo();
        configurarGrafica();
        configurarFiltros();
        filtroPrograma.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? filtroPrograma.getPromptText() : item);
            }
        });
        filtroModulo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? filtroModulo.getPromptText() : item);
            }
        });
        inputDocumento.textProperty().addListener((obs, o, n) -> aplicarFiltros());
    }

    private void configurarColumnas() {
        colTipoDocumento.setCellValueFactory(new PropertyValueFactory<>("tipoDocumento"));
        colDocumento.setCellValueFactory(new PropertyValueFactory<>("documento"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNumeroRegistro.setCellValueFactory(new PropertyValueFactory<>("numeroRegistro"));
        colTipoEvaluado.setCellValueFactory(new PropertyValueFactory<>("tipoEvaluado"));
        colSniesPrograma.setCellValueFactory(new PropertyValueFactory<>("sniesProgramaAcademico"));
        colPrograma.setCellValueFactory(new PropertyValueFactory<>("programa"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        colGrupoReferencia.setCellValueFactory(new PropertyValueFactory<>("nucleoBasicoConocimiento"));
        colPuntajeGlobal.setCellValueFactory(new PropertyValueFactory<>("puntajeGlobal"));
        colPercentilNacionalGlobal.setCellValueFactory(new PropertyValueFactory<>("percentilNacionalGlobal"));
        colPercentilNacionalNbc.setCellValueFactory(new PropertyValueFactory<>("percentilNacionalNbc"));
        colModulo.setCellValueFactory(new PropertyValueFactory<>("modulo"));
        colPuntajeModulo.setCellValueFactory(new PropertyValueFactory<>("puntajeModulo"));
        colNivelDesempeno.setCellValueFactory(new PropertyValueFactory<>("nivelDesempeno"));
        colPercentilNacionalModulo.setCellValueFactory(new PropertyValueFactory<>("percentilNacionalModulo"));
        colPercentilGrupoNbcModulo.setCellValueFactory(new PropertyValueFactory<>("percentilGrupoNbcModulo"));
        colNovedades.setCellValueFactory(new PropertyValueFactory<>("novedades"));
    }

    private void cargarDatosEjemplo() {
        datosOriginales = FXCollections.observableArrayList(
                new Resultado(1, 2023, "CC", 123456, "Juan Pérez", "R001", "Estudiante", "1010", "Ingeniería", "Bogotá", "G1",
                        "88", "90", "85", "Matemáticas", "92", "Alto", "89", "88", "Ninguna"),
                new Resultado(1, 2023, "TI", 987654, "Ana Gómez", "R002", "Estudiante", "1020", "Medicina", "Medellín", "G2",
                        "75", "70", "72", "Lectura crítica", "78", "Medio", "73", "71", "Aplazada")
        );
        datosFiltrados = new FilteredList<>(datosOriginales, r -> true);
        tablaResultados.setItems(datosFiltrados);
        actualizarOpcionesFiltros();
    }

    private void configurarGrafica() {
        graficaPuntajes.setTitle("Distribución de Puntajes por Módulo");
        actualizarGrafica();
    }

    private void actualizarGrafica() {
        graficaPuntajes.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        datosFiltrados.stream()
                .filter(r -> r.getPuntajeModulo()!=null && r.getPuntajeModulo().matches("\\d+(\\.\\d+)?"))
                .collect(Collectors.groupingBy(
                        Resultado::getModulo,
                        Collectors.averagingDouble(r -> Double.parseDouble(r.getPuntajeModulo()))
                ))
                .forEach((mod, avg) -> series.getData().add(new XYChart.Data<>(mod, avg)));
        graficaPuntajes.getData().add(series);
    }

    private void configurarFiltros() {
        filtroPrograma.valueProperty().addListener((o, ov, nv) -> aplicarFiltros());
        filtroModulo.valueProperty().addListener((o, ov, nv) -> aplicarFiltros());
    }

    private void actualizarOpcionesFiltros() {
        filtroPrograma.setItems(FXCollections.observableArrayList(
                datosOriginales.stream().map(Resultado::getPrograma).distinct().toList()));
        filtroModulo.setItems(FXCollections.observableArrayList(
                datosOriginales.stream().map(Resultado::getModulo).distinct().toList()));
    }

    private void aplicarFiltros() {
        String docF = inputDocumento.getText();
        String progF = filtroPrograma.getValue();
        String modF = filtroModulo.getValue();
        datosFiltrados.setPredicate(r ->
                (docF==null||docF.isBlank()||String.valueOf(r.getDocumento()).contains(docF))
                        &&(progF==null||progF.equals(r.getPrograma()))
                        &&(modF==null||modF.equals(r.getModulo()))
        );
        actualizarGrafica();
    }

    @FXML
    public void handleCargarArchivo() {
        int year, cycle;
        try {
            year = Integer.parseInt(inputYear.getText());
            cycle = Integer.parseInt(inputCiclo.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Año o ciclo inválido.", Alert.AlertType.ERROR);
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar archivo de resultados");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de datos", "*.xlsx", "*.csv", "*.json"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        File archivo = fc.showOpenDialog(null);
        if (archivo != null) {
            archivoCargadoLabel.setText("Archivo: "+archivo.getName()+" | Año: "+year+" | Ciclo: "+cycle);
            try {
                // Cargar datos y actualizar UI
                List<Resultado> nuevos = resultadoService.cargarDatosDesdeArchivo(archivo, cycle, year);
                datosOriginales.setAll(nuevos);
                datosFiltrados = new FilteredList<>(datosOriginales, r->true);
                tablaResultados.setItems(datosFiltrados);
                actualizarOpcionesFiltros();
                aplicarFiltros();
                // Enviar al backend
                uploader.subirArchivo(archivo, cycle, year);
            } catch (IOException ex) {
                mostrarAlerta("Error al procesar archivo", ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML public void handleRestablecerFiltros() {
        inputDocumento.clear();
        filtroPrograma.setValue(null);
        filtroModulo.setValue(null);
        datosFiltrados.setPredicate(r->true);
        actualizarGrafica();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    public void handleConectarCFES() {
        // Lógica para conectar al ICFES
        System.out.println("Conectando al ICFES...");
    }

    @FXML
    public void handleExportarPDF() {
        // Lógica para exportar a PDF
        System.out.println("Exportando a PDF...");
        // Implementar exportación real aquí
    }

    @FXML
    public void handleExportarExcel() {
        // Lógica para exportar a Excel
        System.out.println("Exportando a Excel...");
        // Implementar exportación real aquí
    }
}
