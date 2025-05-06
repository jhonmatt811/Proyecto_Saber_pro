package com.java.fx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Duration;
import org.json.JSONObject;
import org.json.JSONException;
import javafx.application.Platform; // Para Platform.runLater




public class CrearUsuariosController {

    private static final String API_URL = "http://localhost:8080/personas";

    @FXML private RadioButton opcion1;
    @FXML private RadioButton opcion2;
    @FXML private RadioButton opcion3;
    @FXML private ToggleGroup grupoOpciones;
    @FXML private TextField nombreField;
    @FXML private TextField segundoNombreField;
    @FXML private TextField apellidoField;
    @FXML private TextField segundoApellidoField;
    @FXML private TextField ccField;
    @FXML private TextField correoField;
    @FXML private PasswordField contrasenaField;

    @FXML
    public void initialize() {
        grupoOpciones = new ToggleGroup();
        opcion1.setToggleGroup(grupoOpciones);
        opcion2.setToggleGroup(grupoOpciones);
        opcion3.setToggleGroup(grupoOpciones);

        // Asignar valores a los radio botones
        opcion1.setUserData(1); // ID del rol Directivo
        opcion2.setUserData(2); // ID del rol Docente
        opcion3.setUserData(3); // ID del rol Director Icfes
    }

    @FXML
    public void registrarUsuario() {
        // Validar campos obligatorios
        if (!validarCampos()) {
            return;
        }

        // Crear objeto con los datos del usuario
        Usuario usuario = new Usuario(
                Long.parseLong(ccField.getText()),
                nombreField.getText(),
                segundoNombreField.getText(),
                apellidoField.getText(),
                segundoApellidoField.getText(),
                correoField.getText(),
                (int) grupoOpciones.getSelectedToggle().getUserData()
        );

        // Enviar datos a la API
        enviarDatosAPI(usuario);
    }

