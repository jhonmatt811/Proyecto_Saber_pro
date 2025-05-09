package com.java.fx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


public class CrearUsuariosController {


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
                (Integer) grupoOpciones.getSelectedToggle().getUserData()
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
                            });
                        }
                    })
                    .exceptionally(e -> {
                        javafx.application.Platform.runLater(() -> {
                            mostrarAlerta("Error", "Error de conexión: " + e.getMessage(), AlertType.ERROR);
                        });
                        // Se debe retornar un valor compatible con el tipo esperado (HttpResponse<String>)
                        return null; // Otra opción sería un `HttpResponse<String>` vacío si necesario
                    });


        } catch (Exception e) {
            mostrarAlerta("Error", "Error al enviar datos: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private String convertirAJson(Usuario usuario) {
        return String.format(
                "{\"cc\": %d, \"primer_nombre\": \"%s\", \"segundo_nombre\": \"%s\", " +
                        "\"primer_apellido\": \"%s\", \"segundo_apellido\": \"%s\", " +
                        "\"email\": \"%s\", \"rol_id\": %d}",
                usuario.cc,
                usuario.primer_nombre,
                usuario.segundo_nombre,
                usuario.primer_apellido,
                usuario.segundo_apellido,
                usuario.email,
                usuario.rol_id
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
        private Long cc;
        private String primer_nombre;
        private String segundo_nombre;
        private String primer_apellido;
        private String segundo_apellido;
        private String email;
        private Integer rol_id;

        public Usuario(Long cc, String primer_nombre, String segundo_nombre,
                       String primer_apellido, String segundo_apellido,
                       String email, Integer rol_id) {
            this.cc = cc;
            this.primer_nombre = primer_nombre;
            this.segundo_nombre = segundo_nombre;
            this.primer_apellido = primer_apellido;
            this.segundo_apellido = segundo_apellido;
            this.email = email;
            this.rol_id = rol_id;

        }
        // Getters
        public long getCc() { return cc; }
        public String getPrimerNombre() { return primer_nombre; }
        public String getSegundoNombre() { return segundo_nombre; }
        public String getPrimerApellido() { return primer_apellido; }
        public String getSegundoApellido() { return segundo_apellido; }
        public String getEmail() { return email; }
        public int getRolId() { return rol_id; }

    }

}
