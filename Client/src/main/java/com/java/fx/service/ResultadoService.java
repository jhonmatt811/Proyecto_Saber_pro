package com.java.fx.service;
import com.java.fx.model.AccionesDeMejora.*;
import com.java.fx.model.Resultado;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.java.fx.model.ResultadoIcfes;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import com.java.fx.Usuarios_y_Roles.Sesion;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.poi.ss.usermodel.Cell;
import java.util.stream.Stream;

@Service
public class ResultadoService {
    private static final String BASE_URL = "http://localhost:8080";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    /*
      Llama al endpoint GET /resultados con los filtros opcionales.
      Crea la URL con query params si no son nulos.
     */
    public List<Resultado> obtenerResultados(
            Integer year,
            Integer ciclo,
            Long documento,
            Integer programaId
    ) throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder(BASE_URL + "/resultados");

        List<String> params = Stream.of(
                        year      != null ? "year="     + year       : null,
                        ciclo     != null ? "ciclo="    + ciclo      : null,
                        documento != null ? "documento="+ documento  : null,
                        programaId!= null ? "programa="  + programaId : null
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!params.isEmpty()) {
            sb.append("?").append(String.join("&", params));
        }
        //
        //String url = sb.toString();
        //System.out.println("→ Llamando al GET " + url);
        //
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(sb.toString()))
                .header("Authorization", "Bearer " + Sesion.getJwtToken())
                .GET()
                .build();

        HttpResponse<String> resp =
                client.send(req, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() != 200) {
            throw new IOException("Error al obtener resultados: " + resp.statusCode()
                    + " / " + resp.body());
        }

        // Parseamos directamente un array de Resultado
        return mapper.readValue(
                resp.body(),
                new TypeReference<List<Resultado>>() {}
        );
    }

    public List<Resultado> cargarDatosDesdeArchivo(File archivo, int ciclo, int year) throws IOException {
        String nombre = archivo.getName().toLowerCase();
        if (nombre.endsWith(".csv")) {
            return cargarDatosDesdeCSV(archivo, ciclo, year);
        } else if (nombre.endsWith(".xlsx")) {
            try {
                return cargarDatosDesdeXLSX(archivo, ciclo, year);
            } catch (InvalidFormatException e) {
                throw new IOException("Formato XLSX inválido: " + e.getMessage(), e);
            }
        } else {
            throw new IOException("Formato de archivo no soportado: " + archivo.getName());
        }
    }

    private List<Resultado> cargarDatosDesdeCSV(File archivo, int ciclo, int year) throws IOException {
        List<Resultado> resultados = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) { primeraLinea = false; continue; }
                String[] campos = linea.split(",");
                if (campos.length >= 18 && campos[1].matches("\\d+")) {
                    resultados.add(parsearCampos(campos, ciclo, year));
                }
            }
        }
        return resultados;
    }

    private List<Resultado> cargarDatosDesdeXLSX(File archivo, int ciclo, int year)
            throws IOException, InvalidFormatException {
        List<Resultado> resultados = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(archivo)) {
            // Recorremos todas las hojas
            for (Sheet sheet : workbook) {
                Iterator<Row> rows = sheet.iterator();
                // Saltamos la fila de cabecera de cada hoja
                if (rows.hasNext()) {
                    rows.next();
                }

                while (rows.hasNext()) {
                    Row row = rows.next();
                    Cell cellId = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (cellId.getCellType() == CellType.NUMERIC) {
                        String[] campos = new String[18];
                        for (int i = 0; i < 18; i++) {
                            Cell c = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            switch (c.getCellType()) {
                                case STRING:
                                    campos[i] = c.getStringCellValue();
                                    break;
                                case NUMERIC:
                                    campos[i] = String.valueOf((long) c.getNumericCellValue());
                                    break;
                                case BOOLEAN:
                                    campos[i] = Boolean.toString(c.getBooleanCellValue());
                                    break;
                                case FORMULA:
                                    campos[i] = c.getCellFormula();
                                    break;
                                default:
                                    campos[i] = "";
                            }
                        }
                        resultados.add(parsearCampos(campos, ciclo, year));
                    }
                }
            }
        }
        return resultados;
    }


    private Resultado parsearCampos(String[] campos, int ciclo, int year) {
        String puntajeGlobal           = campos[9];
        String percentilNacGlobal      = campos[10];
        String percentilNacNbc         = normalizePercentil(campos[11]);
        String modulo                  = campos[12];
        String puntajeModulo           = campos[13];
        String nivelDesempeno          = campos[14];
        String percentilNacModulo      = campos[15];
        String percentilGrupoNbcModulo = normalizePercentil(campos[16]);
        String novedades               = campos[17];

        return new Resultado(
                ciclo, year,
                campos[0], Long.parseLong(campos[1]),
                campos[2], campos[3], campos[4], campos[5],
                campos[6], campos[7], campos[8],
                puntajeGlobal, percentilNacGlobal, percentilNacNbc,
                modulo, puntajeModulo, nivelDesempeno,
                percentilNacModulo, percentilGrupoNbcModulo, novedades
        );
    }

    /**
     * Normaliza valores de percentil, convirtiendo '-' en '0'.
     */
    private String normalizePercentil(String raw) {
        return "-".equals(raw) ? "0" : raw;
    }

    /**
     * Envía la lista de resultados al backend vía HTTP POST,
     * usando el token almacenado en Sesion.jwtToken.
     */
    public void enviarResultadosAlBackend(List<Resultado> resultados) throws IOException {
        String token = Sesion.getJwtToken();
        if (token == null || token.isBlank()) {
            throw new IOException("No hay token de autenticación. Debes iniciar sesión primero.");
        }

        System.out.println("→ Filas a enviar al backend: " + resultados.size());

        URL url = new URL("http://localhost:8080/resultados/file");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setDoOutput(true);

        // Serializar la lista de resultados a JSON
        ObjectWriter ow = new ObjectMapper()
                .writer()
                .withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(resultados);

        //imprimir los json generados
        //System.out.println(json);

        // Enviar cuerpo
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int code = con.getResponseCode();
        if (code < 200 || code >= 300) {
            String errorBody = new String(con.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            throw new IOException("Error al enviar (cód. " + code + "): " + errorBody);
        }

        con.disconnect();
    }


    // Obtener todos los programas
    public List<Programa> obtenerProgramas() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/programas"))
                .header("Authorization", "Bearer " + Sesion.getJwtToken())
                .GET()
                .build();

        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(resp.body(), new TypeReference<List<Programa>>() {});
    }

    // Obtener todos los módulos
    public List<Modulo> obtenerModulos() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/modulos"))
                .header("Authorization", "Bearer " + Sesion.getJwtToken())
                .GET()
                .build();

        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(resp.body(), new TypeReference<List<Modulo>>() {});
    }
    public void enviarSugerencia(String jsonSugerencia) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mejoras"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Sesion.getJwtToken())
                .POST(HttpRequest.BodyPublishers.ofString(jsonSugerencia))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201) { // 201 Created
            throw new IOException("Error al enviar sugerencia: " + response.body());
        }
    }
    public List<SugerenciaMejora> obtenerMejoras() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mejoras"))
                .header("Authorization", "Bearer " + Sesion.getJwtToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Error al obtener mejoras: " + response.body());
        }

        return mapper.readValue(response.body(), new TypeReference<List<SugerenciaMejora>>() {});
    }

    public void actualizarMejora(String id, String json) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mejoras/" + id))
                .header("Authorization", "Bearer " + Sesion.getJwtToken())
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 403) {
            throw new IOException("Modificación bloqueada: Vigencia expirada");
        } else if (response.statusCode() != 200) {
            throw new IOException("Error " + response.statusCode() + ": " + response.body());
        }
    }

    public void eliminarMejora(String id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mejoras/" + id))
                .header("Authorization", "Bearer " + Sesion.getJwtToken())
                .DELETE() // Método HTTP DELETE
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) { // Verifica código de estado exitoso
            throw new IOException("Error al eliminar mejora: " + response.body());
        }
    }

    public AnalisisMejora obtenerAnalisisIA(String idMejora) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mejoras/sugerencias/" + idMejora))
                .header("Authorization", "Bearer " + Sesion.getJwtToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Error al obtener análisis: " + response.body());
        }

        return mapper.readValue(response.body(), AnalisisMejora.class);
    }

    public AnalisisMejora obtenerAnalisisMejora(SugerenciaMejora sugerencia) throws IOException, InterruptedException {
        // Crear estructura del cuerpo esperado por el servidor
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accionMejora", sugerencia);

        String jsonBody = mapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mejoras/sugerencias"))
                .header("Authorization", "Bearer " + Sesion.getJwtToken())
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.ofString(jsonBody)) // Enviar cuerpo en GET
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Error al obtener análisis: " + response.body());
        }

        return mapper.readValue(response.body(), AnalisisMejora.class);
    }

    public AnalisisMejora obtenerAnalisisMejoral(GetAnalisisMejora getDTO) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonBody = mapper.writeValueAsString(getDTO); // Serializa el DTO del GET

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mejoras/sugerencias"))
                .header("Authorization", "Bearer " + Sesion.getJwtToken())
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.ofString(jsonBody)) // GET con cuerpo
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), AnalisisMejora.class);
    }


    public List<ResultadoIcfes> obtenerResultadosIcfes(Integer limit, Integer periodo, Integer offset)
            throws IOException, InterruptedException {

        // Construir URL base
        String url = BASE_URL + "/resultados/icfes";

        // Construir query parameters
        List<String> params = new ArrayList<>();
        if (limit != null) params.add("limit=" + limit);
        params.add("periodo=" + periodo);
        if (offset != null) params.add("offset=" + offset);

        // Crear URL completa
        String fullUrl = url + "?" + String.join("&", params);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Authorization", "Bearer " + Sesion.getJwtToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(response.body(), new TypeReference<List<ResultadoIcfes>>() {});
    }


    public String getSuggest(String id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/sugerencias/" + id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}

