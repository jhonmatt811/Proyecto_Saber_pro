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
    private RolService rolService;    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String mailSenderAddress;


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

    public User register(UserDTO dto, String passwd) {
        Rol rol = rolService.findById(dto.getRol_id());
        PersonaDTO personDTO = dto.getPerson();
        User newUser = new User(personDTO, passwd,rol);
        
        return userRepository.save(newUser);
    }
    
    public User changeActivate(Boolean activate, UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("No se encontró el usuario"));

        user.setIs_active(activate);
        return userRepository.save(user);
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
    @Async
    public void sendEmail(PersonaDTO person, String passwd) throws Exception {
        String subject = "Registro Exitoso - ICFES";

        // Cargar el contenido HTML de un archivo o desde un String
        String htmlContent = loadHtmlTemplate(person, passwd);

        // Crear el MimeMessage y configurar el correo
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, true); // true para contenido HTML
        helper.setTo(person.getEmail());
        helper.setSubject(subject);
        helper.setFrom(mailSenderAddress);  // Usar la variable cargada desde el entorno
        helper.setText(htmlContent, true);  // true indica que es HTML

        mailSender.send(message);  // Enviar el correo
    }

    private String loadHtmlTemplate(PersonaDTO person, String passwd) throws IOException {
        // Aquí cargamos la plantilla HTML desde un archivo local
        Path path = Path.of("src/main/resources/templates/InvitationEmail.html");
        String htmlTemplate = Files.readString(path);

        // Reemplazar las variables en la plantilla
        htmlTemplate = htmlTemplate.replace("{PrimerNombre}", person.getPrimer_nombre());
        htmlTemplate = htmlTemplate.replace("{PrimerApellido}", person.getPrimer_apellido());
        htmlTemplate = htmlTemplate.replace("{email}", person.getEmail());
        htmlTemplate = htmlTemplate.replace("{password}", passwd);

        return htmlTemplate;
    }
}
