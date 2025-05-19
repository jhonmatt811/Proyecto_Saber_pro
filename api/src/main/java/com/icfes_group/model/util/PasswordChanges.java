package com.icfes_group.model.util;

import com.icfes_group.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "cambios_contraseñas")

public class PasswordChanges {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "fecha_cambio",nullable = false)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name="usuario_id",nullable = false)
    private User user;

    public PasswordChanges() {
        this.date = LocalDateTime.now();
    }
}