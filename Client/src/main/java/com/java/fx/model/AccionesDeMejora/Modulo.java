package com.java.fx.model.AccionesDeMejora;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL) // Ignora campos null
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos no esperados
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
    @Override
    public String toString() {
        return this.nombre != null ? this.nombre : "Módulo no disponible";
    }
    public Long getId() {return id;}
    public String getNombre() {return nombre;}

    public void setId(Long id) {this.id = id;}
    public void setNombre(String nombre) {this.nombre = nombre;}
}
