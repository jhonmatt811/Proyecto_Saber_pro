package com.java.fx.model.AccionesDeMejora;

import java.util.Objects;

public class Modulo {
    private Long id;
    private String nombre;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Modulo modulo = (Modulo) o;
        return Objects.equals(nombre, modulo.nombre); // Usamos el campo único nombre
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

    public Long getId() {return id;}
    public String getNombre() {return nombre;}

    public void setId(Long id) {this.id = id;}
    public void setNombre(String nombre) {this.nombre = nombre;}
}
