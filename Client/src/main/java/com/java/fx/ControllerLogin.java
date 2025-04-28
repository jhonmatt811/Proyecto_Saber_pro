package com.java.fx;

// controlador de la logica del login
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.EventObject;
import java.util.ResourceBundle;

@Component
public class ControllerLogin implements Initializable {


    @FXML
    private TextField emailField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox showPasswordCheck;

    @Autowired
    private ApplicationContext context;

    @FXML
    private void onLoginClick() throws IOException {
        String email = emailField.getText();
        String password = showPasswordCheck.isSelected() ? visiblePasswordField.getText() : passwordField.getText();
        Alert alert = new Alert(Alert.AlertType.ERROR);

        if (email.isEmpty() || password.isEmpty()) {
            alert.setTitle("Campos vacíos");
            alert.setHeaderText(null);
            alert.setContentText("Debe ingresar correo y contraseña.");
            alert.showAndWait();
            return;
        }

        if (!isValidEmail(email)) {
            alert.setTitle("Correo inválido");
            alert.setHeaderText(null);
            alert.setContentText("Correo inválido. Intente con uno como ejemplo@gmail.com");
            alert.showAndWait();
            return;
        }

        // Si todo está bien
        System.out.println("Correo válido: " + email);
        System.out.println("Contraseña: " + password);

        emailField.clear();
        passwordField.clear();
        visiblePasswordField.clear();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        BorderPane root = loader.load();

        // Crear una nueva ventana (Stage)
        Stage stage = new Stage();

        stage.setTitle("Gestión De Resultados Saber Pro");
        stage.getIcons().add(new Image("/img/images.png"));

        Scene scene = new Scene(root);
        stage.setScene(scene);

        // Hacer que la ventana sea de pantalla completa
        stage.setMaximized(true); // O stage.setFullScreen(true); si quieres forzar modo cine

        stage.show();

        StageManager.getStage().close();

        // Actualizamos el nuevo Stage
        StageManager.setStage(stage);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.(com|net|org|edu|gov|co)$");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    public void onTogglePasswordVisibility() {
        if (showPasswordCheck.isSelected()) {
            // Mostrar contraseña como texto
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            // Ocultar contraseña
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);

            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
        }
    }
    //la clase que se encarga de que al cambiar de interfaz una se cierre y se abra la siguiente
    public class StageManager {
        private static Stage currentStage;

        public static void setStage(Stage stage) {
            currentStage = stage;
        }

        public static Stage getStage() {
            return currentStage;
        }
    }



}




