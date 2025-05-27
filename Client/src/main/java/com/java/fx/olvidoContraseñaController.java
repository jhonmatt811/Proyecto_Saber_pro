package com.java.fx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.*;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)

public class olvidoContraseñaController {

    @FXML private TextField emailField;
    @FXML private TextField codeField;
    @FXML private PasswordField newPasswordField;
    @FXML private Button sendEmailButton;
    @FXML private Button confirmButton;
    @FXML private Label statusLabel;
    @FXML private BorderPane mainPane;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField visiblenewPasswordField;
    @FXML private TextField visibleConfirmPasswordField;
    @FXML private CheckBox showPasswordCheckForgot;

    @Autowired private ApplicationContext context;

    @Setter private Stage loginStage;
    @Setter private Stage recoveryStage;

    private final ApiService apiService = new ApiService();
    private String storedEmail; // Guarda el correo

    @FXML
    private VBox codeSection;

    @FXML
    public void initialize() {
        visiblenewPasswordField.textProperty().bindBidirectional(newPasswordField.textProperty());
        visibleConfirmPasswordField.textProperty().bindBidirectional(confirmPasswordField.textProperty());
    }

    @FXML
    private void handleSendEmail() {
        storedEmail = emailField.getText().trim();

        // Validación básica
        if (storedEmail.isEmpty()) {
            updateStatus("El correo es obligatorio", "red");
            return;
        }

        if (!storedEmail.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}(\\.[a-zA-Z]{2,})?$")) {
            updateStatus("Correo con formato inválido", "red");
            return;
        }

        try {
            boolean success = apiService.sendPasswordRecoveryEmail(storedEmail);

            if (success) {
                updateStatus("Correo enviado exitosamente", "green");
                showCodeSection(); // Método modificado
                disableEmailSection();
            } else {
                // Si el servicio retorna false a pesar del envío
                updateStatus("Error: El servidor no pudo procesar la solicitud", "red");
                enableEmailSection();
            }
        } catch (Exception e) {
            // Captura errores de conexión o excepciones no controladas
            updateStatus("Error de conexión: " + e.getMessage(), "red");
            enableEmailSection();
        }
    }

    // Métodos auxiliares
    private void updateStatus(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setTextFill(color.equals("green")
                ? javafx.scene.paint.Color.GREEN
                : javafx.scene.paint.Color.RED);
    }

    private void showCodeSection() {
        codeSection.setVisible(true);
        codeSection.setManaged(true);
    }

    private void disableEmailSection() {
        sendEmailButton.setDisable(true);
        emailField.setDisable(true);
    }

    private void enableEmailSection() {
        sendEmailButton.setDisable(false);
        emailField.setDisable(false);
        codeSection.setVisible(false);
        codeSection.setManaged(false);
    }


    @FXML
    private void handleConfirmNewPassword() {
        String code = codeField.getText().trim();
        String newPassword = newPasswordField.isVisible() ? newPasswordField.getText().trim() : visiblenewPasswordField.getText().trim();
        String confirmarPass = confirmPasswordField.isVisible() ? confirmPasswordField.getText().trim() : visibleConfirmPasswordField.getText().trim();

        // Validar campos vacíos
        if (code.isEmpty() || newPassword.isEmpty() || confirmarPass.isEmpty()) {
            statusLabel.setText("Llena todos los campos.");
            return;
        }

        // Validar contraseña segura
        if (!esContrasenaSegura(newPassword)) {
            statusLabel.setText("La contraseña debe tener al menos 6 caracteres, incluir números y símbolos.");
            return;
        }

        // Validar que las contraseñas coincidan
        if (!newPassword.equals(confirmarPass)) {
            statusLabel.setText("Las contraseñas no coinciden.");
            return;
        }

        ApiService apiService = new ApiService();
        try {
            boolean success = apiService.resetPassword(storedEmail, newPassword, code);

            if (success) {
                statusLabel.setStyle("-fx-text-fill: green;");
                statusLabel.setText("Contraseña actualizada correctamente.");

                // Cerrar esta ventana y volver al login
                Platform.runLater(() -> {
                    confirmButton.setDisable(true);
                    newPasswordField.setDisable(true);
                    visiblenewPasswordField.setDisable(true);
                    confirmPasswordField.setDisable(true);
                    visibleConfirmPasswordField.setDisable(true);
                    codeField.setDisable(true);

                    recoveryStage.close();
                    loginStage.show();
                });
            } else {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Error al actualizar la contraseña.");
            }
        } catch (RuntimeException e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Error de conexión.");
            e.printStackTrace();
        }
    }
    private boolean esContrasenaSegura(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[!@#$%^&*._-])[A-Za-z0-9!@#$%^&*._-]{6,}$");
    }

    @FXML
    private void onTogglePasswordVisibility() {
        boolean show = showPasswordCheckForgot.isSelected();

        // Nueva contraseña
        if (show) {
            visiblenewPasswordField.setText(newPasswordField.getText());
            visiblenewPasswordField.setVisible(true);
            visiblenewPasswordField.setManaged(true);

            newPasswordField.setVisible(false);
            newPasswordField.setManaged(false);
        } else {
            newPasswordField.setText(visiblenewPasswordField.getText());
            newPasswordField.setVisible(true);
            newPasswordField.setManaged(true);

            visiblenewPasswordField.setVisible(false);
            visiblenewPasswordField.setManaged(false);
        }

        // Confirmar contraseña
        if (show) {
            visibleConfirmPasswordField.setText(confirmPasswordField.getText());
            visibleConfirmPasswordField.setVisible(true);
            visibleConfirmPasswordField.setManaged(true);

            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
        } else {
            confirmPasswordField.setText(visibleConfirmPasswordField.getText());
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);

            visibleConfirmPasswordField.setVisible(false);
            visibleConfirmPasswordField.setManaged(false);
        }
    }

}

