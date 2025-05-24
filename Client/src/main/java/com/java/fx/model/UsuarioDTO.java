package com.java.fx.model;

public class UsuarioDTO {
    private Long cc;
    private String nombre;
    private String segundoNombre;
    private String apellido;
    private String segundoApellido;
    private String correo;
    private String nombreRol;
    private Integer idRol;

    // Constructor, getters y setters

    public UsuarioDTO(Long cc,String nombre, String segundoNombre, String apellido, String segundoApellido, String correo, String nombreRol) {
        this.cc = cc;
        this.nombre = nombre;
        this.segundoNombre = segundoNombre;
        this.apellido = apellido;
        this.segundoApellido = segundoApellido;
        this.correo = correo;
        this.nombreRol = nombreRol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }
    public Long getCc() { return cc; }
    public void setCc(Long cc) { this.cc = cc; }
}
