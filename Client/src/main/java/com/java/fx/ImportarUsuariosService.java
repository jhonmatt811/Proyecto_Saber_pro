package com.java.fx;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.java.fx.Usuarios_y_Roles.Persona;
import com.java.fx.Usuarios_y_Roles.Usuario;
import com.java.fx.model.Rol;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ImportarUsuariosService {

    private ApiService apiService;

    public ImportarUsuariosService(ApiService apiService) {
        this.apiService = apiService;
    }

    private boolean filaVacia(Row row) {
        if (row == null) return true;
        for (int c = 0; c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK &&
                    !(cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty())) {
                return false;
            }
        }
        return true;
    }



    public List<Usuario> importarUsuariosDesdeExcel(File archivoExcel) throws IOException, InterruptedException, InvalidFormatException {
        List<Usuario> usuarios = new ArrayList<>();
        List<Persona> personas = new ArrayList<>();
        Map<String, Integer> nombreRolToId;

        // Obtener roles del backend
        try {
            nombreRolToId = apiService.obtenerRoles().stream()
                    .collect(Collectors.toMap(Rol::getNombre, Rol::getId));
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron obtener los roles desde el backend: " + e.getMessage(), e);
        }

        try (Workbook workbook = WorkbookFactory.create(archivoExcel)) {
            DataFormatter formatter = new DataFormatter();

            for (Sheet sheet : workbook) {
                Iterator<Row> rows = sheet.iterator();
                if (rows.hasNext()) rows.next(); // Saltar encabezado

                while (rows.hasNext()) {
                    Row row = rows.next();
                    if (filaVacia(row)) continue;

                    try {
                        String[] campos = new String[7];
                        for (int i = 0; i < campos.length; i++) {
                            Cell c = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            campos[i] = formatter.formatCellValue(c).trim();
                        }

                        // Validaciones mínimas
                        if (campos[0].isBlank() || campos[1].isBlank() || campos[3].isBlank() || campos[5].isBlank() || campos[6].isBlank()) {
                            throw new IllegalArgumentException("Campos obligatorios vacíos en fila " + (row.getRowNum() + 1));
                        }

                        // Crear Persona
                        Persona persona = new Persona();
                        persona.setCc(Long.parseLong(campos[0]));
                        persona.setPrimer_nombre(campos[1]);
                        persona.setSegundo_nombre(campos[2]);
                        persona.setPrimer_apellido(campos[3]);
                        persona.setSegundo_apellido(campos[4]);
                        persona.setEmail(campos[5]);

                        // Crear Usuario asociado a Persona
                        Usuario usuario = new Usuario();
                        usuario.setCorreo(campos[5]);

                        Integer rolId = nombreRolToId.get(campos[6]);
                        if (rolId == null) {
                            throw new IllegalArgumentException("Rol no encontrado: " + campos[6]);
                        }
                        usuario.setRol_id(rolId);

                        personas.add(persona);
                        usuarios.add(usuario);

                    } catch (Exception e) {
                        int fila = row.getRowNum() + 1;
                        String mensaje = "Error en fila " + fila + ": " + e.getMessage();

                        // Mostrar alerta en JavaFX
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Error al importar usuario");
                            alert.setHeaderText("Error en la fila " + fila);
                            alert.setContentText(e.getMessage());
                            alert.showAndWait();
                        });

                        System.err.println(mensaje);
                    }
                }
            }
        }

        // Verifica que haya personas antes de continuar
        if (personas.isEmpty()) {
            throw new RuntimeException("No se encontró ninguna persona válida en el archivo.");
        }

        // Crear personas en el backend
        List<Persona> personasCreadas = apiService.crearPersonasEnLote(personas);

        // Validaciones post-creación
        if (personasCreadas.size() != usuarios.size()) {
            throw new RuntimeException("El número de personas creadas no coincide con los usuarios.");
        }

        // Asignar personas creadas a usuarios
        for (int i = 0; i < usuarios.size(); i++) {
            Persona personaCreada = personasCreadas.get(i);
            if (personaCreada == null || personaCreada.getId() == null) {
                throw new RuntimeException("Persona inválida en la posición " + i);
            }
            usuarios.get(i).setPersona(personaCreada);
        }

        // Crear usuarios en el backend
        List<Usuario> usuariosCreados = apiService.crearUsuariosEnLote(usuarios);

        return usuariosCreados;
    }


    private Usuario parsearUsuario(String[] campos, Map<String, Integer> nombreRolToId) {
        Persona persona = new Persona();

        try {
            persona.setCc(Long.parseLong(campos[0]));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("CC inválido: '" + campos[0] + "'");
        }

        persona.setPrimer_nombre(campos[1]);
        persona.setSegundo_nombre(campos[2]);
        persona.setPrimer_apellido(campos[3]);
        persona.setSegundo_apellido(campos[4]);
        persona.setEmail(campos[5]); 

        String nombreRol = campos[6];

        Integer rolId = nombreRolToId.get(nombreRol);
        if (rolId == null) {
            throw new IllegalArgumentException("Rol no encontrado: '" + nombreRol + "'");
        }

        Usuario usuario = new Usuario();
        usuario.setPersona(persona);
        usuario.setRol_id(rolId);
        usuario.setIs_active(true);

        return usuario;
    }
}
