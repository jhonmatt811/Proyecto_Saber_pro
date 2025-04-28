package com.java.fx;
//controlador de la interfaz general de usuarios
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Controller {

    @FXML
    private BorderPane mainPane;

    @FXML
    private Label contentLabel;

    @FXML
    private Button btnInicio;

    @FXML
    private Button btnResultados;

    @FXML
    private Button btnCrearUsuarios;

    @FXML
    private Button btnAccMejora;

    @FXML
    private Button btnUsuariosRoles;

    @FXML
    private Button btnConfiguracion;

    @FXML
    public void initialize() {

        resetButtonStyles();
    }
    @FXML
    public void goInicio() {
        resetButtonStyles();
        btnInicio.getStyleClass().add("boton-rojo");
        loadCenterView("/Inicio.fxml");
    }

    @FXML
    public void goResultados() {
        resetButtonStyles();
        btnResultados.getStyleClass().add("boton-rojo");
        loadCenterView("/Resultados.fxml");
    }

    @FXML
    public void goCrearUsuarios() {
        resetButtonStyles();
        btnCrearUsuarios.getStyleClass().add("boton-rojo");
        loadCenterView("/CrearUsuarios.fxml");
    }

    @FXML
    public void goAccMejora() {
        resetButtonStyles();
        btnAccMejora.getStyleClass().add("boton-rojo");

        Label newLabel = new Label("Estás en Acciones de Mejora");
        newLabel.getStyleClass().add("content-label");

        mainPane.setCenter(null);
        mainPane.setCenter(newLabel);
    }


    @FXML
    public void goUsuariosRoles() {
        resetButtonStyles();
        btnAccMejora.getStyleClass().add("boton-rojo");

        Label newLabel = new Label("Estás en usuarios y roles");
        newLabel.getStyleClass().add("content-label");

        mainPane.setCenter(null);
        mainPane.setCenter(newLabel);
    }

    @FXML
    public void goConfiguracion() {
        resetButtonStyles();
        btnAccMejora.getStyleClass().add("boton-rojo");

        Label newLabel = new Label("Accediendo a configuracion");
        newLabel.getStyleClass().add("content-label");

        mainPane.setCenter(null);
        mainPane.setCenter(newLabel);
    }

    private void resetButtonStyles() {
        btnInicio.getStyleClass().remove("boton-rojo");
        btnResultados.getStyleClass().remove("boton-rojo");
        btnCrearUsuarios.getStyleClass().remove("boton-rojo");
        btnAccMejora.getStyleClass().remove("boton-rojo");
        btnUsuariosRoles.getStyleClass().remove("boton-rojo");
        btnConfiguracion.getStyleClass().remove("boton-rojo");
        btnUsuariosRoles.getStyleClass().remove("boton-rojo");

    }

    private void loadCenterView(String resource) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Node view = loader.load();
            mainPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
