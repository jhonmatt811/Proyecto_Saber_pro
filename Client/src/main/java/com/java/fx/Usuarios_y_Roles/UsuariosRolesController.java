package com.java.fx.Usuarios_y_Roles;

import com.java.fx.ApiService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
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
    @FXML private Button btnCrearUsuarios;
    @FXML private BorderPane mainPane;

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
                new Thread(() -> {
                    try {
                        apiService.cambiarRol(seleccionado.getId(), nuevoRol.getId());

                        // Actualizamos el objeto localmente
                        Platform.runLater(() -> {
                            seleccionado.setRol(nuevoRol); // Cambio local
                            tablaUsuarios.refresh();       // Refresca visualmente
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Platform.runLater(() ->
                                mostrarAlerta("Error", "Error al cambiar el rol: " + ex.getMessage()));
                    }
                }).start();
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

    public void cargarUsuarios() {
        new Thread(() -> {
            try {
                List<Usuario> usuarios = apiService.obtenerUsuarios();
                Platform.runLater(() ->
                        tablaUsuarios.setItems(FXCollections.observableArrayList(usuarios))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void cargarRoles() {
        new Thread(() -> {
            try {
                List<Rol> roles = apiService.obtenerRoles();
                Platform.runLater(() -> {
                    comboNuevoRol.setItems(FXCollections.observableArrayList(roles));
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> mostrarAlerta("Error", "No se pudieron cargar los roles."));
            }
        }).start();
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

    public TableView<Usuario> getTablaUsuarios() {
        return tablaUsuarios;

    }

    public void setTablaUsuarios(TableView<Usuario> tablaUsuarios) {
        this.tablaUsuarios = tablaUsuarios;
    }

    public ComboBox<Rol> getComboNuevoRol() {
        return comboNuevoRol;
    }

    public void setComboNuevoRol(ComboBox<Rol> comboNuevoRol) {
        this.comboNuevoRol = comboNuevoRol;
    }

    public BorderPane getMainPane() {
        return mainPane;
    }

    public void setMainPane(BorderPane mainPane) {
        this.mainPane = mainPane;
    }

    public Button getBtnCambiarRol() {
        return btnCambiarRol;
    }

    public void setBtnCambiarRol(Button btnCambiarRol) {
        this.btnCambiarRol = btnCambiarRol;
    }

    @FXML
    public void goCrearUsuarios() {
        if (btnCrearUsuarios.getOnAction() != null) {
            try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrearUsuarios.fxml"));
            Node view = loader.load();

            // Obtener el controlador del formulario
            CrearUsuariosController controller = loader.getController();
            controller.setUsuariosRolesController(this); // le pasamos esta clase

            mainPane.setCenter(view);
            tablaUsuarios.setVisible(false);
            comboNuevoRol.setVisible(false);
            btnCrearUsuarios.setVisible(false);
            btnCambiarRol.setVisible(false);
        } catch (IOException e) {
            e.printStackTrace();
        }}
    }


    public Button getBtnCrearUsuarios() {
        return btnCrearUsuarios;
    }

    public void setBtnCrearUsuarios(Button btnCrearUsuarios) {
        this.btnCrearUsuarios = btnCrearUsuarios;
    }
}


