package com.java.fx.Usuarios_y_Roles;

public class Sesion {
    public static String jwtToken ;
    public static String rol_id;

    public static void setJwtToken(String token) {
        jwtToken = token;
    }

    public static String getJwtToken() {
        return jwtToken;
    }

}