/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.icfes_group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.icfes_group.model.User;
import java.util.UUID;
/**
 *
 * @author juanc
 */
public interface UserRepository extends JpaRepository<User, UUID>{
    
}
