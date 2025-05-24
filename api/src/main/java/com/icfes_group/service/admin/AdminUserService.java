/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.service.admin;

import com.icfes_group.dto.PersonaDTO;
import com.icfes_group.dto.UserDTO;
import com.icfes_group.email.components.EmailUtil;
import com.icfes_group.model.Rol;
import com.icfes_group.model.User;
import com.icfes_group.repository.UserRepository;
import com.icfes_group.service.RolService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author juanc
 */
@AllArgsConstructor
@Service
public class AdminUserService  {
    private final RolService rolService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailUtil emailUtil;

    public List<User> loadUserPeople() throws Exception{
        List<User> people = userRepository.findWithPersona();
        if(people.isEmpty()){
            throw new Exception("No se encontraron usuarios");
        }
        return people;
    }

    public void sendEmail(PersonaDTO person,String passwd) throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("PrimerNombre", person.getPrimer_nombre());
        params.put("PrimerApellido", person.getPrimer_apellido());
        params.put("email", person.getEmail());
        params.put("password", passwd);

        emailUtil.loadHtmlBody("src/main/resources/templates/InvitationEmail.html");
        emailUtil.addTextParams(params);
        emailUtil.sendEmail(person.getEmail(),"Registro Exitoso - ICFES");
    }

     public User changeActivate(Boolean activate, UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("No se encontró el usuario"));

        user.setIs_active(activate);
        return userRepository.save(user);
    }
    
    public User changeRol(UUID id,Integer rolId) throws Exception{
        try {
            Optional <User> userOp = userRepository.findById(id);
            Rol rol = rolService.findById(rolId);
            if(!userOp.isPresent()){
                throw new Exception("Uusario no existe");
            }
            User user = userOp.get();       
            user.setRol(rol);
            return userRepository.save(user);
        } catch (Exception e) {       
            throw e;
       }
    }
    
    public void register(UserDTO dto, String passwd)throws Exception {
        Rol rol = rolService.findById(dto.getRol_id());
        PersonaDTO personDTO = dto.getPerson();
        User newUser = new User(personDTO, passwd,rol);
        userRepository.save(newUser);
    }
    public String generateDefPasswd() {
        String letras = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder passwd = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i <= 6; i++) {
            int randomIndex = random.nextInt(letras.length());
            passwd.append(letras.charAt(randomIndex));
        }
        return passwd.toString().toUpperCase();
    }
    
    public String hashPasswd(String passwd){
        return passwordEncoder.encode(passwd);
    }
}
