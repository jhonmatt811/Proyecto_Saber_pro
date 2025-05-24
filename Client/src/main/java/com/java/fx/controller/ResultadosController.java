package com.java.fx.controller;

import com.java.fx.model.Resultado;
import com.java.fx.service.AutenticacionService;
import com.java.fx.service.ResultadoService;
import com.java.fx.service.ResultadoUploader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
    @FXML private Label archivoCargadoLabel;

    private ObservableList<Resultado> datosOriginales;
    private FilteredList<Resultado> datosFiltrados;
    // Inyectamos los servicios por campo
    @Autowired private ResultadoService resultadoService;
    @Autowired private ResultadoUploader uploader;

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
            Integer progId  = null;

            // Llamada al API
            List<Resultado> lista = apiService.obtenerResultados(year, ciclo, documento, progId);

            // Aquí imprimimos en consola cuántas filas vinieron
            System.out.println("Filas recibidas desde la API: " + lista.size());

            // Actualizar la tabla y gráfica
            datosOriginales.setAll(lista);
            datosFiltrados = new FilteredList<>(datosOriginales, r->true);
            tablaResultados.setItems(datosFiltrados);
            actualizarOpcionesFiltros();
            aplicarFiltros();
            Platform.runLater(() -> {
                actualizarOpcionesFiltros();
                aplicarFiltros();
            });

        } catch (NumberFormatException nfe) {
            mostrarAlerta("Filtro inválido", "Año, ciclo o documento no tienen un formato numérico correcto.", Alert.AlertType.ERROR);
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();  // para ver el stack en la consola
            mostrarAlerta("Error al obtener resultados", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public ResultadosController() {
        // constructor vacío para JavaFX
    }

    public ResultadosController(ResultadoService resultadoService,
                                ResultadoUploader uploader) {
        this.resultadoService = resultadoService;
        this.uploader = uploader;
    }

    @FXML
    public void initialize() {
        configurarColumnas();

        // Inicializar listas vacías
        datosOriginales = FXCollections.observableArrayList();
        datosFiltrados = new FilteredList<>(datosOriginales, r -> true);
        tablaResultados.setItems(datosFiltrados);

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
        Platform.runLater(() -> {
            System.out.println("Filtros aplicados. Gráfica actualizada."); // Debug
        });
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
                new FileChooser.ExtensionFilter("Todos los archivos", "*")
        );
        File archivo = fc.showOpenDialog(null);
        if (archivo != null) {
            archivoCargadoLabel.setText("Archivo: "+archivo.getName()+" | Año: "+year+" | Ciclo: "+cycle+" | Cargado Correctamente ");
            try {
                // Cargar datos y actualizar UI
                List<Resultado> nuevos = resultadoService.cargarDatosDesdeArchivo(archivo, cycle, year);
                datosOriginales.setAll(nuevos);
                datosFiltrados = new FilteredList<>(datosOriginales, r->true);
                tablaResultados.setItems(datosFiltrados);
                actualizarOpcionesFiltros();
                aplicarFiltros();
                Platform.runLater(() -> {
                    actualizarOpcionesFiltros();
                    aplicarFiltros();
                });
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
    @FXML
    private void handleMostrarGrafica() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GraficaModulos.fxml"));
            Parent root = loader.load();

            // Pasar los datos filtrados a la nueva ventana
            GraficaModulosController controller = loader.getController();
            controller.inicializarDatos(datosFiltrados);

            Stage stage = new Stage();
            stage.setTitle("Gráfica Comparativa");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la gráfica.", Alert.AlertType.ERROR);
        }
    }
}