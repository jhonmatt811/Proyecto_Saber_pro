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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

//pasar datos a la db
import java.net.HttpURLConnection;
import java.net.URL;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ResultadosController {



    // Campos de entrada para año y ciclo
    @FXML private TextField inputYear;
    @FXML private TextField inputCiclo;

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
    @FXML private TextField inputDocumento;
    @FXML private ComboBox<String> filtroPrograma;
    @FXML private ComboBox<String> filtroModulo;


    // Gráfica
    @FXML private BarChart<String, Number> graficaPuntajes;

    @FXML private Label archivoCargadoLabel;

    private String authToken;

    public String getAuthToken() {
        return authToken;}

    private int cycle;
    private int year;

    private ObservableList<Resultado> datosOriginales;
    private FilteredList<Resultado> datosFiltrados;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatosEjemplo();
        configurarGrafica();
        configurarFiltros();
        filtroPrograma.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                // Si está vacío o nulo, muestra el promptText de los filtros; si no, el valor real
                setText(empty || item == null ? filtroPrograma.getPromptText() : item);
            }
        });

        filtroModulo.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? filtroModulo.getPromptText() : item);
            }
        });

        // Escuchar cambios en el TextField de documento
        inputDocumento.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
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
                new Resultado(1, 2023, "CC", 123456, "Juan Pérez", "R001", "Estudiante", "1010", "Ingeniería", "Bogotá", "G1",
                        "88", "90", "85", "Matemáticas", "92", "Alto", "89", "88", "Ninguna"),
                new Resultado(1, 2023, "TI", 987654, "Ana Gómez", "R002", "Estudiante", "1020", "Medicina", "Medellín", "G2",
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
                .filter(r -> r.getPuntajeModulo() != null && r.getPuntajeModulo().matches("\\d+(\\.\\d+)?"))
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
        filtroPrograma.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        filtroModulo.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }

    private void actualizarOpcionesFiltros() {
        filtroPrograma.setItems(getOpcionesUnicas(Resultado::getPrograma));
        filtroModulo.setItems(getOpcionesUnicas(Resultado::getModulo));
    }

    private ObservableList<String> getOpcionesUnicas(java.util.function.Function<Resultado, String> mapper) {
        return FXCollections.observableArrayList(
                datosOriginales.stream().map(mapper).distinct().collect(Collectors.toList())
        );
    }

    private void aplicarFiltros() {
        String docFiltro = inputDocumento.getText();
        String progFiltro = filtroPrograma.getValue();
        String areaFiltro = filtroModulo.getValue();

        datosFiltrados.setPredicate(r ->
                // Filtro por documento (si no vacío, buscar coincidencia parcial)
                (docFiltro == null || docFiltro.isBlank()
                        || String.valueOf(r.getDocumento()).contains(docFiltro))
                        &&
                        // Filtro por programa
                        (progFiltro == null || progFiltro.equals(r.getPrograma()))
                        &&
                        // Filtro por área (módulo)
                        (areaFiltro == null || areaFiltro.equals(r.getModulo()))
        );

        actualizarGrafica();
    }



    @FXML
    public void handleCargarArchivo() {
        // Leer año y ciclo
        try {
            year = Integer.parseInt(inputYear.getText());
            cycle = Integer.parseInt(inputCiclo.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Año o ciclo inválido. Deben ser números enteros.", Alert.AlertType.ERROR);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de resultados");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de datos", ".xlsx", ".csv", "*.json"),
                new FileChooser.ExtensionFilter("Todos los archivos", ".")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            archivoCargadoLabel.setText("Archivo: " + selectedFile.getName() +
                    "  | Año: " + year + "  | Ciclo: " + cycle);
            cargarDatosDesdeArchivo(selectedFile);
        }
    }


    /**
     * Si viene "-", devuelve "0", en otro caso el valor original.
     */
    private String normalizePercentil(String raw) {
        return "-".equals(raw) ? "0" : raw;
    }


    private void cargarDatosDesdeArchivo(File archivo) {
        ObservableList<Resultado> nuevosDatos = FXCollections.observableArrayList();

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) { primeraLinea = false; continue; }

                String[] campos = linea.split(",");
                if (campos.length >= 18 && campos[1].matches("\\d+")) {

                    // Convertir guiones a "0" solo para percentiles
                    String puntajeGlobal          = campos[9];
                    String percentilNacGlobal     = campos[10];
                    String percentilNacNbc        = normalizePercentil(campos[11]);  // <-- aquí
                    String modulo                 = campos[12];
                    String puntajeModulo          = campos[13];
                    String nivelDesempeno         = campos[14];
                    String percentilNacModulo     = campos[15];
                    String percentilGrupoNbcModulo= normalizePercentil(campos[16]);  // <-- aquí
                    String novedades              = campos[17];

                    Resultado r = new Resultado(
                            cycle,
                            year,
                            campos[0],
                            Long.parseLong(campos[1]),
                            campos[2],
                            campos[3],
                            campos[4],
                            campos[5],
                            campos[6],
                            campos[7],
                            campos[8],
                            puntajeGlobal,
                            percentilNacGlobal,
                            percentilNacNbc,
                            modulo,
                            puntajeModulo,
                            nivelDesempeno,
                            percentilNacModulo,
                            percentilGrupoNbcModulo,
                            novedades
                    );
                    nuevosDatos.add(r);
                }
            }

            // actualizar tabla/gráfica y enviar al backend...
            datosOriginales.setAll(nuevosDatos);
            datosFiltrados = new FilteredList<>(datosOriginales);
            tablaResultados.setItems(datosFiltrados);
            actualizarOpcionesFiltros();
            aplicarFiltros();
            actualizarGrafica();
            enviarResultadosAlBackend(nuevosDatos);

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

    @FXML
    public void handleRestablecerFiltros() {
        // Limpiar selección de los ComboBox
        inputDocumento.clear();
        filtroPrograma.setValue(null);
        filtroModulo.setValue(null);

        // Quitar cualquier predicado y mostrar todos los datos
        datosFiltrados.setPredicate(r -> true);

        // Volver a poblar la gráfica con todos los datos
        actualizarGrafica();
    }


    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static class Resultado {
        private final int ciclo;
        private final int year;
        private final long documento;
        private final String tipoDocumento;
        private final String nombre;
        private final String numeroRegistro;
        private final String tipoEvaluado;
        private final String sniesProgramaAcademico;
        private final String programa;
        private final String ciudad;
        private final String nucleoBasicoConocimiento;               // <-- Añadido
        private final String puntajeGlobal;
        private final String percentilNacionalGlobal;
        private final String percentilNacionalNbc;
        private final String modulo;
        private final String puntajeModulo;
        @JsonProperty("nivelDesempeño")                               // mapea nombre con tilde
        private final String nivelDesempeno;
        private final String percentilNacionalModulo;
        private final String percentilGrupoNbcModulo;
        private final String novedades;

        public Resultado(int ciclo,
                         int year, String tipoDocumento, long documento,
                         String nombre, String numeroRegistro,
                         String tipoEvaluado, String sniesProgramaAcademico,
                         String programa, String ciudad, String nucleoBasicoConocimiento,
                         String puntajeGlobal, String percentilNacionalGlobal,
                         String percentilNacionalNbc, String modulo,
                         String puntajeModulo, String nivelDesempeno,
                         String percentilNacionalModulo, String percentilGrupoNbcModulo,
                         String novedades) {
            this.ciclo = ciclo;
            this.year = year;
            this.tipoDocumento = tipoDocumento;
            this.documento = documento;
            this.nombre = nombre;
            this.numeroRegistro = numeroRegistro;
            this.tipoEvaluado = tipoEvaluado;
            this.sniesProgramaAcademico = sniesProgramaAcademico;
            this.programa = programa;
            this.ciudad = ciudad;
            this.nucleoBasicoConocimiento = nucleoBasicoConocimiento;
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

        // Getters
        public int getCiclo() { return ciclo; }
        public int getYear() { return year; }
        public long getDocumento() { return documento; }
        public String getTipoDocumento() { return tipoDocumento; }
        public String getNombre() { return nombre; }
        public String getNumeroRegistro() { return numeroRegistro; }
        public String getTipoEvaluado() { return tipoEvaluado; }
        public String getSniesProgramaAcademico() { return sniesProgramaAcademico; }
        public String getPrograma() { return programa; }
        public String getCiudad() { return ciudad; }
        public String getNucleoBasicoConocimiento() { return nucleoBasicoConocimiento; }
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


    /**
     * Hace POST a /usuarios/inicio-sesion y extrae el token JWT.
     */
    private boolean loginYObtenerToken(String email, String password) {
        try {
            URL url = new URL("http://localhost:8080/usuarios/inicio-sesion");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);

            // Construimos el JSON de login
            String loginJson = String.format(
                    "{\"email\":\"%s\",\"password\":\"%s\"}",
                    email, password
            );

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = loginJson.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK && code != HttpURLConnection.HTTP_ACCEPTED) {
                InputStream errorStream = con.getErrorStream();
                if (errorStream != null) {
                    String errorResponse = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    System.err.println("Respuesta del servidor: " + errorResponse);
                }
                System.err.println("Login fallido, código: " + code);
                return false;
            }
            String responseBody = new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Respuesta JSON: " + responseBody);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);

            if (root.has("token")) {
                authToken = root.get("token").asText();
                System.out.println("Token obtenido: " + authToken);
            } else {
                System.err.println("No se encontró el campo 'token' en la respuesta JSON.");
                return false;
            }

            con.disconnect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Envía por POST la lista completa de Resultados al endpoint definido.
     */
    private void enviarResultadosAlBackend(List<Resultado> resultados) {
        if (authToken == null) {
            boolean ok = loginYObtenerToken("jctobon11.2@gmail.com", "HRCTKMZ");
            if (!ok) {
                archivoCargadoLabel.setText("No fue posible iniciar sesión");
                return;
            }
        }

        try {
            URL url = new URL("http://localhost:8080/resultados/file");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");

            // 🔐 AÑADIMOS EL TOKEN EN EL HEADER
            con.setRequestProperty("Authorization", "Bearer " + authToken);

            con.setDoOutput(true);

            // Serializar lista a JSON
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(resultados);

            // 🔍 Agregamos impresión del JSON para depuración
            System.out.println("JSON que se enviará:");
            System.out.println(json);

            // Escribir cuerpo
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }


            int code = con.getResponseCode();
            if (code < 200 || code >= 300) {
                // Leer y mostrar el mensaje de error completo que devuelve el servidor
                String errorBody = new String(con.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
                System.err.println("Error al enviar (cód. " + code + "):\n" + errorBody);
                archivoCargadoLabel.setText("Error al enviar (cód. " + code + ")");
                return;
            }
            con.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            archivoCargadoLabel.setText("Fallo conexión al backend");
        }
    }

}