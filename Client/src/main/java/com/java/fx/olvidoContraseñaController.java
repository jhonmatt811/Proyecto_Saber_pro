package com.java.fx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
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
    @Autowired private ApplicationContext context;

    @FXML private TextField visiblenewPasswordField;
    @FXML private CheckBox showPasswordCheckForgot;

    @Setter private Stage loginStage;
    @Setter private Stage recoveryStage;

    private final ApiService apiService = new ApiService();
    private String storedEmail; // Guarda el correo para el segundo paso

    @FXML
    private void handleSendEmail() {
        storedEmail = emailField.getText().trim();

        if (storedEmail == null || storedEmail.isEmpty()) {
            statusLabel.setText("El correo es obligatorio");
            return;
        }

        // Validar formato de correo antes de enviar
        if (!storedEmail.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            statusLabel.setText("Correo con formato inválido");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        boolean success = apiService.sendPasswordRecoveryEmail(storedEmail);

        if (success) {
            statusLabel.setText("Correo enviado exitosamente");
            statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

            // Mostrar los campos para ingresar código y nueva contraseña
            codeField.setVisible(true);
            codeField.setManaged(true);
            newPasswordField.setVisible(true);
            newPasswordField.setManaged(true);
            confirmButton.setVisible(true);
            confirmButton.setManaged(true);
            showPasswordCheckForgot.setVisible(true);
            showPasswordCheckForgot.setManaged(true);


            // Deshabilita el envío de más correos SOLO si fue exitoso
            sendEmailButton.setDisable(true);
            emailField.setDisable(true);
        } else {
            statusLabel.setText("Error al enviar el correo");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);

            // Mantén el campo editable si falla
            sendEmailButton.setDisable(false);
            emailField.setDisable(false);
            showPasswordCheckForgot.setVisible(false);
            showPasswordCheckForgot.setManaged(false);

        }
    }


    @FXML
    private void handleConfirmNewPassword() {
        String code = codeField.getText().trim();
        String newPassword = newPasswordField.getText().trim();

        if (code.isEmpty() || newPassword.isEmpty()) {
            statusLabel.setText("Llena todos los campos.");
            return;
        }

        ApiService apiService = new ApiService();
        try {
            boolean success = apiService.resetPassword(storedEmail, newPassword, code);

            if (success) {
                statusLabel.setText("Contraseña actualizada correctamente.");

                // Cerrar esta ventana y volver al login
                Platform.runLater(() -> {
                    confirmButton.setDisable(true);
                    newPasswordField.setDisable(true);
                    codeField.setDisable(true);

                    recoveryStage.close(); // <-- asegurarte que tienes esta variable seteada
                    loginStage.show();     // <-- mostrar login otra vez
                });
            } else {
                statusLabel.setText("Error al actualizar la contraseña." );
            }
        } catch (IOException e) {
            statusLabel.setText("Error de conexión.");
            e.printStackTrace();
        }
    }
    @FXML
    private void  onTogglePasswordVisibility(){
        if (showPasswordCheckForgot.isSelected()) {
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
    }
}

