package com.icfes_group.service;

import com.icfes_group.dto.PersonaDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import com.icfes_group.repository.UserRepository;
import com.icfes_group.model.User;
import com.icfes_group.dto.UserDTO;
import com.icfes_group.model.Rol;
import com.icfes_group.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User login(UserDTO dto) throws Exception
    {
        User userProps = new User(dto);
        User userBd = userRepository.findByEmailWithPersona(dto.getEmail());
        if(userBd == null){
            throw new Exception("No se econtro el email");
        }
        if(!passwordEncoder.matches(userProps.getPassword(),userBd.getPassword())){
            throw new Exception("La contraseña no coincide");
        }
        if(!userBd.getIs_active()){
            throw new Exception("Uusario no activo");
        }
        userProps.setRol(userBd.getRol());
        return userProps;
    }       
}
