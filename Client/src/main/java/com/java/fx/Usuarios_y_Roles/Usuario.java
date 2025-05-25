package com.java.fx.Usuarios_y_Roles;

import com.java.fx.model.Rol;

import java.util.UUID;

public class Usuario {

    private UUID id;
    private String correo;
    private int rol_id;
    private Persona persona;
    private boolean is_active;
    private String password;
    private Rol rol;

    public Usuario() {}

    public Usuario(UUID id, String correo, int rol_id, Persona persona, boolean is_active, String password , Rol rol) {
        this.id = id;
        this.correo = correo;
        this.rol_id = rol_id;
        this.persona = persona;
        this.is_active = is_active;
        this.password = password;
        this.rol = rol;

    }

    // Getters y setters


    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getRol_id() {
        return rol_id;
    }

    public void setRol_id(int rol_id) {
        this.rol_id = rol_id;
    }
    public Persona getCc(Persona cc){return cc; }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

}