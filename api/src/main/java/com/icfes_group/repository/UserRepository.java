    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
     */
    package com.icfes_group.repository;

    import org.springframework.data.jpa.repository.JpaRepository;
    import com.icfes_group.model.User;
    import java.util.UUID;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    /**
     *
     * @author juanc
     */
    public interface UserRepository extends JpaRepository<User, UUID>{
        @Query("SELECT u FROM User u JOIN FETCH u.persona p JOIN FETCH u.rol r WHERE p.email = :email")
        User findByEmailWithPersona(@Param("email") String email);
    }
