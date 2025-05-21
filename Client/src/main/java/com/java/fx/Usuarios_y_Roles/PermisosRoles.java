package com.java.fx.Usuarios_y_Roles;
import java.util.HashMap;
import java.util.Map;

public class PermisosRoles {

    private final Map<String, Integer> jerarquiaRoles = new HashMap<>();
    private final Map<String, Integer> nivelMinimoPorAccion = new HashMap<>();
    private int nivelActual = 0;

    public PermisosRoles(String rol) {
        // Definir jerarquía
        jerarquiaRoles.put("coordinador de saber pro", 1);
        jerarquiaRoles.put("director de programa", 2);
        jerarquiaRoles.put("decano", 3);
        jerarquiaRoles.put("DocenteyEstudiante",1 );
        jerarquiaRoles.put("Comitedelprograma",2);


        // Definir niveles mínimos por acción
        nivelMinimoPorAccion.put("inicio", 1);
        nivelMinimoPorAccion.put("resultados", 1);
        nivelMinimoPorAccion.put("crearUsuarios", 3);
        nivelMinimoPorAccion.put("accMejora", 1);
        nivelMinimoPorAccion.put("usuariosRoles", 3);
        nivelMinimoPorAccion.put("configuracion", 2);
        nivelMinimoPorAccion.put("salir", 1);

        this.nivelActual = jerarquiaRoles.getOrDefault(rol.toLowerCase(), 0);
    }

    public boolean tienePermiso(String accion) {
        return nivelActual >= nivelMinimoPorAccion.getOrDefault(accion, Integer.MAX_VALUE);
    }
}