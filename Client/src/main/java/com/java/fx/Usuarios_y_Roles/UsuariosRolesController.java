package com.java.fx.Usuarios_y_Roles;

import com.java.fx.ApiService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class UsuariosRolesController {
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private ComboBox<Rol> comboNuevoRol;
    @FXML private Button btnCambiarRol;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colSecNombre;
    @FXML private TableColumn<Usuario, String> colApellido;
    @FXML private TableColumn<Usuario, String> colSecApellido;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colNombreRol;

    private final ApiService apiService = new ApiService();

    @FXML
    public void initialize() {
        cargarUsuarios();
        configurarBoton();
        cargarRoles();

        comboNuevoRol.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Rol rol, boolean empty) {
                super.updateItem(rol, empty);
                setText(empty || rol == null ? null : rol.getNombre());
            }
        });

        comboNuevoRol.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Rol rol, boolean empty) {
                super.updateItem(rol, empty);
                setText(empty || rol == null ? null : rol.getNombre());
            }
        });


        colNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersona().getPrimer_nombre()));

        colSecNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersona().getSegundo_nombre()));

        colApellido.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersona().getPrimer_apellido()));

        colSecApellido.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersona().getSegundo_apellido()));

        colCorreo.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPersona().getEmail()));

        colNombreRol.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getRol() != null ? cellData.getValue().getRol().getNombre() : "Sin Rol"
                ));
    }

    private void configurarBoton() {
        btnCambiarRol.setOnAction(e -> {
            Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
            Rol nuevoRol = comboNuevoRol.getValue();

            if (seleccionado != null && nuevoRol != null) {
                apiService.cambiarRol(seleccionado.getId(), nuevoRol.getId());
                cargarUsuarios();
            } else {
                mostrarAlerta("Error", "Selecciona un usuario y un nuevo rol.");
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cargarUsuarios() {
        new Thread(() -> {
            try {
                List<Usuario> usuarios = apiService.obtenerUsuarios();
                System.out.println("Usuarios recibidos: " + usuarios.size());


                Platform.runLater(() ->
                        tablaUsuarios.setItems(FXCollections.observableArrayList(usuarios)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void cargarRoles() {
        /*new Thread(() -> {
            try {
                List<Rol> roles = apiService.obtenerRoles();
                Platform.runLater(() -> {
                    comboNuevoRol.setItems(FXCollections.observableArrayList(roles));
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> mostrarAlerta("Error", "No se pudieron cargar los roles."));
            }
        }).start();*/
    }


}