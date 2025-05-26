package com.java.fx.Usuarios_y_Roles;

import com.java.fx.ApiService;
import com.java.fx.ControllerMain;
import com.java.fx.ControllerLogin;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

@Component
public class CambiarContrasenaController {

    @FXML private TextField txtCodigo;
    @FXML private PasswordField txtNuevaContrasena;
    @FXML private PasswordField txtConfirmarContrasena;
    @FXML private Label lblMensaje;
    @FXML private TextField visibleNuevaContrasena;
    @FXML private TextField visibleConfirmarContrasena;
    @FXML private CheckBox showPasswordCheck;
    @FXML private TextField txtContrasenaActual;



    @FXML
    private void initialize() {
        // Vincular los campos de texto
        visibleNuevaContrasena.textProperty().bindBidirectional(txtNuevaContrasena.textProperty());
        visibleConfirmarContrasena.textProperty().bindBidirectional(txtConfirmarContrasena.textProperty());
    }

    private final ApiService apiService = new ApiService();
    Sesion sesion = new Sesion();

    @FXML
    private void onTogglePasswordVisibility() {
        boolean show = showPasswordCheck.isSelected();

        // Alternar visibilidad de los campos
        txtNuevaContrasena.setVisible(!show);
        txtNuevaContrasena.setManaged(!show);
        visibleNuevaContrasena.setVisible(show);
        visibleNuevaContrasena.setManaged(show);

        txtConfirmarContrasena.setVisible(!show);
        txtConfirmarContrasena.setManaged(!show);
        visibleConfirmarContrasena.setVisible(show);
        visibleConfirmarContrasena.setManaged(show);

        txtConfirmarContrasena.setVisible(!show);
        txtConfirmarContrasena.setManaged(!show);
        visibleConfirmarContrasena.setVisible(show);
        visibleConfirmarContrasena.setManaged(show);
    }

    @FXML
    public void enviarCodigo() {
        if ( sesion.emailUsuario== null ||  sesion.emailUsuario.isEmpty() ) {
            mostrarMensaje("No se puede enviar código: email desconocido."+  sesion.emailUsuario);
            return;
        }

        new Thread(() -> {
            try {
                apiService.sendPasswordRecoveryEmail( sesion.emailUsuario);
                Platform.runLater(() -> mostrarMensaje("Código enviado al correo."+ sesion.emailUsuario));
            } catch (Exception e) {
                Platform.runLater(() -> mostrarMensaje("Error al enviar código: " + e.getMessage()));
            }
        }).start();
    }

    private Runnable onPasswordChangedCallback;

    public void setOnPasswordChangedCallback(Runnable callback) {
        this.onPasswordChangedCallback = callback;
    }


    ControllerMain controllerMain = new ControllerMain();

    @FXML
    public void cambiarContrasena() {
        String codigo = txtCodigo.getText();
        String actualPass = txtContrasenaActual.getText();
        String nuevaPass = txtNuevaContrasena.getText();
        String confirmarPass = txtConfirmarContrasena.getText();

        // Validar campos vacíos
        if (codigo.isBlank() || actualPass.isBlank() || nuevaPass.isBlank() || confirmarPass.isBlank()) {
            mostrarMensaje("Todos los campos son obligatorios.");
            return;
        }

        // Validar contraseña actual
        if (!Sesion.passwordUsuario.equals(actualPass)) {
            mostrarMensaje("La contraseña actual es incorrecta.");
            return;
        }

        // Validar nueva contraseña segura
        if (!esContrasenaSegura(nuevaPass)) {
            mostrarMensaje("La contraseña debe tener al menos 6 caracteres, incluir números y símbolos.");
            System.out.println(nuevaPass);
            return;
        }

        // Validar que la nueva contraseña sea diferente
        if (actualPass.equals(nuevaPass)) {
            mostrarMensaje("La nueva contraseña no puede ser igual a la anterior.");
            return;
        }

        // Confirmar que ambas contraseñas nuevas coincidan
        if (!nuevaPass.equals(confirmarPass)) {
            mostrarMensaje("Las contraseñas no coinciden.");
            return;
        }

        // Hilo para no bloquear la interfaz
        new Thread(() -> {
            try {
                apiService.resetPassword(Sesion.emailUsuario, nuevaPass, codigo);

                // Actualizar la contraseña en memoria
                Sesion.passwordUsuario = nuevaPass;

                Platform.runLater(() -> {
                    mostrarAlertaYVolverAlLogin("Contraseña actualizada correctamente.");
                });

            } catch (Exception e) {
                Platform.runLater(() ->
                        mostrarMensaje("Error al cambiar contraseña: Verifique los datos e intente nuevamente."));
            }
        }).start();
    }


    private boolean esContrasenaSegura(String password) {
        // Permite: letras, números y símbolos comunes (!@#$%^&*._-) con al menos 1 número y 1 símbolo
        return password.matches("^(?=.*[0-9])(?=.*[!@#$%^&*._-])[A-Za-z0-9!@#$%^&*._-]{6,}$");

    }

    private void mostrarAlertaYVolverAlLogin(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Cambio de contraseña");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait(); // Espera hasta que el usuario presione "Aceptar"

        if (onPasswordChangedCallback != null) {
            onPasswordChangedCallback.run();
        }
    }



    private void mostrarMensaje(String mensaje) {
        lblMensaje.setText(mensaje);
        // Auto-ocultar mensaje después de 5 segundos
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(e -> lblMensaje.setText(""));
        pause.play();
    }

    public String getEmailUsuarioActual() {
        return  sesion.emailUsuario;
    }
}

