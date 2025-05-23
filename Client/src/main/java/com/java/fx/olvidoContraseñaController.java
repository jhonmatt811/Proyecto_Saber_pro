package com.java.fx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class olvidoContraseñaController {

    @FXML private TextField emailField;
    @FXML private TextField codeField;
    @FXML private PasswordField newPasswordField;
    @FXML private Button sendEmailButton;
    @FXML private Button confirmButton;
    @FXML private Label statusLabel;

    private String storedEmail; // Guarda el correo para el segundo paso

    @FXML
    private void handleSendEmail() {
        storedEmail = emailField.getText().trim();

        String email = emailField.getText();

        if (email == null || email.isEmpty()) {
            statusLabel.setText("El correo es obligatorio");
            return;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8080/usuarios/contraseña/olvidado";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String json = "{\"email\": \"" + email + "\"}";
            HttpEntity<String> request = new HttpEntity<>(json, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                statusLabel.setText("Correo enviado exitosamente");
                statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            } else {
                statusLabel.setText("Error al enviar el correo");
            }

        } catch (Exception e) {
            statusLabel.setText("No se pudo enviar el correo");
            e.printStackTrace();
        }


        // Mostrar los campos para ingresar código y nueva contraseña
        codeField.setVisible(true);
        codeField.setManaged(true);
        newPasswordField.setVisible(true);
        newPasswordField.setManaged(true);
        confirmButton.setVisible(true);
        confirmButton.setManaged(true);

        // Deshabilita el envío de más correos
        sendEmailButton.setDisable(true);
        emailField.setDisable(true);


    }

    @FXML
    private void handleConfirmNewPassword() {
        String code = codeField.getText().trim();
        String newPassword = newPasswordField.getText().trim();

        if (code.isEmpty() || newPassword.isEmpty()) {
            statusLabel.setText("Llena todos los campos.");
            return;
        }

        try {
            URL url = new URL("http://localhost:8080/usuarios/contraseña");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = String.format(
                    "{\"correo\":\"%s\",\"nuevaContraseña\":\"%s\", \"codigo\":\"%s\"}",
                    storedEmail, newPassword ,code
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                statusLabel.setText("Contraseña actualizada correctamente.");
                Platform.runLater(() -> {
                    confirmButton.setDisable(true);
                    newPasswordField.setDisable(true);
                    codeField.setDisable(true);
                });
            } else {
                statusLabel.setText("Error al actualizar la contraseña.");
                InputStream errorStream = conn.getErrorStream();
                if (errorStream != null) {
                    String errorMessage = new BufferedReader(new InputStreamReader(errorStream))
                            .lines().collect(Collectors.joining("\n"));
                    System.out.println("Error del servidor: " + errorMessage);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Fallo al conectar con el servidor.");
        }
    }
}

