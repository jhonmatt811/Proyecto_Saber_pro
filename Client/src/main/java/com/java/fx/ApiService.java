package com.java.fx;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.fx.Usuarios_y_Roles.Persona;
import com.java.fx.model.Rol;
import com.java.fx.Usuarios_y_Roles.Sesion;
import com.java.fx.Usuarios_y_Roles.Usuario;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;

public class ApiService {

    private static final String BASE_URL = "http://localhost:8080";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Gson gson = new Gson();

    private HttpRequest.Builder builderConAutorizacion(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + Sesion.getJwtToken());
    }

    // -------------------- USUARIOS ------------------------

    public List<Usuario> obtenerUsuarios() throws Exception {
        HttpRequest request = builderConAutorizacion(BASE_URL + "/admin/usuarios").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        manejarErrores(response);

        JsonNode rootNode = mapper.readTree(response.body());

        if (rootNode.isArray()) {
            return mapper.readValue(response.body(), new TypeReference<List<Usuario>>() {});
        }

        JsonNode usuariosNode = rootNode.get("usuarios");
        if (usuariosNode != null && usuariosNode.isArray()) {
            return mapper.readValue(usuariosNode.toString(), new TypeReference<List<Usuario>>() {});
        }

        throw new RuntimeException("No se encontró el arreglo de usuarios en la respuesta");
    }

    public List<Persona> crearPersonasEnLote(List<Persona> personas) throws IOException, InterruptedException {
        String json = gson.toJson(personas);

        HttpRequest request = builderConAutorizacion(BASE_URL + "/admin/personas/lote")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Sesion.getJwtToken())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // ✅ Verificar si la respuesta fue exitosa (2xx)
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            Type personaListType = new TypeToken<List<Persona>>() {}.getType();
            return gson.fromJson(response.body(), personaListType);
        } else {
            // 👀 Imprimir el error detallado del backend
            System.err.println("Error al crear personas en lote: " + response.statusCode());
            System.err.println("Respuesta del servidor: " + response.body());
            throw new RuntimeException("Error al crear personas: " + response.body());
        }
    }
    public List<Usuario> crearUsuariosEnLote(List<Usuario> usuarios) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Filtrar usuarios válidos
            List<Map<String, Object>> usuariosMapeados = usuarios.stream()
                    .filter(u -> {
                        if (u.getPersona() == null) {
                            System.err.println("Usuario sin persona asociada: " + u.getCorreo());
                            return false;
                        }
                        return true;
                    })
                    .map(u -> {
                        Map<String, Object> mapUser = new HashMap<>();
                        mapUser.put("person", u.getPersona());
                        mapUser.put("rol_id", u.getRol_id());
                        return mapUser;
                    })
                    .collect(Collectors.toList());

            if (usuariosMapeados.isEmpty()) {
                System.err.println("No hay usuarios válidos para enviar.");
                return Collections.emptyList();
            }

            // Serializar toda la lista a JSON
            String requestBody = mapper.writeValueAsString(usuariosMapeados);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/admin/usuarios/lote"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + Sesion.getJwtToken())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            String responseBody = response.body();

            if (status == 200 || status == 201) {
                System.out.println("Usuarios creados correctamente.");
                return usuarios.stream()
                        .filter(u -> u.getPersona() != null)
                        .collect(Collectors.toList());
            } else {
                System.err.println("Error al crear usuarios. Código: " + status + ", respuesta: " + responseBody);
                throw new RuntimeException("Error al crear usuarios: " + responseBody);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Excepción al crear usuarios: " + e.getMessage(), e);
        }
    }


    // ---------------------- ROLES ------------------------

    public void cambiarRol(UUID userId, int rolId) {
        String url = BASE_URL + "/admin/usuarios/" + userId + "/rol?rolId=" + rolId;

        HttpRequest request = builderConAutorizacion(url)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            mostrarAlertaInfo("Éxito", "Rol actualizado correctamente.");
                        } else {
                            mostrarAlertaError("Error al actualizar el rol. Código: " + response.statusCode());
                        }
                    });
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    Platform.runLater(() -> mostrarAlertaError("No se pudo actualizar el rol."));
                    return null;
                });
    }

    public List<Rol> obtenerRoles() throws Exception {
        HttpRequest request = builderConAutorizacion(BASE_URL + "/roles")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        manejarErrores(response);

        return mapper.readValue(response.body(), new TypeReference<List<Rol>>() {});
    }

    // ---------------------- CONTRASEÑAS ------------------------

    public boolean sendPasswordRecoveryEmail(String email) {
        try {
            String json = String.format("{\"email\":\"%s\"}", email);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/usuarios/contraseña/olvidado"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean resetPassword(String email, String password, String code) {
        try {
            String json = String.format(
                    "{\"email\":\"%s\",\"password\":\"%s\", \"code\":\"%s\"}",
                    email, password, code
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/usuarios/contrasena"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return true;
            } else {
                System.err.println("Error del servidor: " + response.body());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------------- UTILIDADES ------------------------



    private void manejarErrores(HttpResponse<String> response) {
        if (response.statusCode() >= 400) {
            throw new RuntimeException("Error del servidor (" + response.statusCode() + "): " + response.body());
        }
    }

    private void mostrarAlertaInfo(String titulo, String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    private void mostrarAlertaError(String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }
}
