package com.java.fx.model.AccionesDeMejora;

import java.util.Objects;

public class Programa {
    private Long id;
    private String snies;
    private String nombre;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Programa programa = (Programa) o;
        return Objects.equals(snies, programa.snies); // Usamos el campo único SNIES
    }

    @Override
    public int hashCode() {
        return Objects.hash(snies);
    }

    public Long getId() {return id;}
    public String getSnies() {return snies;}
    public String getNombre() {return nombre;}

    public void setId(Long id) {this.id = id;}
    public void setSnies(String snies) {this.snies = snies;}
    public void setNombre(String nombre) {this.nombre = nombre;}
}
