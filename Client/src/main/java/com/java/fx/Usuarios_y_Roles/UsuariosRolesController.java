package com.java.fx.Usuarios_y_Roles;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.java.fx.ApiService;
import com.java.fx.ImportarUsuariosService;
import com.java.fx.model.Rol;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UsuariosRolesController {
    @Getter
    @Setter
    @FXML private TableView<Usuario> tablaUsuarios;
    @Setter
    @Getter
    @FXML private ComboBox<Rol> comboNuevoRol;
    @Setter
    @Getter
    private Node vistaPrincipalOriginal;

    @FXML private Button btnCambiarRol;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colSecNombre;
    @FXML private TableColumn<Usuario, String> colApellido;
    @FXML private TableColumn<Usuario, String> colSecApellido;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colNombreRol;
    @FXML private Button btnCrearUsuarios;
    @FXML private Button btnCargarUsuarios;
    @Setter
    @Getter
    @FXML private BorderPane mainPane;
    @FXML private ApiService rolService;
    @FXML private TextField txtFiltroNom;
    @FXML private ComboBox<Rol> comboFiltroRol;
    @FXML private Label label_rol;
    @FXML private Label label_nombre;
    @FXML
    private TableColumn<Usuario, Void> accionCol;
    @FXML
    private Label statusLabel;



    public Label getLabel_rol() {
        return label_rol;
    }
    public ComboBox<Rol> getComboFiltroRol() {
        return comboFiltroRol;
    }

    public TableView<Usuario> getTablaUsuarios() {
        return tablaUsuarios;
    }

    public void setTablaUsuarios(TableView<Usuario> tablaUsuarios) {
        this.tablaUsuarios = tablaUsuarios;
    }

    public void setComboFiltroRol(ComboBox<Rol> comboFiltroRol) {
        this.comboFiltroRol = comboFiltroRol;
    }

    public Label getLabel_nombre() {
        return label_nombre;
    }

    public TextField getTxtFiltroNom() {
        return txtFiltroNom;
    }

    public Button getBtnCambiarRol() {
        return btnCambiarRol;
    }

    private final ApiService apiService = new ApiService();
    private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private FilteredList<Usuario> filteredUsuarios = new FilteredList<>(listaUsuarios);

    @FXML
    public void initialize() {

        vistaPrincipalOriginal = mainPane.getCenter();
        tablaUsuarios.setItems(filteredUsuarios);
        configurarBoton();
        cargarUsuarios();
        cargarRoles();
        agregarColumnaAccion();


        txtFiltroNom.textProperty().addListener((obs, oldVal, newVal) -> actualizarFiltro());

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

        comboFiltroRol.valueProperty().addListener((obs, oldVal, newVal) -> actualizarFiltro());

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
                        cellData.getValue().getRol() != null ? cellData.getValue().getRol().getNombre() :"sin rol"
                ));

        tablaUsuarios.setRowFactory(tv -> {
            TableRow<Usuario> row = new TableRow<>();

            // Listener para observar cambios en el estado activo
            row.itemProperty().addListener((obs, oldUsuario, newUsuario) -> {
                if (oldUsuario != null) {
                    oldUsuario.activeProperty().removeListener((observable) -> updateRowStyle(row, oldUsuario));
                }
                if (newUsuario != null) {
                    newUsuario.activeProperty().addListener((observable) -> updateRowStyle(row, newUsuario));
                    updateRowStyle(row, newUsuario);
                } else {
                    row.setStyle("");
                }
            });

            return row;
        });

    }

    // Método auxiliar para aplicar estilo según el estado
    private void updateRowStyle(TableRow<Usuario> row, Usuario usuario) {
        if (!usuario.isActive()) {
            row.setStyle("-fx-background-color: #d3d3d3; -fx-text-fill: #666;");
        } else {
            row.setStyle("");
        }
    }

    private void agregarColumnaAccion() {
        accionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button();

            {
                btn.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    boolean nuevoEstado = !usuario.isActive();

                    cambiarEstadoUsuario(usuario.getId(), nuevoEstado);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    btn.setText(usuario.isActive() ? "Desactivar" : "Activar");
                    btn.setStyle(usuario.isActive() ? "-fx-background-color: #dc3545; -fx-text-fill: white;"
                            : "-fx-background-color: #28a745; -fx-text-fill: white;");
                    setGraphic(btn);
                }
            }
        });
    }
    private void cambiarEstadoUsuario(UUID userId, boolean activate) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                String urlStr = String.format("http://localhost:8080/admin/usuarios/%s/active?activate=%s", userId, activate);
                URL url = new URL(urlStr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setDoOutput(true);

                // Agregar cabecera Authorization con Bearer token
                connection.setRequestProperty("Authorization", "Bearer " + Sesion.jwtToken);

                connection.connect();

                int responseCode = connection.getResponseCode();

                if (responseCode == 200) {
                    Platform.runLater(() -> {
                        mostrarMensaje("Usuario " + (activate ? "activado" : "desactivado") + " exitosamente.", false);
                        Usuario usuario = tablaUsuarios.getItems().stream()
                                .filter(u -> u.getId().equals(userId))
                                .findFirst()
                                .orElse(null);
                        if (usuario != null) {
                            usuario.setIs_active(activate);
                            tablaUsuarios.refresh();
                        }
                    });
                } else {
                    InputStream errorStream = connection.getErrorStream();
                    if (errorStream != null) {
                        String errorMsg = new BufferedReader(new InputStreamReader(errorStream))
                                .lines().collect(Collectors.joining("\n"));
                        System.err.println("Error del servidor: " + errorMsg);
                    }
                    Platform.runLater(() -> mostrarMensaje("Error al actualizar estado del usuario.", false));
                }

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> mostrarMensaje("Error de conexión al servidor.", false));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }


    private void mostrarMensaje(String mensaje, boolean esExito) {
        Platform.runLater(() -> {
            statusLabel.setText(mensaje);
            statusLabel.setStyle(esExito ? "-fx-text-fill: green; -fx-font-weight: bold;" : "-fx-text-fill: red; -fx-font-weight: bold;");
        });
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
                        listaUsuarios.setAll(usuarios));  // Esto actualiza los datos y mantiene el filtrado

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
                    ObservableList<Rol> listaRoles = FXCollections.observableArrayList(roles);

                    // Para el combo de asignación de rol
                    comboNuevoRol.setItems(listaRoles);

                    // Para el combo de filtro de rol
                    ObservableList<Rol> listaFiltro = FXCollections.observableArrayList();
                    listaFiltro.add(null); // opción "Todos"
                    listaFiltro.addAll(roles);
                    comboFiltroRol.setItems(listaFiltro);

                    // Personalizar cómo se muestran los roles en ambos ComboBox
                    configurarComboBoxRol(comboNuevoRol);
                    configurarComboBoxRol(comboFiltroRol);

                    comboFiltroRol.setPromptText("Todos los roles");
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> mostrarAlerta("Error", "No se pudieron cargar los roles."));
            }
        }).start();
    }
    private void configurarComboBoxRol(ComboBox<Rol> comboBox) {
        comboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Rol rol, boolean empty) {
                super.updateItem(rol, empty);
                if (empty || rol == null) {
                    setText("Todos");
                } else {
                    setText(rol.getNombre());
                }
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Rol rol, boolean empty) {
                super.updateItem(rol, empty);
                if (empty || rol == null) {
                    setText("Todos");
                } else {
                    setText(rol.getNombre());
                }
            }
        });
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

    public Button getbtnCargarUsuarios() {return btnCargarUsuarios;}

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
            btnCargarUsuarios.setVisible(false);
            txtFiltroNom.setVisible(false);
            comboFiltroRol.setVisible(false);
            label_nombre.setVisible(false);
            label_rol.setVisible(false);
            comboFiltroRol.setVisible(false);

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
    @FXML
    private void onCargarUsuariosClick() {
        ApiService apiService = new ApiService();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(null);

        if (archivoSeleccionado != null) {
            new Thread(() -> {
                try {
                    ImportarUsuariosService importarService = new ImportarUsuariosService(apiService);
                    List<Usuario> usuarios = importarService.importarUsuariosDesdeExcel(archivoSeleccionado);

                    List<Usuario> usuariosCreados = apiService.crearUsuariosEnLote(usuarios);

                    Platform.runLater(() -> {
                        if (usuariosCreados != null && !usuariosCreados.isEmpty()) {
                            ObservableList<Usuario> nuevaLista = FXCollections.observableArrayList(usuariosCreados);
                            listaUsuarios.addAll(usuariosCreados);


                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Importación exitosa");
                            alert.setHeaderText(null);
                            alert.setContentText("Usuarios creados correctamente.");
                            alert.showAndWait();

                        } else {
                            mostrarError("No se crearon usuarios o la respuesta fue vacía.");
                        }
                    });
                } catch (RuntimeException | IOException | InterruptedException e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error de importación");
                        alert.setHeaderText("No se pudo importar usuarios");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                    });
                }

            }).start();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    private void actualizarFiltro() {
        String filtroNombre = txtFiltroNom.getText().trim().toLowerCase();
        Rol filtroRol = comboFiltroRol.getValue(); // ahora usamos el ComboBox

        filteredUsuarios.setPredicate(usuario -> {
            if (usuario.getPersona() == null) return false;

            String primerNombre = usuario.getPersona().getPrimer_nombre() != null ?
                    usuario.getPersona().getPrimer_nombre().toLowerCase() : "";
            String segundoNombre = usuario.getPersona().getSegundo_nombre() != null ?
                    usuario.getPersona().getSegundo_nombre().toLowerCase() : "";
            String nombreCompleto = (primerNombre + " " + segundoNombre).trim();

            boolean coincideNombre = nombreCompleto.contains(filtroNombre);
            boolean coincideRol = (filtroRol == null) || (usuario.getRol() != null &&
                    filtroRol.getId() == usuario.getRol().getId());

            return coincideNombre && coincideRol;
        });

        tablaUsuarios.refresh();
    }

}
