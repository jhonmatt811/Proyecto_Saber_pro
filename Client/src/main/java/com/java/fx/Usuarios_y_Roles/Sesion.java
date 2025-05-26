package com.java.fx.Usuarios_y_Roles;

public class Sesion {
    public static String jwtToken ;
    public static String rol_id;
    public static String emailUsuario;
    public static Usuario usuarioActual;
    public static String passwordUsuario;

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static String getPasswordUsuario() {
        return passwordUsuario;
    }

    public static void setPasswordUsuario(String passwordUsuario) {
        Sesion.passwordUsuario = passwordUsuario;
    }

    public static void setUsuarioActual(Usuario usuarioActual) {
        Sesion.usuarioActual = usuarioActual;
    }

    public static String getRol_id() {
        return rol_id;
    }

    public static void setJwtToken(String token) {
        jwtToken = token;
    }

    public static String getJwtToken() {
        return jwtToken;
    }

    public static String getEmailUsuario() {
        return emailUsuario;
    }
}