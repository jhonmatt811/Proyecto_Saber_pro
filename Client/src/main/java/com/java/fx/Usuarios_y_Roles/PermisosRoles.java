package com.java.fx.Usuarios_y_Roles;
import java.util.HashMap;
import java.util.Map;

public class PermisosRoles {

    private final Map<String, Integer> jerarquiaRoles = new HashMap<>();
    private final Map<String, Integer> nivelMinimoPorAccion = new HashMap<>();
    private int nivelActual ;

    public PermisosRoles(String rol) {
        // Definir jerarquía
        jerarquiaRoles.put("decano", 4);
        jerarquiaRoles.put("coordinador de saber pro", 3);
        jerarquiaRoles.put("director de programa", 2);
        jerarquiaRoles.put("comité de programa", 1);
        jerarquiaRoles.put("comitedelprograma", 1); // Alias
        jerarquiaRoles.put("profesor", 1);
        jerarquiaRoles.put("profesor y estudiante", 1); // Alias

        // Niveles mínimos por acción
        nivelMinimoPorAccion.put("inicio", 1);
        nivelMinimoPorAccion.put("resultados", 1);
        nivelMinimoPorAccion.put("crearUsuarios", 4);      // Solo Decano
        nivelMinimoPorAccion.put("usuariosRoles", 4);      // Solo Decano
        nivelMinimoPorAccion.put("cargarArchivo", 2);      // Director en adelante
        nivelMinimoPorAccion.put("exportar", 2);           // Director en adelante
        nivelMinimoPorAccion.put("accMejora", 1);          // Comité en adelante
        nivelMinimoPorAccion.put("configuracion", 1);      // Todos pueden cambiar contraseña
        nivelMinimoPorAccion.put("salir", 0);
        nivelMinimoPorAccion.put("resultadosIcfes",1);


        this.nivelActual = jerarquiaRoles.getOrDefault(rol.toLowerCase(), 0);
    }

    public boolean tienePermiso(String accion) {
        return nivelActual >= nivelMinimoPorAccion.getOrDefault(accion, Integer.MAX_VALUE);
    }
}