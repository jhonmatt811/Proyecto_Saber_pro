package com.java.fx;
//controlador de la interfaz general de usuarios
import com.java.fx.Usuarios_y_Roles.CambiarContrasenaController;
import com.java.fx.Usuarios_y_Roles.PermisosRoles;
import com.java.fx.Usuarios_y_Roles.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ControllerMain {

    @Autowired
    private ApplicationContext context;

    @FXML
    private BorderPane mainPane;

    @FXML
    private Label contentLabel;

    @FXML
    private Button btnInicio;

    @FXML
    private Button btnResultados;

    @FXML
    private Button btnResultadosIcfes;

    @FXML
    private Button btnAccMejora;

    @FXML
    private Button btnUsuariosRoles;

    @FXML
    private Button btnConfiguracion;

    @FXML
    private Button btnSalir;


    @FXML
    public void initialize() {

        resetButtonStyles();
        configurarInterfazPorRol();

    }

    private void configurarInterfazPorRol() {
        PermisosRoles permisos = new PermisosRoles(Sesion.rol_id);

        btnResultados.setVisible(permisos.tienePermiso("resultados"));
        //btnCrearUsuarios.setVisible(permisos.tienePermiso("crearUsuarios"));
        btnAccMejora.setVisible(permisos.tienePermiso("accMejora"));
        btnUsuariosRoles.setVisible(permisos.tienePermiso("usuariosRoles"));
        btnConfiguracion.setVisible(permisos.tienePermiso("configuracion"));
        btnSalir.setVisible(permisos.tienePermiso("salir"));
        btnResultadosIcfes.setVisible(permisos.tienePermiso("resultadosIcfes"));
    }



    @FXML
    public void goResultados() {
        resetButtonStyles();
        btnResultados.getStyleClass().add("boton-rojo");
        loadCenterView("/Resultados.fxml");
    }

    @FXML
    public void goResultadosIcfes() {
        resetButtonStyles();
        btnResultadosIcfes.getStyleClass().add("boton-rojo");
        loadCenterView("/resultados-icfes.fxml");
    }

    @FXML
    public void goAccMejora() {
        resetButtonStyles();
        btnAccMejora.getStyleClass().add("boton-rojo");
        loadCenterView("/AccionesDeMejora.fxml");
    }

    @FXML
    public void goUsuariosRoles() {
        resetButtonStyles();
        btnUsuariosRoles.getStyleClass().add("boton-rojo");
        loadCenterView("/UsuariosRoles.fxml");
    }

    @FXML
    public void goConfiguracion() {
        try {
            resetButtonStyles();
            btnConfiguracion.getStyleClass().add("boton-rojo");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Configuracion_contrasena.fxml"));
            Parent root = loader.load();

            // Obtener el controlador del FXML cargado
            CambiarContrasenaController controller = loader.getController();

            // Pasar el callback que redirige al login
            controller.setOnPasswordChangedCallback(() -> volverLogin());

            // Mostrar la vista en el centro de la aplicación
            mainPane.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void resetButtonStyles() {
        btnResultados.getStyleClass().remove("boton-rojo");
        btnResultadosIcfes.getStyleClass().remove("boton-rojo");
        //btnCrearUsuarios.getStyleClass().remove("boton-rojo");
        btnAccMejora.getStyleClass().remove("boton-rojo");
        btnUsuariosRoles.getStyleClass().remove("boton-rojo");
        btnConfiguracion.getStyleClass().remove("boton-rojo");
        btnUsuariosRoles.getStyleClass().remove("boton-rojo");

    }

    private void loadCenterView(String resource) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            // Usar Spring para instanciar el controller
            loader.setControllerFactory(context::getBean);
            Node view = loader.load();
            mainPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void volverLogin() {
        try {
            // Limpio token de sesión
            Sesion.jwtToken = null;

            // Cargo un login NUEVO
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/MainLogin.fxml")
            );
            loader.setControllerFactory(context::getBean);
            Parent loginRoot = loader.load();

            // Nuevo Stage
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(loginRoot));
            loginStage.getIcons().add(new Image(getClass().getResource("/img/images.png").toExternalForm()));

            loginStage.setTitle("Login");
            loginStage.show();

            // Cierro el principal
            ((Stage) mainPane.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}