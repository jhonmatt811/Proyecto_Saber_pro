package com.java.fx;

public class Usuario {


    private String primer_nombre;
    private String segundo_nombre;
    private String primer_apellido;
    private String segundo_apellido;
    private String email;
    private int rol_id;

    // Constructor vacío
    public Usuario() {}

    // Constructor lleno
    public Usuario(long cc, String primer_nombre, String segundo_nombre,
                   String primer_apellido, String segundo_apellido,
                   String email, int rol_id) {
        this.primer_nombre = primer_nombre;
        this.segundo_nombre = segundo_nombre;
        this.primer_apellido = primer_apellido;
        this.segundo_apellido = segundo_apellido;
        this.email = email;
        this.rol_id = rol_id;
    }

    // Getters y Setters (puedes generarlos automáticamente en IntelliJ o Eclipse)
    // ...
}
