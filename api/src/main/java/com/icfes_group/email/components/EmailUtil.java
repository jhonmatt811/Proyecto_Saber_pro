package com.icfes_group.email.components;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Utility component for sending templated HTML emails.
 */
@Component
@RequiredArgsConstructor
public class EmailUtil {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailSenderAddress;

    /**
     * HTML content loaded from template.
     */
    private String htmlBody;

    /**
     * Carga el contenido HTML desde la ruta de un archivo.
     * @param templatePath ruta al archivo HTML
     * @throws IOException si hay un error de E/S
     */
    public void loadHtmlBody(String templatePath) throws IOException {
        Path path = Path.of(templatePath);
        this.htmlBody = Files.readString(path);
    }

    /**
     * Reemplaza los placeholders en el HTML cargado con los parámetros dados.
     * Los placeholders deben estar en el formato {key}.
     * @param params mapa de llaves y valores para reemplazo
     */
    public void addTextParams(Map<String, Object> params) {
        if (htmlBody == null) {
            throw new IllegalStateException("HTML body not loaded. Call loadHtmlBody() first.");
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            htmlBody = htmlBody.replace(placeholder, entry.getValue().toString());
        }
    }

    /**
     * Envía el correo con el HTML previamente cargado y parametrizado.
     * @param to destinatario
     * @param subject asunto
     * @throws Exception si ocurre un error al crear o enviar el correo
     */
    @Async
    public void sendEmail(String to, String subject) throws Exception {
        if (htmlBody == null) {
            throw new IllegalStateException("HTML body not prepared. Call loadHtmlBody() and addTextParams() first.");
        }
        var message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom(mailSenderAddress);
        helper.setText(htmlBody, true);
        mailSender.send(message);
    }
}