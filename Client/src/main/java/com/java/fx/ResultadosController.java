package com.java.fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


import java.io.File;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ResultadosController {

    // Tabla
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

    // Filtros
    @FXML private ComboBox<String> filtroTipoDocumento;
    @FXML private ComboBox<String> filtroPrograma;
    @FXML private ComboBox<String> filtroCiudad;
    @FXML private ComboBox<String> filtroModulo;
    @FXML private ComboBox<String> filtroNivelDesempeno;

    // Gráfica
    @FXML private BarChart<String, Number> graficaPuntajes;

    @FXML private Label archivoCargadoLabel;

    private ObservableList<Resultado> datosOriginales;
    private FilteredList<Resultado> datosFiltrados;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatosEjemplo();
        configurarGrafica();
        configurarFiltros();
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
        colGrupoReferencia.setCellValueFactory(new PropertyValueFactory<>("grupoReferencia"));
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
                new Resultado("CC", "123456", "Juan Pérez", "R001", "Estudiante", "1010", "Ingeniería", "Bogotá", "G1",
                        "88", "90", "85", "Matemáticas", "92", "Alto", "89", "88", "Ninguna"),
                new Resultado("TI", "987654", "Ana Gómez", "R002", "Estudiante", "1020", "Medicina", "Medellín", "G2",
                        "75", "70", "72", "Lectura crítica", "78", "Medio", "73", "71", "Aplazada")
        );

        datosFiltrados = new FilteredList<>(datosOriginales);
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
                .collect(Collectors.groupingBy(
                        Resultado::getModulo,
                        Collectors.averagingDouble(r -> Double.parseDouble(r.getPuntajeModulo()))
                ))
                .forEach((modulo, promedio) ->
                        series.getData().add(new XYChart.Data<>(modulo, promedio))
                );

        graficaPuntajes.getData().add(series);
    }

    private void configurarFiltros() {
        filtroTipoDocumento.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        filtroPrograma.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        filtroCiudad.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        filtroModulo.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        filtroNivelDesempeno.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }

    private void actualizarOpcionesFiltros() {
        filtroTipoDocumento.setItems(getOpcionesUnicas(Resultado::getTipoDocumento));
        filtroPrograma.setItems(getOpcionesUnicas(Resultado::getPrograma));
        filtroCiudad.setItems(getOpcionesUnicas(Resultado::getCiudad));
        filtroModulo.setItems(getOpcionesUnicas(Resultado::getModulo));
        filtroNivelDesempeno.setItems(getOpcionesUnicas(Resultado::getNivelDesempeno));
    }

    private ObservableList<String> getOpcionesUnicas(java.util.function.Function<Resultado, String> mapper) {
        return FXCollections.observableArrayList(
                datosOriginales.stream().map(mapper).distinct().collect(Collectors.toList())
        );
    }

    private void aplicarFiltros() {
        datosFiltrados.setPredicate(r ->
                (filtroTipoDocumento.getValue() == null || filtroTipoDocumento.getValue().equals(r.getTipoDocumento())) &&
                        (filtroPrograma.getValue() == null || filtroPrograma.getValue().equals(r.getPrograma())) &&
                        (filtroCiudad.getValue() == null || filtroCiudad.getValue().equals(r.getCiudad())) &&
                        (filtroModulo.getValue() == null || filtroModulo.getValue().equals(r.getModulo())) &&
                        (filtroNivelDesempeno.getValue() == null || filtroNivelDesempeno.getValue().equals(r.getNivelDesempeno()))
        );
        actualizarGrafica();
    }

    @FXML
    public void handleCargarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de resultados");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de datos (.xlsx, *.csv, *.json)", ".xlsx", ".csv", ".json"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            archivoCargadoLabel.setText("Archivo cargado: " + selectedFile.getName());
            cargarDatosDesdeArchivo(selectedFile);
        }
    }

    private void cargarDatosDesdeArchivo(File archivo) {
        ObservableList<Resultado> nuevosDatos = FXCollections.observableArrayList();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false; // saltar encabezado
                    continue;
                }

                String[] campos = linea.split(","); // separador CSV
                if (campos.length >= 18) {
                    Resultado r = new Resultado(
                            campos[0], campos[1], campos[2], campos[3], campos[4],
                            campos[5], campos[6], campos[7], campos[8], campos[9],
                            campos[10], campos[11], campos[12], campos[13], campos[14],
                            campos[15], campos[16], campos[17]
                    );
                    nuevosDatos.add(r);
                }
            }

            datosOriginales.setAll(nuevosDatos); // Actualizar datos
            datosFiltrados = new FilteredList<>(datosOriginales);
            tablaResultados.setItems(datosFiltrados);
            actualizarOpcionesFiltros();
            aplicarFiltros();
            actualizarGrafica();

        } catch (IOException e) {
            e.printStackTrace();
            archivoCargadoLabel.setText("Error al leer el archivo");
        }
    }


    @FXML public void handleConectarCFES() {
        System.out.println("Conectando al CFES...");
    }

    @FXML public void handleExportarPDF() {
        System.out.println("Exportando a PDF...");
    }

    @FXML public void handleExportarExcel() {
        System.out.println("Exportando a Excel...");
    }

    public static class Resultado {
        private final String tipoDocumento, documento, nombre, numeroRegistro, tipoEvaluado,
                sniesProgramaAcademico, programa, ciudad, grupoReferencia,
                puntajeGlobal, percentilNacionalGlobal, percentilNacionalNbc,
                modulo, puntajeModulo, nivelDesempeno,
                percentilNacionalModulo, percentilGrupoNbcModulo, novedades;

        public Resultado(String tipoDocumento, String documento, String nombre, String numeroRegistro,
                         String tipoEvaluado, String sniesProgramaAcademico, String programa, String ciudad,
                         String grupoReferencia, String puntajeGlobal, String percentilNacionalGlobal,
                         String percentilNacionalNbc, String modulo, String puntajeModulo,
                         String nivelDesempeno, String percentilNacionalModulo, String percentilGrupoNbcModulo,
                         String novedades) {
            this.tipoDocumento = tipoDocumento;
            this.documento = documento;
            this.nombre = nombre;
            this.numeroRegistro = numeroRegistro;
            this.tipoEvaluado = tipoEvaluado;
            this.sniesProgramaAcademico = sniesProgramaAcademico;
            this.programa = programa;
            this.ciudad = ciudad;
            this.grupoReferencia = grupoReferencia;
            this.puntajeGlobal = puntajeGlobal;
            this.percentilNacionalGlobal = percentilNacionalGlobal;
            this.percentilNacionalNbc = percentilNacionalNbc;
            this.modulo = modulo;
            this.puntajeModulo = puntajeModulo;
            this.nivelDesempeno = nivelDesempeno;
            this.percentilNacionalModulo = percentilNacionalModulo;
            this.percentilGrupoNbcModulo = percentilGrupoNbcModulo;
            this.novedades = novedades;
        }

        public String getTipoDocumento() { return tipoDocumento; }
        public String getDocumento() { return documento; }
        public String getNombre() { return nombre; }
        public String getNumeroRegistro() { return numeroRegistro; }
        public String getTipoEvaluado() { return tipoEvaluado; }
        public String getSniesProgramaAcademico() { return sniesProgramaAcademico; }
        public String getPrograma() { return programa; }
        public String getCiudad() { return ciudad; }
        public String getGrupoReferencia() { return grupoReferencia; }
        public String getPuntajeGlobal() { return puntajeGlobal; }
        public String getPercentilNacionalGlobal() { return percentilNacionalGlobal; }
        public String getPercentilNacionalNbc() { return percentilNacionalNbc; }
        public String getModulo() { return modulo; }
        public String getPuntajeModulo() { return puntajeModulo; }
        public String getNivelDesempeno() { return nivelDesempeno; }
        public String getPercentilNacionalModulo() { return percentilNacionalModulo; }
        public String getPercentilGrupoNbcModulo() { return percentilGrupoNbcModulo; }
        public String getNovedades() { return novedades; }
    }

    private String convertirAJson(Resultado resultado) {
        return String.format(
                "{" +
                        "\"tipoDocumento\": \"%s\", \"documento\": \"%s\", \"nombre\": \"%s\", " +
                        "\"numeroRegistro\": \"%s\", \"tipoEvaluado\": \"%s\", \"sniesProgramaAcademico\": \"%s\", " +
                        "\"programa\": \"%s\", \"ciudad\": \"%s\", \"grupoReferencia\": \"%s\", " +
                        "\"puntajeGlobal\": \"%s\", \"percentilNacionalGlobal\": \"%s\", \"percentilNacionalNbc\": \"%s\", " +
                        "\"modulo\": \"%s\", \"puntajeModulo\": \"%s\", \"nivelDesempeno\": \"%s\", " +
                        "\"percentilNacionalModulo\": \"%s\", \"percentilGrupoNbcModulo\": \"%s\", \"novedades\": \"%s\"" +
                        "}",
                resultado.getTipoDocumento(), resultado.getDocumento(), resultado.getNombre(),
                resultado.getNumeroRegistro(), resultado.getTipoEvaluado(), resultado.getSniesProgramaAcademico(),
                resultado.getPrograma(), resultado.getCiudad(), resultado.getGrupoReferencia(),
                resultado.getPuntajeGlobal(), resultado.getPercentilNacionalGlobal(), resultado.getPercentilNacionalNbc(),
                resultado.getModulo(), resultado.getPuntajeModulo(), resultado.getNivelDesempeno(),
                resultado.getPercentilNacionalModulo(), resultado.getPercentilGrupoNbcModulo(), resultado.getNovedades()
        );
    }

}