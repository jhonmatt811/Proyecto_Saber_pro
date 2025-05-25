package com.java.fx.Usuarios_y_Roles;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.util.List;

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
    @FXML private TextField txtFiltroRol;
    @FXML private Label label_rol;
    @FXML private Label label_nombre;

    public Label getLabel_rol() {
        return label_rol;
    }

    public Label getLabel_nombre() {
        return label_nombre;
    }

    public TextField getTxtFiltroNom() {
        return txtFiltroNom;
    }

    public TextField getTxtFiltroRol() {
        return txtFiltroRol;
    }

    private final ApiService apiService = new ApiService();
    private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private FilteredList<Usuario> filteredUsuarios = new FilteredList<>(listaUsuarios);

    @FXML
    public void initialize() {

        tablaUsuarios.setItems(filteredUsuarios);
        cargarUsuarios();
        configurarBoton();
        cargarRoles();

        txtFiltroNom.textProperty().addListener((obs, oldVal, newVal) -> actualizarFiltro());
        txtFiltroRol.textProperty().addListener((obs, oldVal, newVal) -> actualizarFiltro());

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
            txtFiltroRol.setVisible(false);
            label_nombre.setVisible(false);
            label_rol.setVisible(false);
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
            try {
                ImportarUsuariosService importarService = new ImportarUsuariosService(apiService);
                List<Usuario> usuarios = importarService.importarUsuariosDesdeExcel(archivoSeleccionado);

                // ✅ Enviar usuarios al backend
                List<Usuario> usuariosCreados = apiService.crearUsuariosEnLote(usuarios);


                // ✅ Mostrar en la tabla los usuarios creados
                tablaUsuarios.getItems().addAll(usuariosCreados);
                tablaUsuarios.refresh();       // Refresca visualmente


            } catch (Exception e) {
                e.printStackTrace();
                mostrarError("Error al importar usuarios: " + e.getMessage());
            }
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
        String filtroRol = txtFiltroRol.getText().trim().toLowerCase();

        filteredUsuarios.setPredicate(usuario -> {
            // Manejo seguro de nulos para Persona
            if (usuario.getPersona() == null) return false;

            // Obtener componentes del nombre
            String primerNombre = usuario.getPersona().getPrimer_nombre() != null ?
                    usuario.getPersona().getPrimer_nombre().toLowerCase() : "";
            String segundoNombre = usuario.getPersona().getSegundo_nombre() != null ?
                    usuario.getPersona().getSegundo_nombre().toLowerCase() : "";

            // Combinar nombres para búsqueda
            String nombreCompleto = (primerNombre + " " + segundoNombre).trim();

            // Manejo seguro de nulos para Rol
            String rolNombre = usuario.getRol() != null && usuario.getRol().getNombre() != null ?
                    usuario.getRol().getNombre().toLowerCase() : "";

            // Aplicar filtros combinados
            return nombreCompleto.contains(filtroNombre) && rolNombre.contains(filtroRol);
        });

        // Forzar actualización de la tabla
        tablaUsuarios.refresh();
    }

}
