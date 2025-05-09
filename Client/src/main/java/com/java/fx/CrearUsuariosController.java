package com.java.fx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;


public class CrearUsuariosController {


    private static final String API_URL = "http://localhost:8080/admin/personas";


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
                apellidoField.getText().isEmpty() || correoField.getText().isEmpty()){
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
            String jsonPersona = convertirAJson(usuario);
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();

            // 1) Creo la persona
            HttpRequest reqPersona = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/admin/personas"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + Sesion.jwtToken)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPersona))
                    .build();

            client.sendAsync(reqPersona, HttpResponse.BodyHandlers.ofString())
                    .thenCompose(respPersona -> {
                        if (respPersona.statusCode() == 201 || respPersona.statusCode() == 200) {
                            // 2) Recupero el JSON completo de la persona recién creada
                            String bodyPersonaCreada = respPersona.body();

                            // 3) Construyo el JSON de usuario anidando la persona
                            String jsonUsuario = String.format(
                                    "{\"person\": %s, \"rol_id\": %d}",
                                    bodyPersonaCreada,
                                    usuario.rol_id
                            );

                            // 4) Preparo el segundo POST a /admin/usuarios
                            HttpRequest reqUsuario = HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8080/admin/usuarios"))
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + Sesion.jwtToken)
                                    .POST(HttpRequest.BodyPublishers.ofString(jsonUsuario))
                                    .build();

                            // 5) Devuelvo el CompletableFuture del segundo request
                            return client.sendAsync(reqUsuario, HttpResponse.BodyHandlers.ofString());
                        } else {
                            // si persona no se creó, corto la cadena devolviendo un Future fallido
                            return CompletableFuture.<HttpResponse<String>>failedFuture(
                                    new RuntimeException("Error creando persona: " + respPersona.statusCode())
                            );
                        }
                    })
                    .thenAccept(respUsuario -> {
                        // 6) Manejo la respuesta final de /admin/usuarios
                        if (respUsuario.statusCode() == 201 || respUsuario.statusCode() == 200) {
                            Platform.runLater(() -> {
                                mostrarAlerta("Éxito", "Usuario creado correctamente", AlertType.INFORMATION);
                                limpiarFormulario();
                            });
                        } else {
                            Platform.runLater(() -> {
                                mostrarAlerta("Error",
                                        "Error al crear usuario:\nCódigo: " + respUsuario.statusCode() +
                                                "\n" + respUsuario.body(),
                                        AlertType.ERROR);
                            });
                        }
                    })
                    .exceptionally(e -> {
                        Platform.runLater(() -> {
                            mostrarAlerta("Error de conexión", e.getMessage(), AlertType.ERROR);
                        });
                        return null;
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

/**
import org.springframework.http.*;
        import org.springframework.web.client.RestTemplate;
import java.util.*;

public class ApiConsumer {

    private static final String TOKEN = "Bearer <tu_token>";
    private static final String PERSON_URL = "http://localhost:8080/admin/personas";
    private static final String USER_URL = "http://localhost:8080/admin/usuarios";

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        // Paso 1: Crear Persona
        Map<String, Object> personaRequest = new HashMap<>();
        personaRequest.put("cc", "12345675200");
        personaRequest.put("primer_nombre", "Juan");
        personaRequest.put("segundo_nombre", "Carlos");
        personaRequest.put("primer_apellido", "Pérez");
        personaRequest.put("segundo_apellido", "Gómez");
        personaRequest.put("email", "juanacob2on11.4@gmail.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", TOKEN);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(personaRequest, headers);

        ResponseEntity<Map> response = restTemplate.exchange(PERSON_URL, HttpMethod.POST, entity, Map.class);
        String personaId = (String) response.getBody().get("id");

        // Paso 2: Crear Usuario
        Map<String, Object> personData = new HashMap<>(personaRequest);
        personData.put("id", personaId);

        Map<String, Object> userRequest = new HashMap<>();
        userRequest.put("person", personData);
        userRequest.put("rol_id", 2);

        HttpEntity<Map<String, Object>> userEntity = new HttpEntity<>(userRequest, headers);

        ResponseEntity<Map> userResponse = restTemplate.exchange(USER_URL, HttpMethod.POST, userEntity, Map.class);

        System.out.println("Usuario creado: " + userResponse.getBody());
    }
}*/