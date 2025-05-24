package com.java.fx.controller.UsersController;

import com.java.fx.service.UsersService.UsuarioService;
import com.java.fx.Usuarios_y_Roles.ExcelUsuarioImporter;
import com.java.fx.Usuarios_y_Roles.Usuario;
import com.java.fx.ApiService;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ImportarUsuariosController {

    public void importarUsuariosDesdeExcel(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));

        File archivo = fileChooser.showOpenDialog(stage);
        if (archivo != null) {
            Map<String, com.java.fx.Usuarios_y_Roles.Rol> rolesMap = ApiService.obtenerRoles();
            List<Usuario> usuarios = ExcelUsuarioImporter.importarUsuariosDesdeExcel(archivo, rolesMap);
            UsuarioService.crearUsuariosMasivamente(usuarios);

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Éxito");
            alerta.setHeaderText(null);
            alerta.setContentText("Usuarios importados correctamente.");
            alerta.showAndWait();
        }
    }
}

