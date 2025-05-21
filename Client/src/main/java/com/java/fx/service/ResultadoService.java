package com.java.fx.service;
import com.java.fx.controller.ResultadosController;
import com.java.fx.model.Resultado;


import com.java.fx.model.Resultado;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultadoService {
    private String authToken;

    /**
     * Carga resultados desde un archivo CSV o similar.
     * @param archivo File con los datos.
     * @param ciclo  ciclo (int).
     * @param year   año (int).
     * @return lista de Resultados.
     * @throws IOException en caso de fallo de lectura.
     */
    public List<Resultado> cargarDatosDesdeArchivo(File archivo, int ciclo, int year) throws IOException {
        List<Resultado> resultados = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) { primeraLinea = false; continue; }
                String[] campos = linea.split(",");
                if (campos.length >= 18 && campos[1].matches("\\d+")) {
                    String puntajeGlobal           = campos[9];
                    String percentilNacGlobal      = campos[10];
                    String percentilNacNbc         = normalizePercentil(campos[11]);
                    String modulo                  = campos[12];
                    String puntajeModulo           = campos[13];
                    String nivelDesempeno          = campos[14];
                    String percentilNacModulo      = campos[15];
                    String percentilGrupoNbcModulo = normalizePercentil(campos[16]);
                    String novedades               = campos[17];

                    Resultado r = new Resultado(
                            ciclo, year,
                            campos[0], Long.parseLong(campos[1]),
                            campos[2], campos[3], campos[4], campos[5],
                            campos[6], campos[7], campos[8],
                            puntajeGlobal, percentilNacGlobal, percentilNacNbc,
                            modulo, puntajeModulo, nivelDesempeno,
                            percentilNacModulo, percentilGrupoNbcModulo, novedades
                    );
                    resultados.add(r);
                }
            }
        }
        return resultados;
    }

    /**
     * Normaliza valores de percentil, convirtiendo '-' en '0'.
     */
    private String normalizePercentil(String raw) {
        return "-".equals(raw) ? "0" : raw;
    }

    /**
     * Envía la lista de resultados al backend vía HTTP POST.
     */
    public void enviarResultadosAlBackend(List<Resultado> resultados) throws IOException {
        ensureAuthToken();

        URL url = new URL("http://localhost:8080/resultados/file");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + authToken);
        con.setDoOutput(true);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(resultados);

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

    /**
     * Asegura tener un token válido, llamando a login si es necesario.
     */
    private void ensureAuthToken() throws IOException {
        if (authToken == null) {
            if (!loginYObtenerToken("jctobon11.2@gmail.com", "HRCTKMZ")) {
                throw new IOException("No fue posible iniciar sesión en el backend");
            }
        }
    }

    /**
     * Realiza login para obtener JWT.
     */
    private boolean loginYObtenerToken(String email, String password) {
        try {
            URL url = new URL("http://localhost:8080/usuarios/inicio-sesion");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);

            String loginJson = String.format("{\"email\":\"%s\",\"password\":\"%s\"}",
                    email, password);
            try (OutputStream os = con.getOutputStream()) {
                os.write(loginJson.getBytes(StandardCharsets.UTF_8));
            }

            int code = con.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK && code != HttpURLConnection.HTTP_ACCEPTED) {
                return false;
            }

            String responseBody = new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            JsonNode root = new ObjectMapper().readTree(responseBody);
            if (root.has("token")) {
                authToken = root.get("token").asText();
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
