package com.java.fx;

import com.java.fx.Usuarios_y_Roles.Sesion;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;
import java.util.ResourceBundle;
import java.io.IOException;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ControllerLogin implements Initializable {

    @FXML private TextField emailField;
    @FXML private TextField visiblePasswordField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox showPasswordCheck;

    @Autowired
    private ApplicationContext context;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    private void onLoginClick() {
        String email = emailField.getText();
        String password = showPasswordCheck.isSelected() ?
                visiblePasswordField.getText() : passwordField.getText();

        if (!validateFields(email, password)) return;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(" http://ec2-3-149-24-90.us-east-2.compute.amazonaws.com/usuarios/inicio-sesion"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password)))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> handleResponse(response))
                .exceptionally(ex -> {
                    showAlert("Error de conexión", "No se pudo conectar con el servidor.");
                    return null;
                });
    }

    private boolean validateFields(String email, String password) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Campos vacíos", "Debe ingresar correo y contraseña.");
            return false;
        }

        if (!isValidEmail(email)) {
            showAlert("Correo inválido", "Formato de correo inválido. Ejemplo: usuario@dominio.com");
            return false;
        }

        return true;
    }

    private void handleResponse(HttpResponse<String> response) {
        Platform.runLater(() -> {
            try {
                if (response.statusCode() == 200 || response.statusCode() ==202) {
                    JSONObject jsonResponse = new JSONObject(response.body());
                    handleSuccessfulLogin(jsonResponse);
                } else {
                    showAlert("Error de autenticación",
                            "Credenciales inválidas , Revise que Correo y Contraseña esten correctamente  ");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showAlert("Error de formato", "Respuesta del servidor inválida");
            }
        });
    }

    private void handleSuccessfulLogin(JSONObject jsonResponse) throws JSONException {
        String token = jsonResponse.getString("token");
        Sesion.jwtToken = token;

        // Decodificar payload del JWT
        String[] chunks = token.split("\\.");
        JSONObject payload = new JSONObject(new String(Base64.getDecoder().decode(chunks[1])));

        Sesion.rol_id = payload.getString("role");
        Sesion.emailUsuario = emailField.getText();

        // Guardar la contraseña usada en el login
        String passwordIngresada = showPasswordCheck.isSelected()
                ? visiblePasswordField.getText()
                : passwordField.getText();
        Sesion.passwordUsuario = passwordIngresada;

        loadMainWindow();
        clearFields();
    }

    private void loadMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
            loader.setControllerFactory(context::getBean); // Integración con Spring

            BorderPane root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gestión De Resultados Saber Pro");
            stage.getIcons().add(new Image("/img/images.png"));
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            // Cerrar ventana actual
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error de interfaz", "No se pudo cargar la ventana principal");
        }
    }

    private void clearFields() {
        emailField.clear();
        passwordField.clear();
        visiblePasswordField.clear();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}(\\.[a-zA-Z]{2,})?$");
    }


    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    @FXML
    private void onTogglePasswordVisibility() {
        if (showPasswordCheck.isSelected()) {
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);

            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
        }
    }

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private void onForgotPasswordClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/olvido_contraseña.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la ventana de recuperación
        olvidoContraseñaController controller = loader.getController();

        // Pasar la ventana actual (login) y la nueva (recovery)
        Stage loginStage = (Stage) forgotPasswordLink.getScene().getWindow();
        Stage recoveryStage = new Stage();

        controller.setLoginStage(loginStage);
        controller.setRecoveryStage(recoveryStage);

        recoveryStage.setTitle("Recuperar contraseña");

        Image icon = new Image(getClass().getResourceAsStream("/img/images.png"));
        recoveryStage.getIcons().add(icon);
        recoveryStage.setScene(new Scene(root));
        recoveryStage.initModality(Modality.WINDOW_MODAL);
        recoveryStage.initOwner(loginStage); // hace que el login quede bloqueado mientras tanto
        recoveryStage.show();

    }


}