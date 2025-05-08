/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.model.IcfesTest;
import com.icfes_group.dto.ScoreFileDTO;
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
/**
 *
 * @author juanc
 */
@Entity
@Data
@Table(name = "evaluados")
public class Evaluated {
    @Id
    private Long documento;
    @ManyToOne
    @JoinColumn(name="tipo_documento_id")
    private TypeIdentityCard tipo_cc;
    private String nombre;
    public Evaluated(ScoreFileDTO dto,TypeIdentityCard typeCard){
        this.documento = dto.getDocumento();
        this.nombre = dto.getNombre();
        this.tipo_cc = typeCard;
    }
    public Evaluated(){}
}
