package com.java.fx.controller;

import com.java.fx.Usuarios_y_Roles.PermisosRoles;
import com.java.fx.Usuarios_y_Roles.Sesion;
import com.java.fx.controller.GraficaModulosController;
import com.java.fx.controller.LineaController;
import com.java.fx.model.Resultado;
import com.java.fx.service.ResultadoService;
import com.java.fx.service.ResultadoUploader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

// Apache POI
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;

import javax.swing.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ResultadosController {

    @FXML
    private TextField inputYear;
    @FXML
    private TextField inputCiclo;
    @FXML
    private TableView<Resultado> tablaResultados;
    @FXML
    private TableColumn<Resultado, String> colTipoDocumento;
    @FXML
    private TableColumn<Resultado, String> colDocumento;
    @FXML
    private TableColumn<Resultado, String> colNombre;
    @FXML
    private TableColumn<Resultado, String> colNumeroRegistro;
    @FXML
    private TableColumn<Resultado, String> colTipoEvaluado;
    @FXML
    private TableColumn<Resultado, String> colSniesPrograma;
    @FXML
    private TableColumn<Resultado, String> colPrograma;
    @FXML
    private TableColumn<Resultado, String> colCiudad;
    @FXML
    private TableColumn<Resultado, String> colGrupoReferencia;
    @FXML
    private TableColumn<Resultado, String> colPuntajeGlobal;
    @FXML
    private TableColumn<Resultado, String> colPercentilNacionalGlobal;
    @FXML
    private TableColumn<Resultado, String> colPercentilNacionalNbc;
    @FXML
    private TableColumn<Resultado, String> colModulo;
    @FXML
    private TableColumn<Resultado, String> colPuntajeModulo;
    @FXML
    private TableColumn<Resultado, String> colNivelDesempeno;
    @FXML
    private TableColumn<Resultado, String> colPercentilNacionalModulo;
    @FXML
    private TableColumn<Resultado, String> colPercentilGrupoNbcModulo;
    @FXML
    private TableColumn<Resultado, String> colNovedades;

    @FXML
    private TextField inputDocumento;
    @FXML
    private ComboBox<String> filtroPrograma;
    @FXML
    private ComboBox<String> filtroModulo;
    @FXML
    private Label archivoCargadoLabel;

    private ObservableList<Resultado> datosOriginales;
    private FilteredList<Resultado> datosFiltrados;
    // Inyectamos los servicios por campo
    @Autowired
    private ResultadoService resultadoService;
    @Autowired
    private ResultadoUploader uploader;

    @FXML
    private Button btnObtenerResultados;
    @FXML
    private Button btnCargarArchivo;
    @FXML
    private Button btnObtenerExportarExcel;
    @FXML
    private Button btnCompararconGrupo;


    @Autowired
    private ResultadoService apiService;

    @FXML
    public void handleObtenerResultados() {
        try {
            // Leer filtros (si el campo está vacío, lo pasamos como null)
            Integer year = inputYear.getText().isBlank() ? null : Integer.parseInt(inputYear.getText());
            Integer ciclo = inputCiclo.getText().isBlank() ? null : Integer.parseInt(inputCiclo.getText());
            Long documento = inputDocumento.getText().isBlank() ? null : Long.parseLong(inputDocumento.getText());
            Integer progId = null;

            // Llamada al API
            List<Resultado> lista = apiService.obtenerResultados(year, ciclo, documento, progId);

            // Aquí imprimimos en consola cuántas filas vinieron
            System.out.println("Filas recibidas desde la API: " + lista.size());

            // Actualizar la tabla y gráfica
            datosOriginales.setAll(lista);
            datosFiltrados = new FilteredList<>(datosOriginales, r -> true);
            tablaResultados.setItems(datosFiltrados);
            actualizarOpcionesFiltros();
            aplicarFiltros();
            Platform.runLater(() -> {
                actualizarOpcionesFiltros();
                aplicarFiltros();
            });


        } catch (NumberFormatException nfe) {
            mostrarAlerta("Filtro inválido", "Formato numérico incorrecto", Alert.AlertType.ERROR);
        } catch (IOException | InterruptedException ex) {  // ¡Aquí se añade InterruptedException!
            ex.printStackTrace();
            mostrarAlerta("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        PermisosRoles permisos = new PermisosRoles(Sesion.getRol_id());
        btnCargarArchivo.setVisible(permisos.tienePermiso("cargarArchivo"));
        btnObtenerExportarExcel.setVisible(permisos.tienePermiso("cargarArchivo"));

        // Inicializar listas vacías
        datosOriginales = FXCollections.observableArrayList();
        datosFiltrados = new FilteredList<>(datosOriginales, r -> true);
        tablaResultados.setItems(datosFiltrados);

        configurarFiltros();
        filtroPrograma.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? filtroPrograma.getPromptText() : item);
            }
        });
        filtroModulo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
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
                (docF == null || docF.isBlank() || String.valueOf(r.getDocumento()).contains(docF))
                        && (progF == null || progF.equals(r.getPrograma()))
                        && (modF == null || modF.equals(r.getModulo()))
        );
        Platform.runLater(() -> {
            System.out.println("Filtros aplicados. Gráfica actualizada."); // Debug
        });
    }

    @FXML
    public void handleCargarArchivo() {
        PermisosRoles permisos = new PermisosRoles(Sesion.getRol_id());
        if (!permisos.tienePermiso("cargarArchivo")) {
            mostrarAlerta("Acceso denegado", "No tienes permisos para cargar archivos.", Alert.AlertType.ERROR);
            return;
        }

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
                new FileChooser.ExtensionFilter("Archivos de datos", "*.xlsx", "*.csv"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*")
        );

        File archivo = fc.showOpenDialog(tablaResultados.getScene().getWindow());
        if (archivo != null) {
            // Crear diálogo de carga modal
            Stage loadingDialog = new Stage();
            ProgressIndicator progress = new ProgressIndicator();
            Scene scene = new Scene(progress, 200, 200);

            loadingDialog.initModality(Modality.APPLICATION_MODAL);
            loadingDialog.initOwner(tablaResultados.getScene().getWindow());
            loadingDialog.setTitle("Cargando archivo...");
            loadingDialog.setScene(scene);
            loadingDialog.show();

            // Crear Task para la carga en segundo plano
            Task<List<Resultado>> cargaTask = new Task<>() {
                @Override
                protected List<Resultado> call() throws Exception {
                    try {
                        return resultadoService.cargarDatosDesdeArchivo(archivo, cycle, year);
                    } catch (IOException ex) {
                        throw new Exception("Error al leer el archivo: " + ex.getMessage(), ex);
                    }
                }
            };

            // Manejar finalización
            cargaTask.setOnSucceeded(e -> {
                loadingDialog.close();
                try {
                    List<Resultado> nuevos = cargaTask.get();
                    archivoCargadoLabel.setText("Archivo: " + archivo.getName() + " | Año: " + year + " | Ciclo: " + cycle + " | Cargado Correctamente ");

                    // Actualizar UI
                    Platform.runLater(() -> {
                        datosOriginales.setAll(nuevos);
                        datosFiltrados = new FilteredList<>(datosOriginales, r -> true);
                        tablaResultados.setItems(datosFiltrados);
                        actualizarOpcionesFiltros();
                        aplicarFiltros();
                    });

                    // Enviar al backend (manejar posibles IOException aquí)
                    try {
                        uploader.subirArchivo(archivo, cycle, year);
                    } catch (IOException ex) {
                        mostrarAlerta("Error al subir archivo", ex.getMessage(), Alert.AlertType.ERROR);
                    }

                } catch (InterruptedException | ExecutionException ex) {
                    mostrarAlerta("Error", "Error al obtener resultados: " + ex.getCause().getMessage(), Alert.AlertType.ERROR);
                }
            });

            cargaTask.setOnFailed(e -> {
                loadingDialog.close();
                Throwable error = cargaTask.getException().getCause();
                mostrarAlerta("Error al procesar archivo", error.getMessage(), Alert.AlertType.ERROR);
            });

            // Ejecutar en hilo separado
            new Thread(cargaTask).start();
        }
    }


    @FXML
    public void handleRestablecerFiltros() {
        inputDocumento.clear();
        filtroPrograma.setValue(null);
        filtroModulo.setValue(null);
        datosFiltrados.setPredicate(r -> true);
    }

    @FXML
    public void handleConectarCFES() {
        try {
            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resultados-icfes.fxml"));
            Parent root = fxmlLoader.load();

            // Crear una nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Resultados ICFES");
            stage.setScene(new Scene(root));
            Image icon = new Image(getClass().getResourceAsStream("/img/images.png"));
            stage.getIcons().add(icon);
            stage.show();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la ventana de resultados ICFES", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // ComboBox para área/grupo

    @FXML
    private void handleMostrarComparacion() {
        try {
            // Validar que haya un ID ingresado
            String idEstudiante = inputDocumento.getText().trim();
            if (idEstudiante.isEmpty()) {
                mostrarAlerta("Error", "Debe ingresar un documento de estudiante", Alert.AlertType.ERROR);
                return;
            }

            // Cargar la vista de comparación
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/comparar-view.fxml"));
            Parent root = loader.load();

            // Obtener el controlador y pasar parámetros
            ChartController chartController = loader.getController();
            chartController.configurarParametros(
                    idEstudiante,
                    inputYear.getText(),
                    filtroPrograma.getValue(),  // Programa seleccionado
                    filtroModulo.getValue()     // Grupo/Núcleo seleccionado
            );

            // Mostrar en nueva ventana
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Comparación con Grupo");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace(); // Debug en consola
            mostrarAlerta("Error",
                    "Error al cargar: " + e.getMessage(), // Mensaje específico
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleExportarExcel() {
        PermisosRoles permisos = new PermisosRoles(Sesion.getRol_id());
        if (!permisos.tienePermiso("cargarArchivo")) {
            mostrarAlerta("Acceso denegado", "No tienes permisos para cargar archivos.", Alert.AlertType.ERROR);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar como Excel");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                XSSFSheet sheet = workbook.createSheet("Resultados Saber Pro");

                // Crear encabezados
                String[] headers = {
                        "Tipo Documento", "Documento", "Nombre", "Número Registro",
                        "Tipo Evaluado", "SNIES Programa", "Programa", "Ciudad",
                        "Núcleo Básico", "Puntaje Global", "% Nal. Global", "% Nal. NBC",
                        "Módulo", "Puntaje Módulo", "Nivel Desempeño", "% Nal. Módulo",
                        "% Grupo NBC Módulo", "Novedades"
                };

                // Escribir encabezados
                XSSFRow headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                }

                // Escribir datos
                int rowNum = 1;
                for (Resultado resultado : datosFiltrados) {
                    XSSFRow row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultado.getTipoDocumento());
                    row.createCell(1).setCellValue(resultado.getDocumento());
                    row.createCell(2).setCellValue(resultado.getNombre());
                    row.createCell(3).setCellValue(resultado.getNumeroRegistro());
                    row.createCell(4).setCellValue(resultado.getTipoEvaluado());
                    row.createCell(5).setCellValue(resultado.getSniesProgramaAcademico());
                    row.createCell(6).setCellValue(resultado.getPrograma());
                    row.createCell(7).setCellValue(resultado.getCiudad());
                    row.createCell(8).setCellValue(resultado.getNucleoBasicoConocimiento());
                    row.createCell(9).setCellValue(resultado.getPuntajeGlobal());
                    row.createCell(10).setCellValue(resultado.getPercentilNacionalGlobal());
                    row.createCell(11).setCellValue(resultado.getPercentilNacionalNbc());
                    row.createCell(12).setCellValue(resultado.getModulo());
                    row.createCell(13).setCellValue(resultado.getPuntajeModulo());
                    row.createCell(14).setCellValue(resultado.getNivelDesempeno());
                    row.createCell(15).setCellValue(resultado.getPercentilNacionalModulo());
                    row.createCell(16).setCellValue(resultado.getPercentilGrupoNbcModulo());
                    row.createCell(17).setCellValue(resultado.getNovedades());
                }

                // Autoajustar columnas
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Guardar archivo
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    workbook.write(outputStream);
                    mostrarAlerta("Éxito", "Archivo exportado correctamente.", Alert.AlertType.INFORMATION);
                }
            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo exportar el archivo.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
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

    // Método para abrir gráfica de líneas
    @FXML
    private void handleMostrarTendencias() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LineChartView.fxml"));
            Parent root = loader.load();

            LineaController controller = loader.getController();
            controller.inicializarDatos(datosFiltrados);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Tendencias por Ciclo");
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la gráfica de tendencias.", Alert.AlertType.ERROR);
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
    public void handleExportarFormato() {
        PermisosRoles permisos = new PermisosRoles(Sesion.getRol_id());
        if (!permisos.tienePermiso("cargarArchivo")) {
            mostrarAlerta("Acceso denegado", "No tienes permisos para esta acción.", Alert.AlertType.ERROR);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar formato vacío");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                XSSFSheet sheet = workbook.createSheet("Formato Saber Pro");

                // Encabezados (mismos que en handleExportarExcel)
                String[] headers = {
                        "Tipo Documento", "Documento", "Nombre", "Número Registro",
                        "Tipo Evaluado", "SNIES Programa", "Programa", "Ciudad",
                        "Núcleo Básico", "Puntaje Global", "% Nal. Global", "% Nal. NBC",
                        "Módulo", "Puntaje Módulo", "Nivel Desempeño", "% Nal. Módulo",
                        "% Grupo NBC Módulo", "Novedades"
                };

                // Solo escribimos los encabezados
                XSSFRow headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                }

                // Autoajustar columnas
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Guardar archivo
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    workbook.write(outputStream);
                    mostrarAlerta("Éxito", "Formato descargado correctamente.", Alert.AlertType.INFORMATION);
                }
            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo generar el formato.", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }
}

