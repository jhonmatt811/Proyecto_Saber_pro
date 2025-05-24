package com.java.fx;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.fx.Usuarios_y_Roles.Rol;
import com.java.fx.Usuarios_y_Roles.Sesion;
import com.java.fx.Usuarios_y_Roles.Usuario;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ApiService {
    private static final String BASE_URL = "http://localhost:8080";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Usuario> obtenerUsuarios() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/admin/usuarios"))
                .header("Authorization", "Bearer " + Sesion.getJwtToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Respuesta completa:");
        System.out.println(response.body());

        JsonNode rootNode = mapper.readTree(response.body());

        // Verifica las claves del objeto JSON
        System.out.println("Claves disponibles en el JSON:");
        rootNode.fieldNames().forEachRemaining(System.out::println);

        // Si rootNode es directamente una lista
        if (rootNode.isArray()) {
            return mapper.readValue(response.body(), new TypeReference<List<Usuario>>() {});
        }

        // Si hay alguna clave como "usuarios"
        JsonNode usuariosNode = rootNode.get("usuarios");
        if (usuariosNode != null && usuariosNode.isArray()) {
            return mapper.readValue(usuariosNode.toString(), new TypeReference<List<Usuario>>() {});
        }

        throw new RuntimeException("No se encontró el arreglo de usuarios en la respuesta");
    }

    public void cambiarRol(UUID userId, int rolId) {
        String url = BASE_URL + "/admin/usuarios/" + userId + "/rol?rolId=" + rolId;

        System.out.println("URL final = " + url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + Sesion.getJwtToken())
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    System.out.println("Código de respuesta: " + response.statusCode());
                    System.out.println("Cuerpo: " + response.body());
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            mostrarAlerta("Éxito", "Rol actualizado correctamente.");
                        } else {
                            mostrarAlerta("Error", "Error del servidor: " + response.statusCode());
                        }
                    });
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    Platform.runLater(() -> mostrarAlerta("Error", "No se pudo actualizar el rol."));
                    return null;
                });
    }
    public static List<Rol> obtenerRoles() throws Exception {
        String url = BASE_URL + "/roles";
        System.out.println("Llamando a URL: " + url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + Sesion.getJwtToken())
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response status: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            List<Rol> roles = mapper.readValue(response.body(), new TypeReference<List<Rol>>() {});
            return roles;
        } else {
            throw new RuntimeException("Error al obtener roles: " + response.statusCode());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Tipo de alerta
        alert.setTitle(titulo); // Título de la ventana
        alert.setHeaderText(null); // Encabezado (opcional)
        alert.setContentText(mensaje); // Mensaje principal
        alert.showAndWait(); // Muestra la alerta y espera
    }
    public boolean resetPassword(String email, String password, String code) throws IOException {
        URL url = new URL(BASE_URL + "/usuarios/contrasena");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonInputString = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\", \"code\":\"%s\"}",
                email, password, code
        );

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();

        if (responseCode == 200) {
            return true;
        } else {
            InputStream errorStream = conn.getErrorStream();
            if (errorStream != null) {
                String errorMessage = new BufferedReader(new InputStreamReader(errorStream))
                        .lines().collect(Collectors.joining("\n"));
                System.out.println("Error del servidor: " + errorMessage);
            }
            return false;
        }
    }
    public boolean sendPasswordRecoveryEmail(String email) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = BASE_URL + "/usuarios/contraseña/olvidado";

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String json = String.format("{\"email\": \"%s\"}", email);
            HttpEntity<String> request = new HttpEntity<>(json, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }}

}