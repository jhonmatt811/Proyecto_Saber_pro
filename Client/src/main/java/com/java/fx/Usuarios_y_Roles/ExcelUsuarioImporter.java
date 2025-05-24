package com.java.fx.Usuarios_y_Roles;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class ExcelUsuarioImporter {

    public static List<Usuario> importarUsuariosDesdeExcel(File archivoExcel, Map<String, Rol> rolesMap) {
        List<Usuario> usuarios = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(archivoExcel);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Primera hoja
            Iterator<Row> filas = sheet.iterator();

            if (filas.hasNext()) filas.next(); // Saltar encabezado

            while (filas.hasNext()) {
                Row fila = filas.next();

                String primerNombre = fila.getCell(0).getStringCellValue();
                String segundoNombre = fila.getCell(1).getStringCellValue();
                String primerApellido = fila.getCell(2).getStringCellValue();
                String segundoApellido = fila.getCell(3).getStringCellValue();
                String correo = fila.getCell(4).getStringCellValue();
                String nombreRol = fila.getCell(5).getStringCellValue();

                Rol rol = rolesMap.get(nombreRol);
                if (rol == null) continue;

                Persona persona = new Persona();
                persona.setPrimer_nombre(primerNombre);
                persona.setSegundo_nombre(segundoNombre);
                persona.setPrimer_apellido(primerApellido);
                persona.setSegundo_apellido(segundoApellido);
                persona.setEmail(correo);

                Usuario usuario = new Usuario();
                usuario.setPersona(persona);
                usuario.setCorreo(correo);
                usuario.setRol_id(rol.getId());
                usuario.setRol(rol);
                usuario.setIs_active(true);

                usuarios.add(usuario);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuarios;
    }
    @FXML
    private void handleImportarUsuariosDesdeExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona archivo Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));

        File archivo = fileChooser.showOpenDialog(null);

        if (archivo != null) {
            try {
                List<Rol> roles = apiService.obtenerRoles(); // USAS TU MÉTODO AQUÍ
                List<Usuario> usuariosImportados = ExcelUserImporter.importarUsuarios(archivo, roles);

                boolean exito = apiService.crearUsuariosMasivamente(usuariosImportados);

                if (exito) {
                    tablaUsuarios.getItems().addAll(usuariosImportados);
                    mostrarMensaje("Usuarios importados correctamente.");
                } else {
                    mostrarMensaje("Error al importar usuarios.");
                }

            } catch (Exception e) {
                mostrarMensaje("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}