    private boolean validarCampos() {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        // Validar campos obligatorios
        if (ccField.getText().isEmpty() || nombreField.getText().isEmpty() ||
                apellidoField.getText().isEmpty() || correoField.getText().isEmpty() ||
                contrasenaField.getText().isEmpty()) {
            mostrarAlerta("Campos vacíos", "Debe completar todos los campos obligatorios.", AlertType.ERROR);
            return false;
        }

        // Validar formato de cédula (solo números)
        try {
            Long.parseLong(ccField.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Cédula inválida", "La cédula debe contener solo números.", AlertType.ERROR);
            return false;
        }

        // Validar email
        if (!isValidEmail(correoField.getText())) {
            mostrarAlerta("Correo inválido", "Correo inválido. Intente con uno como ejemplo@gmail.com", AlertType.ERROR);
            return false;
        }

        // Validar rol seleccionado
        if (grupoOpciones.getSelectedToggle() == null) {
            mostrarAlerta("Rol no seleccionado", "Debe seleccionar un rol antes de registrar.", AlertType.WARNING);
            return false;
        }


        return true;
    }

    private void enviarDatosAPI(Usuario usuario) {
        HttpClient client = HttpClient.newHttpClient();

        // 1. Crear JSON para la persona
        String personaJson = String.format(
                "{\"cc\": %d, \"primer_nombre\": \"%s\", \"segundo_nombre\": \"%s\", " +
                        "\"primer_apellido\": \"%s\", \"segundo_apellido\": \"%s\", \"email\": \"%s\"}",
                usuario.getCc(),
                usuario.getPrimerNombre(),
                usuario.getSegundoNombre(),
                usuario.getPrimerApellido(),
                usuario.getSegundoApellido(),
                usuario.getEmail()
        );

        // 2. Crear y enviar solicitud para la persona
        HttpRequest requestPersona = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(personaJson))
                .build();

        client.sendAsync(requestPersona, HttpResponse.BodyHandlers.ofString())
                .thenAccept(personaResponse -> {
                    int statusCode = personaResponse.statusCode();
                    if (statusCode >= 200 && statusCode < 300) {
                        try {
                            JSONObject jsonPersona = new JSONObject(personaResponse.body());
                            if (jsonPersona.has("id")) {
                                String personaId = jsonPersona.getString("id"); // Declaración correcta de personaId

                                // 3. Crear JSON para el usuario usando el ID de la persona
                                String usuarioJson = String.format(
                                        "{ \"person\": { \"id\": \"%s\" }, \"rol_id\": %d, \"passwd\": \"%s\" }",
                                        personaId, // ✅ Variable ahora está definida
                                        usuario.getRolId(),
                                        contrasenaField.getText()
                                );

                                // 4. Crear y enviar solicitud para el usuario
                                HttpRequest requestUsuario = HttpRequest.newBuilder()
                                        .uri(URI.create("http://localhost:8080/usuarios"))
                                        .header("Content-Type", "application/json")
                                        .POST(BodyPublishers.ofString(usuarioJson))
                                        .build();

                                client.sendAsync(requestUsuario, HttpResponse.BodyHandlers.ofString())
                                        .thenAccept(usuarioResponse -> {
                                            if (usuarioResponse.statusCode() == 200 || usuarioResponse.statusCode() == 201) {
                                                Platform.runLater(() -> {
                                                    mostrarAlerta("Éxito", "Usuario creado correctamente.", AlertType.INFORMATION);
                                                    limpiarFormulario();
                                                });
                                            } else {
                                                Platform.runLater(() -> {
                                                    mostrarAlerta("Error", "Error al crear usuario: " + usuarioResponse.body(), AlertType.ERROR);
                                                });
                                            }
                                        });
                            } else {
                                Platform.runLater(() -> {
                                    mostrarAlerta("Error", "La respuesta no contiene el ID de la persona.", AlertType.ERROR);
                                });
                            }
                        } catch (JSONException e) {
                            Platform.runLater(() -> {
                                mostrarAlerta("Error", "Error en el formato JSON: " + e.getMessage(), AlertType.ERROR);
                            });
                        }
                    } else {
                        Platform.runLater(() -> {
                            mostrarAlerta("Error", "Error al crear persona: " + personaResponse.body(), AlertType.ERROR);
                        });
                    }
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        mostrarAlerta("Error", "Error de conexión: " + e.getMessage(), AlertType.ERROR);
                    });
                    return null;
                });
    }
    private void showErrorAlert(String message) {
        Platform.runLater(() -> { // <-- Platform ahora está importado
            mostrarAlerta("Error", message, AlertType.ERROR);
        });
    }



    private String convertirAJson(Usuario usuario) {
        // Formatear el objeto Usuario como JSON
        return String.format(
                "{\"cc\": %d, \"primer_nombre\": \"%s\", \"segundo_nombre\": \"%s\", " +
                        "\"primer_apellido\": \"%s\", \"segundo_apellido\": \"%s\", " +
                        "\"email\": \"%s\", \"rol_id\": %d, \"foto\": \"%s\"}",
                usuario.getCc(),
                usuario.getPrimerNombre(),
                usuario.getSegundoNombre(),
                usuario.getPrimerApellido(),
                usuario.getSegundoApellido(),
                usuario.getEmail(),
                usuario.getRolId()
        );
    }

    private void limpiarFormulario() {
        nombreField.clear();
        segundoNombreField.clear();
        apellidoField.clear();
        segundoApellidoField.clear();
        ccField.clear();
        correoField.clear();
        contrasenaField.clear();
        grupoOpciones.selectToggle(null);
    }

    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.(com|net|org|edu|gov|co)$");
    }

    // Clase interna para representar los datos del usuario
    private static class Usuario {
        private long cc;
        private String primerNombre;
        private String segundoNombre;
        private String primerApellido;
        private String segundoApellido;
        private String email;
        private int rolId;
        private String foto;

        public Usuario(long cc, String primerNombre, String segundoNombre,
                       String primerApellido, String segundoApellido,
                       String email, int rolId, String foto) {
            this.cc = cc;
            this.primerNombre = primerNombre;
            this.segundoNombre = segundoNombre;
            this.primerApellido = primerApellido;
            this.segundoApellido = segundoApellido;
            this.email = email;
            this.rolId = rolId;
            this.foto = foto;
        }

        public Usuario(long cc, String primerNombre, String segundoNombre,
                       String primerApellido, String segundoApellido,
                       String email, int rolId) {
            this.cc = cc;
            this.primerNombre = primerNombre;
            this.segundoNombre = segundoNombre;
            this.primerApellido = primerApellido;
            this.segundoApellido = segundoApellido;
            this.email = email;
            this.rolId = rolId;
           // O null, si no usas fotos por ahora
        }

        // Getters
        public long getCc() { return cc; }
        public String getPrimerNombre() { return primerNombre; }
        public String getSegundoNombre() { return segundoNombre; }
        public String getPrimerApellido() { return primerApellido; }
        public String getSegundoApellido() { return segundoApellido; }
        public String getEmail() { return email; }
        public int getRolId() { return rolId; }

    }
}