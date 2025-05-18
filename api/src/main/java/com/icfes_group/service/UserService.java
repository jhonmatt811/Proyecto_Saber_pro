package com.icfes_group.service;

import com.icfes_group.dto.PersonaDTO;
import com.icfes_group.email.components.EmailUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
import java.util.*;

@Service
@AllArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailUtil emailUtil;

    private Integer generateCode(){
        final Random random = new Random();
        int minValue = 1000;
        int maxValue = 9999;
        return new Random().nextInt(maxValue + 1 - minValue) + minValue;
    }

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

    public void changePassword(@NotNull UserDTO dto) throws Exception {
        User userBd = userRepository.findByEmailWithPersona(dto.getEmail());
        if(userBd == null){
            throw new Exception("No se econtro el email");
        }
        if(passwordEncoder.matches(userBd.getPassword(),dto.getPassword())){
            throw new Exception("La contraseña es la misma");
        }
        String passwdCrypt = passwordEncoder.encode(dto.getPassword());
        userBd.setPassword(passwdCrypt);
        userRepository.save(userBd);
    }

    public void forgetPassword(@NotBlank String email) throws Exception {
        User userBd = userRepository.findByEmailWithPersona(email);
        if(userBd == null){
            throw new Exception("No se econtro el email");
        }
        int code = generateCode();
        Map<String,Object> params = new HashMap<>();
        params.put("code", code);
        emailUtil.loadHtmlBody("src/main/resources/templates/forgetPassword.html");
        emailUtil.addTextParams(params);
        emailUtil.sendEmail(email,"Codigo de Acceso");
    }
}
