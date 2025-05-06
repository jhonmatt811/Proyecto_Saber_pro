package com.java.fx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Duration;
import java.util.Base64;

public class CrearUsuariosController {

    // Constante con la URL de tu API
    // Cambia esto:

    private static final String API_URL = "http://localhost:8080/usuarios";

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
    @FXML private Button subirFotoButton;
    @FXML private ImageView imagenUsuario;

    private String fotoBase64; // Para almacenar la imagen en base64

    @FXML
    public void subirFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File archivoSeleccionado = fileChooser.showOpenDialog(subirFotoButton.getScene().getWindow());
        if (archivoSeleccionado != null) {
            try {
                // Convertir imagen a base64
                byte[] fileContent = java.nio.file.Files.readAllBytes(archivoSeleccionado.toPath());
                fotoBase64 = Base64.getEncoder().encodeToString(fileContent);

                // Mostrar imagen en el ImageView
                Image imagen = new Image(archivoSeleccionado.toURI().toString());
                imagenUsuario.setImage(imagen);
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo cargar la imagen", AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void initialize() {
        grupoOpciones = new ToggleGroup();
        opcion1.setToggleGroup(grupoOpciones);
        opcion2.setToggleGroup(grupoOpciones);
        opcion3.setToggleGroup(grupoOpciones);

        // Asignar valores a los radio buttons según tu lógica de roles
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
                (int) grupoOpciones.getSelectedToggle().getUserData(),
                fotoBase64
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
        try {
            // Convertir objeto Usuario a JSON
            String jsonBody = convertirAJson(usuario);

            // Crear cliente HTTP
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();

            // Crear request HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + Sesion.jwtToken) // <-- Agrega el token
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            // Enviar request de forma asíncrona
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 201 || response.statusCode() == 200) {
                            javafx.application.Platform.runLater(() -> {
                                mostrarAlerta("Éxito", "Usuario creado correctamente", AlertType.INFORMATION);
                                limpiarFormulario();
                            });
                        } else {
                            javafx.application.Platform.runLater(() -> {
                                String mensaje = "Código: " + response.statusCode() + "\nRespuesta: " + response.body();
                                System.err.println(mensaje);
                                mostrarAlerta("Error", "Error al crear usuario:\n" + mensaje, AlertType.ERROR);
                                System.out.println("Código de estado: " + response.statusCode());
                                System.out.println("Cuerpo de la respuesta: " + response.body());

                            });
                        }
                    })
                    .exceptionally(e -> {
                        javafx.application.Platform.runLater(() -> {
                            mostrarAlerta("Error", "Error de conexión: " + e.getMessage(), AlertType.ERROR);
                        });
                        return null;
                    });

        } catch (Exception e) {
            mostrarAlerta("Error", "Error al enviar datos: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
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
                usuario.getRolId(),
                usuario.getFoto() != null ? usuario.getFoto() : ""
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
        imagenUsuario.setImage(null);
        fotoBase64 = null;
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

        // Getters
        public long getCc() { return cc; }
        public String getPrimerNombre() { return primerNombre; }
        public String getSegundoNombre() { return segundoNombre; }
        public String getPrimerApellido() { return primerApellido; }
        public String getSegundoApellido() { return segundoApellido; }
        public String getEmail() { return email; }
        public int getRolId() { return rolId; }
        public String getFoto() { return foto; }
    }
}