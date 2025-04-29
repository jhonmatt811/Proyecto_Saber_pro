/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.model;
import com.icfes_group.dto.PersonaDTO;
import com.icfes_group.dto.UserDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.Data;
/**
 *
 * @author juanc
 */
@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    private Persona persona;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean is_active;

    @ManyToOne
    @JoinColumn(name="rol_id")
    private Rol rol;    

    public User(UserDTO dto){
        this.id = dto.getPersonId();
        this.is_active = true;
        this.password = dto.getPassword();
    }
    
    public User(PersonaDTO person, String passwd,Rol rol){
        this.id = person.getId();
        this.is_active = true;
        this.password = passwd;
        this.rol = rol;
    }

    public User(){}
}
